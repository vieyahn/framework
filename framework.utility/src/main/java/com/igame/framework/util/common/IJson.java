package com.igame.framework.util.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;

/**
 * @Title: IJson.java
 * @Package com.heygam.zombie.util
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年9月12日 下午4:31:09
 * @Description: json序列化 工具（实现把long类型 序列化成String类型）
 * @Version V1.0
 */
public class IJson {
	private static final ValueFilter LONG_FILTER = new ValueFilter() {
		public Object process(Object obj, String s, Object value) {
			if (null != value) {
				if (value instanceof java.lang.Long) {
					return String.valueOf(value);
				}
				return value;
			} else {
				return value;
			}
		}
	};

	public static String toJSONString(Object object) {
		return toJSONString(object, new SerializerFeature[0]);
	}

	public static String toJSONString(Object object, SerializerFeature... features) {
		SerializeWriter out = new SerializeWriter();
		String s;
		JSONSerializer serializer = new JSONSerializer(out);
		SerializerFeature arr$[] = features;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++) {
			SerializerFeature feature = arr$[i$];
			serializer.config(feature, true);
		}
		serializer.getValueFilters().add(LONG_FILTER);
		serializer.write(object);
		s = out.toString();
		out.close();
		return s;
	}

	public static <T> T parseObject(String json, Class<? extends T> objClass) {
		return JSON.parseObject(json, objClass);
	}
}
