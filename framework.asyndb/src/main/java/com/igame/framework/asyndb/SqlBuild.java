package com.igame.framework.asyndb;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igame.framework.util.common.DateUtil;

/**
 * @ClassName: SqlBuild
 * @Package com.heygam.common.sql
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月8日 下午1:55:18
 * @Description: 运行期获取MyBatis执行的SQL(已加入参数)
 * @Version V1.0
 */
public class SqlBuild {

	private SqlSessionFactory sqlSessionFactory;// ibatis session工厂
	private static final String REGEX = "?";

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	private Logger logger = LoggerFactory.getLogger(SqlBuild.class);

	/**
	 * 从myBatis底层获取Sql语句
	 * 
	 * @param id
	 * @param parameterObject
	 * @return
	 */
	public String getSql(String id, Object parameterObject) {
		MappedStatement ms = sqlSessionFactory.getConfiguration().getMappedStatement(id);
		BoundSql boundSql = ms.getBoundSql(parameterObject);
		String sql = boundSql.getSql();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			Object[] parameterArray = new Object[parameterMappings.size()];
			MetaObject metaObject = parameterObject == null ? null : SystemMetaObject.forObject(parameterObject);
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value;
					String propertyName = parameterMapping.getProperty();
					PropertyTokenizer prop = new PropertyTokenizer(propertyName);
					if (parameterObject == null) {
						value = null;
					} else if (ms.getConfiguration().getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
						value = boundSql.getAdditionalParameter(prop.getName());
						if (value != null) {
							value = SystemMetaObject.forObject(value).getValue(propertyName.substring(prop.getName().length()));
						}
					} else {
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}
					parameterArray[i] = value;
				}
			}
			StringBuffer sqlbuff = new StringBuffer(sql);
			int index = 0;
			for (int i = 0; i < parameterArray.length; i++) {
				index = sqlbuff.indexOf(REGEX);
				if (index > -1) {

					if (parameterArray[i] instanceof Integer)
						sqlbuff.replace(index, index + 1, parameterArray[i].toString());
					else if (parameterArray[i] instanceof Float)
						sqlbuff.replace(index, index + 1, parameterArray[i].toString());
					else if (parameterArray[i] instanceof Double)
						sqlbuff.replace(index, index + 1, parameterArray[i].toString());
					else if (parameterArray[i] instanceof Long)
						sqlbuff.replace(index, index + 1, parameterArray[i].toString());
					else if (parameterArray[i] instanceof Date)
						sqlbuff.replace(index, index + 1, new StringBuffer().append("'").append(DateUtil.formatDate((Date) parameterArray[i], DateUtil.DAY_HOUR_FORMAT_STRING)).append("'").toString());
					else {
						if (parameterArray[i] == null) {
							sqlbuff.replace(index, index + 1, "null");
						} else {
							sqlbuff.replace(index, index + 1, new StringBuffer("'").append(parameterArray[i].toString()).append("'").toString());
						}
					}
				} else {
					break;
				}
			}
			// 带上时间戳
			sqlbuff.append("#").append(System.currentTimeMillis());
			sql = sqlbuff.toString();
			sql = sql.replaceAll("\t", "");
			sql = sql.replaceAll("\n", " ");

			// for (Object object : parameterArray) {
			// if (object instanceof Integer)
			// sql = sql.replaceFirst("[?]", new
			// StringBuffer().append(object).toString());
			// else if (object instanceof Float)
			// sql = sql.replaceFirst("[?]", new
			// StringBuffer().append(object).toString());
			// else if (object instanceof Double)
			// sql = sql.replaceFirst("[?]", new
			// StringBuffer().append(object).toString());
			// else if (object instanceof Long)
			// sql = sql.replaceFirst("[?]", new
			// StringBuffer().append(object).toString());
			// else if (object instanceof Date)
			// sql = sql.replaceFirst("[?]", new
			// StringBuffer().append("'").append(DateUtil.formatDate((Date)
			// object,
			// DateUtil.DAY_HOUR_FORMAT_STRING)).append("'").toString());
			// else {
			// if (object == null) {
			// sql = sql.replaceFirst("[?]", "null");
			// } else {
			// sql = sql.replaceFirst("[?]", "'" + new
			// StringBuffer().append(object).toString() + "'");
			// }
			// }
			// }

		}
		logger.debug("#### 获取sql {} ", sql);
		return sql;
	}
}
