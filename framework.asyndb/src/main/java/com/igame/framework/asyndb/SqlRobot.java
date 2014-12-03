package com.igame.framework.asyndb;

import java.util.List;

/**
 * @ClassName: SqlRobot
 * @Package com.heigam.common.sql
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
	public boolean close();

	/**
	 * 添加sql
	 * 
	 * @param _sqls
	 */
	public void addSql(List<String> _sqls);
}
