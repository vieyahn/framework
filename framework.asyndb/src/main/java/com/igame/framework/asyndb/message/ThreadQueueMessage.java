package com.igame.framework.asyndb.message;

import java.util.List;

/**
 * @ClassName: ThreadQueueMessage
 * @Package com.heygam.common.sql
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月8日 上午11:42:13
 * @Description: sql 线程缓存
 * @Version V1.0
 */
public class ThreadQueueMessage {

	private static ThreadLocal<QueueMessage> qms = new ThreadLocal<QueueMessage>();

	public static List<String> pop() {
		List<String> sqls = null;
		QueueMessage msg = qms.get();
		if (msg != null) {
			sqls = msg.flush();
		}
		return sqls;
	}

	public static void addSql(String sql) {
		QueueMessage msg = qms.get();
		if (msg == null) {
			msg = new QueueMessage();
			qms.set(msg);
		}
		msg.addSqls(sql);
	}

	public static void remove() {
		qms.remove();
	}
}
