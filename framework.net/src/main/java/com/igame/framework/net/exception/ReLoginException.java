package com.igame.framework.net.exception;


/**
 * @Title: ReLoginException.java
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年5月26日 下午6:07:17
 * @Description:重新登录
 * @Version V1.0
 */
public class ReLoginException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -916091357562958353L;

	public ReLoginException() {
		super();
	}

	public ReLoginException(String message) {
		super(message);
	}

}
