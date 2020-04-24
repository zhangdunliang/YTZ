package com.yxjr.http.core.io;

import java.io.Closeable;

/**
 * IO流操作
 */

public final class IO {
	/**
	 * 关闭流
	 */
	public static void close(Closeable... closeables) {
		for (Closeable cb : closeables) {
			try {
				if (null == cb) {
					continue;
				}
				cb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
