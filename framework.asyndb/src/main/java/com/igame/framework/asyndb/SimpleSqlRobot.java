/** 
 * 
 * Copyright (c) 2014, allen.ime@gmail.com All Rights Reserved. 
 * 
 */
package com.igame.framework.asyndb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.asyndb.sender.ISqlSender;

/**
 * @Title: SimpleSqlRobot.java
 * @Package com.igame.framework.asyndb
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年9月17日 下午12:08:33
 * @Description: 不会实时写文件但是停止的时候等待重新发送的数据会备份
 * @Version V1.0
 */
public class SimpleSqlRobot implements SqlRobot {

	private Logger logger = LoggerFactory.getLogger(SimpleSqlRobot.class);

	/**
	 * 当前sql容器最大容量
	 */
	// private int maxSize = 2000;

	/**
	 * 如果数量不满足的情况下 自动提交时间 秒
	 */
	private int autoTime = 120;

	/**
	 * 发送sql的最小数量
	 */
	private int push_Sql_Number = 200;

	/**
	 * 当前sql容器
	 */
	private List<String> sqls = new ArrayList<String>(push_Sql_Number + 50);

	/**
	 * 本地备份文件目录
	 */
	private String path;

	/**
	 * 文件编码
	 */
	private String fileEncode = "UTF-8";

	/**
	 * 备份写文件
	 */
	// private Writer writer;

	/**
	 * 操作系统
	 */
	private String os;

	/**
	 * 当前备份文件序号
	 */
	private long index;

	/**
	 * 保证新增sql安全
	 */
	public ReentrantLock lock;

	/**
	 * 备份文件前缀
	 */
	private String fileNameSubfix = "sql_temp_";// 文件前缀

	/**
	 * 当前sql备份文件
	 */
	// private File file;

	/**
	 * 换行符
	 */
	private String lineSeparator;// 换行符

	/**
	 * 是否停止发送
	 */
	private boolean isStopSender = false;
	/**
	 * 是否停止接收
	 */
	private boolean isStopReceive = false;

	/**
	 * 等待发送的sql数据
	 */

	private ConcurrentLinkedQueue<SqlsInfo> tryCache;// 需要发送的sql数据

	/**
	 * 等待重发的sql
	 */
	private ConcurrentLinkedQueue<SqlsInfo> retryCache;

	/**
	 * sql发送task
	 */
	private SendMsg sendMsg;

	/**
	 * 标致 删除文件
	 */
	private String DELETE_FILE_SYMBOL = "DELETED_FILE";

	/**
	 * 标致 重发文件
	 */
	private String RETRY_FILE_SYMBOL = "RETRY_FILE";

	/**
	 * sql发送工具
	 */
	private ISqlSender sqlSender;// mq工具包
	// 最后提交时间
	private long last_submit_time = System.currentTimeMillis();

	public void setSqlSender(ISqlSender sqlSender) {
		this.sqlSender = sqlSender;
	}

	public SimpleSqlRobot() {
		lock = new ReentrantLock(false);
		sendMsg = new SendMsg(true, logger);
		tryCache = new ConcurrentLinkedQueue<SqlsInfo>();
		path = System.getProperty("user.dir");
		lineSeparator = System.getProperty("line.separator");
		retryCache = new ConcurrentLinkedQueue<SqlsInfo>();
		logger.debug(" ============sql文件缓存地址 sql_temp: {}===============", path);
		os = System.getProperty("os.name").toUpperCase();
	}

	public void sendThreadStart() {
		// 后台线程
		Thread sendMsgThread = new Thread(sendMsg, "asynDB-thread");
		sendMsgThread.setDaemon(true);
		sendMsgThread.start();
	}

	/**
	 * 启动初始化
	 */
	public void init() {
		if (sqlSender == null)// mq消息发送未实例化
			throw new NullPointerException("sql发送器为空");

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		// 文件过滤器
		FilenameFilter fnf = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith(fileNameSubfix))
					return true;
				return false;
			}
		};

		File[] files = file.listFiles(fnf);// 获取最近的sql临时文件
		if (files.length > 0) {
			List<File> fileList = Arrays.asList(files);
			Collections.sort(fileList, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					if (o1.lastModified() > o2.lastModified()) {
						return 1;
					}
					return 0;
				}
			});

			// 读取文件中的sql
			file: for (File hisFile : fileList) {
				boolean isRetry = false;
				// 如果文件不存在或者是文件夹则返回
				if (!hisFile.exists() || hisFile.isDirectory())
					continue;

				BufferedReader reader = null;
				List<String> sql_temp = new ArrayList<String>(40);
				try {
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(hisFile), fileEncode));
					String sql;
					while ((sql = reader.readLine()) != null) {
						if (sql.startsWith(DELETE_FILE_SYMBOL)) { // 等待删除文件
							if (reader != null)
								reader.close();
							sql_temp.clear();
							sql_temp = null;
							deleteFele(hisFile);
							logger.info("delete file : " + hisFile.getName());
							continue file;
						} else if (sql.startsWith(RETRY_FILE_SYMBOL)) { // 等待重发的文件

							// TODO 该文件后续可能标志为已删除文件 故没有直接添加到重发队列

							isRetry = true;// 标志添加到重发队列
							continue;
						}
						sql_temp.add(sql);
					}

				} catch (Exception e) {
					logger.error("red history File error", e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							logger.error("init error ", e);
						}
					}
					// deleteFele(hisFile);
				}
				if (isRetry) {
					retryCache.offer(new SqlsInfo(sql_temp, hisFile.getAbsolutePath()));// 添加到重发队列
				} else {
					if (sql_temp.size() >= push_Sql_Number) { // 如果达到发送的数量则放入发送列表
						tryCache.offer(new SqlsInfo(sql_temp, hisFile.getAbsolutePath()));
						logger.info("add file to try file : " + hisFile.getAbsolutePath());
					} else { // 写文件时同步的 所以有多个文件存在情况下 只可能有1个文件是大小不满足发送条件的
						sqls.addAll(sql_temp);
						index = Integer.parseInt(hisFile.getName().substring(hisFile.getName().length() - 1)) + 1;
					}
				}

			}
		}

		sendThreadStart();
		logger.info("=================== 数据库同步启动成功 =========================");
	}

	public void deleteFele(File deleteFile) {
		if (deleteFile.isFile() && !deleteFile.delete()) { // 如果删除失败
			// ,则在文件末尾加上删除标记,并且执行强制删除
			Writer writerDelete = null;
			try {
				writerDelete = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(deleteFile, true), fileEncode));
				writerDelete.write(DELETE_FILE_SYMBOL + lineSeparator);
				writerDelete.close();
			} catch (Exception e) {
				logger.error("delete file error ", e);
			} finally {
				try {
					if (writerDelete != null)
						writerDelete.close();
				} catch (Exception e) {
					logger.error("close Writer error ", e);
				}
			}

			// 如果不是windows操作系统 则调用系统命令删除
			if (os.indexOf("WINDOWS") == -1) {
				String path = deleteFile.getAbsolutePath();
				try {
					Runtime.getRuntime().exec("rm -f " + path);
				} catch (IOException e) {
					logger.error("rm -f  error", e);
				}
				logger.info("rm -f " + path);
			}
		}
	}

	/**
	 * 添加sql到队列
	 * 
	 * @param queueMessage
	 */
	public void addSql(List<String> _sqls) {
		if (isStopReceive) {
			throw new RuntimeException("停止接受sqls");
		}

		if (_sqls == null || _sqls.size() == 0)
			return;

		this.lock.lock();// 加锁
		sqls.addAll(_sqls);
		if (sqls.size() >= push_Sql_Number || ((System.currentTimeMillis() - last_submit_time) / 1000) > autoTime) {
			// 判断是否超过指定数量
			sendMsg(); // 发送
			last_submit_time = System.currentTimeMillis();
		}
		if (this.lock.isLocked() && this.lock.isHeldByCurrentThread())
			this.lock.unlock(); // 释放锁
	}

	/**
	 * 当前缓存sql加入到等待发送的sql容器
	 * 
	 * @return
	 */
	// 未执行完毕需要写文件
	public boolean close() {
		isStopReceive = true;
		this.lock.lock();// 加锁
		if (sqls.size() > 0) {
			sendMsg(); // 发送
			last_submit_time = System.currentTimeMillis();
		}
		isStopSender = true;
		if (this.lock.isLocked() && this.lock.isHeldByCurrentThread())
			this.lock.unlock(); // 释放锁
		try {
			while (!sendMsg.isFinish()) {
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("异步数据同步停止成功");
		return true;
	}

	private void sendMsg() {
		try {
			tryCache.offer(new SqlsInfo(new ArrayList<>(sqls), null));
			sqls.clear();
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}

	private class SqlsInfo {
		private List<String> sqlList;
		private String path;

		public SqlsInfo(List<String> sqlList, String path) {
			this.sqlList = sqlList;
			this.path = path;
		}

		public List<String> getSqlList() {
			return sqlList;
		}

		public String getPath() {
			return path;
		}

	}

	/**
	 * 
	 * @ClassName: SendMsg
	 * @Package org.heygam.common.sql
	 * @Author Allen allen.ime@gmail.com
	 * @Date 2014年3月8日 下午1:27:28
	 * @Description: 消息发送 可重发线程只能有一个
	 * @Version V1.0
	 */
	private class SendMsg extends Thread {

		private boolean isFinish = false;

		public boolean isFinish() {
			return isFinish;
		}

		/**
		 * 标示是否为可重发线程
		 */
		public boolean retryer;

		private Logger logger;

		public SendMsg(boolean retryer, Logger logger) {
			this.retryer = retryer;
			this.logger = logger;
		}

		public void run() {
			logger.debug("启动 异步sql 消息发生发送线程  ");
			long sleepTime = 300;
			while (true) {
				try {
					sleep(sleepTime);
					// 如果重发队列不为空 那么所有非可重发线程都进行休眠
					if (retryCache.size() > 0 && !retryer) {
						sleep((1 + retryCache.size()) * sleepTime);
						continue;
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

				boolean sendRetry = false;
				SqlsInfo sqlsInfo = null;

				if (retryCache.size() > 0) {// 优先处理重发文件
					sendRetry = true;
					// 获取但不移除此队列的头；如果此队列为空，则返回 null。
					sqlsInfo = retryCache.peek();

				} else {
					sqlsInfo = tryCache.poll();
				}
				if (sqlsInfo == null) {// 全部提交完毕才会停机
					if (isStopSender) {
						break;
					}
					try {
						sleep(30000);// 如果不存在待处理数据 则休息30s
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
					continue;
				}
				String fileName = sqlsInfo.getPath();
				List<String> tempList = sqlsInfo.getSqlList();

				try {
					int ret = sqlSender.sendSqls(tempList);

					if (ret == ISqlSender.SUCCESS) { // 如果发送成功则删除缓存
						// 如果发送成功的是重发队列 那么从重发队列中移除当前文件
						if (sendRetry) {
							retryCache.poll();
						}
						if (fileName != null) {
							deleteFele(new File(fileName));
						}
					} else if (ret == ISqlSender.ERROR_SYSTEM_ERROR) {
						if (sendRetry) { // 如果已经是重发操作 则休眠 等待下次
							continue;
						} else { // 如果是新发数据 那么在文件尾加入重发标记 并且加入重发队列
							retryCache.offer(new SqlsInfo(tempList, fileName));
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

				if (isStopSender) {
					break;
				}
			}

			SqlsInfo sqlsInfo;

			// 备份数据重发数据
			if (retryCache.size() > 0) {
				while ((sqlsInfo = retryCache.poll()) != null) {
					backupList(sqlsInfo);
				}
			}

			// 备份待发送数据
			if (tryCache.size() > 0) {
				while ((sqlsInfo = tryCache.poll()) != null) {
					backupList(sqlsInfo);
				}

			}
			isFinish = true;
			logger.info("备份数据成功！");
		}
	}

	/**
	 * 备份数据等待下次重发
	 * 
	 * @param sqlsInfo
	 */
	public void backupList(SqlsInfo sqlsInfo) {
		if (sqlsInfo.getSqlList() == null || sqlsInfo.getSqlList().isEmpty()) {
			return;
		}
		Writer writer = null;
		try {
			String temp_path = sqlsInfo.getPath();
			if (temp_path == null) {// 表示未备份
				File file = new File(path + File.separator + fileNameSubfix + index++);
				file.createNewFile();
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), fileEncode));
				List<String> back_sqls = sqlsInfo.getSqlList();
				for (String sql : back_sqls) {
					writer.write(sql + lineSeparator);
					writer.flush();
				}
				writer.write(RETRY_FILE_SYMBOL + lineSeparator);
				writer.close();
			}
		} catch (IOException e1) {
			logger.error(e1.getMessage(), e1);
		}

	}
}
