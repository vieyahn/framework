package com.igame.framework.rpc.filter;

import com.igame.framework.net.session.Session;
import com.igame.framework.rpc.CommandContext;

/**
 * @ClassName: IFilter
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2014年12月28日 下午4:52:59
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public interface IFilter {
	public void doFilter(Session session, CommandContext commandContext, Object params) throws Exception;
}
