package com.yxjr.http.builder;

import java.io.File;
import java.util.IdentityHashMap;

/**
 * 请求参数
 */
public final class RequestParams {
	private IdentityHashMap<Key, String> textParams;
	private IdentityHashMap<Key, File> multiParams;

	public RequestParams() {
		textParams = new IdentityHashMap<>();
	}

	public RequestParams put(String name, String value) {
		textParams.put(new Key(name), value);
		return this;
	}

	public RequestParams put(String name, int value) {
		return put(name, String.valueOf(value));
	}

	public RequestParams put(String name, long value) {
		return put(name, String.valueOf(value));
	}

	public RequestParams put(String name, double value) {
		return put(name, String.valueOf(value));
	}

	public RequestParams put(String name, float value) {
		return put(name, String.valueOf(value));
	}

	public RequestParams put(String name, byte value) {
		return put(name, String.valueOf(value));
	}

	public RequestParams put(String name, boolean value) {
		return put(name, String.valueOf(value));
	}

	public RequestParams putFile(String name, File file) {
		if (multiParams == null)
			multiParams = new IdentityHashMap<>();
		if (!file.exists())
			return this;//throw new IllegalArgumentException("request param file not find exception");
		multiParams.put(new Key(name), file);
		return this;
	}

	public RequestParams putFile(String name, String fileName) {
		return putFile(name, new File(fileName));
	}

	public IdentityHashMap<Key, String> getTextParams() {
		return textParams;
	}

	public IdentityHashMap<Key, File> getMultiParams() {
		return multiParams;
	}

	/**
	 * 静态内部类包装Key,避免内存泄露
	 */
	public static class Key {
		private String name;

		public Key(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
