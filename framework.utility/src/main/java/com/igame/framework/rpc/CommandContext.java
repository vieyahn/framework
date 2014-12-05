package com.igame.framework.rpc;

import java.lang.reflect.Method;

/**  
 * @ClassName: CommandContext   
 * @Author: Allen allen.ime@gmail.com  
 * @Date: 2014年12月5日 下午12:01:44  
 * @Description: 指令码上下文  
 */
public class CommandContext {

	/**
	 * 命令码
	 */
	private int command;
	/**
	 * 是否需要验证登录
	 */
	private boolean isToken;

	/**
	 * 是否是gm命令
	 */
	private boolean isGm;

	/**
	 * 外部是否可以调用
	 */
	private boolean outCall;

	/**
	 * 处理类
	 */
	private Object executor;
	/**
	 * 请求方法
	 */
	private Method method;

	/**
	 * 队列
	 */
	private int queue;

	/**
	 * 请求数据类
	 */
	private Class<?> requestClass;

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public boolean isToken() {
		return isToken;
	}

	public void setToken(boolean isToken) {
		this.isToken = isToken;
	}

	public Object getExecutor() {
		return executor;
	}

	public void setExecutor(Object executor) {
		this.executor = executor;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Class<?> getRequestClass() {
		return requestClass;
	}

	public void setRequestClass(Class<?> requestClass) {
		this.requestClass = requestClass;
	}

	public int getQueue() {
		return queue;
	}

	public void setQueue(int queue) {
		this.queue = queue;
	}

	public boolean isGm() {
		return isGm;
	}

	public void setGm(boolean isGm) {
		this.isGm = isGm;
	}

	public boolean isOutCall() {
		return outCall;
	}

	public void setOutCall(boolean outCall) {
		this.outCall = outCall;
	}

}
