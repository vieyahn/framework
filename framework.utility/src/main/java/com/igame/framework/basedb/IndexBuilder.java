package com.igame.framework.basedb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import com.igame.framework.basedb.annotation.Index;
/**   
 * @Title: IndexBuilder.java
 * @Package com.igame.framework.basedb
 * @Author Allen allen.ime@gmail.com  
 * @Date 2014年9月29日 下午6:02:30
 * @Description: 索引生成器
 * @Version V1.0   
 */
public class IndexBuilder {

	/**
	 * 创建索引访问器
	 * 
	 * @param clazz
	 * @return
	 */
	public static Map<String, IndexVisitor> createIndexVisitors(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Index.class)) {
				fields.add(field);
			}
		}
		Map<String, IndexVisitor> indexMap = new HashMap<String, IndexVisitor>();
		for (Field field : fields) {
			Index index = (Index) field.getAnnotation(Index.class);
			if (index != null) {
				IndexVisitor indexVisitor = (IndexVisitor) indexMap.get(index.name());
				if (indexVisitor == null) {
					indexVisitor = new IndexVisitor(index.name());
					indexMap.put(index.name(), indexVisitor);
				}
				indexVisitor.attachField(new Field[] { field });
			}
		}
		if (!indexMap.isEmpty()) {
			for (IndexVisitor indexVisitor : indexMap.values()) {
				List<Field> fieldList = indexVisitor.getFields();
				if ((fieldList != null) && (fieldList.size() > 1)) {
					Collections.sort(fieldList, new Comparator<Field>() {
						@Override
						public int compare(Field f1, Field f2) {
							Index index1 = (Index) f1.getAnnotation(Index.class);
							Index index2 = (Index) f2.getAnnotation(Index.class);
							return index1.order() < index2.order() ? -1 : 1;
						}
					});
				}
			}
		}
		return indexMap;
	}

	/**
	 * 索引访问器
	 * 
	 * @author zhujuan
	 */
	public static class IndexVisitor {
		private final String name;
		private final List<Field> fields = new ArrayList<Field>();

		public IndexVisitor(String indexName) {
			name = indexName;
		}

		/**
		 * 附加索引字段
		 * 
		 * @param fieldList
		 */
		public void attachField(Field... fieldList) {
			if ((fieldList != null) && (fieldList.length > 0)) {
				for (Field field : fieldList) {
					fields.add(field);
				}
			}
		}

		/**
		 * 获得索引访问器名称
		 * 
		 * @return
		 */
		public String getName() {
			return name;
		}

		/**
		 * 获得索引字段集合
		 * 
		 * @return
		 */
		public List<Field> getFields() {
			return fields;
		}

		/**
		 * 获得索引字段值域的组合键名(类名&索引名称#索引值1^索引值2)
		 * 
		 * @param obj
		 * @return
		 */
		public String getIndexKey(Object obj) {
			if (obj != null) {
				Object[] fieldValues = null;
				if ((fields != null) && (!fields.isEmpty())) {
					fieldValues = new String[fields.size()];
					for (int i = 0; i < fields.size(); i++) {
						Field field = (Field) fields.get(i);
						field.setAccessible(true);
						Object fieldValue = ReflectionUtils.getField(field, obj);
						if (fieldValue != null) {
							fieldValues[i] = String.valueOf(fieldValue);
						} else {
							fieldValues[i] = "";
						}
					}
				}
				return KeyBuilder.buildIndexKey(obj.getClass(), name, fieldValues);
			}
			return null;
		}

	}

}
