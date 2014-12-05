package com.igame.framework.util.common;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

public class ConcurrentRunnable<K> {
	private final ConcurrentHashMap<K, FutureTask<?>> tasks = new ConcurrentHashMap<K, FutureTask<?>>();

	/**
	 * @param task
	 * @return
	 */
	public void executeTask(final K key, final Runnable _task, Executor executor) {
		final FutureTask<?> futureTask = new FutureTask<Object>(new Callable<Object>() {
			public Object call() throws Exception {
				try {
					// 执行任务
					_task.run();
				} finally {
					// 移除这个Key任务
					tasks.remove(key);
				}
				return null;
			}
		});
		boolean interrupted = false;
		try {
			for (;;) {// 确保同一个key值的task 不会同时执行

				FutureTask<?> old = this.tasks.putIfAbsent(key, futureTask);
				if (old == null) {
					if (executor != null) {
						executor.execute(futureTask);
					} else {
						futureTask.run();
						try {
							futureTask.get();
						} catch (InterruptedException ex) {
							interrupted = true;
						} catch (ExecutionException ex) {
							throw new RuntimeException(ex);
						}
					}
					break;
				} else {
					try {
						old.get();
					} catch (InterruptedException e) {
						interrupted = true;
					} catch (ExecutionException e) {
						// throw new RuntimeException(e);
						// ignore
					}
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean taskIsDone(K key) {
		FutureTask<?> futureTask = tasks.get(key);
		if (futureTask != null) {
			return futureTask.isDone();
		}
		return false;
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean taskIsCancelled(K key) {
		FutureTask<?> futureTask = tasks.get(key);
		if (futureTask != null) {
			return futureTask.isCancelled();
		}
		return false;
	}

	/**
	 * @param key
	 * @return
	 */
	public Object getTaskResult(K key) {
		FutureTask<?> futureTask = tasks.get(key);

		if (futureTask.isDone()) {
			try {
				Object result = futureTask.get();
				tasks.remove(key);
				return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * @param key
	 */
	public void removeTask(String key) {
		tasks.remove(key);
	}

	/** 
     *  
     */
	public void clearTaskList() {
		tasks.clear();
	}

	public synchronized void stop() {
	}

	/**
	 * @param key
	 */
	public void cancelTask(K key) {

		FutureTask<?> futureTask = tasks.get(key);
		if (futureTask != null) {
			if (!futureTask.isDone()) {
				futureTask.cancel(true);
			}
		}
	}

}
