package com.fiveoneofly.cgw.net.upload;

import android.os.Handler;
import android.os.Looper;

import com.fiveoneofly.cgw.utils.JsonUtil;
import com.fiveoneofly.cgw.utils.LogUtil;
import com.fiveoneofly.cgw.utils.ThreadUtil;
import com.yxjr.http.core.Response;
import com.yxjr.http.core.call.IRequestCallBack;
import com.yxjr.http.core.call.IUploadListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaochangyou on 2018/5/15.
 */
public class FileCallBack implements IUploadListener, IRequestCallBack {

    public void onSucces(String result) {
        onFinish();
    }

    public void onFailure(String errorCode, String errorMsg) {
        onFinish();
    }

    public void onProgress(long currentLength, long totalLength, int index) {
    }

    public void onFinish() {
    }
//
//    private Handler getHandler() {
//        synchronized (AsyncTask.class) {
//            if (mHandler == null) {
//                mHandler = new InternalHandler();
//            }
//            return mHandler;
//        }
//    }

    private Handler mHandler;

    public FileCallBack() {
        mHandler = new Handler(Looper.getMainLooper());
    }

//    private InternalHandler mHandler;

//    @SuppressLint("HandlerLeak")
//    private class InternalHandler extends Handler {
//        public InternalHandler() {
//            super(Looper.getMainLooper());
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            Bundle obj = (Bundle) msg.obj;
//            switch (msg.what) {
//                case ON_PROGRESS:
//                    onProgress(obj.getLong("currentLength"), obj.getLong("totalLength"), obj.getInt("index"));
//                    break;
//                case ON_SUCCES:
//                    onSucces(obj.getString("result"));
//                    break;
//                case ON_FAILURE:
//                    onFailure(obj.getString("status"), obj.getString("errorMsg"));
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

//    private final int ON_PROGRESS = 100;
//    private final int ON_SUCCES = 200;
//    private final int ON_FAILURE = 300;

    /*private void sendMessage(int what, Object object) {
        Message message = getHandler().obtainMessage(what, object);
        message.sendToTarget();
    }*/

    private void progress(final int index, final long currentLength, final long totalLength) {
        if (ThreadUtil.uiThread()) {
            this.onProgress(currentLength, currentLength, index);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    progress(index, currentLength, totalLength);
                }
            });
        }
    }

    private void succes(final String result) {
        if (ThreadUtil.uiThread()) {
            this.onSucces(result);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    succes(result);
                }
            });
        }
    }

    private void failure(final String errorCode, final String errorMsg) {
        if (ThreadUtil.uiThread()) {
            this.onFailure(errorCode, errorMsg);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFailure(errorCode, errorMsg);
                }
            });
        }
    }

    @Override
    public void onProgress(int index, long currentLength, long totalLength) {
//        YxLog.d("---======第 " + index + " 个文件======");
//        YxLog.d("---======当前长度 " + currentLength + " ======");
//        YxLog.d("---======文件总长度 " + totalLength + "  ======");
//        YxLog.d("---======上传进度 " + ((float) currentLength / totalLength) * 100 + "  ======");
//        mBundle.putInt("index", index);
//        mBundle.putLong("currentLength", currentLength);
//        mBundle.putLong("totalLength", totalLength);
//        sendMessage(ON_PROGRESS, mBundle);
        progress(index, currentLength, totalLength);
    }

    @Override
    public void onResponse(Response response) {
        String result = response.getBody();
//        YxLog.d("---======文件上传完毕======");
        if (result == null) {
//            YxLog.e("---======返回数据为空======");
            return;
        }
        LogUtil.d(result);

        JSONObject responseObj = null;
        try {
            responseObj = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (responseObj == null) {
            failure("null", "解析异常");
            return;
        }

        String errorMsg = JsonUtil.getString(responseObj, "errorMsg");
        String filePath = JsonUtil.getString(responseObj, "filePath");
        String status = JsonUtil.getString(responseObj, "status");

        if (status == null || !status.equals("S")) {
//            YxLog.e("---======状态为空或非S======");
//            mBundle.putString("status", "" + status == null ? "null" : status);
//            mBundle.putString("errorMsg", errorMsg);
//            sendMessage(ON_FAILURE, mBundle);
            failure(status == null ? "null" : status, errorMsg);
            return;
        }
        if (filePath == null) {
//            YxLog.e("---======路径为空======");
//            mBundle.putString("status", status);
//            mBundle.putString("errorMsg", errorMsg);
//            sendMessage(ON_FAILURE, mBundle);
            failure(status, errorMsg);
            return;
        }
//        YxLog.d("---======文件上传成功======");
//        mBundle.putString("result", filePath);
//        sendMessage(ON_SUCCES, mBundle);
        succes(filePath);
    }

    @Override
    public void onFailure(Exception e) {
//        YxLog.d("---======文件上传失败======" + e);
//        mBundle.putString("status", "9999");
//        mBundle.putString("errorMsg", "网络出错");
//        sendMessage(ON_FAILURE, mBundle);
        failure("9999", "网络出错");
    }
}
