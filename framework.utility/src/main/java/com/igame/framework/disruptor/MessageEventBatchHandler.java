package com.igame.framework.disruptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;

/**
 * @Title: MessageEventBatchHandler.java
 * @Package com.heygam.disruptor.handler
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年4月11日 下午1:00:17
 * @Description: 消息处理器抽象 并行消费
 * @Version V1.0
 */
public class MessageEventBatchHandler implements EventHandler<Event> {
	private static final Logger LOG = LoggerFactory.getLogger(MessageEventBatchHandler.class);

	@Override
	public void onEvent(final Event event, final long sequence, final boolean endOfBatch) throws Exception {
		try {
			event.task.run();
		} catch (Exception e) {
			LOG.error("EXECUTE TASK ERROR:", e);
		}
	}
}
