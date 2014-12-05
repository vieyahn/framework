/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.igame.framework.net.codec.multiprotocol;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @ClassName: serverInitializer
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月6日 下午3:41:18
 * @Description: 将编码器、解码器添加到channelPipeline
 */
@Deprecated
public abstract class ServerInitializer extends ChannelInitializer<SocketChannel> {

	// private Object portUnificationHandler = createHandler();

	public abstract AbsPortUnificationHandler createHandler();

	@Override
	public void initChannel(SocketChannel socketChannel) throws Exception {

		ChannelPipeline channelPipeline = socketChannel.pipeline();

		channelPipeline.addLast("portUnificationHandler", createHandler());
	}
}
