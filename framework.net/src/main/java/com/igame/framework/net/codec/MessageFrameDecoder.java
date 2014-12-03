package com.igame.framework.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: MessageFrameDecoder
 * @Package com.heygam.net.codec
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月5日 下午2:41:35
 * @Description: 解析数据包
 * @Version V1.0
 */
public class MessageFrameDecoder extends ByteToMessageDecoder {
	public static Logger logger = LoggerFactory.getLogger(MessageFrameDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		logger.debug("============== MessageFrameDecoder  接受到消息开始解析  =================");
		in.markReaderIndex();
		if (!in.isReadable()) {
			in.resetReaderIndex();
			return;
		}

		// ===============正式解析客户端协议包 =====================
		if (in.readableBytes() < 4) {
			logger.debug("========== 可读字节数不够 =============");
			return;
		}
		int length = Integer.reverseBytes(in.readInt());// 接收字节长度

		if (length < 0) {
			logger.error("========== CorruptedFrameException =============");
			throw new CorruptedFrameException("negative length: " + length);
		}
		if (in.readableBytes() < length) {
			in.resetReaderIndex();
		} else {
			out.add(in.readBytes(length));
		}
	}

}
