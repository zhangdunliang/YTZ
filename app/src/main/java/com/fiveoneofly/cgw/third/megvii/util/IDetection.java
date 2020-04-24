package com.fiveoneofly.cgw.third.megvii.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.megvii.livenessdetection.Detector.DetectionType;
import com.yxjr.credit.R;
import com.fiveoneofly.cgw.statistics.BuriedStatistics;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 实体验证工具类
 */
public class IDetection {

    public int timeOut = 10;
    private View rootView;
    private Context mContext;

    private int mStepsnum = 3;// 动作数量
    public int mCurShowIndex = -1;// 现在底部展示试图的索引值
    public ArrayList<DetectionType> mDetectionSteps;// 活体检测动作列表

    private TextView detectionNameText;
    private String detectionNameStr;

    public IDetection(Context context, View view) {
        this.mContext = context;
        this.rootView = view;
    }

    public void changeType(final DetectionType detectiontype) {
        mCurShowIndex = mCurShowIndex == -1 ? 0 : (mCurShowIndex == 0 ? 1 : 0);
        initAnimation(detectiontype, null);
    }

    private void initAnimation(DetectionType detectiontype, View layoutView) {
        rootView.findViewById(R.id.liveness_layout_promptText).setVisibility(View.GONE);
        detectionNameText = (TextView) rootView.findViewById(R.id.detection_step_name);
        detectionNameText.setVisibility(View.VISIBLE);
        Animation animationIN = AnimationUtils.loadAnimation(mContext, R.anim.yx_credit_face_liveness_rightin);
        detectionNameText.setAnimation(animationIN);
        detectionNameStr = getDetectionName(detectiontype);
        showTxt(detectionNameStr);

    }

    public void checkFaceTooLarge(boolean isLarge) {
        if (detectionNameStr != null && detectionNameText != null) {
            if (isLarge && !detectionNameText.getText().toString().equals("请再离远一些")) {
                showTxt("请再离远一些");
            } else if (!isLarge && detectionNameText.getText().toString().equals("请再离远一些")) {
                showTxt(detectionNameStr);
            }
        }
    }

    private void showTxt(String str) {
        new BuriedStatistics.Builder().addContext(mContext).addErrorInfo(str).addOperPageName("Face++人脸识别")
                .addOperElementType("ocr").addOperElementName("活体扫描").addSessionId("无").build();
        detectionNameText.setText(str);
    }

    // 返回检测的动作名字
    private String getDetectionName(DetectionType detectionType) {
        String detectionName = null;
        switch (detectionType) {
            case POS_PITCH:
                detectionName = "缓慢点头";
                break;
            case POS_PITCH_UP:
                detectionName = "向上抬头";
                break;
            case POS_PITCH_DOWN:
                detectionName = " 向下点头";
                break;
            case POS_YAW:
                detectionName = "左右摇头";
                break;
            case POS_YAW_LEFT:
                detectionName = "向左摇头";
                break;
            case POS_YAW_RIGHT:
                detectionName = "向右摇头";
                break;
            case MOUTH:
                detectionName = "张嘴";
                break;
            case BLINK:
                detectionName = "眨眼";
                break;
        }
        return detectionName;
    }

    /**
     * 初始化检测动作
     */
    public void detectionTypeInit() {
        ArrayList<DetectionType> tmpTypes = new ArrayList<DetectionType>();
        tmpTypes.add(DetectionType.BLINK);// 眨眼
        tmpTypes.add(DetectionType.MOUTH);// 张嘴
        tmpTypes.add(DetectionType.POS_PITCH);// 缓慢点头&上下点头
        // tmpTypes.add(Detector.DetectionType.POS_PITCH_UP);// 向上抬头
        // tmpTypes.add(Detector.DetectionType.POS_PITCH_DOWN);// 向下点头
        tmpTypes.add(DetectionType.POS_YAW);// 左右摇头
        // tmpTypes.add(Detector.DetectionType.POS_YAW_LEFT);// 向左摇头
        // tmpTypes.add(Detector.DetectionType.POS_YAW_RIGHT);// 向右摇头
        Collections.shuffle(tmpTypes);// 打乱顺序

        mDetectionSteps = new ArrayList<DetectionType>(mStepsnum);
        for (int i = 0; i < mStepsnum; i++) {
            mDetectionSteps.add(tmpTypes.get(i));
        }
    }

    public void onDestroy() {
        rootView = null;
        mContext = null;
    }
}
