/** 
 *
 * Copyright (c) 2015, allen.ime@gmail.com All Rights Reserved. 
 * 
 */

package com.igame.framework.asyndb2.test.mapper;

import java.lang.management.ThreadMXBean;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.igame.framework.asyndb.message.ThreadQueueMessage;
import com.igame.framework.asyndb2.test.entity.SecRole;

/**
 * @ClassName: SecRoleService
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2015年1月5日 上午11:47:01
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
@Service
public class SecRoleService {
	@Autowired
	private SecRoleMapper mapper;
	
	int index = 0;

	public void getSecRole() {
		List<SecRole> list = mapper.getSecRole(22L,"管理员");
	}

	public void updateSecRole() {
		index++;
		mapper.updateSecRole(1,"管理者"+index);
	}
}
