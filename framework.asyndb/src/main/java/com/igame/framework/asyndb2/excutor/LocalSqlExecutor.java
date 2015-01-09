/** 
 *
 * Copyright (c) 2015, allen.ime@gmail.com All Rights Reserved. 
 * 
 */

package com.igame.framework.asyndb2.excutor;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.asyndb2.ISqlExcutor;

/**
 * @ClassName: DefaultSqlExecutor
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2015年1月6日 上午10:17:31
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public class LocalSqlExecutor implements ISqlExcutor {
	private static final Logger logger = LoggerFactory.getLogger(LocalSqlExecutor.class);
	private static Logger errorSqlLogger = LoggerFactory.getLogger("errorLog");

	private SQLFile sqlFile;
	private DataSource dataSource;
	private boolean backFile = true;// 是否备份文件
	private String sqlPath;

	@Override
	public boolean executeSQLS(List<String> sqls) {
		System.out.print("LocalSqlExecutor --> executeSQLS"+sqls);
		boolean flag = false;
		try {
			if (backFile) {
				sqlFile.write(sqls.toArray(new String[0]));// sql到备份文件
			}

			if (!sqls.isEmpty()) {
				toDB(sqls); // 执行SQL
				if (backFile) {
					sqlFile.fileCommit(); // 修改备份文件
				}
			}
			flag = true;
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return flag;
	}

	public void init() {
		if (dataSource == null) {
			throw new NullPointerException();
		}
		if (backFile) {
			if (sqlPath == null) {
				throw new NullPointerException();
			}
			sqlFile = new SQLFile(sqlPath);
			if (checkIfResumeData()) { // 检查是否需要恢复数据
				resumeData();
			}
			logger.info("LocalSqlExecutor init success. backFile:{},sqlPath:{}", backFile, sqlPath);
		} else {
			logger.info("LocalSqlExecutor init success. backFile:{}", backFile);
		}
	}

	/**
	 * @Description: 恢复sql文件
	 */
	private void resumeData() {
		logger.info("begin resume data....");
		List<String> sqls = sqlFile.getLastUncommitedSqls(); // 获取未提交的SQL
		logger.info("uncommited sql :" + sqls);
		sqlFile.openResumeFile(); // 打开要恢复的文件
		try {
			toDB(sqls); // 执行SQL
			sqlFile.fileCommit(); // 修改备份文件
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * @Description: 检查是否有需要恢复的sql文件
	 * @return
	 */
	private boolean checkIfResumeData() {
		logger.info("check if need to resume data....");
		String lastSqlFile = sqlFile.getLastLineFromSqlIndex(); // 从sql.index中拿出最后一条记录
		if (lastSqlFile.trim().equals("")) {
			logger.info("last sql file empty, so will start new work");
			return false;
		}
		String comment = sqlFile.getLastLineFromFile(); // 从最后一条记录的文件中取出最后一行,如果最后一行是空
														// 或者不包含"commited"
														// 则表示需要恢复数据
		if (comment.trim().equals("") || !comment.contains("commited")) {
			logger.info("last sql file not commited, so will resume data");
			return true;
		}

		logger.info("last sql file is commited, so will start new work");
		return false;
	}

	Comparator<String> comparator = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			o1 = o1.substring(o1.lastIndexOf("#") + 1);
			o2 = o2.substring(o2.lastIndexOf("#") + 1);
			if ((Long.parseLong(o1) - Long.parseLong(o2)) > 0)
				return 1;
			else if ((Long.parseLong(o1) - Long.parseLong(o2)) == 0)
				return 0;
			else
				return -1;
		}
	};

	/**
	 * @Description: 同步到数据库
	 * @param sqls
	 * @throws SQLException
	 */
	private void toDB(List<String> sqls) throws SQLException {
		try {
			Collections.sort(sqls, comparator);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		Connection con = dataSource.getConnection();
		con.setAutoCommit(false);
		Statement stmt = con.createStatement();
		for (String sql : sqls) {
			try {
				sql = sql.substring(0, sql.lastIndexOf("#"));
				stmt.addBatch(sql);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				if (con == null || stmt == null || con.isClosed() || stmt.isClosed()) {
					executeSQLS(sqls);
					return;
				}
			}
		}

		try {
			stmt.executeBatch();
		} catch (BatchUpdateException be) {
			logger.error(be.getMessage(), be);
			try {
				int[] effectSql = be.getUpdateCounts();
				for (int i = 0; i < effectSql.length; i++) {
					if (effectSql[i] == Statement.EXECUTE_FAILED) {
						String errorSql = sqls.get(i);
						errorSqlLogger.error(errorSql + " #" + be);
						logger.error("fail execute sql :" + errorSql + " #" + be);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		try {
			con.commit();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			con.rollback();
			for (String sql : sqls) {
				errorSqlLogger.error(sql);
			}
		} finally {
			if (stmt != null) {
				stmt.clearBatch();
				stmt.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setBackFile(boolean backFile) {
		this.backFile = backFile;
	}

	public void setSqlPath(String sqlPath) {
		this.sqlPath = sqlPath;
	}

}
