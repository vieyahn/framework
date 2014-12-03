package com.igame.framework.net.codec.multiprotocol;

import io.netty.channel.ChannelHandlerContext;

@Deprecated
public interface IPortUnificationHandler {
	public abstract void switchToHttp(ChannelHandlerContext ctx);

	public abstract void switchToSocket(ChannelHandlerContext ctx);
}
