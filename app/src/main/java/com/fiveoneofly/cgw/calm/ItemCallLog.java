package com.fiveoneofly.cgw.calm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.provider.CallLog;

import com.fiveoneofly.cgw.utils.LogUtil;
import com.fiveoneofly.cgw.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemCallLog {
    /**
     * 姓名长度限制：50 15
     */
    private final int mNameMaxLength = 15;
    /**
     * 手机长度限制：20 20
     */
    private final int mPhoneNumberMaxLength = 20;

    public JSONArray getData(Context context, long recordTime) {
        String selection = null;
        if (recordTime > 0) {
            selection = CallLog.Calls.DATE + " >'" + recordTime + "'";//查询条件
        }
        JSONArray callLogInfo = new JSONArray();
        Cursor callLogCursor = null;
        try {
            String[] projection = new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.CACHED_NAME, CallLog.Calls.DURATION};
            callLogCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, selection, null, "date desc");
            if (callLogCursor != null && callLogCursor.moveToFirst()) {
                do {
                    //联系号码
                    String callMobile = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));
                    //通话类型
                    String callType;
                    switch (Integer.parseInt(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.TYPE)))) {
                        case CallLog.Calls.INCOMING_TYPE:
                            callType = "1";//呼入
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            callType = "2";//呼出
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            callType = "3";//未接
                            break;
                        default:
                            callType = "4";//挂断
                            break;
                    }
                    //通话时间
                    @SuppressLint("SimpleDateFormat")
                    String callTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(callLogCursor.getString(callLogCursor.getColumnIndexOrThrow(CallLog.Calls.DATE)))));
                    //通话号码名字(通讯录有的话)
                    String callName = callLogCursor.getString(callLogCursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
                    //通话时长(单位:s)
                    String callDuration = callLogCursor.getString(callLogCursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                    JSONObject callLog = new JSONObject();
                    if (null != callName) {
                        if (callName.length() > mNameMaxLength) {// 姓名长度限制
                            callLog.put("callName", callName.substring(0, mNameMaxLength));
                        } else {
                            callLog.put("callName", callName);
                        }
                    }
                    if (null != callMobile) {
                        if (callMobile.length() > mPhoneNumberMaxLength) {// 手机号长度限制
                            callLog.put("callMobile", callMobile.substring(0, mPhoneNumberMaxLength));
                        } else {
                            callLog.put("callMobile", callMobile);
                        }
                    }
                    callLog.put("callDuration", verifyCallDuration(callDuration));
                    callLog.put("callTime", callTime);
                    callLog.put("callType", callType);
                    callLogInfo.put(callLog);
                } while (callLogCursor.moveToNext());
                callLogCursor.close();
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } catch (SecurityException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != callLogCursor) {
                callLogCursor.close();
            }
        }
        LogUtil.d("Calm---CallLog", " query end ====> CallLog");
        return callLogInfo;
    }

    /**
     * 通话时长特定格式转换【xx时xx分xx秒|xx分xx秒|xx:xx:xx|xx:xx】
     * 其他格式原样返回
     *
     * @param callDuration
     * @return
     */
    private String verifyCallDuration(String callDuration) {
        /*时*/
        int h = 0;
        /*分*/
        int m = 0;
        /*秒*/
        int f = 0;
        try {
            if (!StringUtil.isEmpty(callDuration)) {
                if (callDuration.contains("时") || callDuration.contains("分") || callDuration.contains("秒")) {//是否存在 时分秒// 00时00分00秒 或 00分00秒
                    if (callDuration.contains("时") && callDuration.contains("分") && callDuration.contains("秒")) {//00时00分00秒
                        String sh = null;//时
                        String sm = null;//分
                        String sf = null;//秒
                        if (callDuration.length() == 9) {
                            sh = callDuration.substring(0, callDuration.indexOf("时"));
                            sm = callDuration.substring(callDuration.indexOf("时") + 1, callDuration.indexOf("分"));
                            sf = callDuration.substring(callDuration.indexOf("分") + 1, callDuration.indexOf("秒"));
                        }
                        if (sh != null && sh.length() == 2 && sm != null && sm.length() == 2 && sf != null && sf.length() == 2) {
                            h = Integer.parseInt(sh);
                            m = Integer.parseInt(sm);
                            f = Integer.parseInt(sf);
                        }
                    } else if (callDuration.contains("分") && callDuration.contains("秒")) {//00分00秒
                        String sm = null;//分
                        String sf = null;//秒
                        if (callDuration.length() == 6) {
                            sm = callDuration.substring(0, callDuration.indexOf("分"));
                            sf = callDuration.substring(callDuration.indexOf("分") + 1, callDuration.indexOf("秒"));
                        }
                        if (sm != null && sm.length() == 2 && sf != null && sf.length() == 2) {
                            m = Integer.parseInt(sm);
                            f = Integer.parseInt(sf);
                        }
                    }
                } else if (callDuration.contains(":")) {//是否存在 : //00：00：00或00：00
                    int count, frist, two;
                    count = frist = two = 0;
                    for (int i = 0; i < callDuration.length(); i++) {
                        if (callDuration.charAt(i) == ':') {
                            count++;
                            if (frist == 0)
                                frist = i;
                            if (frist != 0)
                                two = i;
                        }
                    }
                    if (count == 2) {//00：00：00
                        String sh = null;//时
                        String sm = null;//分
                        String sf = null;//秒
                        if (callDuration.length() == 8) {
                            sh = callDuration.substring(0, frist);
                            sm = callDuration.substring(frist + 1, two);
                            sf = callDuration.substring(two + 1, callDuration.length());
                        }
                        if (sh != null && sh.length() == 2 && sm != null && sm.length() == 2 && sf != null && sf.length() == 2) {
                            h = Integer.parseInt(sh);
                            m = Integer.parseInt(sm);
                            f = Integer.parseInt(sf);
                        }
                    } else if (count == 1) {//00：00
                        String sm = null;//分
                        String sf = null;//秒
                        if (callDuration.length() == 5) {
                            sm = callDuration.substring(0, callDuration.indexOf(":"));
                            sf = callDuration.substring(callDuration.indexOf(":") + 1, callDuration.length());
                        }
                        if (sm != null && sm.length() == 2 && sf != null && sf.length() == 2) {
                            m = Integer.parseInt(sm);
                            f = Integer.parseInt(sf);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            h = m = f = 0;
        }
        if (h != 0 || m != 0 || f != 0) {
            callDuration = String.valueOf(h * 60 * 60 + m * 60 + f);
        }
        return callDuration;
    }

}
