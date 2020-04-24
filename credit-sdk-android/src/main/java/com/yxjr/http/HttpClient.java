package com.yxjr.http;

import com.yxjr.http.builder.Request;
import com.yxjr.http.core.Dispatcher;
import com.yxjr.http.core.call.ICall;
import com.yxjr.http.core.call.RealCall;
import com.yxjr.http.core.connection.SSLManager;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;

import javax.net.ssl.SSLSocketFactory;

/**
 * Http客户端
 */
public final class HttpClient {
	private Dispatcher mDispatcher;

	private Proxy mProxy;//为当前客户端开启全局代理

	private SSLManager mSslManager;

	public HttpClient() {
		init(true);
	}

	public HttpClient(boolean isSerial) {
		init(isSerial);
	}

	private void init(boolean isSerial) {
		mDispatcher = new Dispatcher(isSerial);
		mSslManager = new SSLManager();
	}

	public ICall newCall(Request request) {
		return new RealCall(this, request);
	}

	public ICall newCall(String url) {
		return new RealCall(this, getDefaultRequest(url));
	}

	public Dispatcher dispatcher() {
		return mDispatcher;
	}

	public Proxy getProxy() {
		return mProxy;
	}

	/**
	 * 开启全局代理
	 */
	public void setProxy(Proxy proxy) {
		this.mProxy = proxy;
	}

	/**
	 * 开启全局代理
	 */
	public void setProxy(String host, int port) {
		this.mProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
	}

	/**
	 * 导入证书
	 */
	public HttpClient setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
		mSslManager.setSslSocketFactory(sslSocketFactory);
		return this;
	}

	/**
	 * 导入证书
	 */
	public HttpClient setSslSocketFactory(InputStream... cerInputStream) {
		mSslManager.setSslSocketFactory(cerInputStream);
		return this;
	}

	public HttpClient setSslSocketFactory(String... cerPaths) {
		mSslManager.setSslSocketFactory(cerPaths);
		return this;
	}

	public HttpClient setSslSocketFactoryAsString(String... cerValues) {
		mSslManager.setSslSocketFactoryAsString(cerValues);
		return this;
	}

	//	public void cancelAll() {
	//		mDispatcher.shutdown();
	//	}

	public SSLSocketFactory getSslSocketFactory() {
		return mSslManager.getSslSocketFactory();
	}

	private Request getDefaultRequest(String url) {
		return new Request.Builder().url(url).build();
	}
}
