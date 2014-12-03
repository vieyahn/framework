package com.igame.framework.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BindCommand {
	/**
	 * 指令码
	 * 
	 * @return
	 */
	int value();

	/**
	 * 处理队列id -1表示根据userId选择线程 (未登录状态下 不能指定为-1)
	 * 
	 * @return
	 */
	int queue() default -1;

	/**
	 * 是否需要登录
	 * 
	 * @return
	 */
	boolean isToken() default true;

	/**
	 * 外部调用
	 * 
	 * @return
	 */
	boolean outCall() default true;

	/**
	 * 是否是GM命令
	 * 
	 * @return
	 */
	boolean isGm() default false;
}
