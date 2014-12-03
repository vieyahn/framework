package com.igame.framework.disruptor;

/**
 * @Title: DispatcherListener.java
 * @Package com.igame.queue.disruptor.Listener
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年5月29日 下午12:17:00
 * @Description: 任务调度
 * @Version V1.0
 */
public interface DispatcherListener {
	/**
	 * 任务调度是否可用
	 * 
	 * @return
	 */
	boolean alive();

	/**
	 * 初始化处理
	 */
	void init(int thread_size);

	/**
	 * 停止任务调度（阻塞直到所有提交任务完成）
	 * 
	 * @return
	 */
	boolean awaitAndShutdown();

	/**
	 * 停止任务调度
	 */
	void shutdown();

	/**
	 * 强制停止任务调度（正在执行的任务将被停止，未执行的任务将被丢弃）
	 */
	void halt();

	/**
	 * 分发任务
	 * 
	 * @param event
	 */
	void dispatch(Runnable event);
}
