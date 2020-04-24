package com.fiveoneofly.cgw.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fiveoneofly.cgw.utils.DateUtil;
import com.fiveoneofly.cgw.utils.StringUtil;
import com.megvii.livenessdetection.bean.FaceIDDataStruct;
import com.fiveoneofly.cgw.third.megvii.BasicMegviiFaceActivity;
import com.fiveoneofly.cgw.third.megvii.FaceServer;
import com.fiveoneofly.cgw.third.megvii.util.Util;


import java.io.File;

/**
 * Created by xiaochangyou on 2018/5/15.
 */
public class MegviiFace1Activity extends BasicMegviiFaceActivity {

    public static void startMegviiFace(Context contect, String id, String appNo, String categoryCode) {
        Intent liveIntent = new Intent(contect, MegviiFace1Activity.class);
        liveIntent.putExtra("id", id);
        liveIntent.putExtra("appNo", StringUtil.isEmpty(appNo) ? "empty" : appNo);
        liveIntent.putExtra("categoryCode", StringUtil.isEmpty(categoryCode) ? "empty" : categoryCode);
        contect.startActivity(liveIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFaceServer = new FaceServer(this);
        mFaceServer.initializeParam(this);
        super.onCreate(savedInstanceState);
    }


    // 处理验证结果
    @Override
    protected void handleResult(int resID, int rawID) {

        final File[] files = new File[2];
        final boolean isSuccess = resID == com.yxjr.credit.R.string.face_verify_success;

        if (isSuccess) {
            final FaceIDDataStruct idDataStruct = mDetector.getFaceIDDataStruct();

            for (String key : idDataStruct.images.keySet()) {
                byte[] data = idDataStruct.images.get(key);
                switch (key) {
                    case "image_best": {
//                    byte[] imageBestData = data;// 这是最好的一张图片
                        String picName = "P3_" + DateUtil.getDate() + ".jpg";
                        String filePath = Util.saveJPGFile(this, data, picName);
                        files[0] = new File(filePath);
                        break;
                    }
                    case "image_env": {
//                    byte[] imageEnvData = data;// 这是一张全景图
                        String picName = "P4_" + DateUtil.getDate() + ".jpg";
                        String filePath = Util.saveJPGFile(this, data, picName);
                        files[1] = new File(filePath);
                        break;
                    }
                    default:// 其余为其他图片，根据需求自取
                        break;
                }
            }
        }

        release();

        mFaceUI.showResult(
                this,
                isSuccess,
                mIMediaPlayer,
                resID,
                rawID,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isSuccess) {
                            MegviiFace1Activity.this.finish();
                        } else {
                            if (mDetector.getFaceIDDataStruct().delta != null) {
                                mFaceServer.upload(files, mDetector.getFaceIDDataStruct().delta);
                            } else {
                                MegviiFace1Activity.this.finish();
                            }
                        }
                    }
                });
    }

}

