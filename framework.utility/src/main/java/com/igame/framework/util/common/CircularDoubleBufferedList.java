/** 
 * @Title: CircularDoubleBufferedList.java  
 * @Package: com.igame.framework.util.common  
 * @Date: 2014年12月4日上午11:37:59 
 * @Description: TODO(用一句话描述该文件做什么)  
 * @Version: V1.0
 * Copyright (c) 2014, allen.ime@gmail.com All Rights Reserved. 
 * 
 */

package com.igame.framework.util.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName: CircularDoubleBufferedList
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2014年12月4日 上午11:37:59
 * @Description: 双缓存模式队列
 */
public class CircularDoubleBufferedList<E> {

	private List<E> itemW;

	private ConcurrentLinkedQueue<List<E>> itemsR;

	private ReentrantLock writeLock;// 写入锁

	private volatile int writeCount;// 写入队列大小

	public CircularDoubleBufferedList(int writeCount) {
		this.writeCount = writeCount;
		itemW = new ArrayList<E>(writeCount);
		itemsR = new ConcurrentLinkedQueue<List<E>>();
		writeLock = new ReentrantLock();
	}

	@SuppressWarnings("unchecked")
	public void add(E... elements) {
		if (null == elements) {
			throw new NullPointerException();
		}
		try {
			writeLock.lock();
			if (itemW.size() + elements.length > writeCount) {
				queueSwitch();
			}
			Collections.addAll(itemW, elements);
		} catch (Exception e) {

		} finally {
			if (this.writeLock.isLocked() && this.writeLock.isHeldByCurrentThread())
				this.writeLock.unlock(); // 释放锁
		}
	}

	public List<E> poll() {
		if (itemsR.isEmpty())
			return null;

		return itemsR.poll();
	}

	public void flush() {
		try {
			writeLock.lock();
			queueSwitch();
		} catch (Exception e) {

		} finally {
			if (this.writeLock.isLocked() && this.writeLock.isHeldByCurrentThread())
				this.writeLock.unlock(); // 释放锁
		}
	}

	private void queueSwitch() {
		if (itemW.size() == 0) {
			return;
		}
		List<E> tempItemW = new ArrayList<E>(itemW);
		itemsR.offer(tempItemW);
		itemW.clear();
	}

}
