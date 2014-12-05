package com.igame.framework.basedb.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;

import com.igame.framework.basedb.annotation.CfgId;
import com.igame.framework.util.common.DateUtil;

/**  
 * @ClassName: Class2SqlUtil   
 * @Author: Allen allen.ime@gmail.com  
 * @Date: 2014年12月5日 上午11:59:13  
 * @Description: 类-->sql 
 */
public class Class2SqlUtil {

	public static String getInsertTableSql(Class<?> confClass, Collection<?> objs) throws Exception {
		String postfix = confClass.getSimpleName();
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ").append("c_").append(postfix).append(" (");

		Field[] fields = confClass.getDeclaredFields();
		for (Field param : fields) {
			if (param.getType().isArray()) {//
				continue;
			}
			if (Modifier.isFinal(param.getModifiers())) {
				continue;
			}
			// 仅支持基本数据类型和对应的包装类和Date类型
			if (char.class.isAssignableFrom(param.getType()) || String.class.isAssignableFrom(param.getType()) || boolean.class.isAssignableFrom(param.getType())
					|| Boolean.class.isAssignableFrom(param.getType()) || byte.class.isAssignableFrom(param.getType()) || Byte.class.isAssignableFrom(param.getType())
					|| short.class.isAssignableFrom(param.getType()) || Short.class.isAssignableFrom(param.getType()) || int.class.isAssignableFrom(param.getType())
					|| Integer.class.isAssignableFrom(param.getType()) || long.class.isAssignableFrom(param.getType()) || Long.class.isAssignableFrom(param.getType())
					|| float.class.isAssignableFrom(param.getType()) || Float.class.isAssignableFrom(param.getType()) || double.class.isAssignableFrom(param.getType())
					|| Double.class.isAssignableFrom(param.getType()) || Date.class.isAssignableFrom(param.getType())) {
				sql.append(param.getName()).append(",");
			}
		}
		sql.append(") ").deleteCharAt(sql.lastIndexOf(","));
		sql.append("values");

		try {
			for (Object t : objs) {
				sql.append("(");
				for (Field param : fields) {
					if (Modifier.isFinal(param.getModifiers())) {
						continue;
					}
					param.setAccessible(true);
					if (Date.class.isAssignableFrom(param.getType())) {
						Object date = param.get(t);
						String dataStr = null;
						if (date != null) {
							dataStr = DateUtil.formatDate((Date) date);
						}
						sql.append("'").append(dataStr).append("',");
					} else if (char.class.isAssignableFrom(param.getType()) || String.class.isAssignableFrom(param.getType())) {
						Object value = param.get(t);
						if (value == null) {
							sql.append("'',");
						} else {
							sql.append("'").append(value).append("',");
						}
					} else if (boolean.class.isAssignableFrom(param.getType()) || Boolean.class.isAssignableFrom(param.getType()) || byte.class.isAssignableFrom(param.getType())
							|| Byte.class.isAssignableFrom(param.getType()) || short.class.isAssignableFrom(param.getType()) || Short.class.isAssignableFrom(param.getType())
							|| int.class.isAssignableFrom(param.getType()) || Integer.class.isAssignableFrom(param.getType()) || long.class.isAssignableFrom(param.getType())
							|| Long.class.isAssignableFrom(param.getType())) {
						Object value = param.get(t);
						if (value == null) {
							sql.append("0").append(",");
						} else {
							sql.append(value);
						}
						sql.append(",");
					} else if (float.class.isAssignableFrom(param.getType()) || Float.class.isAssignableFrom(param.getType()) || double.class.isAssignableFrom(param.getType())
							|| Double.class.isAssignableFrom(param.getType())) {
						Object value = param.get(t);
						if (value == null) {
							sql.append(0.0D);
						} else {
							sql.append(value);
						}
						sql.append(",");
					}
				}
				sql.deleteCharAt(sql.lastIndexOf(",")).append("),\n");
			}
		} catch (Exception e) {
			throw e;
		}
		sql.deleteCharAt(sql.lastIndexOf(","));
		return sql.toString();
	}

	public static String getCreateTableSql(Class<?> confClass) {
		String postfix = confClass.getSimpleName();
		StringBuffer sql = new StringBuffer();
		sql.append("drop table if exists ").append("c_").append(postfix).append(";\n");
		sql.append("create table ").append("c_").append(postfix).append(" \n(\n");

		for (Field param : confClass.getDeclaredFields()) {
			if (Modifier.isFinal(param.getModifiers())) {
				continue;
			}
			if (char.class.isAssignableFrom(param.getType()) || String.class.isAssignableFrom(param.getType()) || boolean.class.isAssignableFrom(param.getType())
					|| Boolean.class.isAssignableFrom(param.getType()) || byte.class.isAssignableFrom(param.getType()) || Byte.class.isAssignableFrom(param.getType())
					|| short.class.isAssignableFrom(param.getType()) || Short.class.isAssignableFrom(param.getType()) || int.class.isAssignableFrom(param.getType())
					|| Integer.class.isAssignableFrom(param.getType()) || long.class.isAssignableFrom(param.getType()) || Long.class.isAssignableFrom(param.getType())
					|| float.class.isAssignableFrom(param.getType()) || Float.class.isAssignableFrom(param.getType()) || double.class.isAssignableFrom(param.getType())
					|| Double.class.isAssignableFrom(param.getType()) || Date.class.isAssignableFrom(param.getType())) {
				sql.append("   ").append(param.getName());
				boolean isPrimaryKey = param.isAnnotationPresent(CfgId.class);
				if (char.class.isAssignableFrom(param.getType())) {
					if (isPrimaryKey) {
						sql.append(" char(1) not null primary key,\n");
					} else {
						sql.append(" char(1) default NULL,\n");
					}
				} else if (String.class.isAssignableFrom(param.getType())) {
					if (isPrimaryKey) {
						sql.append(" varchar(200) not null primary key,\n");
					} else {
						sql.append(" varchar(200) default '',\n");
					}
				} else if (boolean.class.isAssignableFrom(param.getType()) || Boolean.class.isAssignableFrom(param.getType())) {
					if (isPrimaryKey) {
						sql.append(" tinyint(1) not null primary key,\n");
					} else {
						sql.append(" tinyint(1) default 0,\n");
					}
				} else if (byte.class.isAssignableFrom(param.getType()) || Byte.class.isAssignableFrom(param.getType())) {
					if (isPrimaryKey) {
						sql.append(" tinyint(4) not null primary key,\n");
					} else {
						sql.append(" tinyint(4) default 0,\n");
					}
				} else if (short.class.isAssignableFrom(param.getType()) || short.class.isAssignableFrom(param.getType()) || int.class.isAssignableFrom(param.getType())
						|| Integer.class.isAssignableFrom(param.getType())) {
					if (isPrimaryKey) {
						sql.append(" int(11) not null primary key,\n");
					} else {
						sql.append(" int(11) default 0,\n");
					}
				} else if (long.class.isAssignableFrom(param.getType()) || Long.class.isAssignableFrom(param.getType())) {
					if (isPrimaryKey) {
						sql.append(" bigint(20) not null primary key,\n");
					} else {
						sql.append(" bigint(20) default 0,\n");
					}
				} else if (float.class.isAssignableFrom(param.getType()) || Float.class.isAssignableFrom(param.getType()) || double.class.isAssignableFrom(param.getType())
						|| Double.class.isAssignableFrom(param.getType())) {
					if (isPrimaryKey) {
						sql.append(" double(10,2) not null primary key,\n");
					} else {
						sql.append(" double(10,2) default 0.0,\n");
					}
				} else if (Date.class.isAssignableFrom(param.getType())) {
					if (isPrimaryKey) {
						sql.append(" datetime not null primary key,\n");
					} else {
						sql.append(" datetime NULL DEFAULT NULL  ,\n");
					}
				}

			}
		}
		sql.append(")");
		sql.deleteCharAt(sql.lastIndexOf(","));
		return sql.toString();
	}

}
