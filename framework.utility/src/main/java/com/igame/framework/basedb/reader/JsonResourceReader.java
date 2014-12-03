package com.igame.framework.basedb.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSON;
import com.igame.framework.basedb.ResourceReader;

/**
 * @Title: JsonResourceReader.java
 * @Package com.igame.framework.basedb.reader
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年9月29日 下午6:03:21
 * @Description: json数据读取
 * @Version V1.0
 */
public class JsonResourceReader implements ResourceReader {
	private static final Logger logger = LoggerFactory.getLogger(JsonResourceReader.class);

	@Override
	public <E> Iterator<E> read(String path, Class<E> clazz) {
		StringBuilder str = new StringBuilder();
		BufferedReader in = null;
		try {
			URL resource = ClassUtils.getDefaultClassLoader().getResource(path);
			File file = resource != null ? ResourceUtils.getFile(resource) : null;
			if ((file == null) || (!file.exists())) {
				resource = ResourceUtils.getURL(path);
			}
			if (resource == null) {
				FormattingTuple message = MessageFormatter.format("基础数据[{}]所对应的资源文件[{}]不存在!", clazz.getName(), path);
				logger.error(message.getMessage());
				return null;
			}

			in = new BufferedReader(new FileReader(file));
			String s;
			try {
				while ((s = in.readLine()) != null)
					str.append(s);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			logger.error("==== JsonFileRead error path:{},class:{}", path, clazz);
			throw new RuntimeException(e);
		}
		String json = str.toString();

		List<E> list = null;
		if (json != null && json.length() > 0) {
			list = JSON.parseArray(json, clazz);
		}

		if (list != null && !list.isEmpty()) {
			return list.iterator();
		}
		return null;
	}

}
