package com.fiveoneofly.cgw.net.api;

import android.content.Context;

import com.fiveoneofly.cgw.app.MainApplication;
import com.fiveoneofly.cgw.app.activity.LoginActivity;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.calm.CalmController;
import com.fiveoneofly.cgw.net.entity.req.Content;
import com.fiveoneofly.cgw.net.entity.req.Header;
import com.fiveoneofly.cgw.net.entity.req.ReqHeader;
import com.fiveoneofly.cgw.net.entity.req.Request;
import com.fiveoneofly.cgw.net.entity.req.reqHeader.App;
import com.fiveoneofly.cgw.net.entity.req.reqHeader.Device;
import com.fiveoneofly.cgw.net.entity.req.reqHeader.Net;
import com.fiveoneofly.cgw.utils.AndroidUtil;
import com.fiveoneofly.cgw.utils.NetUtil;
import com.card.calendar.security.AESSecurity;
import com.card.calendar.security.CCSecurity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.yxjr.credit.constants.SharedKey;

import org.json.JSONException;

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by xiaochangyou on 2017/6/14.
 */
public class ApiHandler {

    protected final int TYPE_TOKEN = 0;
    protected final int TYPE_JS = 1;
    protected final int TYPE_NATIVE = 2;

    protected String tempToken;// 临时token：保证请求与返回时都是同一token来进行加解密，避免中途token被清空

    protected final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 以前老接口必要的几个参数，统一加上
     */
    protected Map putOldParam(final Context context, Map map) {
        Map<Object, Object> m = new HashMap<Object, Object>() {{
            put("partnerId", UserManage.get(context).partnerId());
            put("partnerLoginId", UserManage.get(context).userCertMD5());
            put("productId", SharedUtil.getString(context, SharedKey.PRODUCT_ID));
            put("productIdChild", SharedUtil.getString(context, SharedKey.PRODUCT_ID_CHILD));
        }};
        m.putAll(map);
        return m;
    }

    /**
     * @param context 上下文
     * @return 请求报文头 reqHeader 信息
     */
    protected ReqHeader handleHeader(Context context) {

        ReqHeader reqHeader = new ReqHeader();

        App app = new App();
        app.setBundleId(AndroidUtil.getPackageName(context));
        app.setName(AndroidUtil.getAppName(context));
        app.setVersion(AndroidUtil.getVersionName(context));
        reqHeader.setApp(app);

        Device device = new Device();
        device.setUuid(AndroidUtil.getUUID(context));
        device.setImei(AndroidUtil.getDeviceId(context));
        device.setImsi(AndroidUtil.getIMSI(context));
        device.setBrand(AndroidUtil.getBrand());
        device.setModel(AndroidUtil.getModel());
        device.setOsType("ANDROID");
        device.setOsVersion(AndroidUtil.getOsVersion());
        device.setOsJailbreak(AndroidUtil.isRootSystem() ? "Y" : "N");
        device.setFactoryId(AndroidUtil.getFactoryId(context));
        device.setAndroidId(AndroidUtil.getAndroidId(context));
        device.setSimNo(AndroidUtil.getSimSerialNumber(context));
        device.setSimMobileNo(AndroidUtil.getNativePhoneNumber(context));
        reqHeader.setDevice(device);

        Net net = new Net();
        net.setIpAddress(NetUtil.getIP());
        net.setMacAddress(NetUtil.getMac());
        net.setNetType(NetUtil.isWifi(context) ? "WIFI" : "MONET");
        net.setGps(SharedUtil.getString(context, SharedKey.LONGITUDE) + "|" + SharedUtil.getString(context, SharedKey.LATITUDE) + "|");//经度|纬度|海拔
        reqHeader.setNet(net);

        reqHeader.setSessionId(UserManage.get(context).custId());

        return reqHeader;
    }

    /**
     * 处理请求报文
     *
     * @param reqService 接口号
     * @param reqBody    请求参数
     * @return 处理后的完整报文「加密后」
     * @throws NetException 异常
     */
    protected <B> Request handleRequest(int type, String reqService, ReqHeader reqHeader, B reqBody) throws NetException {
//        ***请求报文完整格式***
//        {
//            "header":{
//                  "appId":"xxx",
//                  "appInit":"Y"
//            },
//            "content":{
//                  "reqService":"reqService",
//                  "reqHeader":{
//                        "device":{
//                            "uuid":"xxx",
//                            "model":"xxx",
//                            "osType":"xxx",
//                            "osVersion":"xxx",
//                            "osJailbreak":"xxx"
//                        },
//                        "net":{
//                            "netType":"xxx",
//                            "ipAddress":"xxx",
//                            "macAddress":"xxx"
//                            "gps":"xxx"
//                        },
//                        "app":{
//                            "name":"xxx",
//                            "bundleId":"xxx",
//                            "version":"xxx"
//                        }
//                  },
//                  "reqBody":{
//                      "id":"id"
//                  }
//            }
//
//        }

        Header header = new Header();
        header.setAppId(reqHeader.getDevice().getUuid());// 保证唯一即可，此处使用reqHeader的uuid
        header.setAppInit(type == TYPE_TOKEN ? "Y" : "");// 仅生成token接口时为Y，其余时候必须为空值

        String contentStr;// 最终content值

        if (type == TYPE_TOKEN) {
            contentStr = CCSecurity.encryptString((String) reqBody);// 生成token接口时，content里仅有一个随机数值
        } else {
            Content content = new Content();
            content.setReqService(reqService);
            content.setReqHeader(reqHeader);
            content.setReqBody(reqBody);

            contentStr = aesEncypt(obj2String(content), tempToken);// 仅加密content值
        }

        // 最终完整请求报文
        Request request = new Request();
        request.setHeader(header);
        request.setContent(contentStr);

        return request;
    }

    /**
     * @param responseMessage 返回报文
     * @return 处理后的解密报文「明文」
     * @throws NetException 异常
     */
    protected String handleResponse(String serviceCode, int type, String responseMessage) throws NetException {
//        ***完整返回报文格式***
//        {
//            "header":{
//                  "respStatus":"xxx",
//                  "respStatusMsg":"xxx"
//            },
//            "content[token]":"AES后的appToken字符串",
//            "content[normal]":{
//                  "respCode":"xxx",
//                  "respMsg":"xxx",
//                  "respHeader[maybe]":{
//                      "xxx":"xxx"
//                  },
//                  "respBody":{
//                      "xxx":"xxx"
//                  }
//            }
//        }
        JsonNode jsonNode = readJson(responseMessage);

        // header获取
        // 注意：header响应码为3位数
        JsonNode header = jsonNode.get("header");
        String respStatus = header.get("respStatus").asText();
        String respStatusMsg = header.get("respStatusMsg").asText();

        // header响应码校验
        if (respStatus.equals(ApiCode.R000.getCode())) {

            // content获取并解密
            JsonNode contentCipher = jsonNode.get("content");
            String contentStr = aesDecrypt(contentCipher.toString(), tempToken);

            /*
             * 生成 token 或 JS 调用的接口
             * 不对 content 内容进行处理
             * 解密后原样返回
             */
            if (type == TYPE_TOKEN || type == TYPE_JS) {// retrun respBody
                if (type == TYPE_TOKEN) {
                    MainApplication.token = contentStr;
                }
                return contentStr;
            } else {// retrun content
                JsonNode content = readJson(contentStr);
                String respCode = content.get("respCode").asText();
                String respMsg = content.get("respMsg").asText();
                if (respCode.equals(ApiCode.S0000.getCode())) {
                    return content.get("respBody").toString();
                } else {
                    throw new NetException(respCode, respMsg);
                }
            }
        } else {
            if (respStatus.equals(ApiCode.R001.getCode())) {
                MainApplication.token = null;
            }

            throw new NetException(respStatus, respStatusMsg);
        }
    }


    /**
     * 异常转换
     */
    protected ApiCode handleException(Throwable e) {
        ApiCode apiCode;
        if (e instanceof EOFException) {
            apiCode = ApiCode.E4001;
        } else if (e instanceof ConnectException) {
            apiCode = ApiCode.E4002;
        } else if (e instanceof BindException) {
            apiCode = ApiCode.E4004;
        } else if (e instanceof SocketException) {
            apiCode = ApiCode.E4003;
        } else if (e instanceof SocketTimeoutException) {
            apiCode = ApiCode.E4005;
        } else if (e instanceof UnknownHostException) {
            apiCode = ApiCode.E4006;
        } else if (e instanceof JSONException) {
            apiCode = ApiCode.E5001;
        } else if (e instanceof JsonProcessingException) {
            apiCode = ApiCode.E5002;
        } else if (e instanceof NoSuchAlgorithmException) {
            apiCode = ApiCode.E3001;
        } else if (e instanceof BadPaddingException) {
            apiCode = ApiCode.E3002;
        } else if (e instanceof InvalidKeyException) {
            apiCode = ApiCode.E3003;
        } else if (e instanceof InvalidAlgorithmParameterException) {
            apiCode = ApiCode.E3004;
        } else if (e instanceof NoSuchPaddingException) {
            apiCode = ApiCode.E3005;
        } else if (e instanceof IllegalBlockSizeException) {
            apiCode = ApiCode.E3006;
        } else if (e instanceof UnsupportedEncodingException) {
            apiCode = ApiCode.E3007;
        } else {
            apiCode = ApiCode.E1000;
        }
        return apiCode;
    }

    /**
     * 解析json对象
     */
    protected String obj2String(Object obj) throws NetException {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new NetException(handleException(e));
        }
    }

    /**
     * 解析json字符串
     */
    private JsonNode readJson(String json) throws NetException {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            throw new NetException(ApiCode.E6002);
        }
    }

    /**
     * AES加密
     */
    private String aesEncypt(String data, String aesKey) throws NetException {
        try {
            return AESSecurity.encrypt(data, aesKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException(ApiCode.E3008);
        }
    }

    /**
     * AES解密
     */
    private String aesDecrypt(String data, String aesKey) throws NetException {
        try {
            return AESSecurity.decrypt(data, aesKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException(ApiCode.E3009);
        }
    }

}
