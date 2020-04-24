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

public class ItemBrowserHistory {


    @SuppressLint("SimpleDateFormat")
    public JSONArray getData(Context context, long recordTime) {
        String selection = null;
        if (recordTime > 0) {
            selection = " date >'" + recordTime + "'";//查询条件
        }
        Cursor browserHistoryCursor = null;
        JSONArray browserHistoryInfo = new JSONArray();
        try {
            String[] projection = new String[]{"title", "url", "date"};
            //	browserHistoryCursor = mContext.getContentResolver().query(Browser.BOOKMARKS, projection, selection, null, "date desc");
            //	browserHistoryCursor = mContext.getContentResolver().query(Browser.BOOKMARKS_URI, projection, selection, null, "date desc");
            browserHistoryCursor = context.getContentResolver().query(Uri.parse("content://browser/bookmarks"), projection, selection, null, "date desc");
            if (browserHistoryCursor != null && browserHistoryCursor.moveToFirst()) {
                do {
                    String webUrl = browserHistoryCursor.getString(browserHistoryCursor.getColumnIndex("url"));
                    String webTitle = browserHistoryCursor.getString(browserHistoryCursor.getColumnIndex("title"));
                    String date = browserHistoryCursor.getString(browserHistoryCursor.getColumnIndex("date"));
                    String browerTime = null;
                    if (date != null) {
                        browerTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(date)));
                    }
                    JSONObject browserHistory = new JSONObject();
                    browserHistory.put("webUrl", webUrl);
                    browserHistory.put("webTitle", webTitle.length() > 300 ? webTitle.substring(0, 300) : webTitle);//标题300长度
                    browserHistory.put("browerTime", browerTime);
                    browserHistoryInfo.put(browserHistory);
                } while (browserHistoryCursor.moveToNext());
                browserHistoryCursor.close();
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } catch (SecurityException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != browserHistoryCursor) {
                browserHistoryCursor.close();
            }
        }
        LogUtil.d("Calm---BrowserHistory", " query end ====> BrowserHistory");
        return browserHistoryInfo;
    }

}
