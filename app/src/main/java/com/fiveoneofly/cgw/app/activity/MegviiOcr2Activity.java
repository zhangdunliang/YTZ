package com.fiveoneofly.cgw.app.activity;

import android.content.Intent;
import android.os.Bundle;

import com.fiveoneofly.cgw.statistics.BuriedStatistics;
import com.fiveoneofly.cgw.third.megvii.BasicMegviiOcrActivity;
import com.fiveoneofly.cgw.third.megvii.util.Util;
import com.megvii.idcardquality.IDCardQualityResult;
import com.megvii.idcardquality.bean.IDCardAttr;

public class MegviiOcr2Activity extends BasicMegviiOcrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSide = getIntent().getIntExtra("side", 0) == 0 ? IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT : IDCardAttr.IDCardSide.IDCARD_SIDE_BACK;
        super.onCreate(savedInstanceState);
    }


    protected void handleSuccess(IDCardQualityResult result) {

        Intent intent = new Intent();
        intent.putExtra("side", mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? 0 : 1);
        intent.putExtra("idcardImg", Util.bmp2byteArr(result.croppedImageOfIDCard()));
        if (result.attr.side == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {// 正面有头像
            intent.putExtra("portraitImg", Util.bmp2byteArr(result.croppedImageOfPortrait()));
        }
        setResult(RESULT_OK, intent);
        String message=mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT?"身份证正面":"身份证反面";
        new BuriedStatistics.Builder().addContext(MegviiOcr2Activity.this)
                .addOperElementName(message+"拍照")
                .addOperElementType("button")
                .addOperPageName("身份证拍照页面")
                .addErrorInfo(message+"识别结束")
                .addSessionId("")
                .build();
        finish();
    }
}