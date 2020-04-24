package com.yxjr.http.core.call;

/**
 * 上传文件进度监听
 */

public interface IUploadListener {
	/**
	 * 上传进度回调
	 * 
	 * @param index
	 *            当前文件
	 * @param currentLength
	 *            当前进度
	 * @param totalLength
	 *            文件大小
	 */
	void onProgress(int index, long currentLength, long totalLength);
}
