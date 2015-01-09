/** 
 *
 * Copyright (c) 2015, allen.ime@gmail.com All Rights Reserved. 
 * 
 */

package com.igame.framework.asyndb2.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.igame.framework.asyndb2.test.entity.SecRole;

/**
 * @ClassName: SecRoleMapper
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2015年1月5日 上午11:35:56
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public interface SecRoleMapper {

	public List<SecRole> getSecRole(@Param("id") long id, @Param("code") String code);

	public void updateSecRole(long id, String name);
}
