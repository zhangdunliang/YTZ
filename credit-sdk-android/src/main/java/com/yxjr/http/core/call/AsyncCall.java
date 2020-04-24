package com.yxjr.http.core.call;

import com.yxjr.http.HttpClient;
import com.yxjr.http.builder.Request;
import com.yxjr.http.core.Response;
import com.yxjr.http.core.connection.Connection;
import com.yxjr.http.core.connection.HttpConnection;
import com.yxjr.http.core.connection.HttpsConnection;

import java.io.IOException;

/**
 * 请求的封装执行
 */

public class AsyncCall implements Runnable {
	private IRequestCallBack mCallBack;
	private Request mRequest;
	private Connection mConnection;

	AsyncCall(HttpClient client, Request request, IRequestCallBack callBack, IUploadListener listener) {
		this.mCallBack = callBack;
		this.mRequest = request;
		mConnection = request.url().startsWith("https") ? new HttpsConnection(client, request, listener) : new HttpConnection(client, request, listener);
	}

	@Override
	public void run() {
		mConnection.connect(mCallBack);
	}

	public Response execute() throws IOException {
		return mConnection.connect();
	}

	public Request getRequest() {
		return mRequest;
	}

	Connection getConnection() {
		return mConnection;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof AsyncCall) {
			return mRequest.url().equalsIgnoreCase(((AsyncCall) o).getRequest().url());
		}
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}