package com.yxjr.http.core.call;

import com.yxjr.http.HttpClient;
import com.yxjr.http.builder.Request;
import com.yxjr.http.core.Response;

import java.io.IOException;

/**
 * 请求的真正代理实现
 */
public class RealCall implements ICall {
	private HttpClient mClient;
	private Request mRequest;
	private AsyncCall mAsyncCall;
	private IUploadListener mListener;

	public RealCall(HttpClient client, Request request) {
		this.mClient = client;
		this.mRequest = request;
	}

	@Override
	public ICall intercept(IUploadListener listener) {
		this.mListener = listener;
		return this;
	}

	@Override
	public Response execute() throws IOException {
		if (mAsyncCall == null)
			mAsyncCall = new AsyncCall(mClient, mRequest, null, mListener);
		return mAsyncCall.execute();
	}

	@Override
	public void execute(IRequestCallBack callBack) {
		if (mAsyncCall == null)
			mAsyncCall = new AsyncCall(mClient, mRequest, callBack, mListener);
		mClient.dispatcher().execute(mAsyncCall);
	}

	@Override
	public void cancel() {
		mAsyncCall.getConnection().disconnect();
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
