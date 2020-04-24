package com.fiveoneofly.cgw.calm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;


import com.fiveoneofly.cgw.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemSMS {


    @SuppressLint("SimpleDateFormat")
    public JSONArray getData(Context context, long recordDate) {
        String selection = null;
        if (recordDate > 0) {
            selection = "date >'" + recordDate + "'";//查询条件
        }
        final String SMS_URI_ALL = "content://sms/";// 所有的短信
        //		Uri uri = Sms.Inbox.CONTENT_URI;
        //		final String SMS_URI_INBOX = "content://sms/inbox";// 收件箱短信
        // 		final String SMS_URI_SEND = "content://sms/sent";// 发件箱短信
        // 		final String SMS_URI_DRAFT = "content://sms/draft";// 草稿箱短信
        JSONArray smsarray = new JSONArray();
        Cursor cur = null;
        try {
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            cur = context.getContentResolver().query(Uri.parse(SMS_URI_ALL), projection, selection, null, "date desc");
            if (cur.moveToFirst()) {//查询的数据是否为空
                int nameColumn = cur.getColumnIndex("person");
                int phoneNumberColumn = cur.getColumnIndex("address");
                int smsbodyColumn = cur.getColumnIndex("body");
                int dateColumn = cur.getColumnIndex("date");
                int typeColumn = cur.getColumnIndex("type");// 短信类型1是接收到的，2是已发出
                do {
                    String name = cur.getString(nameColumn);// 发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
                    String phoneNumber = cur.getString(phoneNumberColumn);// 发件人地址，即手机号
                    String smsbody = cur.getString(smsbodyColumn);// body：短信具体内容
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(cur.getString(dateColumn))));// 日期，long型，如1256539465022，可以对日期显示格式进行设置
                    int type = cur.getInt(typeColumn);
                    if (type == 1 || type == 2) {
                        if (smsbody == null) {
                            smsbody = "";
                        }
                        JSONObject smsTemp = new JSONObject();
                        if (name != null) {
                            smsTemp.put("smsName", name);
                        } else {
                            smsTemp.put("smsName", "");
                        }
                        smsTemp.put("smsNumber", phoneNumber);
                        smsTemp.put("smsDate", date);
                        smsTemp.put("smsContent", smsbody);
                        smsTemp.put("smsType", type);
                        smsarray.put(smsTemp);
                    }
                } while (cur.moveToNext());
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } catch (SecurityException se) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cur) {
                cur.close();
            }
        }
        LogUtil.d("Calm---SMS", " query end ====> SMS");
        return smsarray;
    }

}
