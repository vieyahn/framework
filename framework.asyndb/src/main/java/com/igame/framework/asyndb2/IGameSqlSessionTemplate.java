/*
 *    Copyright 2010-2013 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.igame.framework.asyndb2;

import java.text.SimpleDateFormat;
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
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import com.igame.framework.asyndb.message.ThreadQueueMessage;

/**
 * Thread safe, Spring managed, {@code SqlSession} that works with Spring transaction management to ensure that that the actual SqlSession used is the
 * one associated with the current Spring transaction. In addition, it manages the session life-cycle, including closing, committing or rolling back
 * the session as necessary based on the Spring transaction configuration.
 * <p>
 * The template needs a SqlSessionFactory to create SqlSessions, passed as a constructor argument. It also can be constructed indicating the executor
 * type to be used, if not, the default executor type, defined in the session factory will be used.
 * <p>
 * This template converts MyBatis PersistenceExceptions into unchecked DataAccessExceptions, using, by default, a {@code MyBatisExceptionTranslator}.
 * <p>
 * Because SqlSessionTemplate is thread safe, a single instance can be shared by all DAOs; there should also be a small memory savings by doing this.
 * This pattern can be used in Spring configuration files as follows:
 *
 * <pre class="code">
 * {@code
 * <bean id="sqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
 *   <constructor-arg ref="sqlSessionFactory" />
 * </bean>
 * }
 * </pre>
 *
 * @author Putthibong Boonbong
 * @author Hunter Presnall
 * @author Eduardo Macarron
 * 
 * @see SqlSessionFactory
 * @see MyBatisExceptionTranslator
 * @version $Id$
 */
public class IGameSqlSessionTemplate extends SqlSessionTemplate {
	private static final Logger logger = LoggerFactory.getLogger(IGameSqlSessionTemplate.class);

	private static final String REGEX = "?";

	private boolean asyn = true;

	/**
	 * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory} provided as an argument.
	 *
	 * @param sqlSessionFactory
	 */
	public IGameSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
	}

	/**
	 * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory} provided as an argument and the given {@code ExecutorType}
	 * {@code ExecutorType} cannot be changed once the {@code SqlSessionTemplate} is constructed.
	 *
	 * @param sqlSessionFactory
	 * @param executorType
	 */
	public IGameSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
		this(sqlSessionFactory, executorType, new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), true));
	}

	/**
	 * Constructs a Spring managed {@code SqlSession} with the given {@code SqlSessionFactory} and {@code ExecutorType}. A custom
	 * {@code SQLExceptionTranslator} can be provided as an argument so any {@code PersistenceException} thrown by MyBatis can be custom translated to
	 * a {@code RuntimeException} The {@code SQLExceptionTranslator} can also be null and thus no exception translation will be done and MyBatis
	 * exceptions will be thrown
	 *
	 * @param sqlSessionFactory
	 * @param executorType
	 * @param exceptionTranslator
	 */
	public IGameSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType, PersistenceExceptionTranslator exceptionTranslator) {
		super(sqlSessionFactory, executorType, exceptionTranslator);
	}

	@Override
	public int insert(String statement) {
		if (asyn) {
			String sql = getSql(statement, null);
			ThreadQueueMessage.addSql(sql);
			return 1;
		} else {
			return super.insert(statement);
		}
	}
	
	@Override
	public <E> List<E> selectList(String statement, Object parameter) {
		// TODO Auto-generated method stub
		return super.selectList(statement, parameter);
	}

	@Override
	public int insert(String statement, Object parameter) {
		if (asyn) {
			String sql = getSql(statement, parameter);
			ThreadQueueMessage.addSql(sql);
			return 1;
		} else {
			return super.insert(statement, parameter);
		}
	}

	@Override
	public int update(String statement) {
		if (asyn) {
			String sql = getSql(statement, null);
			ThreadQueueMessage.addSql(sql);
			return 1;
		} else {
			return super.update(statement);
		}
	}

	@Override
	public int update(String statement, Object parameter) {
		if (asyn) {
			String sql = getSql(statement, parameter);
			ThreadQueueMessage.addSql(sql);
			return 1;
		} else {
			return super.update(statement, parameter);
		}
	}

	@Override
	public int delete(String statement) {
		if (asyn) {
			String sql = getSql(statement, null);
			ThreadQueueMessage.addSql(sql);
			return 1;
		} else {
			return super.delete(statement);
		}
	}

	@Override
	public int delete(String statement, Object parameter) {
		if (asyn) {
			String sql = getSql(statement, null);
			ThreadQueueMessage.addSql(sql);
			return 1;
		} else {
			return super.delete(statement, parameter);
		}
	}

	public String getSql(String statement, Object parameterObject) {
		MappedStatement ms = getConfiguration().getMappedStatement(statement);
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
					else if (parameterArray[i] instanceof Date) {
						SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						sqlbuff.replace(index, index + 1, new StringBuffer().append("'").append(dateformat1.format(parameterArray[i])).append("'").toString());
					} else {
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

		}
		logger.debug("#### 获取sql {} ", sql);
		return sql;
	}

}
