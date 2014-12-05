/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igame.framework.mq;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: TopicSender
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月5日 上午10:43:11
 * @Description:Use in conjunction with TopicListener to test the performance of ActiveMQ Topics.
 * @Version V1.0
 */
public class TopicSender {
	private static final Logger log = LoggerFactory.getLogger(TopicSender.class);

	private Connection connection;
	private Session session;
	private MessageProducer publisher;
	private Topic topic;

	private TopicSender(String user, String password, String url, String subject) throws Exception {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, password, url);
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		topic = session.createTopic(subject);

		publisher = session.createProducer(topic);
		publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		// session.createConsumer(control).setMessageListener(this);
		connection.start();

	}

	public void close() throws Exception {
		if (connection != null) {
			connection.stop();
			connection.close();
		}
	}

	/**
	 * 发送广播(世界消息)
	 * 
	 * @param commandVO
	 * @param serverid
	 */
	public synchronized void sendCommandMessage(String content) {
		try {
			ObjectMessage message = session.createObjectMessage();
			message.setObject(content);
			publisher.send(message);
		} catch (Exception e) {
			log.error("TopicSender.sendCommandMessage ", e);
		}
	}
}
