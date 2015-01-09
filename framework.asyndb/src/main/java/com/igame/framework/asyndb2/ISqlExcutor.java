/** 
 *
 * Copyright (c) 2015, allen.ime@gmail.com All Rights Reserved. 
 * 
 */

package com.igame.framework.asyndb2;

import java.util.List;

/**
 * @ClassName: ISqlExcutor
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2015年1月5日 下午3:34:12
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public interface ISqlExcutor {

	public boolean executeSQLS(List<String> sqls);
	
	public void init();
}
