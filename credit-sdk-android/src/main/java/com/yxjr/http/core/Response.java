package com.yxjr.http.core;

import com.yxjr.http.core.io.IO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * the request response
 */
public class Response {
	private String mServiceId;
	private Map<String, List<String>> mHeaders;
	private int mCode;
	private String mBody;
	private InputStream mInputStream;
	private String mEncode;
	private int mContentLength;

	public Response(int code, InputStream is, Map<String, List<String>> headers, String encode, int contentLength) {
		this.mCode = code;
		this.mInputStream = is;
		this.mHeaders = headers;
		this.mEncode = encode;
		this.mContentLength = contentLength;
	}

	public Response(String serviceId, int code, InputStream is, Map<String, List<String>> headers, String encode, int contentLength) {
		this.mServiceId = serviceId;
		this.mCode = code;
		this.mInputStream = is;
		this.mHeaders = headers;
		this.mEncode = encode;
		this.mContentLength = contentLength;
	}

	public String getServiceId() {
		return mServiceId;
	}

	/**
	 * 返回状态码
	 * 
	 * @return http code
	 */
	public int getCode() {
		return mCode;
	}

	/**
	 * 获取返回body
	 * 
	 * @return 转为字符串
	 */
	public String getBody() {
		if (mBody == null) {
			try {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				int b;
				while ((b = mInputStream.read()) != -1) {
					os.write(b);
				}
				mBody = new String(os.toByteArray(), mEncode);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				close();
			}
		}
		return mBody;
	}

	/**
	 * 返回数据流
	 * 
	 * @return 获取流用于文件处理
	 */
	public InputStream toStream() {
		return mInputStream;
	}

	/**
	 * 获取响应头
	 * 
	 * @return 响应头
	 */
	public Map<String, List<String>> getHeaders() {
		return mHeaders;
	}

	/**
	 * 获取内容长度
	 * 
	 * @return 内容长度
	 */
	public int getContentLength() {
		return mContentLength;
	}

	/**
	 * 关闭流
	 */
	public void close() {
		IO.close(mInputStream);
	}
}
