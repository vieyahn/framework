package com.igame.framework.net.handler.impl;

import io.netty.channel.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.net.constant.SessionKey;
import com.igame.framework.net.handler.IRequestHandler;
import com.igame.framework.rpc.CommandContext;

/**
 * @Title: CommandValidatedService.java
 * @Package com.heygam.net.component
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月13日 下午5:12:38
 * @Description: 命令码登录校验
 * @Version V1.0
 */

public class CommandValidatedHandler<T> implements IRequestHandler<T> {
	private static final Logger logger = LoggerFactory.getLogger(CommandValidatedHandler.class);

	@Override
	public void execute(Channel channel, T message, CommandContext commandContext) {
		logger.debug("=============命令码登录校验 CommandValidatedService ====== ");
		if (!commandContext.isOutCall())
			throw new SecurityException("exception.illegal.operation");
		if (commandContext.isToken()) {// 需要验证是否登录
			if (channel.attr(SessionKey.TOKEN).get() == null) {
				logger.debug("========== 用户未登录，请登录... 命令码:" + commandContext.getCommand() + " ============ ");
				throw new SecurityException("user.login.not");
			}
		}
	}

}
