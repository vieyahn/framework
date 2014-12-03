package com.igame.framework.disruptor;

/**
 * @Title: MessageEvent.java
 * @Package com.heygam.disruptor.event
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年4月11日 下午1:00:04
 * @Description: 消息事件
 * @Version V1.0
 */
public class Event {

	/** 事件对象 */
	public Runnable task;

	public void distory() {
		task = null;
	}
}
