package com.igame.framework.basedb;

import java.util.Iterator;

/**
 * @Title: ResourceReader.java
 * @Package com.igame.framework.basedb
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年9月22日 下午4:30:42
 * @Description: 资源加载接口
 * @Version V1.0
 */
public interface ResourceReader {

	/**
	 * 读取资源
	 * 
	 * @param paramInputStream
	 * @param paramClass
	 * @return
	 */
	public <E> Iterator<E> read(String path, Class<E> paramClass);
}
