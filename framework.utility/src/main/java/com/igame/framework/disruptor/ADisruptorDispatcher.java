package com.igame.framework.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * @ClassName: ADisruptorDispatcher
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2014年12月5日 上午11:59:47
 * @Description: 任务分发接口
 */
public abstract class ADisruptorDispatcher implements DispatcherListener {
	public static final Logger logger = LoggerFactory.getLogger(ADisruptorDispatcher.class);
	/** 缓存区大小 */
	public static final int BUFFER_SIZE = 1024;
	/** 消费者数量 */
	public static final int DEFAULT_IO_THREADS = Runtime.getRuntime().availableProcessors() * 2;

	private final AtomicBoolean alive = new AtomicBoolean(true);

	protected final Disruptor<Event> disruptor;
	protected final ExecutorService executor;
	/**
	 * 核心，绝大多数功能都委托给ringBuffer处理
	 */
	private final RingBuffer<Event> ringBuffer;

	public ADisruptorDispatcher() {
		this(1, null, BUFFER_SIZE, ProducerType.MULTI, new BlockingWaitStrategy());
	}

	public ADisruptorDispatcher(String executorName) {
		this(1, executorName, BUFFER_SIZE, ProducerType.MULTI, new BlockingWaitStrategy());
	}

	public ADisruptorDispatcher(final int thread_size) {
		this(thread_size, null, BUFFER_SIZE, ProducerType.MULTI, new BlockingWaitStrategy());
	}

	public ADisruptorDispatcher(int thread_size, String executorName) {
		this(thread_size, executorName, BUFFER_SIZE, ProducerType.MULTI, new BlockingWaitStrategy());
	}

	public ADisruptorDispatcher(int thread_size, final String executorName, int bufferSize, ProducerType producerType, WaitStrategy waitStrategy) {
		if ((bufferSize < 0) || (bufferSize & (bufferSize - 1)) != 0) {
			throw new IllegalArgumentException("bufferSize must be power of 2.");
		}
		/**
		 * 初始化线程池
		 */
		ThreadFactory threadFactory = new ThreadFactory() {
			private AtomicInteger counter = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, (executorName == null ? "Default-ringbuffer" : executorName) + "-" + counter.getAndIncrement());
				t.setDaemon(true);
				// t.setPriority(Thread.MAX_PRIORITY);
				return t;
			}
		};

		if (thread_size < 2) {
			executor = Executors.newSingleThreadExecutor(threadFactory);
		} else {
			executor = Executors.newFixedThreadPool(DEFAULT_IO_THREADS, threadFactory);
		}

		disruptor = new Disruptor<Event>(new EventFactory<Event>() {
			@Override
			public Event newInstance() {
				return new Event();
			}
		}, bufferSize, executor, producerType, waitStrategy);

		init(thread_size);

		ringBuffer = disruptor.start();
	}

	/**
	 * 任务分发
	 * 
	 * @param task
	 */
	@Override
	public void dispatch(Runnable task) {
		if (!alive()) {
			throw new IllegalStateException("This Dispatcher has been shutdown");
		}
		long sequence = ringBuffer.next();
		Event event = ringBuffer.get(sequence);
		event.task = task;
		ringBuffer.publish(sequence);
	}

	@Override
	public void shutdown() {
		alive.compareAndSet(true, false);
		disruptor.shutdown();
	}

	@Override
	public boolean alive() {
		return alive.get();
	}

	@Override
	public boolean awaitAndShutdown() {
		disruptor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("awaitAndShutdown error", e);
		}
		return false;
	}

	@Override
	public void halt() {
		alive.compareAndSet(true, false);
		disruptor.halt();
		executor.shutdown();
	}

}
