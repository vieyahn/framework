/** 
 *
 * Copyright (c) 2015, allen.ime@gmail.com All Rights Reserved. 
 * 
 */

package com.igame.framework.asyndb2.test;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.igame.framework.asyndb.message.ThreadQueueMessage;
import com.igame.framework.asyndb2.DefaultSqlRobot;
import com.igame.framework.asyndb2.test.mapper.SecRoleService;

/**
 * @ClassName: Test2
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2015年1月5日 上午11:28:34
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public class Test2 {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		SecRoleService service = context.getBean(SecRoleService.class);
		DefaultSqlRobot defaultSqlRobot = context.getBean(DefaultSqlRobot.class);
		while (true) {
			service.getSecRole();
			service.updateSecRole();

			List<String> sql = ThreadQueueMessage.pop();
			if (sql != null && !sql.isEmpty()) {
				System.out.println("threadmessage --> " + sql);
				defaultSqlRobot.addSql(sql);
			} else {
				System.out.println("threadmessage --> " + null);
			}

			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
