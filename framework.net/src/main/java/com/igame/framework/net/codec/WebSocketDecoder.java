package com.igame.framework.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.net.constant.NetConstant;

/**
 * @ClassName: MessageDecoder
 * @Package com.heygam.net.codec
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月5日 下午2:58:40
 * @Description: 解析出json字符串 并反序列化为java对象
 * @Version V1.0
 */
@Sharable
public class WebSocketDecoder extends MessageToMessageDecoder<WebSocketFrame> {
	public static Logger logger = LoggerFactory.getLogger(WebSocketDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
		logger.debug("============== MessageDecoder  接受到消息开始解析 =================");
		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			getHandshaker(ctx).close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		if (!(frame instanceof BinaryWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
		}

		BinaryWebSocketFrame binaryWebSocketFrame = ((BinaryWebSocketFrame) frame);

		/**
		 * 解码
		 */
		ByteBuf byteBuf = binaryWebSocketFrame.content();
		if (byteBuf.readableBytes() < 4) {
			logger.debug("========== 可读字节数不够 =============");
			return;
		}
		out.add(byteBuf);
	}

	private WebSocketServerHandshaker getHandshaker(ChannelHandlerContext ctx) {
		return ctx.attr(NetConstant.HANDSHAKER_ATTR_KEY).get();
	}

}
