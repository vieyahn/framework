package com.igame.framework.disruptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;

/**  
 * @ClassName: MessageEventBatchHandler   
 * @Author: Allen allen.ime@gmail.com  
 * @Date: 2014年12月5日 下午12:01:06  
 * @Description: 消息处理器抽象 并行消费   
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
