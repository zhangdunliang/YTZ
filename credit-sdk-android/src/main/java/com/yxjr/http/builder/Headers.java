package com.yxjr.http.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the http/https header
 */
public class Headers {
	private Map<String, List<String>> mHeaders;

	private Headers(Builder builder) {
		this.mHeaders = builder.mHeaders;
	}

	public Map<String, List<String>> getHeaders() {
		return mHeaders;
	}

	public void setHeaders(Map<String, List<String>> mHeaders) {
		this.mHeaders = mHeaders;
	}

	public static final class Builder {
		private Map<String, List<String>> mHeaders;

		public Builder() {
			mHeaders = new HashMap<>();
		}

		public Builder addHeader(String name, String value) {
			checkNotNull(name, value);
			if (mHeaders.containsKey(name)) {
				if (mHeaders.get(name) == null)
					mHeaders.put(value, new ArrayList<String>());
				mHeaders.get(name).add(value);
			} else {
				List<String> h = new ArrayList<>();
				h.add(value);
				mHeaders.put(name, h);
			}
			return this;
		}

		public Builder setHeader(String name, String value) {
			if (mHeaders.containsKey(name)) {
				mHeaders.remove(name);
			}
			List<String> h = new ArrayList<>();
			h.add(value);
			mHeaders.put(name, h);
			return this;
		}

		private void checkNotNull(String name, String value) {
			if (name == null||name.length()==0)
				throw new NullPointerException("name can not be null");
			if (value == null||name.length()==0)
				throw new NullPointerException("value can not be null");
		}

		public Headers build() {
			return new Headers(this);
		}
	}
}
