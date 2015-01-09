/** 
 *
 * Copyright (c) 2015, allen.ime@gmail.com All Rights Reserved. 
 * 
 */

package com.igame.framework.asyndb2;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.asyndb.SqlRobot;

/**
 * @ClassName: SimpleSqlRobot
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2015年1月5日 下午3:08:37
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public class DefaultSqlRobot implements SqlRobot {
	private static final Logger logger = LoggerFactory.getLogger("sql_backup");

	/**
	 * 每次提交到数据库的条数(近似值)
	 */
	private final int capability;
	/**
	 * 当前sql容器
	 */
	private List<String> currentSqls;

	private final ConcurrentLinkedQueue<List<String>> sqls = new ConcurrentLinkedQueue<List<String>>();

	/**
	 * 执行器
	 */
	private ISqlExcutor sqlExcutor;

	private ReentrantLock lock;

	private Condition notEmpty;

	private Worker worker;

	private boolean isStop = false;

	public DefaultSqlRobot(int capability) {
		this.capability = capability;
	}

	public void init() {
		if(sqlExcutor == null){
			throw new NullPointerException();
		}
		currentSqls = new ArrayList<String>(capability + 50);
		lock = new ReentrantLock();
		notEmpty = lock.newCondition();
		worker = new Worker();
		worker.setDaemon(true);
		worker.start();
	}

	public void setSqlExcutor(ISqlExcutor sqlExcutor) {
		this.sqlExcutor = sqlExcutor;
	}

	public FutureTask<Boolean> stop(final CountDownLatch latch) {
		FutureTask<Boolean> result = new FutureTask<Boolean>(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				lock.lock();// 加锁
				try {
					if (sqls.size() > 0) {
						flush(true);
					}
				} finally {
					lock.unlock(); // 释放锁
				}

				for (;;) {
					if (worker.getState() == State.TERMINATED) {
						if (latch != null) {
							latch.countDown();
						}
						break;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		});
		new Thread(result).start();
		return result;
	}

	public void addSql(List<String> _sqls) {
		if (_sqls == null)
			return;
		lock.lock();
		try {
			for (String sql : _sqls) {
				if (sql == null || sql.length() == 0) {
					continue;
				}
				logger.info(sql);
				currentSqls.add(sql);
			}
			if (currentSqls.size() >= capability) {
				flush(false);
			}
		} finally {
			lock.unlock();
		}
	}

	private void flush(boolean _isStop) {
		sqls.add(currentSqls);
		currentSqls = new ArrayList<String>();
		this.isStop = _isStop;
		notEmpty.signal();
	}

	class Worker extends Thread {
		@Override
		public void run() {
			while (true) {
				List<String> execute_sqls = sqls.poll();
				if (execute_sqls != null) {
					sqlExcutor.executeSQLS(execute_sqls);
				} else {

					lock.lock();
					try {
						if (sqls.isEmpty() && isStop) {
							break;
						}
						notEmpty.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock.unlock();
					}
				}
			}
		}
	}

}
