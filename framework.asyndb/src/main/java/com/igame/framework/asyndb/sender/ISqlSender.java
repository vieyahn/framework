package com.igame.framework.asyndb.sender;

import java.util.List;

/**
 * @Title: ISqlSender.java
 * @Package com.igame.asyndb
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年6月3日 下午5:09:01
 * @Description: sql发送器
 * @Version V1.0
 */
public interface ISqlSender {

	public static final int SUCCESS = 0;
	public static final int ERROR_SYSTEM_ERROR = 1;

	/**
	 * sql发送
	 * 
	 * @param sqls
	 * @return
	 */
	public int sendSqls(List<String> sqls);

	public void close();
}
