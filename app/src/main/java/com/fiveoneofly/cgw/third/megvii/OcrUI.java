package com.fiveoneofly.cgw.third.megvii;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fiveoneofly.cgw.third.megvii.util.ScreenUtil;
import com.fiveoneofly.cgw.utils.DensityUtil;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.megvii.idcardquality.bean.IDCardAttr;
import com.yxjr.credit.R;
import com.yxjr.credit.constants.SharedKey;
import com.fiveoneofly.cgw.utils.StringUtil;


/**
 * Created by xiaochangyou on 2018/5/16.
 */
public class OcrUI {

    private TextView frontOrBackTxt, idLaterFour;
    private ImageView frontOrBackImg;

    public void findView(final BasicMegviiOcrActivity activity) {

        idLaterFour = activity.findViewById(R.id.yx_credit_after_four);
        frontOrBackTxt = activity.findViewById(R.id.yx_credit_front_or_back_txt);
        frontOrBackImg = activity.findViewById(R.id.yx_credit_front_or_back_img);
        activity.mPrompt = activity.findViewById(R.id.idcardscan_layout_horizontalTitle);
        activity.mTextureView = activity.findViewById(R.id.yx_credit_idcardscan_layout_surface);
        activity.mTextureView.setSurfaceTextureListener(activity);
        activity.mTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.mICamera.autoFocus();
            }
        });
        activity.mIDCardNewIndicator = activity.findViewById(R.id.yx_credit_idcardscan_layout_newIndicator);
        activity.findViewById(R.id.yx_credit_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public void initializeLayout(BasicMegviiOcrActivity activity, IDCardAttr.IDCardSide side) {
        if (side == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {// 拍正面
            frontOrBackTxt.setText("身份证正面");
            frontOrBackImg.setImageResource(R.drawable.yxjr_credit_face_idcard_front);
        } else if (side == IDCardAttr.IDCardSide.IDCARD_SIDE_BACK) {// 拍背面
            frontOrBackTxt.setText("身份证反面");
            frontOrBackImg.setImageResource(R.drawable.yxjr_credit_face_idcard_back);
        }
        String idCardNum = SharedUtil.getString(activity, SharedKey.PARTNER_ID_CARD_NUM);
        if (!StringUtil.isEmpty(idCardNum)) {
            idLaterFour.setText(StringUtil.laterFour(idCardNum));
        }
        /*-----------------------*/
        /*initLayout 初始化 上、下、右的布局位置*/
        // 横屏，高度、宽度调换一下
        int heightPixels = ScreenUtil.getHeight(activity, false);// 屏幕-高
        int widthPixels = ScreenUtil.getWidth(activity, false);// 屏幕-宽
        int right_width = (int) (widthPixels * activity.mIDCardNewIndicator.RIGHT_RATIO);
        int centerX = (widthPixels - right_width) >> 1;// 中心X
        int centerY = heightPixels >> 1;// 中心Y
        int idWidth = (int) ((widthPixels - right_width) * activity.mIDCardNewIndicator.SHOW_CONTENT_RATIO);// 身份证-框宽
        int idHeight = (int) (idWidth / activity.mIDCardNewIndicator.IDCARD_RATIO);// 身份证-框高
        int left = (int) (centerX - idWidth / 2.0f);// 身份证-左边X
        int top = centerY - idHeight / 2;// 身份证-上边Y
        int right = idWidth + left;// 身份证-右边X
        int bottom = idHeight + top;// 身份证-下边Y
        // “请保证信息...”布局位置
        int rightSize = widthPixels - right;
        RelativeLayout rightLayout = activity.findViewById(R.id.yx_credit_rl_right_hint);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DensityUtil.dp2Px(activity, 117), ViewGroup.LayoutParams.WRAP_CONTENT);
        if (rightSize - layoutParams.width > 0) {
            layoutParams.setMargins(
                    right + rightSize / 2 - layoutParams.width / 2,
                    top,
                    0,
                    0);// 4个参数按顺序分别是左上右下
        } else {
            layoutParams.setMargins(right, top, 0, 0);// 4个参数按顺序分别是左上右下
        }
        rightLayout.setLayoutParams(layoutParams);
        // “请扫描尾号为.....”布局位置
        LinearLayout bottomLayout = activity.findViewById(R.id.yx_credit_ll_bottom);
        RelativeLayout.LayoutParams bottomLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomLayout.measure(0, 0);
        int bottomLayoutWidth = bottomLayout.getMeasuredWidth();
        // int bottomLayoutHeight = bottomLayout.getMeasuredHeight();
        bottomLayoutParams.setMargins(
                left + idWidth / 2 - bottomLayoutWidth / 2,
                bottom + ((heightPixels - bottom) / 2),
                0,
                0);
        bottomLayout.setLayoutParams(bottomLayoutParams);
        // “完成扫描.....”布局位置
        LinearLayout topLayout = activity.findViewById(R.id.yx_credit_ll_top);
        RelativeLayout.LayoutParams topLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        topLayout.measure(0, 0);
        int topLayoutWidth = topLayout.getMeasuredWidth();
        int topLayoutHeight = topLayout.getMeasuredHeight();
        topLayoutParams.setMargins(
                left + idWidth / 2 - topLayoutWidth / 2,
                bottom + ((heightPixels - bottom) / 4) - topLayoutHeight / 2,
                0,
                0);
        topLayout.setLayoutParams(topLayoutParams);
        // 错误提示 布局位置
        RelativeLayout.LayoutParams promptLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        activity.mPrompt.measure(0, 0);
        int promptWidth = topLayout.getMeasuredWidth();
        int promptHeight = topLayout.getMeasuredHeight();
        promptLayoutParams.setMargins(
                left + idWidth / 2 - promptWidth / 2,
                top / 2 - promptHeight / 2,
                0,
                0);
        activity.mPrompt.setLayoutParams(promptLayoutParams);
    }
}
