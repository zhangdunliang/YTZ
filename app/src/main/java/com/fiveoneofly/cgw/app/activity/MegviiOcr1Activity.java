package com.fiveoneofly.cgw.app.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fiveoneofly.cgw.utils.StringUtil;
import com.megvii.idcardquality.IDCardQualityResult;
import com.megvii.idcardquality.bean.IDCardAttr;
import com.fiveoneofly.cgw.third.megvii.BasicMegviiOcrActivity;
import com.fiveoneofly.cgw.third.megvii.OcrServer;

public class MegviiOcr1Activity extends BasicMegviiOcrActivity {

    public static void startMegviiOcr(Context context, String scanType, String mode, String appNoOrCustId, String categoryCodeOrData) {

        Intent scanIntent = new Intent(context, MegviiOcr1Activity.class);
        /*
         * 扫描类型
         *
         * 0 正面扫描上传成功紧接反面扫描
         * 1 单独正面扫描
         * 2 单独反面扫描
         */
        if (scanType != null && (scanType.equals(OcrServer.ONLY_FRONT) || scanType.equals(OcrServer.ONLY_BACK))) {
            scanIntent.putExtra(OcrServer.paramScanType, Integer.parseInt(scanType));
        } else {
            scanIntent.putExtra(OcrServer.paramScanType, Integer.parseInt(OcrServer.DEFALUT_NORMAL));
        }

        switch (mode) {
            case OcrServer.MODE_CREDIT:
                scanIntent.putExtra(OcrServer.paramMode, mode);
                scanIntent.putExtra(OcrServer.paramAppNo, StringUtil.isEmpty(appNoOrCustId) ? "" : appNoOrCustId);
                scanIntent.putExtra(OcrServer.paramCategoryCode, StringUtil.isEmpty(categoryCodeOrData) ? "" : categoryCodeOrData);
                break;
            case OcrServer.MODE_ALONG:
                scanIntent.putExtra(OcrServer.paramCustId, StringUtil.isEmpty(appNoOrCustId) ? "" : appNoOrCustId);
                scanIntent.putExtra(OcrServer.paramQueryData, StringUtil.isEmpty(categoryCodeOrData) ? "" : categoryCodeOrData);
                break;
            default:
                return;
        }

        context.startActivity(scanIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mOcrServer = new OcrServer(this);
        mSide = mOcrServer.initializeParam(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void handleSuccess(IDCardQualityResult result) {
        int side = mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? 0 : 1;
        mOcrServer.handleResult(side, result);
    }
}