package com.igame.framework.net.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: ApplicationResources.java
 * @Package com.heygam.game.component
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年5月16日 上午11:44:21
 * @Description: 国际化处理
 * @Version V1.0
 */
public class ApplicationResources {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationResources.class);
	private Locale locale;
	private String area;
	private ResourceBundle messages;
	protected static final String CHARSET = "UTF-8";
	/**
	 * @Fields 默认国际化key值
	 */
	private String defalutCode = "exception.unknown.exception";

	public void init() {
		String language;
		String country;
		String[] lan_c = area.split("_");
		language = lan_c[0];
		country = lan_c[1];
		locale = new Locale(language, country, CHARSET);
		messages = ResourceBundle.getBundle("message", locale);
	}

	public String getString(String code, Object... args) {
		String message = null;
		try {
			if (StringUtils.isBlank(code)) {// 默认下发消息
				code = defalutCode;
			}
			message = messages.getString(code);
		} catch (MissingResourceException e) {
			logger.error("没有找到国际化配置 {}", code);
			message = messages.getString("exception.unknown.exception");
		}
		if (args != null && args.length > 0) {
			message = MessageFormat.format(message, args);
		}
		return message;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public void setDefalutCode(String defalutCode) {
		this.defalutCode = defalutCode;
	}

}
