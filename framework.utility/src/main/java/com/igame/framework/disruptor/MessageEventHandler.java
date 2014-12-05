package com.igame.framework.disruptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.WorkHandler;

/**
 * @ClassName: MessageEventHandler
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2014年12月5日 下午12:01:20
 * @Description: 消息处理器抽象 排队消费 但是线程执行时间不保证
 */
// WorkProcessor
// 多线程排队领event然后再执行,不同线程执行不同是不同的event.但是多了个排队领event的过程,这个是为了减少对生产者队列查询的压力吧.
public class MessageEventHandler implements WorkHandler<Event> {

	private static final Logger LOG = LoggerFactory.getLogger(MessageEventHandler.class);

	@Override
	public void onEvent(Event event) throws Exception {
		try {
			event.task.run();
		} catch (Exception e) {
			LOG.error("EXECUTE TASK ERROR:", e);
		}
	}
}
