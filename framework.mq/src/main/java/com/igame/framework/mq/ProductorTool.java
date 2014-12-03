package com.igame.framework.mq;

import java.io.Serializable;
import java.net.ConnectException;
import java.util.HashMap;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: ProductorTool
 * @Package com.heygam.common.mq
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月10日 下午3:25:11
 * @Description: 通过此类将消息发往ActiveMQ (点对点消息)
 * @Version V1.0
 */
public class ProductorTool {
	/**
	 * A simple tool for publishing messages
	 * 
	 * @version $Revision: 1.2 $
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProductorTool.class);

	private String user;
	private String password;
	private String url;

	// private Destination destination;
	// private int messageCount = 500;
	// long sleepTime = 0;
	// private boolean verbose = true;
	// private int messageSize = 255;
	private long timeToLive = 0; // 消息存活时间
	// private String user = ActiveMQConnection.DEFAULT_USER;
	// private String password = ActiveMQConnection.DEFAULT_PASSWORD;
	// private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	// private String subject; // subject的名字,默认是TOOL.DEFAULT
	// private boolean topic;
	private boolean transacted = false; // 是否采用事务
	// private boolean persistent = false;
	// private P2PQueue p2pQueue;
	private Session session;
	private Connection connection = null;
	private MessageProducer producer;
	private static HashMap<String, MessageProducer> producerMap = new HashMap<String, MessageProducer>(10);

	public ProductorTool(String user, String password, String url) throws Exception {
		this.user = user;
		this.password = password;
		this.url = url;
	}

	public void init() throws ConnectException {
		try {
			// Create the connection.
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
			connection = connectionFactory.createConnection();
			connection.start();
			// Create the session
			session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
			logger.debug("============  MQ连接创建连接成功 ==============");
		} catch (Exception e) {
			logger.error("============ MQ连接创建成功连接失败 ==============", e);
			throw new ConnectException("MQ服务器连接失败");
		} finally {

		}
	}

	/**
	 * 发送单个指令
	 * 
	 * @param commandVO
	 * @throws JMSException
	 */
	public synchronized int sendCommandMessage(Serializable content, String subject) {
		int ret = 0;
		try {
			producer = producerMap.get(subject);
			if (producer == null) {
				producer = session.createProducer(session.createQueue(subject));
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				producer.setTimeToLive(timeToLive);
				producerMap.put(subject, producer);
			}

			ObjectMessage message = session.createObjectMessage();
			message.setObject(content);

			producer.send(message);
		} catch (Exception e) {
			logger.error("ProductorTool.sendCommandMessage ", e);
			ret = 1;
		}
		return ret;
	}

	/**
	 * 关闭
	 * 
	 * @throws JMSException
	 */
	public void close() {
		if (connection != null)
			try {
				connection.close();
			} catch (JMSException e) {
				logger.error("停止失败mq服务失败 ", e);
			}
	}

	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public void setTransacted(boolean transacted) {
		this.transacted = transacted;
	}

}
