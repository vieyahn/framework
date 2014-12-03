package com.igame.framework.net.constant;

/**
 * @Title: LinkType.java
 * @Package com.igame.framework.net
 * @Author Allen allen.ime@gmail.com  
 * @Date 2014年9月12日 下午1:28:14
 * @Description: 链接类型
 * @Version V1.0
 */
public enum LinkType {
	SOCKET("SOCKET链接"), HTTP("HTTP链接"), WEBSOCKET("WEBSOCKET链接");

	LinkType(String desc) {
		this.desc = desc;
	}

	private String desc;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
