/** 
 * 
 * Copyright (c) 2014, allen.ime@gmail.com All Rights Reserved. 
 * 
 */

package com.igame.framework.log;

import java.lang.Thread.State;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.util.common.CircularDoubleBufferedList;

/**
 * @ClassName: IGameLog
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2014年12月4日 下午5:35:38
 * @Description: 异步日志
 */
public abstract class IGameAsynLog<E> {
	private static final Logger log = LoggerFactory.getLogger(IGameAsynLog.class);

	/**
	 * @Fields DEFAULT_WRITECOUNT : 每次提交默认数量(可能会稍微大点)
	 */
	private static final int DEFAULT_WRITECOUNT = 500;
	/**
	 * @Fields DEFAULT_LOG_NAME : 默认线程名称
	 */
	private static final String DEFAULT_LOG_NAME = "IGameLog";

	/**
	 * @Fields DEFAULT_DELAY : 默认提交间隔时间
	 */
	private static final long DEFAULT_DELAY = 300;
	private long delay = 3000;

	private String log_name;

	private CircularDoubleBufferedList<E> cache;

	private boolean isStarted = true;// 是否开始
	// private boolean isStoped = false;// 是否停止

	private Worker worker;

	public IGameAsynLog() {
		this(DEFAULT_WRITECOUNT);
	}

	public IGameAsynLog(int writeCount) {
		this(null, writeCount);
	}

	public IGameAsynLog(String log_name, int writeCount) {
		this(log_name, writeCount, DEFAULT_DELAY, TimeUnit.MILLISECONDS);
	}

	public IGameAsynLog(String log_name, int writeCount, long delay, TimeUnit unit) {
		this.log_name = log_name;
		this.delay = unit.toMillis(delay);
		cache = new CircularDoubleBufferedList<E>(writeCount);
		// start();
	}

	@SuppressWarnings("unchecked")
	public void info(E... obj) {
		cache.add(obj);
	}

	public void start() {
		worker = new Worker();
		worker.setName(null == log_name ? DEFAULT_LOG_NAME : log_name);
		worker.setDaemon(true);
		worker.start();
	}

	public FutureTask<Boolean> stop(final CountDownLatch latch) {
		isStarted = false;
		FutureTask<Boolean> result = new FutureTask<Boolean>(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				for (;;) {
					if (worker.getState() == State.TERMINATED) {
						if (latch != null) {
							latch.countDown();
						}
						log.info("日志系统停止完毕");
						break;
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		});
		new Thread(result).start();
		return result;
	}

	class Worker extends Thread {
		public void run() {
			log.debug(" ==> ==> 启动异步操作日志消息发送线程  ");
			while (true) {
				try {
					List<E> objs = cache.poll();
					if (objs != null) {
						handler(objs);
					} else {
						if (!isStarted) {
							// isStoped = true;
							break;
						}
						Thread.sleep(delay);
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public abstract void handler(List<E> objs);
}
