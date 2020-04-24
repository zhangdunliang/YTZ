package com.fiveoneofly.cgw.third.megvii;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yxjr.credit.R;
import com.fiveoneofly.cgw.third.megvii.util.IMediaPlayer;
import com.fiveoneofly.cgw.third.megvii.widget.AutoRatioImageview;
import com.fiveoneofly.cgw.third.megvii.widget.RotaterView;


/**
 * Created by xiaochangyou on 2018/5/17.
 */

public class FaceUI {

    public final int navBarId = 1;

    private TextView title;
    private LinearLayout headViewLinear;// "请在光线充足的情况下进行检测"这个视图
    private RelativeLayout timeOutRel;// 倒计时部分
    private TextView resultTxt;
    private RotaterView rotaterView;
    private RelativeLayout resultLayout;// 返回结果区域

    public void findView(final BasicMegviiFaceActivity activity) {
        activity.mRootView = activity.findViewById(R.id.liveness_layout_rootRel);
        activity.mFaceMask = activity.findViewById(R.id.liveness_layout_facemask);
        activity.mFaceMask.setVisibility(View.GONE);// 仅调试时用
        activity.mPromptText = activity.findViewById(R.id.liveness_layout_promptText);
        activity.mCameraPreview = activity.findViewById(R.id.liveness_layout_textureview);
        activity.mCameraPreview.setSurfaceTextureListener(activity);
        activity.mProgressBar = activity.findViewById(R.id.liveness_layout_progressbar);
        activity.mProgressBar.setVisibility(View.INVISIBLE);
        activity.mTimeOutText = activity.findViewById(R.id.detection_step_timeout_garden);
        activity.mTimeOutBar = activity.findViewById(R.id.detection_step_timeout_progressBar);
        activity.findViewById(R.id.yx_credit_liveness_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        headViewLinear = activity.findViewById(R.id.liveness_layout_bottom_tips_head);
        headViewLinear.setVisibility(View.VISIBLE);
        timeOutRel = activity.findViewById(R.id.detection_step_timeoutRel);
        resultLayout = activity.findViewById(R.id.liveness_layout_result);
        title = activity.findViewById(R.id.yx_credit_liveness_title);
        resultTxt = activity.findViewById(R.id.liveness_layout_result_str);
        rotaterView = activity.findViewById(R.id.liveness_layout_result_rotater);
    }

    public void initializeLayout(BasicMegviiFaceActivity activity) {
        activity.findViewById(R.id.liveness_layout_topBar).setId(navBarId);// 顶部导航栏设置ID

        RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout_params.addRule(RelativeLayout.BELOW, navBarId);
        activity.mFaceMask.setLayoutParams(layout_params);// 调试时UI
        activity.findViewById(R.id.liveness_layout_result).setLayoutParams(layout_params);// 返回结果
        // 顶部文字
        RelativeLayout.LayoutParams hint_layout_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hint_layout_params.addRule(RelativeLayout.BELOW, navBarId);
        activity.findViewById(R.id.liveness_layout_hint).setLayoutParams(hint_layout_params);// 顶部文字
        // 人头引导图
        RelativeLayout.LayoutParams mask_layout_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mask_layout_params.addRule(RelativeLayout.BELOW, navBarId);
        AutoRatioImageview mask = activity.findViewById(R.id.liveness_layout_head_mask);
        mask.setScaleType(ImageView.ScaleType.FIT_XY);
        mask.setLayoutParams(mask_layout_params);// 人头引导图
    }

    // 开始检测
    public void handleStart(BasicMegviiFaceActivity activity) {

        Animation animationOut = AnimationUtils.loadAnimation(activity, R.anim.yx_credit_face_liveness_leftout);
        headViewLinear.startAnimation(animationOut);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                timeOutRel.setVisibility(View.VISIBLE);
            }
        });
    }

    // 显示结果处理
    public void showResult(final BasicMegviiFaceActivity activity,
                           final boolean isSuccess,
                           IMediaPlayer iMediaPlayer,
                           int resID,
                           int rawID,
                           View.OnClickListener clickListener) {

        iMediaPlayer.doPlay(rawID);

        resultTxt.setText(isSuccess ? "识别成功!" : "识别失败!");
        resultTxt.setTextColor(isSuccess ? 0xff1ab4fb : 0xfff45555);
        title.setText("面部验证结果");

        if (!isSuccess) {
            TextView suggestTxt = activity.findViewById(R.id.liveness_layout_suggest);
            suggestTxt.setVisibility(View.VISIBLE);
            suggestTxt.setText(resID);
        }

        Animation animationIN = AnimationUtils.loadAnimation(activity, R.anim.yx_credit_face_liveness_rightin);

        resultLayout.setVisibility(View.VISIBLE);
        resultLayout.setAnimation(animationIN);

        final Button resultOkBtn = activity.findViewById(R.id.liveness_layout_ok);
        resultOkBtn.setOnClickListener(clickListener);

        rotaterView.setColour(isSuccess ? 0xff1ab4fb : 0xfff45555);
        final ImageView statusView = activity.findViewById(R.id.liveness_layout_result_status);
        statusView.setVisibility(View.INVISIBLE);
        statusView.setImageResource(isSuccess ? R.drawable.yxjr_credit_face_liveness_result_success : R.drawable.yxjr_credit_face_liveness_result_failded);

        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(rotaterView, "progress", 0, 100);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setDuration(600);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Animation scaleanimation = AnimationUtils.loadAnimation(activity, R.anim.yx_credit_face_liveness_scaleoutin);
                statusView.startAnimation(scaleanimation);
                statusView.setVisibility(View.VISIBLE);
                resultTxt.startAnimation(scaleanimation);
                resultTxt.setVisibility(View.VISIBLE);
                resultOkBtn.startAnimation(scaleanimation);
                resultOkBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        objectAnimator.start();
    }
}
