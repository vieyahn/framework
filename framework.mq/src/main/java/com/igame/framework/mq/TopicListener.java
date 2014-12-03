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
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Topic 消息接收
 * @ClassName: TopicListener  
 * @Package com.heigam.common.mq 
 * @Author Allen allen.ime@gmail.com  
 * @Date 2014年3月5日 上午10:45:24
 * @Description: 
 * Use in conjunction with TopicPublisher to test the performance of ActiveMQ Topics. 
 * @Version V1.0
 */
public class TopicListener implements MessageListener {
	private static final Logger log = LoggerFactory.getLogger(TopicListener.class);
	private Connection connection;
	private Session session;
	private Topic topic;
	private int logicserver; //当前服务器id

	public TopicListener(String user, String password, String url, String subject) throws JMSException {
		
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, password, url);
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		topic = session.createTopic(subject+logicserver);

		MessageConsumer consumer = session.createConsumer(topic);
		consumer.setMessageListener(this);

		connection.start();
	}

	public void onMessage(Message message) {
		log.debug("========接收到TopicListener消息========start==========");
		try {
			ObjectMessage objectMessage = (ObjectMessage) message;
			String mqObject = (String) objectMessage.getObject();
			//TODO 接收到消息进行处理
		} catch (Exception e) {
			log.error("TopicListener error ", e);
		}
		log.debug("========TopicListener消息=======end===========");
	}

	public void setLogicserver(int logicserver) {
		this.logicserver = logicserver;
	}

	
}
