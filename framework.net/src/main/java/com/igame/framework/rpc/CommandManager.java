package com.igame.framework.rpc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.igame.framework.rpc.annotation.BindCommand;
import com.igame.framework.rpc.annotation.CommandWorker;
import com.igame.framework.util.common.PackageUtility;

/**  
 * @ClassName: CommandManager   
 * @Author: Allen allen.ime@gmail.com  
 * @Date: 2014年12月5日 下午12:01:58  
 * @Description: 指令码管理器  
 */
public class CommandManager implements ApplicationContextAware {
	private static final Logger log = LoggerFactory.getLogger(CommandManager.class);
	private String packageName;
	private ApplicationContext applicationContext;
	private boolean isSpring = true;
	/**
	 * rpc映射关系
	 */
	private static Map<Integer, CommandContext> commandToMethod;

	public void initLocalRpc() throws Exception {
		commandToMethod = new HashMap<Integer, CommandContext>();
		// 获取业务处理包下所有的类
		String[] pkgs = packageName.split(",");
		for (String pkg : pkgs) {
			Set<Class<?>> classes = PackageUtility.scanPackages(pkg);
			for (Class<?> clazz : classes) {
				CommandWorker worker = clazz.getAnnotation(CommandWorker.class);
				if (worker == null) {
					continue;
				}
				Object executor = null;
				if (isSpring) {
					System.out.println(clazz);
					executor = applicationContext.getBean(clazz);
				} else {
					executor = clazz.newInstance();
				}
				Method[] methods = clazz.getDeclaredMethods();
				if (methods != null) {
					for (Method method : methods) {
						// 获取BindCommand注释
						BindCommand bindCommand = method.getAnnotation(BindCommand.class);
						if (bindCommand != null) {
							if (commandToMethod.containsKey(bindCommand.value())) {
								CommandContext commandContext = commandToMethod.get(bindCommand.value());
								StringBuilder sd = new StringBuilder();
								sd.append("一个命令码不能绑定到多个方法,").append(bindCommand.value()).append(":");
								sd.append(clazz.getClass()).append("#").append(method.getName());
								sd.append(" ,  ").append(commandContext.getMethod().getDeclaringClass());
								sd.append("#").append(commandContext.getMethod().getName());
								log.error(sd.toString());
								throw new IllegalArgumentException(sd.toString());
							}

							if ((!bindCommand.isToken()) && (bindCommand.queue() == -1)) {
								throw new IllegalArgumentException("未登录状态可以访问的指令不能指定线程策略为-1");
							}
							Class<?>[] classes2 = method.getParameterTypes();
							if (classes2 == null || classes2.length!=2) {
								StringBuilder sd = new StringBuilder();
								sd.append("方法参数不匹配 ").append(bindCommand.value()).append(":");
								sd.append(clazz.getClass()).append("#").append(method.getName());
								throw new IllegalArgumentException(sd.toString());
							}
							Class<?> reqClass = (Class<?>) method.getParameterTypes()[1];
							CommandContext commandContext = new CommandContext();
							commandContext.setCommand(bindCommand.value());
							commandContext.setQueue(bindCommand.queue());
							commandContext.setToken(bindCommand.isToken());
							commandContext.setExecutor(executor);
							commandContext.setMethod(method);
							commandContext.setRequestClass(reqClass);
							commandToMethod.put(bindCommand.value(), commandContext);
						}
					}
				}
			}
		}
		log.debug("========== rpc处理方法个数：{} ======", commandToMethod.size());
	}

	/**
	 * 根据命令码获取处理器
	 * 
	 * @param id
	 * @return
	 */
	public static CommandContext getCommandContext(int id) {
		if (commandToMethod == null) {
			throw new IllegalArgumentException("rpc 容器未初始化");
		}
		return commandToMethod.get(id);
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
