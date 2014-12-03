//package com.igame.framework.disruptor;
//
///**
// * @ClassName: DisruptorManager
// * @Author: Allen allen.ime@gmail.com
// * @Date: 2014年12月2日 下午4:42:47
// * @Description: 业务线程管理器
// */
//public class DisruptorManager {
//	/**
//	 * 普通线程数量
//	 */
//	// private int general;
//	/**
//	 * 消息处理队列
//	 */
//	private DisruptorDispatcher[] disruptorDispatchers;
//
//	/**
//	 * 初始化
//	 * @param size 线程总大小
//	 */
//	public void init(int size) {
//		if (size <= 0) {
//			throw new IllegalArgumentException();
//		}
//		disruptorDispatchers = new DisruptorDispatcher[size];
//		for (int i = 0; i < size; i++) {
//			disruptorDispatchers[i] = new DisruptorDispatcher("queue-disruptor-" + i);
//		}
//	}
//
//	/**
//	 * 根据指令码择取线程
//	 * 
//	 * @param reqTask
//	 * @throws BadRequestExecption
//	 */
//	// public static void publish(ReqTask reqTask) throws BadRequestExecption {
//	// // ReqTask task = new ReqTask(session, commandContext, request);
//	// int queue = reqTask.getCommandContext().getQueue();
//	// if (queue == -1) {
//	// // TODO 登录成功后才可调用
//	// long userId = reqTask.getSession().getUserId();
//	// queue = selectQueue(userId);
//	// }
//	// publish(reqTask, queue);
//	// }
//
//	/**
//	 * 根据userId 择取线程
//	 * 
//	 * @param userId
//	 * @return
//	 * @throws BadRequestExecption
//	 */
//	// public static int selectQueue(long value) {
//	// return (int) (value >> 22 & (general - 1));
//	// }
//
//	/**
//	 * 指定线程 游戏业务内部分发使用
//	 * 
//	 * @param reqTask
//	 * @param queue
//	 * @throws BadRequestExecption
//	 */
//	public void publish(Runnable task, int queue) {
//		disruptorDispatchers[queue].dispatch(task);
//	}
//
//	public int size() {
//		return disruptorDispatchers.length;
//	}
//
//	public void shutDown() {
//		for (DisruptorDispatcher dispatcher : disruptorDispatchers) {
//			dispatcher.halt();
//		}
//	}
//}
