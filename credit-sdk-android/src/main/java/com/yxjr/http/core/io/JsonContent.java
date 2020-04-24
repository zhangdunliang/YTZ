package com.yxjr.http.core.io;

import com.yxjr.http.core.call.IUploadListener;

import java.io.IOException;

/**
 * Json POST请求
 */
public class JsonContent extends AbsHttpContent {
	private String mJson;

	public JsonContent(String json) {
		super(null, "UTF-8");
		this.mJson = json;
	}

	public JsonContent(String json, String encode) {
		super(null, encode);
		this.mJson = json;
	}

	public String getJson() {
		return mJson;
	}

	public void setJson(String mJson) {
		this.mJson = mJson;
	}

	@Override
	public void doOutput() throws IOException {
		if (mJson != null) {
			mOutputStream.write(mJson.getBytes(mEncode));
		}
	}

	@Override
	public void doOutput(IUploadListener listener) throws IOException {
		doOutput();
	}

	@Override
	public String intoString() {
		return mJson;
	}

}
