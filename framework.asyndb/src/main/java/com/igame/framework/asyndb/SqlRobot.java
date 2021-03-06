package com.igame.framework.asyndb;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * @ClassName: SqlRobot
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月4日 下午7:46:48
 * @Description: sql管理器(每次都写文件备份)
 * @Version V1.0
 */
public interface SqlRobot {

	/**
	 * 初始化
	 */
	public void init();

	/**
	 * 停止
	 * 
	 * @return
	 */
	public FutureTask<Boolean> stop(CountDownLatch latch);

	/**
	 * 添加sql
	 * 
	 * @param _sqls
	 */
	public void addSql(List<String> _sqls);
}
