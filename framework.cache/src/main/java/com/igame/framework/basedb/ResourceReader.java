package com.igame.framework.basedb;

import java.util.Iterator;

/**  
 * @ClassName: ResourceReader   
 * @Author: Allen allen.ime@gmail.com  
 * @Date: 2014年12月5日 上午11:58:08  
 * @Description: 资源加载接口   
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
