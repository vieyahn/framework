package com.igame.framework.cache;

import java.util.Date;

public abstract class CacheEntity<K> {
	// public static final int STATUS_VALID = 0;// 对象有效状态
	// public static final int STATUS_INVALID = 1;// 对象无效状态

	public abstract K getPk();

	private Status valid;// 对象是否有效
	private Date create_time;// 创建时间
	private Date modify_time;// 更新时间

	public Status getValid() {
		return valid;
	}

	public void setValid(Status valid) {
		this.valid = valid;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getModify_time() {
		return modify_time;
	}

	public void setModify_time(Date modify_time) {
		this.modify_time = modify_time;
	}

	public boolean active() {
		return valid == Status.STATUS_VALID ? true : false;
	}

	static enum Status {
		STATUS_VALID, STATUS_INVALID// 对象有效状态
	}
}
