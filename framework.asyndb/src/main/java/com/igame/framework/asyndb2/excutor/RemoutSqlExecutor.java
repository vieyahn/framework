/** 
 *
 * Copyright (c) 2015, allen.ime@gmail.com All Rights Reserved. 
 * 
 */

package com.igame.framework.asyndb2.excutor;

import java.io.IOException;
import java.util.List;

import com.igame.framework.asyndb2.ISqlExcutor;

/**
 * @ClassName: RemoutSqlExecutor
 * @Author: Allen allen.ime@gmail.com
 * @Date: 2015年1月6日 下午12:00:54
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public abstract class RemoutSqlExecutor implements ISqlExcutor {

	private boolean backup;
	private String path;
	private SQLFile sqlFile;

	@Override
	public boolean executeSQLS(List<String> sqls) {
		boolean flag = false;
		try {
			if (backup) {
				sqlFile.write(sqls.toArray(new String[0]));
			}
			flag = true;
			send(sqls);
			if (backup) {
				sqlFile.fileCommit();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	public abstract void send(List<String> sqls);

	@Override
	public void init() {
		if (backup) {
			if (path == null) {
				path = "sql/";
			}
			sqlFile = new SQLFile(path);
		}

	}

}
