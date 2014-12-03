package com.igame.framework.net.codec;

import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.net.constant.LinkType;
import com.igame.framework.net.constant.SessionKey;

/**
 * @Title: MutiPotocalEncoder.java
 * @Package com.igame.framework.net.codec
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年9月12日 下午3:04:42
 * @Description: 多协议编码(适用于http websocket编码)
 * @Version V1.0
 */
@Sharable
public class MutiPotocalEncoder extends MessageToMessageEncoder<ByteBuf> {
	private static final Logger log = LoggerFactory.getLogger(MutiPotocalEncoder.class);
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		log.debug("MutiPotocalEncoder encode");
		LinkType linkType = ctx.channel().attr(SessionKey.LINK_TYPE).get();
		if (linkType == LinkType.SOCKET) {
			// TODO socket 编码
		} else if (linkType == LinkType.WEBSOCKET) {
			BinaryWebSocketFrame webSocketFrame = new BinaryWebSocketFrame(msg);
			out.add(webSocketFrame);
		} else if (linkType == LinkType.HTTP) {
			FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK);
			res.content().writeBytes(msg);
			setContentLength(res, res.content().readableBytes());
			out.add(res);
		} else {
			throw new UnsupportedEncodingException("不支持此协议 :linkType = " + linkType);
		}
	}
}
