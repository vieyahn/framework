package com.igame.framework.mq;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: ConsumerTool
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月10日 下午3:24:49
 * @Description: p2p消息的消费
 */
public class ConsumerTool implements MessageListener, ExceptionListener {
	private static final Logger log = LoggerFactory.getLogger(ConsumerTool.class);
	private boolean running;

	private Session session;
	private Destination destination;
	private boolean transacted;
	// durable为true的时候,需要配置clientID
	private boolean durable = false; // '是否持久化消息,默认是false
	private String clientId;
	private int ackMode = Session.AUTO_ACKNOWLEDGE;

	// private String consumerName;

	// private long sleepTime;
	// private long receiveTimeOut;

	// [true|flase] 是否使用topic,默认是false
	// [subject] subject的名字,默认是TOOL.DEFAULT
	// [durabl] 是否持久化消息,默认是false
	// [messagecount] 发送消息数量,默认是10
	// [messagesize] 消息长度,默认是255
	// [clientID] durable为true的时候,需要配置clientID
	// [timeToLive] 消息存活时间
	// [sleepTime] 发送消息中间的休眠时间
	// [transacte] 是否采用事务
	//
	//
	// ConsumerTool [url] broker的地址,默认的是tcp://localhost:61616
	// [true|flase] 是否使用topic,默认是false
	// [subject] subject的名字,默认是TOOL.DEFAULT
	// [durabl] 是否持久化消息,默认是false
	// [maxiumMessages] 接受最大消息数量,0表示不限制
	//
	// [clientID] durable为true的时候,需要配置clientID
	//
	// [transacte] 是否采用事务
	// [sleepTime] 接受消息中间的休眠时间,默认是0,onMeesage方法不休眠
	// [receiveTimeOut] 接受超时

	// public static void main(String[] args) {
	// ConsumerTool consumerTool = new ConsumerTool();
	// String[] unknown = CommandLineSupport.setOptions(consumerTool, args);
	// if (unknown.length > 0) {
	// System.out.println("Unknown options: " + Arrays.toString(unknown));
	// System.exit(-1);
	// }
	// consumerTool.run();
	// }

	public ConsumerTool(String user, String password, String url, String subject) throws JMSException {
		try {

			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
			Connection connection = connectionFactory.createConnection();
			if (durable && clientId != null && clientId.length() > 0 && !"null".equals(clientId)) {
				connection.setClientID(clientId);
			}
			connection.setExceptionListener(this);
			connection.start();

			session = connection.createSession(transacted, ackMode);

			// 频道id+服务器id
			String tempSubject = subject;

			destination = session.createQueue(tempSubject);

			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(this);

		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}

	public void onMessage(Message message) {
		log.debug("========ConsumerTool 接收到Queue消息========start==========");
		try {
			ObjectMessage objectMessage = (ObjectMessage) message;
			String mqObject = (String) objectMessage.getObject();
			if (mqObject != null) {
				// JSONArray array = JSON.parseArray(mqObject);
				// List<String> sqls = (List<String>) array.get(1);
				// MessageSql messageSql = MessageSql.parseFrom(mqObject.getContent());
				// System.out.println(" 收到消息  *********** " + sqls);
				// TODO 处理接收到的消息
			}

		} catch (Exception e) {
			log.error("ConsumerTool onMessage error ", e);
		}
		log.debug("========Queue消息=======end===========");
	}

	public synchronized void onException(JMSException ex) {
		log.error("ConsumerTool JMS Exception occured.  Shutting down client.", ex);
		running = false;
	}

	synchronized boolean isRunning() {
		return running;
	}

	public void setClientId(String clientID) {
		this.clientId = clientID;
	}

	public void setDurable(boolean durable) {
		this.durable = durable;
	}

}
