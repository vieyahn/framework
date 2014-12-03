package com.igame.framework.net.codec;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.net.constant.LinkType;
import com.igame.framework.net.constant.NetConstant;
import com.igame.framework.net.constant.SessionKey;

/**
 * @ClassName: MessageDecoder
 * @Package com.heygam.net.codec
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月5日 下午2:58:40
 * @Description: 解析出json字符串 并反序列化为java对象
 * @Version V1.0
 */
@Sharable
public class HttpDecoder extends MessageToMessageDecoder<FullHttpRequest> {
	public static Logger logger = LoggerFactory.getLogger(HttpDecoder.class);
	private static final String WEBSOCKET_PATH = "/websocket";// websocket后缀地址
	private static final String HTTP_PATH = "/igame";// http后缀地址

	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpRequest req, List<Object> out) throws Exception {
		logger.debug("============== HttpDecoder  接受到消息开始解析 =================");
		// 设置链接类型
		if (ctx.channel().attr(SessionKey.LINK_TYPE).get() == null) {
			ctx.channel().attr(SessionKey.LINK_TYPE).set(LinkType.HTTP);
		}
		if (!req.getDecoderResult().isSuccess()) {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
			return;
		}

		if (req.getMethod() == POST) {
			ByteBuf byteBuf = req.content();
			if (!HTTP_PATH.equals(req.getUri())) {
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
				return;
			}
			int length = byteBuf.readableBytes();
			if (byteBuf != null && length > 0) {
				out.add(byteBuf.readBytes(length));
			}
			return;
		}

		if ("/favicon.ico".equals(req.getUri())) {
			FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
			sendHttpResponse(ctx, req, res);
			return;
		}

		if (WEBSOCKET_PATH.equals(req.getUri())) {// Websocket握手
			// Handshake
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, false);
			final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
			if (handshaker == null) {
				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			} else {
				handshaker.handshake(ctx.channel(), req);
				setHandshaker(ctx, handshaker);
			}
			// 保存链接类型
			ctx.channel().attr(SessionKey.LINK_TYPE).set(LinkType.WEBSOCKET);
		} else {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
			return;
		}
	}

	private void setHandshaker(ChannelHandlerContext ctx, WebSocketServerHandshaker handshaker) {
		ctx.attr(NetConstant.HANDSHAKER_ATTR_KEY).set(handshaker);
	}

	private static String getWebSocketLocation(FullHttpRequest req) {
		return "ws://" + req.headers().get(HOST) + WEBSOCKET_PATH;
	}

	/**
	 * @Description: http返回消息
	 * @param ctx
	 * @param req
	 * @param res
	 */
	private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
		// Generate an error page if response getStatus code is not OK (200).
		if (res.getStatus().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
			setContentLength(res, res.content().readableBytes());
		}
		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!isKeepAlive(req) || res.getStatus().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

}
