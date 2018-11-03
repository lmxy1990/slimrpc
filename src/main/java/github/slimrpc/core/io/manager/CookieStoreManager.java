package github.slimrpc.core.io.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CookieStoreManager {
	Logger log = LoggerFactory.getLogger(getClass());

	private String saveFilePath;
	
	final static String emptyContent = "[]";

	public CookieStoreManager(String connectionName, String savePath) {
		super();
		saveFilePath = savePath + File.separatorChar;
		File path = new File(saveFilePath);
		path.mkdirs();
		saveFilePath = saveFilePath + connectionName + "_cookie.json";
	}

	
	public synchronized String loadCookieFromStore() {
		FileReader in;
		try {
			in = new FileReader(saveFilePath);
		} catch (FileNotFoundException e) {
			return emptyContent;
		}

		char[] buff = new char[10240];
		int totalCount = 0;
		int count;
		try {
			count = in.read(buff, totalCount, buff.length - totalCount);
			while (count != -1 && totalCount < buff.length) {
				totalCount += count;
				count = in.read(buff, totalCount, buff.length - totalCount);
			}
		} catch (IOException e) {
			log.error("{saveFilePath:'" + saveFilePath + "'}", e);
			throw new RuntimeException(e);
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				log.error("{saveFilePath:'" + saveFilePath + "'}", e);
			}
		}
		if (totalCount >= buff.length) {
			return emptyContent;
		} else {
			return new String(buff, 0, totalCount);
		}
	}

	public synchronized void flushCookieToStore(String content) {
		FileWriter out = null;
		try {
			out = new FileWriter(saveFilePath);
			out.write(content);
		} catch (IOException e) {
			log.error("{saveFilePath:'" + saveFilePath + "'}", e);
			throw new RuntimeException(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				log.error("{saveFilePath:'" + saveFilePath + "'}", e);
			}
		}
	}
}
