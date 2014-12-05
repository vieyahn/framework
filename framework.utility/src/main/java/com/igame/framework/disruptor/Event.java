package com.igame.framework.disruptor;

/**  
 * @ClassName: Event   
 * @Author: Allen allen.ime@gmail.com  
 * @Date: 2014年12月5日 下午12:00:49  
 * @Description:消息事件  
 */
public class Event {

	/** 事件对象 */
	public Runnable task;

	public void distory() {
		task = null;
	}
}
