package com.igame.framework.net.core;

import io.netty.channel.Channel;
import io.netty.handler.codec.UnsupportedMessageTypeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.net.exception.handler.IExceptionHadler;
import com.igame.framework.net.handler.IRequestHandler;
import com.igame.framework.rpc.CommandContext;
import com.igame.framework.rpc.CommandManager;

/**
 * @Title: ARequestDispatcher.java
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年10月21日 下午2:19:31
 * @Description: 业务验证以及分发处理 可再次拓展成路由功能
 */
public abstract class ARequestDispatcher<T> {
	private static final Logger logger = LoggerFactory.getLogger(ARequestDispatcher.class);
	/** 统一异常处理 */
	private IExceptionHadler exceptionHadler;
	/** 链式处理命令码 */
	private List<IRequestHandler<T>> dispatchServices = new ArrayList<IRequestHandler<T>>();

	/**
	 * 分发数据
	 * 
	 * @param baseRequest
	 * @param channel
	 */
	public void dispatchProcess(final T message, final Channel channel) {
		int commandCode = getCommand(message);
		logger.debug(" =========== 服务器收到消息 开始分发 requestType = " + commandCode + " ======= ");
		/** 获取指令码上下文信息 */
		try {
			CommandContext commandContext = CommandManager.getCommandContext(commandCode);
			if (commandContext == null) {
				throw new UnsupportedMessageTypeException("404 请求命令 码不存在");
			}

			// 循序执行自定义handler 有点后悔，网络层是否不应该做一些登录校验的操作?
			Iterator<IRequestHandler<T>> it = dispatchServices.iterator();
			while (it.hasNext()) {
				it.next().execute(channel, message, commandContext);
			}
		} catch (Exception e) {// 其他异常处理
			exceptionHadler.excute(commandCode, e, channel);
		}
	}

	/**
	 * 根据上行消息获取命令码
	 * 
	 * @param request
	 * @return
	 */
	public abstract int getCommand(T request);

	public List<IRequestHandler<T>> getDispatchServices() {
		return dispatchServices;
	}

	public void setDispatchServices(List<IRequestHandler<T>> dispatchServices) {
		this.dispatchServices = dispatchServices;
	}

	public void setExceptionHadler(IExceptionHadler exceptionHadler) {
		this.exceptionHadler = exceptionHadler;
	}

}
