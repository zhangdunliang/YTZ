package com.yxjr.http.core.io;

import com.yxjr.http.builder.RequestParams;
import com.yxjr.http.core.call.IUploadListener;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Form表单的形式
 */
public class FormContent extends AbsHttpContent {
	public FormContent(RequestParams params, String encode) {
		super(params, encode);
	}

	@Override
	public void doOutput() throws IOException {
		if (mParams != null && mParams.getTextParams() != null && mParams.getTextParams().size() > 0) {
			StringBuffer buffer = new StringBuffer();
			intoString(buffer);
			mOutputStream.write(buffer.substring(0, buffer.length() - 1).getBytes(mEncode));
		}
	}

	@Override
	public void doOutput(IUploadListener listener) throws IOException {
		doOutput();
	}

	private void intoString(StringBuffer buffer) {
		Set<RequestParams.Key> set = mParams.getTextParams().keySet();
		IdentityHashMap<RequestParams.Key, String> texts = mParams.getTextParams();
		for (RequestParams.Key keys : set) {
			String key = urlEncode(keys.getName());
			String value = urlEncode(texts.get(keys));
			buffer.append(key).append("=").append(value).append("&");
		}
	}

	@Override
	public String intoString() {
		if (mParams.getTextParams() == null || mParams.getTextParams().size() == 0)
			return "";
		StringBuffer buffer = new StringBuffer();
		intoString(buffer);
		return buffer.substring(0, buffer.length() - 1);
	}
}
