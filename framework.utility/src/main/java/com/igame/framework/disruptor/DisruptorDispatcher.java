package com.igame.framework.disruptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * @Title: QueueDispatcher.java
 * @Package com.heygam.disruptor.boot
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年4月11日 下午12:59:26
 * @Description: 异步执行队列
 * @Version V1.0
 */
public class DisruptorDispatcher extends ADisruptorDispatcher {
	private static final Logger logger = LoggerFactory.getLogger(DisruptorDispatcher.class);

	public DisruptorDispatcher() {
		this(1, null);
	}

	public DisruptorDispatcher(String executorName) {
		this(1, executorName);
	}

	public DisruptorDispatcher(int thread_size, String executorName) {
		super(thread_size, executorName);
	}

	@Override
	public void init(int thread_size) {
		disruptor.handleExceptionsWith(new ExceptionHandler() {
			@Override
			public void handleEventException(Throwable ex, long sequence, Object event) {
				if (logger.isErrorEnabled()) {
					logger.error(ex.getMessage(), ex);
				}
			}

			@Override
			public void handleOnStartException(Throwable ex) {
				if (logger.isErrorEnabled()) {
					logger.error(ex.getMessage(), ex);
				}
			}

			@Override
			public void handleOnShutdownException(Throwable ex) {
				if (logger.isErrorEnabled()) {
					logger.error(ex.getMessage(), ex);
				}
			}
		});

		if (thread_size < 2) {
			disruptor.handleEventsWith(new MessageEventBatchHandler());
		} else {
			WorkHandler<Event>[] handlers = new MessageEventHandler[thread_size];
			for (int i = 0; i < thread_size; i++) {
				handlers[i] = new MessageEventHandler();
			}
			disruptor.handleEventsWithWorkerPool(handlers);
		}
	}
}