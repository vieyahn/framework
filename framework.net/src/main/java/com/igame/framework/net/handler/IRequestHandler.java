package com.igame.framework.net.handler;

import io.netty.channel.Channel;

import com.igame.framework.rpc.CommandContext;

/**
 * @Title: IDispatchService.java
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月13日 下午4:47:21
 * @Description: 网络层链式处理器(不建议业务处理)
 * @Version V1.0
 */

public interface IRequestHandler<T> {
	public void execute(final Channel channel, T message, CommandContext commandContext) throws Exception;

}
