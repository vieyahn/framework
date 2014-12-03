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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.net.constant.LinkType;
import com.igame.framework.net.constant.SessionKey;

/**
 * @Title: AbsPortUnificationHandler.java
 * @Package com.igame.net.codec
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年5月30日 下午2:00:52
 * @Description: 多协议支持 需要单例注入 采用spring lookup-method 效率不高 舍弃
 * @Version V1.0
 */
@Deprecated
public abstract class AbsPortUnificationHandler extends ByteToMessageDecoder implements IPortUnificationHandler {
	public static final Logger logger = LoggerFactory.getLogger(AbsPortUnificationHandler.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// Will use the first five bytes to detect a protocol.
		logger.debug("============== PortUnificationServerHandler protocal:{}", in.toString(Charset.defaultCharset()));
		if (in.readableBytes() < 4) {
			return;
		}
		final int magic1 = in.getUnsignedByte(in.readerIndex());
		final int magic2 = in.getUnsignedByte(in.readerIndex() + 1);
		if (isHttp(magic1, magic2)) {
			// 设定为http链接 如果是websocket链接后续会更新
			ctx.channel().attr(SessionKey.LINK_TYPE).set(LinkType.HTTP);
			switchToHttp(ctx);
		} else {
			ctx.channel().attr(SessionKey.LINK_TYPE).set(LinkType.SOCKET);
			switchToSocket(ctx);
		}
	}

	private static boolean isHttp(int magic1, int magic2) {
		return magic1 == 'G' && magic2 == 'E' || // GET
				magic1 == 'P' && magic2 == 'O' || // POST
				magic1 == 'P' && magic2 == 'U' || // PUT
				magic1 == 'H' && magic2 == 'E' || // HEAD
				magic1 == 'O' && magic2 == 'P' || // OPTIONS
				magic1 == 'P' && magic2 == 'A' || // PATCH
				magic1 == 'D' && magic2 == 'E' || // DELETE
				magic1 == 'T' && magic2 == 'R' || // TRACE
				magic1 == 'C' && magic2 == 'O'; // CONNECT
	}

}
