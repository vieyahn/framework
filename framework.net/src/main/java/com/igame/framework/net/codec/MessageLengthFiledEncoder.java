package com.igame.framework.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: MessageLengthFiledEncoder
 * @Package com.heygam.net.codec
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月6日 下午3:37:38
 * @Description: 下发数据前添加长度
 * @Version V1.0
 */
@Sharable
public class MessageLengthFiledEncoder extends MessageToByteEncoder<ByteBuf> {
	private static final Logger logger = LoggerFactory.getLogger(MessageLengthFiledEncoder.class);

	/**
	 * Response Frame Structure ----------------- |<-----frame header
	 * part--->|<---Response frame body part(repeatable)--->| --
	 * --------------------------------------------------------------- --
	 * -------------- | Frame Length | Msg --------------------------
	 * ------------------------------------------------------------------
	 * ------------------|<---1 int-->|<---byte[]--->|----------------
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		int bodyLen = msg.readableBytes();
		int length = Integer.reverseBytes(bodyLen);
		out.writeInt(length);
		out.writeBytes(msg, msg.readerIndex(), bodyLen);
		if (logger.isDebugEnabled()) {
			double num = out.readableBytes() / 1024.0;
			logger.debug("下发字节数 :{} byte ,{} kb\n\n\n", out.readableBytes(), String.format("%.2f", num));
		}
	}
}
