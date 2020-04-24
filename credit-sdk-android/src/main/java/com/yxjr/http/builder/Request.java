package com.yxjr.http.builder;

import com.yxjr.http.core.io.AbsHttpContent;
import com.yxjr.http.core.io.FormContent;
import com.yxjr.http.core.io.MultiPartContent;

/**
 * to build a request 构建一个请求
 */
public final class Request {
	private String mUrl;
	private String mMethod;
	private RequestParams mParams;
	private Headers mHeaders;
	private String mEncode;
	private int mTimeout;
	private AbsHttpContent mHttpContent;
	private String mHost;
	private int mPort;

	private Request(Builder builder) {
		this.mUrl = builder.mUrl;
		this.mHeaders = builder.mHeaders.build();
		this.mMethod = builder.mMethod;
		this.mParams = builder.mParams;
		this.mHttpContent = builder.mHttpContent;
		this.mEncode = builder.mEncode;
		this.mTimeout = builder.mTimeout;
		this.mHost = builder.mHost;
		this.mPort = builder.mPort;
	}

	public String url() {
		return mUrl;
	}

	public String method() {
		return mMethod;
	}

	public RequestParams params() {
		return mParams;
	}

	public Headers headers() {
		return mHeaders;
	}

	public AbsHttpContent content() {
		return this.mHttpContent;
	}

	public String encode() {
		return mEncode;
	}

	public int timeout() {
		return mTimeout;
	}

	public String host() {
		return mHost;
	}

	public int port() {
		return mPort;
	}

	public static final class Builder {
		private String mUrl;
		private String mMethod;
		private String mEncode;
		private int mTimeout;
		private RequestParams mParams;
		private Headers.Builder mHeaders;
		private AbsHttpContent mHttpContent;
		private String mHost;
		private int mPort;

		public Builder() {
			this.mMethod = "GET";
			this.mEncode = "UTF-8";
			this.mTimeout = 13000;
			this.mHeaders = new Headers.Builder();
		}

		public Builder url(String url) {
			if (url == null)
				throw new NullPointerException("url can not be null");
			this.mUrl = url;
			return this;
		}

		public Builder encode(String encode) {
			if (encode == null)
				throw new NullPointerException("encode can not be null");
			this.mEncode = encode;
			return this;
		}

		public Builder timeout(int timeout) {
			this.mTimeout = timeout;
			if (mTimeout <= 0)
				mTimeout = 13000;
			return this;
		}

		public Builder method(String method) {
			if (method == null)
				throw new NullPointerException("method can not be null");
			this.mMethod = method;
			return this;
		}

		public Builder params(RequestParams params) {
			if (params == null)
				throw new NullPointerException("params can not be null");
			this.mParams = params;
			return this;
		}

		public Builder headers(Headers.Builder headers) {
			this.mHeaders = headers;
			return this;
		}

		public Builder content(AbsHttpContent content) {
			if (content == null)
				throw new NullPointerException("content can not be null");
			this.mHttpContent = content;
			return this;
		}

		public Builder proxy(String host, int port) {
			if (host == null)
				throw new NullPointerException("host can not be null");
			this.mHost = host;
			this.mPort = port;
			return this;
		}

		public Request build() {
			if (mHttpContent == null && mParams != null) {
				if (mParams.getMultiParams() != null) {
					mHttpContent = new MultiPartContent(mParams, mEncode);
				} else {
					mHttpContent = new FormContent(mParams, mEncode);
				}
			}
			return new Request(this);
		}
	}
}
