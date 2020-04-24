package com.yxjr.http.core.call;

import com.yxjr.http.core.Response;

import java.io.IOException;

/**
 * 执行Request
 */
public interface ICall {

	/**
	 * 添加拦截上传监听
	 * 
	 * @param listener
	 *            请求监听
	 * @return call
	 */
	ICall intercept(IUploadListener listener);

	/**
	 * 异步执行一个请求
	 * 
	 * @param callBack
	 *            回调
	 */
	void execute(IRequestCallBack callBack);

	/**
	 * 同步执行一个请求，必须在子线程执行
	 * 
	 * @return 请求结果
	 * @throws IOException
	 *             异常
	 */
	Response execute() throws IOException;

	/**
	 * 取消一个http请求
	 */
	void cancel();
}
