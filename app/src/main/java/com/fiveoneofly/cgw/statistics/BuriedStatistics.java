package com.fiveoneofly.cgw.statistics;

import android.annotation.SuppressLint;
import android.content.Context;

import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.constants.HttpParam;
import com.fiveoneofly.cgw.utils.AndroidUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 埋点统计
 */
public class BuriedStatistics {
    private int BUFFER_SIZE = 1024;
    private Context mContext;
    private String operErrorInfo = "";//页面上的报错信息
    private String operDate = "";//用户操作的日期
    private String systemPageVersion = "";//系统当前页面脚本
    private String custMobile = "";//用户手机号
    private String sessionId = "";//用户当前访问的sessionId;
    private String custName = "";//用户姓名
    private String operPageName = "";//页面的名称
    private String operElementType = "";//页面上的元素类型
    private String operElementName = "";//页面上的元素名称
    private String systemCode = "";//系统标识
    private String custCert = "";//用户身份证号
    private String partnerId = "";//合作方标识
    private String phoneOsType = "";//手机系统类型
    private String operTime = "";//用户操作时间
    private String channelNo = "";//渠道

    @SuppressLint("SimpleDateFormat")
    public void init(Context context) {
        mContext = context;
        //取用户的基本信息
        custName = UserManage.get(context).userName();
        custMobile = UserManage.get(context).phoneNo();
        custCert = UserManage.get(context).userCert();
        partnerId = UserManage.get(context).partnerId();
        systemCode = "WALLET";
        channelNo = "XYJQB";
        //得到系统时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        operDate = df.format(new Date());
        SimpleDateFormat dfs = new SimpleDateFormat("HHmmss");
        operTime = dfs.format(new Date());
        phoneOsType = "android";
        systemPageVersion = AndroidUtil.getVersionName(mContext);
    }

    public String getOperErrorInfo() {
        return operErrorInfo;
    }

    public void setOperErrorInfo(String operErrorInfo) {
        this.operErrorInfo = operErrorInfo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getOperPageName() {
        return operPageName;
    }

    public void setOperPageName(String operPageName) {
        this.operPageName = operPageName;
    }

    public String getOperElementType() {
        return operElementType;
    }

    public void setOperElementType(String operElementType) {
        this.operElementType = operElementType;
    }

    public String getOperElementName() {
        return operElementName;
    }

    public void setOperElementName(String operElementName) {
        this.operElementName = operElementName;
    }

    public BuriedStatistics(Builder builder) {
        this.mContext = builder.mcontext;
        this.operErrorInfo = builder.operErrorInfo;
        this.sessionId = builder.sessionId;
        this.operElementType = builder.operElementType;
        this.operElementName = builder.operElementName;
        this.operPageName = builder.operPageName;
        init(mContext);
        new Thread(runnable).start();

    }

    /**
     * 上传时间数据
     */
    private void toSend() {
        String s = tojson();
        OutputStream httpOutputStream = null;
        ByteArrayOutputStream baos = null;
        BufferedInputStream httpInputStream = null;
        try {
            //创建连接
            URL url = new URL(HttpParam.PULL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(10000);//
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            httpOutputStream = connection.getOutputStream();
            byte[] outputByte = new byte[BUFFER_SIZE];
            int count = 0;
            httpOutputStream.write(s.getBytes());
            //			byte[] outputByte = new byte[YxCommonConstant.HttpParamValue.BUFFER_SIZE];
            //			int count = 0;
            //			while ((count = in.read(outputByte, 0, YxCommonConstant.HttpParamValue.BUFFER_SIZE)) != -1) {
            //				httpOutputStream.write(outputByte, 0, count);
            //				httpOutputStream.flush();
            //			}
            httpOutputStream.flush();
            httpOutputStream.close();
            httpInputStream = new BufferedInputStream(connection.getInputStream());
            baos = new ByteArrayOutputStream();
            while ((count = httpInputStream.read(outputByte, 0, BUFFER_SIZE)) != -1) {
                baos.write(outputByte, 0, count);
            }
            baos.flush();
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            toSend();
        }
    };

    public static class Builder {
        private String operErrorInfo = "";//页面上的报错信息
        private String sessionId = "";//用户当前访问的sessionId;
        private String operPageName = "";//页面的名称
        private String operElementType = "";//页面上的元素类型
        private String operElementName = "";//页面上的元素名称
        private Context mcontext;

        public Builder addContext(Context context) {
            this.mcontext = context;
            return this;
        }

        public Builder addErrorInfo(String error) {
            this.operErrorInfo = error.length() > 210 ? error.substring(0, 210) : error;
            return this;
        }

        public Builder addSessionId(String sessionid) {
            this.sessionId = sessionid;
            return this;
        }

        public Builder addOperPageName(String operpagename) {
            this.operPageName = operpagename;
            return this;
        }

        public Builder addOperElementType(String elementtype) {
            this.operElementType = elementtype;
            return this;
        }

        public Builder addOperElementName(String elementName) {
            this.operElementName = elementName;
            return this;
        }

        public BuriedStatistics build() {
            return new BuriedStatistics(this);
        }

    }

    /**
     * 把对象转化成json数据
     */
    private String tojson() {
        JSONObject message = new JSONObject();//信息载体
        try {
            message.put("operErrorInfo", this.operErrorInfo);
            message.put("operDate", this.operDate);
            message.put("systemPageVersion", this.systemPageVersion);
            message.put("custMobile", this.custMobile);
            message.put("sessionId", this.sessionId);
            message.put("custName", this.custName);
            message.put("operPageName", this.operPageName);
            message.put("operElementType", this.operElementType);
            message.put("operElementName", this.operElementName);
            message.put("systemCode", this.systemCode);
            message.put("custCert", this.custCert);
            message.put("partnerId", this.partnerId);
            message.put("phoneOsType", this.phoneOsType);
            message.put("operTime", this.operTime);
            message.put("channelNo", this.channelNo);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message.toString();

    }

}
