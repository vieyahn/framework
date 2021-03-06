package com.igame.framework.asyndb.message;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: QueueMessage
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月10日 上午9:40:24
 * @Description: sqls
 * @Version V1.0
 */
public class QueueMessage {

	private List<String> sqls;

	public QueueMessage() {
		this.sqls = new ArrayList<String>();
	}

	public List<String> flush() {
		if (sqls.isEmpty())
			return null;
		return sqls;
	}

	public void addSqls(String sql) {
		this.sqls.add(sql);
	}

	@Override
	public String toString() {
		return sqls.toString();
	}

}
