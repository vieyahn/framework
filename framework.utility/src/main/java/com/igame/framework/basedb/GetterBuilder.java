package com.igame.framework.basedb;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ReflectionUtils;

import com.igame.framework.basedb.annotation.CfgId;

/**   
 * @Title: GetterBuilder.java
 * @Package com.igame.framework.basedb
 * @Author Allen allen.ime@gmail.com  
 * @Date 2014年9月29日 下午6:02:21
 * @Description: 取值生成器
 * @Version V1.0   
 */
public class GetterBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetterBuilder.class);

	/**
	 * 创建唯一标识取值器
	 * 
	 * @param clz
	 * @return
	 */
	public static Getter createIdGetter(Class<?> clz) {

		Field idField = null;
		for (Field field : clz.getDeclaredFields()) {
			if (field.isAnnotationPresent(CfgId.class)) {
				if (idField != null) {
					FormattingTuple message = MessageFormatter.format("类 [{}] 有多个唯一标识声明", clz);
					LOGGER.error(message.getMessage());
					throw new RuntimeException(message.getMessage());
				} else {
					idField = field;
				}
			}
		}
		if (idField == null) {
			FormattingTuple message = MessageFormatter.format("类 [{}] 缺少唯一标识声明", clz);
			LOGGER.error(message.getMessage());
			throw new RuntimeException(message.getMessage());
		} else {
			return new FieldGetter(idField);
		}
	}

	/**
	 * @Title: GetterBuilder.java
	 * @Package com.igame.framework.basedb
	 * @Author Allen allen.ime@gmail.com
	 * @Date 2014年9月22日 下午5:23:04
	 * @Description: TODO 描述
	 * @Version V1.0
	 */
	private static class FieldGetter implements Getter {
		private final Field field;

		public FieldGetter(Field field) {
			ReflectionUtils.makeAccessible(field);
			this.field = field;
		}

		@Override
		public Object getValue(Object object) {
			Object value = null;
			try {
				value = field.get(object);
			} catch (Exception e) {
				FormattingTuple message = MessageFormatter.format("标识符属性访问异常", e);
				GetterBuilder.LOGGER.error(message.getMessage());
				throw new RuntimeException(message.getMessage());
			}
			return value;
		}
	}
}
