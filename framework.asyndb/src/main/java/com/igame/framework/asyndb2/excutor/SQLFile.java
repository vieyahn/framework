package com.igame.framework.asyndb2.excutor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: SQLFile.java
 * @Package com.syn.util
 * @Author Allen allen.ime@gmail.com
 * @Date 2014年3月27日 上午9:46:04
 * @Description: 备份文件
 * @Version V1.0
 */
public class SQLFile {

	private static Logger logger = LoggerFactory.getLogger(SQLFile.class);
	private SimpleDateFormat sdfh = new SimpleDateFormat("yyyy-MM-dd-HH");
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private Writer writer = null;
	private String lastSqlFile;
	private String fileEncode;
	private String sqlPath;

	public SQLFile(String sqlPath) {
		this.sqlPath = sqlPath;
		lastSqlFile = "";
		fileEncode = System.getProperty("file.encoding");
	}

	private void open() {
		try {
			Date now = new Date();
			String fileName = sdfh.format(now);
			String folderName = sdf.format(now);
			File folder = new File(sqlPath + folderName);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			File writeFile = new File(sqlPath + folderName + "/" + fileName + ".sql");
			if (!writeFile.exists()) {
				writeFile.createNewFile();
				String path = writeFile.getPath();
				writeFileToIndexFile(path);
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeFile, true), fileEncode));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void openResumeFile() {
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lastSqlFile, true), fileEncode));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void write(String[] sqls) throws IOException {
		if (writer == null) {
			open();
		}

		for (int i = 0; i < sqls.length; i++) {
			writer.write(new String(sqls[i].getBytes("UTF-8"), fileEncode) + System.getProperty("line.separator"));
		}
		writer.flush();
	}

	public void fileCommit() {
		try {
			writer.write("# commited" + System.getProperty("line.separator"));
			writer.flush();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
					writer = null;
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void writeFileToIndexFile(String path) {
		FileWriter indexWriter = null;
		try {
			indexWriter = new FileWriter(sqlPath + "sql.index", true);
			indexWriter.write(path + System.getProperty("line.separator"));
			indexWriter.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (indexWriter != null)
					indexWriter.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public String getLastLineFromSqlIndex() {
		try {
			File indexFile = new File(sqlPath + "sql.index");
			if (!indexFile.exists()) {
				// 判断目标文件所在的目录是否存在
				if (!indexFile.getParentFile().exists()) {
					// 如果目标文件所在的目录不存在，则创建父目录
					System.out.println("目标文件所在目录不存在，准备创建它！");
					if (!indexFile.getParentFile().mkdirs()) {
						System.out.println("创建目标文件所在目录失败！");
					}
				}
				// 创建目标文件
				indexFile.createNewFile();
				return "";
			}
			lastSqlFile = readLastLine(indexFile).trim();
			logger.info("last line of sql.index is :" + lastSqlFile);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return lastSqlFile;
	}

	public String getLastLineFromFile() {
		try {
			String lastLineOfSql = readLastLine(new File(lastSqlFile));
			logger.info("last line of " + lastSqlFile + " is :" + lastLineOfSql);
			return lastLineOfSql;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return "";
	}

	public List<String> getLastUncommitedSqls() {
		try {
			List<String> sqls_inverte = getLastUncommitedSqlsInverted();
			Collections.reverse(sqls_inverte);
			return sqls_inverte;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Collections.emptyList();
	}

	private List<String> getLastUncommitedSqlsInverted() throws IOException {
		RandomAccessFile raf = null;
		List<String> sqls = new ArrayList<String>();
		try {
			raf = new RandomAccessFile(lastSqlFile, "r");
			long pos = raf.length() - 1; // 从文件尾开始读取
			long lastPos = raf.length();
			String temp = "";
			while (pos > 0) {
				pos--; // 游标每次循环都向文件头移动1个长度
				raf.seek(pos); // 设置读取起点位置
				if (raf.readByte() == '\n') { // 如果当前字符是换行 (windows \r\n linux
												// \n)
					byte[] bytes = new byte[(int) (lastPos - pos)];
					lastPos = pos;
					raf.read(bytes); // 读取起点位置到换行符之间的字符
					temp = new String(bytes, "UTF-8").trim();
					if (temp.indexOf("commited") == -1) {
						sqls.add(temp);
						continue;
					} else {
						break;
					}
				}
			}
			if (pos == 0) {
				raf.seek(0);
				byte[] bytes = new byte[(int) (lastPos - pos)];
				raf.read(bytes);
				temp = new String(bytes, "UTF-8").trim();
				if (temp.indexOf("commited") == -1)
					sqls.add(temp);
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (Exception e2) {
					logger.error(e2.getMessage(), e2);
				}
			}
		}
		return sqls;
	}

	private static String readLastLine(File file) throws IOException {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			long len = raf.length();
			if (len == 0L) {
				return "";
			} else {
				long pos = len - 1;
				while (pos > 0) {
					pos--;
					raf.seek(pos);
					if (raf.readByte() == '\n') {
						break;
					}
				}
				if (pos == 0) {
					raf.seek(0);
				}
				byte[] bytes = new byte[(int) (len - pos)];
				raf.read(bytes);
				return new String(bytes, "utf-8");

			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (Exception e2) {
					logger.error(e2.getMessage(), e2);
				}
			}
		}
		return null;
	}
}
