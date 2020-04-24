package com.yxjr.http.core.call;

import com.yxjr.http.core.Response;

/**
 * request callback
 */

public interface IRequestCallBack {
	/**
	 * 成功回调
	 * 
	 * @param response
	 *            结果返回
	 */
	void onResponse(Response response);

	/**
	 * 失败回调
	 * 
	 * @param e
	 *            异常信息
	 */
	void onFailure(Exception e);
}
