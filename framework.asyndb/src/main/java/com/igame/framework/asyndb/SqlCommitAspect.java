package com.igame.framework.asyndb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.asyndb.message.ThreadQueueMessage;

/**
 * @ClassName: SqlCommitInterceptor
 * @Package com.heygam.common.sql
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月8日 上午11:40:44
 * @Description: 声明一个sql处理切面，提交sql
 * @Version V1.0
 */
public class SqlCommitAspect {
	private static final Logger logger = LoggerFactory.getLogger(SqlCommitAspect.class);
	private SqlRobot sqlRobot;

	public void setSqlRobot(SqlRobot sqlRobot) {
		this.sqlRobot = sqlRobot;
	}

	// @Before("anyMethod() && args(name)")
	// 定义前置通知,拦截的方法不但要满足声明的切入点的条件,而且要有一个String类型的输入参数,否则不会拦截
	// public void doAccessCheck(String name) {
	// System.out.println("前置通知:" + name);
	// }

	// @AfterReturning(pointcut = "anyMethod()", returning = "result")
	// 定义后置通知,拦截的方法的返回值必须是int类型的才能拦截
	// public void doAfterReturning(int result) {
	// System.out.println("后置通知:" + result);
	// }
	// @AfterThrowing(pointcut = "anyMethod()", throwing = "e")
	// 定义例外通知
	// public void doAfterThrowing(Exception e) {
	// System.out.println("例外通知:" + e);
	// }

	// 定义最终通知
	public void doAfter() {
		logger.debug("******* 最终通知  ({处理sql}) **********");
		this.sqlRobot.addSql(ThreadQueueMessage.pop());
	}

	// @Around("anyMethod()")
	// 定义环绕通知
	// public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable
	// {
	// // if(){//判断用户是否在权限
	// System.out.println("进入方法");
	// Object result = pjp.proceed();// 当使用环绕通知时，这个方法必须调用，否则拦截到的方法就不会再执行了
	// System.out.println("退出方法");
	// // }
	// return result;
	// }

}
