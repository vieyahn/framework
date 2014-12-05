package com.igame.framework.net.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: ClientServer.java
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年10月21日 下午2:20:10
 * @Description: netty 网络链接客户端
 */
public abstract class ClientServer extends Server {
	private static final Logger log = LoggerFactory.getLogger(ClientServer.class);
	private final String host;
	private final int port;
	private Channel channel;

	private int nThreads = Runtime.getRuntime().availableProcessors() - 1;// 线程数

	public void setnThreads(int nThreads) {
		this.nThreads = nThreads;
	}

	/**
	 * 消息发送
	 * 
	 * @param message
	 */
	public void send(Object message) {
		channel.writeAndFlush(message);
	}

	public ClientServer(String host, int port, String server_name, int server_id, String server_platform) {
		super(host, server_id, host);
		this.host = host;
		this.port = port;
	}

	@Override
	public void run() {
		super.run();
		EventLoopGroup group = new NioEventLoopGroup(nThreads);
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					initChannelInner(ch);
				}

			});
			channel = b.connect(host, port).sync().channel();
			// Start the connection attempt.
			channel.closeFuture().sync();
		} catch (Exception e) {
			log.error("ClientServer start error ", e);
		} finally {
			group.shutdownGracefully();
		}
	}

	protected abstract void initChannelInner(SocketChannel ch) throws Exception;

}
