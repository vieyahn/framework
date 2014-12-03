package com.igame.framework.net.exception.handler;

import io.netty.channel.Channel;

/**
 * @Title: IExceptionHadler.java
 * @Package com.igame.net.exception
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年5月30日 上午11:24:00
 * @Description: 统一异常处理
 * @Version V1.0
 */
public interface IExceptionHadler {
	
	public void excute(int command,Exception e, Channel channel);
}
