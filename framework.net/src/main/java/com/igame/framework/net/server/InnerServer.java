package com.igame.framework.net.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: InnerServer.java
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年9月30日 下午5:16:02
 * @Description: 内网服务器
 */
public abstract class InnerServer extends Server {
	private int port = 9998;// 默认端口
	protected int timeoutSeconds = 300;
	private int nThreads = Runtime.getRuntime().availableProcessors() - 1;// 线程数

	protected InnerServer(String server_name, int server_id, String server_platform, int port) {
		super(server_name, server_id, server_platform);
		this.port = port;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public void setnThreads(int nThreads) {
		this.nThreads = nThreads;
	}

	private static final Logger logger = LoggerFactory.getLogger(InnerServer.class);

	// private int so_rcvbuf = 1024;
	// private int so_sndbuf = 2 * 1024;

	public void run() {
		super.run();
		logger.info("InnerServer start --> port:{}, timeoutSeconds:{}, nThreads{}", port, timeoutSeconds, nThreads);
		/**
		 * Socket选项： TCP_NODELAY：是否立即发送数据，Negale算法，发送大批量数据，并且接收端及时响应的情况下很有用， 如果是发送少量数据并且接收端不及时响应则会严重降低性能 ，采用该算法将把发送数据缓冲起来到一定大小后一次性发出，等待接收端响应后再发送下一批数据。
		 * 默认false采用该算法。
		 * 
		 * SO_RESUSEADDR：调用Socket的close方法关闭连接后，不会立刻端口，这时其他进程绑定到该端口将失败， 设置SO_RESUSEADDR为true将使得其他进程可以重用这个端口 。必须在绑定到端口之前调用。一般是服务器端存在该问题。新进程不会收到之前的数据。
		 * 
		 * SO_TIMEOUT：接收数据的等待超时时间，单位毫秒，默认0表示永远等待。要在接收数据之前设置。
		 * 
		 * SO_LINGER：Socket连接是否立即关闭
		 * 
		 * SO_SNFBUF：输出数据缓冲区大小，大批量数据建议大点，频繁传送小数据建议小点
		 * 
		 * SO_RCVBUF：输入数据缓冲区大小
		 * 
		 * SO_KEEPALIVE：为true时底层实现会监视连接有效性，客户端使用
		 * 
		 * OOBINLINE：为true时表示支持发送一个字节的紧急数据，一般不用。
		 * 
		 * SO_REUSEADDR:是否允许重用绑定地址，多个DatagramSocket是否可以绑定到相同的IP地址和端口， 会复制给所有的DatagramSocket。
		 * 
		 * SO_BROADCAST:是否允许对网络广播地址收发数据。
		 */
		ServerBootstrap bootstrap = new ServerBootstrap();
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(nThreads);
		// EventLoopGroup workerGroup = new NioEventLoopGroup(3);
		try {
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
			// .option(ChannelOption.SO_BACKLOG, 10)
					.option(ChannelOption.TCP_NODELAY, true)// 设置每一个非主监听连接的端口可以重用
					// .option(ChannelOption.SO_SNDBUF, so_sndbuf)// 设置输出缓冲区的大小
					// .option(ChannelOption.SO_RCVBUF, so_rcvbuf)// 设置输入缓冲区的大小
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							initChannelInner(ch);
						}
					});
			// Bind and start to accept incoming connections.
			logger.info("======================= netty网络模块启动成功{} : {}，即将启动(绑定端口:{})  ===================", getServerName(), getServerId(), port);
			// bootstrap.bind(port).sync().channel().closeFuture().sync();
			ChannelFuture f = bootstrap.bind(port).sync();
			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();

		} catch (Exception e) {
			logger.error("=========服务器启动失败 ", e);
		} finally {
			// Shut down all event loops to terminate all threads.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	protected abstract void initChannelInner(SocketChannel ch) throws Exception;

}
