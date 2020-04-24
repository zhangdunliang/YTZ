//package com.yxjr.credit.fragment;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.yxjr.credit.R;
//import com.yxjr.credit.WebActivity;
//import com.yxjr.credit.constants.ActivityCode;
//import com.yxjr.credit.constants.BridgeCode;
//import com.yxjr.credit.constants.HttpParam;
//import com.yxjr.credit.constants.HttpService;
//import com.yxjr.credit.constants.SharedKey;
//import com.yxjr.credit.constants.YxjrParam;
//import com.yxjr.credit.fragment.deprecated.YxBmpFactory;
//import com.yxjr.credit.fragment.deprecated.YxPictureUtil;
//import com.yxjr.credit.net.HttpRequest;
//import com.yxjr.credit.net.RequestCallBack;
//import com.yxjr.credit.net.upload.FileCallBack;
//import com.yxjr.credit.net.upload.FileUpload;
//import com.yxjr.credit.statistics.BuriedStatistics;
//import com.yxjr.credit.third.event.InvokeJsEvent;
//import com.yxjr.credit.utils.DataUtil;
//import com.yxjr.credit.utils.DensityUtil;
//import com.yxjr.credit.utils.LoadingUtil;
//import com.yxjr.credit.utils.SharedUtil;
//import com.yxjr.credit.utils.StringUtil;
//import com.yxjr.credit.utils.ToastUtil;
//
//import org.greenrobot.eventbus.EventBus;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.StringTokenizer;
//
//import static android.view.View.inflate;
//
///**
// * Created by xiaochangyou on 2018/4/18.
// * <p>
// * 旧版本-实名认证
// */
//public class VerifiedFragment extends Fragment implements WebActivity.IAssetFragment {
//
////    /**
////     * 实名认证布局文件(XML)
////     */
////    private String autonymID = "yxjr_credit_certify_approve";
////    /**
////     * 返回按钮(RelativeLayout)
////     */
////    private String autonymBackID = "yx_credit_certify_autonym_Back";
////    /**
////     * 标题(TextView)
////     */
////    private String autonymTitleID = "yx_credit_certify_autonym_Title";
////    /**
////     * 常见问题
////     */
////    private String autonymSubTitleID = "yx_credit_certify_autonym_SublTitle";
////    /**
////     * 姓名(EditText)[请输入您的姓名]
////     */
////    private String autonymNameID = "yx_credit_certify_autonym_Name";
////    /**
////     * 身份证号(EditText)[请输入您的身份证号]
////     */
////    private String autonymIdentityNumID = "yx_credit_certify_autonym_IdentityNum";
////    /**
////     * 身份证正面拍照按钮(ImageView)
////     */
////    private String autonymIdNumFrontPicBtnID = "yx_credit_certify_autonym_IdNumFrontPic_btn";
////    /** 身份证正面照片提示文字(TextView)[身份证正面] */
////    // private String autonymIdNumFrontPicTxtID =
////    // "yx_credit_certify_autonym_IdNumFrontPic_txt";
////    /**
////     * 身份证反面拍照按钮(ImageView)
////     */
////    private String autonymIdNumVersoPicBtnID = "yx_credit_certify_autonym_IdNumVersoPic_btn";
////    /** 身份证反面照片提示文字(TextView)[身份证反面] */
////    // private String autonymIdNumVersoPicTxtID =
////    // "yx_credit_certify_autonym_IdNumVersoPic_txt";
////    /**
////     * 手持身份证拍照按钮(ImageView)
////     */
////    private String autonymIdNumHandPicBtnID = "yx_credit_certify_autonym_IdNumHandPic_btn";
////    /** 手持身份证照片提示文字(TextView)[手持身份证] */
////    // private String autonymIdNumHandPicTxtID =
////    // "yx_credit_certify_autonym_IdNumHandPic_txt";
////    /**
////     * 提交(Button)[提交]
////     */
////    private String autonymSubmitID = "yx_credit_certify_autonym_Submit";
////    /**
////     * [额度宝会保护您的信息]
////     */
////    private String autonymText1ID = "yx_credit_certify_autonym_Text1";
////    /**
////     * [身份证拍照]
////     */
////    private String autonymText2ID = "yx_credit_certify_autonym_Text2";
////    /** [拍照上传] */
////    // private String autonymText3ID = "yx_credit_certify_autonym_Text3";
////    /**
////     * [实名影像认证说明：]
////     */
////    private String autonymText4ID = "yx_credit_certify_autonym_Text4";
////    /**
////     * [1、]
////     */
////    private String autonymText5ID = "yx_credit_certify_autonym_Text5";
////    /**
////     * [身份证正面、反面照片须无遮挡、无反光、无修改、字迹、头像均清晰可见;]
////     */
////    private String autonymText6ID = "yx_credit_certify_autonym_Text6";
////    /**
////     * [2、]
////     */
////    private String autonymText7ID = "yx_credit_certify_autonym_Text7";
////    /**
////     * [手持身份证照片须手持身份证，同时露脸并露肘身份证及本人均需无遮挡、无反光、无修改，本人及身份证影像字迹、头像均清晰可见。]
////     */
////    private String autonymText8ID = "yx_credit_certify_autonym_Text8";
////    /**
////     * [3、]
////     */
////    private String autonymText9ID = "yx_credit_certify_autonym_Text9";
////    /**
////     * 查看影像示例(TextView)[查看影像示例]
////     */
////    private String autonymText10ID = "yx_credit_certify_autonym_Text10";
//
//    //    /** 布局文件 */
////    private View layout;
//    // *返回按钮
//    private RelativeLayout autonymBack;
//    // *标题
//    private TextView autonymTitle;
//    // *常见问题
//    private TextView autoymSubTitle;
//    // *姓名
//    private EditText autonymName;
//    // *身份证号
//    private EditText autonymIdentityNum;
//    // *身份证正面拍照按钮
//    private ImageView autonymIdNumFrontPicBtn;
//    // *身份证正面照片提示文字
//    // private TextView autonymIdNumFrontPicTxt;
//    // *身份证反面拍照按钮
//    private ImageView autonymIdNumVersoPicBtn;
//    // *身份证反面照片提示文字
//    // private TextView autonymIdNumVersoPicTxt;
//    // *手持身份证拍照按钮
//    private ImageView autonymIdNumHandPicBtn;
//    // *手持身份证照片提示文字
//    // private TextView autonymIdNumHandPicTxt;
//    // *提交
//    private Button autonymSubmitBtn;
//    // *额度宝会保护您的信息
//    private TextView autonymText1;
//    // *身份证拍照
//    private TextView autonymText2;
//    // *拍照上传
//    // private TextView autonymText3;
//    // *实名影像认证说明：
//    private TextView autonymText4;
//    // *1、
//    private TextView autonymText5;
//    // *身份证正面、反面照片须无遮挡、无反光、无修改、字迹、头像均清晰可见;
//    private TextView autonymText6;
//    // *2、
//    private TextView autonymText7;
//    // *手持身份证照片须手持身份证，同时露脸并露肘身份证及本人均需无遮挡、无反光、无修改，本人及身份证影像字迹、头像均清晰可见。
//    private TextView autonymText8;
//    // *3、
//    private TextView autonymText9;
//    // *查看影像示例
//    private TextView autonymText10;
//
//
//    private Activity mActivity;
//
//    private String certId;
//    private String categoryCode;
//    public YxBmpFactory bmpFactory;
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        certId = this.getArguments().getString("certId", null);
//        categoryCode = this.getArguments().getString("categoryCode", null);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//
//        View view = inflate(mActivity, R.layout.yxjr_credit_fragment_verified, null);
////        layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
//        autonymBack = (RelativeLayout) view.findViewById(R.id.yx_credit_certify_autonym_Back);
//        autonymTitle = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Title);
//        autoymSubTitle = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_SublTitle);
//        autonymName = (EditText) view.findViewById(R.id.yx_credit_certify_autonym_Name);
//        autonymIdentityNum = (EditText) view.findViewById(R.id.yx_credit_certify_autonym_IdentityNum);
//        autonymIdNumFrontPicBtn = (ImageView) view.findViewById(R.id.yx_credit_certify_autonym_IdNumFrontPic_btn);
//        // autonymIdNumFrontPicTxt = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_IdNumFrontPic_txt);
//        autonymIdNumVersoPicBtn = (ImageView) view.findViewById(R.id.yx_credit_certify_autonym_IdNumVersoPic_btn);
//        // autonymIdNumVersoPicTxt = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_IdNumVersoPic_txt);
//        autonymIdNumHandPicBtn = (ImageView) view.findViewById(R.id.yx_credit_certify_autonym_IdNumHandPic_btn);
//        // autonymIdNumHandPicTxt = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_IdNumHandPic_txt);
//        autonymSubmitBtn = (Button) view.findViewById(R.id.yx_credit_certify_autonym_Submit);
//        autonymText1 = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Text1);
//        autonymText2 = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Text2);
//        // autonymText3 = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Text3);
//        autonymText4 = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Text4);
//        autonymText5 = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Text5);
//        autonymText6 = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Text6);
//        autonymText7 = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Text7);
//        autonymText8 = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Text8);
//        autonymText9 = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Text9);
//        autonymText10 = (TextView) view.findViewById(R.id.yx_credit_certify_autonym_Text10);
//
//
//        int screenWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
//        autonymTitle.setText("实名认证");
//        autonymName.setText(SharedUtil.getString(mActivity, SharedKey.PARTNER_REAL_NAME));
//        autonymName.setFocusable(false);
//        autonymName.setFocusableInTouchMode(false);
//        autonymIdentityNum.setText(SharedUtil.getString(mActivity, SharedKey.PARTNER_ID_CARD_NUM));
//        autonymIdentityNum.setFocusable(false);
//        autonymIdentityNum.setFocusableInTouchMode(false);
//        autonymIdNumFrontPicBtn.setImageResource(R.drawable.yx_credit_identity_card_front);
//        ViewGroup.LayoutParams frontLayoutParams = autonymIdNumFrontPicBtn.getLayoutParams();
//        frontLayoutParams.height = screenWidth / 3 - DensityUtil.dp2Px(mActivity, 15);// 高度=控件宽度(屏幕总宽/3-多余的15dp)
//        autonymIdNumFrontPicBtn.setLayoutParams(frontLayoutParams);
//        // autonymIdNumFrontPicTxt.setText("身份证正面");
//        autonymIdNumVersoPicBtn.setImageResource(R.drawable.yx_credit_identity_card_verso);
//        ViewGroup.LayoutParams versoLayoutParams = autonymIdNumVersoPicBtn.getLayoutParams();
//        versoLayoutParams.height = screenWidth / 3 - DensityUtil.dp2Px(mActivity, 15);
//        autonymIdNumVersoPicBtn.setLayoutParams(versoLayoutParams);
//        // autonymIdNumVersoPicTxt.setText("身份证反面");
//        autonymIdNumHandPicBtn.setImageResource(R.drawable.yx_credit_identity_card_in_hand);
//        ViewGroup.LayoutParams handLayoutParams = autonymIdNumHandPicBtn.getLayoutParams();
//        handLayoutParams.height = screenWidth / 3 - DensityUtil.dp2Px(mActivity, 15);
//        autonymIdNumHandPicBtn.setLayoutParams(handLayoutParams);
//
//        // autonymIdNumHandPicTxt.setText("手持身份证");
//        autonymSubmitBtn.setText("提交");
//        autonymText1.setText("额度宝会保护您的信息");
//        autonymText2.setText("身份证拍照");
//        // autonymText3.setText("拍照上传");
//        autonymText4.setText("实名影像认证说明：");
//        autonymText5.setText("1、");
//        autonymText6.setText("身份证正面、反面照片须无遮挡、无反光、无修改、字迹、头像均清晰可见;");
//        autonymText7.setText("2、");
//        autonymText8.setText("手持身份证照片须手持身份证，同时露脸并露肘身份证及本人均需无遮挡、无反光、无修改，本人及身份证影像字迹、头像均清晰可见。");
//        autonymText9.setText("3、");
//        autonymText10.setText("查看影像示例");
//        autonymBack.setOnClickListener(clickListener);
//        autoymSubTitle.setOnClickListener(clickListener);
//        autonymIdNumFrontPicBtn.setScaleType(ImageView.ScaleType.FIT_XY);
//        autonymIdNumFrontPicBtn.setOnClickListener(clickListener);
//        autonymIdNumVersoPicBtn.setScaleType(ImageView.ScaleType.FIT_XY);
//        autonymIdNumVersoPicBtn.setOnClickListener(clickListener);
//        autonymIdNumHandPicBtn.setScaleType(ImageView.ScaleType.FIT_XY);
//        autonymIdNumHandPicBtn.setOnClickListener(clickListener);
//        autonymSubmitBtn.setOnClickListener(clickListener);
//        autonymText10.setOnClickListener(clickListener);
////        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
////        this.addView(layout);
//
//        bmpFactory = new YxBmpFactory(mActivity);
//
//        return view;
//    }
//
//    private boolean isSelectFront = false;// 是否添加身份证正面照
//    private boolean isSelectVerso = false;// 是否添加身份证反面照
//    private boolean isSelectHand = false;// 是否添加身份证手持照
//
//    //* 身份证正面原照片路径
//    private String frontBmpPath = null;
//    //* 身份证反面原照片路径
//    private String versoBmpPath = null;
//    //* 手持身份证原照片路径
//    private String handBmpPath = null;
//
//
////    public void setAutonymIdNumFrontPic(String frontBmpPath) {
////        if (frontBmpPath != null) {
////            if (this.frontBmpPath != null) {
////                YxPictureUtil.deleteTempFile(this.frontBmpPath);
////            }
////            this.frontBmpPath = frontBmpPath;
////            this.autonymIdNumFrontPicBtn.setImageBitmap(YxPictureUtil.getSmallBitmap(frontBmpPath));
////            isSelectFront = true;
////        }
////    }
////
////    public void setAutonymIdNumVersoPic(String versoBmpPath) {
////        if (versoBmpPath != null) {
////            if (this.versoBmpPath != null) {
////                YxPictureUtil.deleteTempFile(this.versoBmpPath);
////            }
////            this.versoBmpPath = versoBmpPath;
////            this.autonymIdNumVersoPicBtn.setImageBitmap(YxPictureUtil.getSmallBitmap(versoBmpPath));
////            isSelectVerso = true;
////        }
////    }
////
////    public void setAutonymIdNumHandPic(String handBmpPath) {
////        if (handBmpPath != null) {
////            if (this.handBmpPath != null) {
////                YxPictureUtil.deleteTempFile(this.handBmpPath);
////            }
////            this.handBmpPath = handBmpPath;
////            this.autonymIdNumHandPicBtn.setImageBitmap(YxPictureUtil.getSmallBitmap(handBmpPath));
////            isSelectHand = true;
////        }
////    }
//
//    @Override
//    public YxBmpFactory getBmp() {
//        return bmpFactory;
//    }
//
//    @Override
//    public void setPath(int type, String bmpPath) {
//        if (bmpPath != null) {
//            if (type == 1) {
//                if (this.frontBmpPath != null) {
//                    YxPictureUtil.deleteTempFile(this.frontBmpPath);
//                }
//                this.frontBmpPath = bmpPath;
//                this.autonymIdNumFrontPicBtn.setImageBitmap(YxPictureUtil.getSmallBitmap(bmpPath));
//                isSelectFront = true;
//
//            } else if (type == 2) {
//                if (this.versoBmpPath != null) {
//                    YxPictureUtil.deleteTempFile(this.versoBmpPath);
//                }
//                this.versoBmpPath = bmpPath;
//                this.autonymIdNumVersoPicBtn.setImageBitmap(YxPictureUtil.getSmallBitmap(bmpPath));
//                isSelectVerso = true;
//            } else if (type == 3) {
//                if (this.handBmpPath != null) {
//                    YxPictureUtil.deleteTempFile(this.handBmpPath);
//                }
//                this.handBmpPath = bmpPath;
//                this.autonymIdNumHandPicBtn.setImageBitmap(YxPictureUtil.getSmallBitmap(bmpPath));
//                isSelectHand = true;
//            }
//        }
//    }
//
//    @Override
//    public Fragment getFragment() {
//        return this;
//    }
//
//    private View.OnClickListener clickListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            int id = v.getId();
//
//            int autonymBack = R.id.yx_credit_certify_autonym_Back;
//            int autonmySubTitleInt = R.id.yx_credit_certify_autonym_SublTitle;
//            int autonymIdNumFrontPicBtn = R.id.yx_credit_certify_autonym_IdNumFrontPic_btn;
//            int autonymIdNumVersoPicBtn = R.id.yx_credit_certify_autonym_IdNumVersoPic_btn;
//            int autonymIdNumHandPicBtn = R.id.yx_credit_certify_autonym_IdNumHandPic_btn;
//            int autonymSubmitBtn = R.id.yx_credit_certify_autonym_Submit;
//            int autonymText10 = R.id.yx_credit_certify_autonym_Text10;
//
//            if (id == autonymBack) {// 返回
////                mCallBack.removeAutonymCertify();
//                mActivity.onBackPressed();
//            } else if (id == autonmySubTitleInt) {
////                mCallBack.addQuestion();
//
//                final Bundle bundle = new Bundle();
//                bundle.putString("url", HttpParam.USUAL_QUESTION_URL);
//                bundle.putString("htmlLabel", null);
//                bundle.putString("type", null);
//                bundle.putString("backHint", null);
//                bundle.putBoolean("isRefresh", false);
//                bundle.putBoolean("showToolbar", true);//默认true
//
//                WebActivity webActivity = (WebActivity) mActivity;
//                webActivity.addFragment(bundle);
//
//            } else if (id == autonymIdNumFrontPicBtn) {// 身份证正面
//                if (!isSelectFront) {
//                    mActivity.startActivityForResult(
//                            new Intent(mActivity, CameraActivity.class).putExtra("picName",
//                                    YxjrParam.FileName.IMG_A1 + DataUtil.getCurrentTime2() + ".jpg"),
//                            ActivityCode.Request.AUTONYM_CERTIFY_ID_CARD_FRONT);
//                } else {
//                    showDialog(frontBmpPath,
//                            ActivityCode.Request.AUTONYM_CERTIFY_ID_CARD_FRONT,
//                            YxjrParam.FileName.IMG_A1 + DataUtil.getCurrentTime2() + ".jpg");
//                }
//            } else if (id == autonymIdNumVersoPicBtn) {// 身份证反面
//                if (!isSelectVerso) {
//                    mActivity.startActivityForResult(
//                            new Intent(mActivity, CameraActivity.class).putExtra("picName",
//                                    YxjrParam.FileName.IMG_A2 + DataUtil.getCurrentTime2()
//                                            + ".jpg"),
//                            ActivityCode.Request.AUTONYM_CERTIFY_ID_CARD_VERSO);
//                } else {
//                    showDialog(versoBmpPath,
//                            ActivityCode.Request.AUTONYM_CERTIFY_ID_CARD_VERSO,
//                            YxjrParam.FileName.IMG_A2 + DataUtil.getCurrentTime2() + "jpg");
//                }
//            } else if (id == autonymIdNumHandPicBtn) {// 手持身份证
//                if (!isSelectHand) {
//                    mActivity.startActivityForResult(
//                            new Intent(mActivity, CameraActivity.class)
//                                    .putExtra("picName",
//                                            YxjrParam.FileName.IMG_A3
//                                                    + DataUtil.getCurrentTime2() + ".jpg")
//                                    .putExtra("isvertical", true).putExtra("isChange", true),
//                            ActivityCode.Request.AUTONYM_CERTIFY_ID_CARD_HAND);
//                } else {
//                    showDialog(handBmpPath,
//                            ActivityCode.Request.AUTONYM_CERTIFY_ID_CARD_HAND,
//                            YxjrParam.FileName.IMG_A3 + DataUtil.getCurrentTime2() + ".jpg");
//                }
//            } else if (id == autonymSubmitBtn) {// 提交
//                submitCredential();
//            } else if (id == autonymText10) {// 影像示例
////                mCallBack.addExample();
//
//                final Bundle bundle = new Bundle();
//                bundle.putString("url", HttpParam.EXAMPLE_URL);
//                bundle.putString("htmlLabel", null);
//                bundle.putString("type", null);
//                bundle.putString("backHint", null);
//                bundle.putBoolean("isRefresh", false);
//                bundle.putBoolean("showToolbar", false);//默认true
//
//                WebActivity webActivity = (WebActivity) mActivity;
//                webActivity.addFragment(bundle);
//
//            }
//        }
//    };
//
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.mActivity = (Activity) context;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
//
//
//    private void showDialog(final String picPath, final int requestCode, final String picName) {
//        if (mActivity == null) {
//            return;
//        }
//        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Wallpaper_NoTitleBar);
//        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.yxjr_credit_verified_dialog, null);
//        ImageView close = (ImageView) view.findViewById(R.id.yx_credit_iv_close);
//        ImageView preview = (ImageView) view.findViewById(R.id.yx_credit_iv_preview);
//        Button again = (Button) view.findViewById(R.id.yx_credit_bt_again);
//        close.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        preview.setAdjustViewBounds(true);
//        preview.setImageBitmap(YxPictureUtil.getSmallBitmap(picPath));
//
//        again.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (requestCode == ActivityCode.Request.AUTONYM_CERTIFY_ID_CARD_HAND) {
//                    mActivity.startActivityForResult(new Intent(mActivity, CameraActivity.class)
//                                    .putExtra("picName", picName).putExtra("isvertical", true).putExtra("isChange", true),
//                            requestCode);
//                } else {
//                    mActivity.startActivityForResult(
//                            new Intent(mActivity, CameraActivity.class).putExtra("picName", picName), requestCode);
//                }
//                dialog.dismiss();
//            }
//        });
//        dialog.setContentView(view,
//                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        // dialog.setContentView(view);
//
//        Window window = dialog.getWindow();
//        window.getDecorView().setPadding(0, 0, 0, 0);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        window.setAttributes(lp);
//        dialog.show();
//    }
//
//
//    private void submitCredential() {
//
//        if (StringUtil.isEmpty(autonymName.getText().toString())) {
//            ToastUtil.showToast(mActivity, "请填写姓名");
//            new BuriedStatistics.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo("请填写姓名")
//                    .addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
//            return;
//        }
//        if (StringUtil.isEmpty(autonymIdentityNum.getText().toString())) {
//            ToastUtil.showToast(mActivity, "请填写身份证号");
//            new BuriedStatistics.Builder().addContext(mActivity).addOperPageName("请填写身份证号").addErrorInfo("请填写姓名")
//                    .addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
//            return;
//        }
//        if (!isSelectFront) {
//            ToastUtil.showToast(mActivity, "请添加身份证正面照");
//            new BuriedStatistics.Builder().addContext(mActivity).addOperPageName("请添加身份证正面照").addErrorInfo("请填写姓名")
//                    .addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
//            return;
//        }
//        if (!isSelectVerso) {
//            ToastUtil.showToast(mActivity, "请添加身份证反面照");
//            new BuriedStatistics.Builder().addContext(mActivity).addOperPageName("请添加身份证反面照").addErrorInfo("请填写姓名")
//                    .addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
//            return;
//        }
//        if (!isSelectHand) {
//            ToastUtil.showToast(mActivity, "请添加手持身份证照");
//            new BuriedStatistics.Builder().addContext(mActivity).addOperPageName("请添加手持身份证照").addErrorInfo("请填写姓名")
//                    .addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
//            return;
//        }
//        List<Bitmap> bmpList = new ArrayList<Bitmap>();
//        if (null != frontBmpPath) {
//            bmpList.add(YxPictureUtil.getSmallBitmap(frontBmpPath));
//        }
//        if (null != versoBmpPath) {
//            bmpList.add(YxPictureUtil.getSmallBitmap(versoBmpPath));
//        }
//        if (null != handBmpPath) {
//            bmpList.add(YxPictureUtil.getSmallBitmap(handBmpPath));
//        }
//        FileOutputStream fos = null;
//        BufferedOutputStream bos = null;
//        File[] files = new File[bmpList.size()];
//        try {
//            for (int i = 0; i < bmpList.size(); i++) {
//                String picName = "A" + (i + 1) + "_" + DataUtil.getCurrentTime2() + ".jpg";
//                files[i] = new File(YxPictureUtil.getAlbumDir(), picName);
//                fos = new FileOutputStream(files[i]);
//                bos = new BufferedOutputStream(fos);
//                bmpList.get(i).compress(Bitmap.CompressFormat.JPEG, YxjrParam.Common.PICTURE_COMPRESS, bos);// compress是压缩率,50表示压缩50%;如果不压缩是100,表示压缩率为0%
//                fos.flush();
//                bos.flush();
////                YxLog.d("======uploadfiles add file" + new FileInputStream(files[i]).available());
////                YxLog.d("======uploadfiles Path " + files[i].getPath());
//            }
//        } catch (IOException e) {
////            YxLog.e("Exception:图片转换错误!");
//            e.printStackTrace();
//            ToastUtil.showToast(mActivity, "图片转换错误");
//            return;
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (bos != null) {
//                try {
//                    bos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        final File[] uploadFiles = files;
//
////        final DialogLoading dialogLoading = new DialogLoading(mActivity, "上传中...");
////        dialogLoading.show();
////        new RequestEngine(mContext).upload(certId, uploadFiles, new UploadCallBack() {
////            @Override
////            public void onSucces(String result) {
////                // TODO Auto-generated method stub
////                YxLog.d("======文件上传成功：" + result);
////                String[] filePathAfterSplit = new String[3];
////                // filePathAfterSplit = result.split(","); //
////                // 以“,”作为分隔符来分割date字符串，并把结果放入3个字符串中。
////                StringTokenizer token = new StringTokenizer(result, ",");
////                while (token.hasMoreTokens()) {
////                    String path = token.nextToken();
////                    if (path.contains("A1")) {
////                        filePathAfterSplit[0] = path;
////                    } else if (path.contains("A2")) {
////                        filePathAfterSplit[1] = path;
////                    } else if (path.contains("A3")) {
////                        filePathAfterSplit[2] = path;
////                    }
////                }
////
////                String houseProvinceAndCityValueValue = autonymIdentityNum.getText().toString();
////                String autonymNameValue = autonymName.getText().toString();
////                JSONObject params = new JSONObject();
////                try {
////                    params.put("cert", houseProvinceAndCityValueValue);// 身份证号
////                    params.put("name", autonymNameValue);// 姓名
////                    params.put("certFront", filePathAfterSplit[0]);// 身份证正面
////                    params.put("certBack", filePathAfterSplit[1]);// 身份证反面
////                    params.put("certHandheld", filePathAfterSplit[2]);// 手持身份证
////                    params.put("categoryCode", categoryCode);
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
////                if (categoryCode.equals("")) {
////                    submitName(HttpConstant.Request.NAME_ASSET_APPROVE, params);
////                } else {
////                    submitName(HttpConstant.Request.NAME_ASSET_APPROVE_PATCH, params);
////                }
////                super.onSucces(result);
////            }
////
////            @Override
////            public void onFailure(String errorCode, String errorMsg) {
////                // TODO Auto-generated method stub
////                YxLog.d("======文件上传失败：" + errorCode + "==" + errorMsg);
////                ToastUtil.showToast(mContext, "身份证上传失败！");
////                new StatisticalTime.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo(errorMsg)
////                        .addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
////                super.onFailure(errorCode, errorMsg);
////            }
////
////            @Override
////            public void onFinish() {
////                // TODO Auto-generated method stub
////                for (int i = 0; i < uploadFiles.length; i++) {
////                    if (null != uploadFiles[i].getAbsolutePath()) {
////                        YxPictureUtil.deleteTempFile(uploadFiles[i].getAbsolutePath());
////                    }
////                }
////                dialogLoading.cancel();
////                super.onFinish();
////            }
////
////            @Override
////            public void onProgress(long currentLength, long totalLength, int index) {
////                super.onProgress(currentLength, totalLength, index);
////            }
////        });
//
//
//        LoadingUtil.display(mActivity);
//        new FileUpload.Builder()
//                .appNo(certId)
//                .files(uploadFiles)
//                .upload(new FileCallBack() {
//                    @Override
//                    public void onSucces(String result) {
//                        super.onSucces(result);
//
//                        String[] filePathAfterSplit = new String[3];
//                        // filePathAfterSplit = result.split(","); //
//                        // 以“,”作为分隔符来分割date字符串，并把结果放入3个字符串中。
//                        StringTokenizer token = new StringTokenizer(result, ",");
//                        while (token.hasMoreTokens()) {
//                            String path = token.nextToken();
//                            if (path.contains("A1")) {
//                                filePathAfterSplit[0] = path;
//                            } else if (path.contains("A2")) {
//                                filePathAfterSplit[1] = path;
//                            } else if (path.contains("A3")) {
//                                filePathAfterSplit[2] = path;
//                            }
//                        }
//
//                        String houseProvinceAndCityValueValue = autonymIdentityNum.getText().toString();
//                        String autonymNameValue = autonymName.getText().toString();
//                        JSONObject params = new JSONObject();
//                        try {
//                            params.put("cert", houseProvinceAndCityValueValue);// 身份证号
//                            params.put("name", autonymNameValue);// 姓名
//                            params.put("certFront", filePathAfterSplit[0]);// 身份证正面
//                            params.put("certBack", filePathAfterSplit[1]);// 身份证反面
//                            params.put("certHandheld", filePathAfterSplit[2]);// 手持身份证
//                            params.put("categoryCode", categoryCode);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        if (StringUtil.isEmpty(categoryCode)) {
//                            submitName(HttpService.NAME_ASSET_APPROVE, params);
//                        } else {
//                            submitName(HttpService.NAME_ASSET_APPROVE_PATCH, params);
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(String errorCode, String errorMsg) {
//                        super.onFailure(errorCode, errorMsg);
//                        ToastUtil.showToast(mActivity, "身份证上传失败！");
//                        new BuriedStatistics.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo(errorMsg)
//                                .addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        super.onFinish();
//                        for (int i = 0; i < uploadFiles.length; i++) {
//                            if (null != uploadFiles[i].getAbsolutePath()) {
//                                YxPictureUtil.deleteTempFile(uploadFiles[i].getAbsolutePath());
//                            }
//                        }
//                        LoadingUtil.dismiss();
//                    }
//                });
//    }
//
//    // 实名认证
//    private void submitName(final String serviceId, final JSONObject name) {
//
////        final DialogLoading dialogLoading = new DialogLoading(mActivity);
////        dialogLoading.show();
////        new RequestEngine(mContext).execute(serviceId, name, new RequestCallBack(mContext) {
////            @Override
////            public void onSucces(String result) {
////                dialogLoading.cancel();
////                ToastUtil.showToast(mContext, "提交成功！");
////                // 统计提交
////                new StatisticalTime.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo("")
////                        .addOperElementType("button").addOperElementName("提交").addSessionId(serviceId).build();
////                mCallBack.removeAutonymCertify();
////                mCallBack.loadUrl(JsConstant.H5_REFRESH, null);
////                // 删除临时原图片
////                if (null != frontBmpPath) {
////                    YxPictureUtil.deleteTempFile(frontBmpPath);
////                }
////                if (null != versoBmpPath) {
////                    YxPictureUtil.deleteTempFile(versoBmpPath);
////                }
////                if (null != handBmpPath) {
////                    YxPictureUtil.deleteTempFile(handBmpPath);
////                }
////                super.onSucces(result);
////            }
////
////            @Override
////            public void onFailure(String errorCode, String errorMsg) {
////                dialogLoading.cancel();
////                new StatisticalTime.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo(errorMsg)
////                        .addOperElementType("button").addOperElementName("提交").addSessionId(serviceId).build();
////                super.onFailure(errorCode, errorMsg);
////            }
////        });
//
//        new HttpRequest.Builder(mActivity)
//                .serviceId(serviceId)
//                .params(name)
//                .execute(new RequestCallBack(mActivity) {
//                    @Override
//                    public void onSucces(String result) {
//                        ToastUtil.showToast(mActivity, "提交成功！");
//                        // 统计提交
//                        new BuriedStatistics.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo("")
//                                .addOperElementType("button").addOperElementName("提交").addSessionId(serviceId).build();
//
////                        mCallBack.removeAutonymCertify();
////                        mCallBack.loadUrl(JsConstant.H5_REFRESH, null);
//                        EventBus.getDefault().post(new InvokeJsEvent(BridgeCode.H5_REFRESH, null));
//                        mActivity.onBackPressed();
//
//                        // 删除临时原图片
//                        if (null != frontBmpPath) {
//                            YxPictureUtil.deleteTempFile(frontBmpPath);
//                        }
//                        if (null != versoBmpPath) {
//                            YxPictureUtil.deleteTempFile(versoBmpPath);
//                        }
//                        if (null != handBmpPath) {
//                            YxPictureUtil.deleteTempFile(handBmpPath);
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(String errorCode, String errorMsg) {
//                        new BuriedStatistics.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo(errorMsg)
//                                .addOperElementType("button").addOperElementName("提交").addSessionId(serviceId).build();
//                    }
//                });
//    }
//
//}
