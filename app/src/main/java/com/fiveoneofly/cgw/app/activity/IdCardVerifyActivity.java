package com.fiveoneofly.cgw.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.basic.BasicActivity;
import com.fiveoneofly.cgw.app.basic.NavigateBarBlue;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.ServiceCode;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.net.entity.bean.BasicResponse;
import com.fiveoneofly.cgw.net.entity.bean.IdcardVerifyRequest;
import com.fiveoneofly.cgw.net.entity.bean.IdcardVerifyResponse;
import com.fiveoneofly.cgw.net.entity.bean.LoginInResponse;
import com.fiveoneofly.cgw.net.upload.FileCallBack;
import com.fiveoneofly.cgw.net.upload.FileUpload;
import com.fiveoneofly.cgw.statistics.BuriedStatistics;
import com.fiveoneofly.cgw.third.megvii.AuthUtil;
import com.fiveoneofly.cgw.third.megvii.util.Util;
import com.fiveoneofly.cgw.utils.DateUtil;
import com.fiveoneofly.cgw.utils.DialogUtil;
import com.fiveoneofly.cgw.utils.LoadingUtil;
import com.megvii.idcardquality.bean.IDCardAttr;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.OnClick;

import static android.os.Build.VERSION_CODES.M;

/**
 * 1、正面、反面图片不允许重拍
 * 2、1003接口调用失败后允许重拍
 * 3、正反面图片都上传成功 * 且两次1002都调用成功后 * 方可提交并调用1003
 * 4、1003调用成功后，更新用户缓存信息「实名状态、姓名、身份证号」
 */
public class IdCardVerifyActivity extends BasicActivity {

    @BindView(R.id.ic_card_verify_front)
    RelativeLayout takeFront;
    @BindView(R.id.ic_card_verify_back)
    RelativeLayout takeBack;
    @BindView(R.id.id_card_verify_submit)
    Button submit;

    private int mSide;
    private Bitmap idcardBmp;

    private static final int INTO_IDCARDSCAN_PAGE = 101;
    private static final int EXTERNAL_STORAGE_REQ_CAMERA_CODE = 102;

    private final String P1 = "P1";//身份证正面图片标识
    private final String P2 = "P2";//身份证反面图片标识
    private final String P5 = "P5";//身份证正面人像图片标识

    private String certName;// 仅正面有值
    private String certNo;// 仅正面有值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card_verify);
        setStatusBarColor(R.color.colorPrimary);
    }

    @OnClick({R.id.ic_card_verify_front, R.id.ic_card_verify_back, R.id.id_card_verify_submit})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ic_card_verify_front:
                netAuth(0);
                break;
            case R.id.ic_card_verify_back:
                if (isBackEnable() && isFrontEnable())
                    Toast.makeText(this, R.string.idcard_first_front, Toast.LENGTH_SHORT).show();
                else
                    netAuth(1);
                break;
            case R.id.id_card_verify_submit:
                if (isFrontEnable())
                    Toast.makeText(this, R.string.idcard_first_front, Toast.LENGTH_SHORT).show();
                else if (isBackEnable())
                    Toast.makeText(this, R.string.idcard_first_back, Toast.LENGTH_SHORT).show();
                else
                    idCardInfoConfirm(certName, certNo);
                break;
            default:
                break;
        }
    }

    private void upload(final boolean isFront, final File[] files) {

        FileUpload.Builder builder = new FileUpload.Builder().custId(UserManage.get(this).custId());

        if (builder != null) {
            LoadingUtil.display(this);
            builder.files(files).upload(new FileCallBack() {
                @Override
                public void onSucces(String result) {
                    super.onSucces(result);
                    String picType;
                    String certFilePath;
                    String certPortraitFilePath;
                    if (isFront) {
                        String[] filePathAfterSplit = new String[2];
                        StringTokenizer token = new StringTokenizer(result, ",");
                        while (token.hasMoreTokens()) {//校验字符串正确
                            String path = token.nextToken();
                            if (path.contains(P1)) {
                                filePathAfterSplit[0] = path;
                            } else if (path.contains(P5)) {
                                filePathAfterSplit[1] = path;
                            }
                        }
                        picType = P1;
                        certFilePath = filePathAfterSplit[0];
                        certPortraitFilePath = filePathAfterSplit[1];
                        buringPoint(true,true);

                    } else {
                        picType = P2;
                        certFilePath = result;
                        certPortraitFilePath = null;
                        buringPoint(false,true);

                    }
                    send(picType, certFilePath, certPortraitFilePath);
                }

                @Override
                public void onFailure(String errorCode, String errorMsg) {
                    super.onFailure(errorCode, errorMsg);
                    if (isFront) {
                        buringPoint(true,false);
                        Toast.makeText(IdCardVerifyActivity.this, R.string.idcard_front_up_fail, Toast.LENGTH_SHORT).show();
                    } else {
                        buringPoint(false,false);
                        Toast.makeText(IdCardVerifyActivity.this, R.string.idcard_back_up_fail, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (!isFront) {
                        for (File file : files) {
                            if (null != file) {
                                Util.deleteTempFile(file.getAbsolutePath());
                            }
                        }
                    }
                    LoadingUtil.dismiss();
                }
            });
        }
    }

    private void send(final String picType,
                      String certFilePath,
                      String certPortraitFilePath) {

        IdcardVerifyRequest request = new IdcardVerifyRequest();
        request.setCustId(UserManage.get(this).custId());
        request.setMobileNo(UserManage.get(this).phoneNo());
        request.setCertFront(picType.equals(P1) ? certFilePath : "");
        request.setCertFrontPortrait(picType.equals(P1) ? certPortraitFilePath : "");
        request.setCertBack(picType.equals(P2) ? certFilePath : "");
        request.setPicType(picType);
        new ApiRealCall(this, ServiceCode.IDCARD_VERIFY).request(request, IdcardVerifyResponse.class, new ApiCallback<IdcardVerifyResponse>() {
            @Override
            public void onSuccess(IdcardVerifyResponse response) {

                if (picType.equals(P1)) {
                    Map<String, String> map = response.getMap();
                    certName = map.get("certName");
                    certNo = map.get("certNo");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        takeFront.setBackground(new BitmapDrawable(getResources(), idcardBmp));
                    } else {
                        takeFront.setBackgroundDrawable(new BitmapDrawable(idcardBmp));
                    }
                    setFrontEnable(false);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        takeBack.setBackground(new BitmapDrawable(getResources(), idcardBmp));
                    } else {
                        takeBack.setBackgroundDrawable(new BitmapDrawable(idcardBmp));
                    }
                    setBackEnable(false);
                }
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                Toast.makeText(IdCardVerifyActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 埋点
     */
    private void buringPoint(boolean isFront,boolean isSuccess){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("身份证");
        stringBuilder.append(isFront?"正":"反");
        stringBuilder.append("面上传影响系统");
        stringBuilder.append(isSuccess?"成功":"失败");

        new BuriedStatistics.Builder().addContext(IdCardVerifyActivity.this)
                .addOperElementName("")
                .addOperElementType("button")
                .addOperPageName("身份证验证页面")
                .addErrorInfo(stringBuilder.toString())
                .addSessionId("")
                .build();
    }

    private void netAuth(final int side) {
        AuthUtil.netOcrAuth(IdCardVerifyActivity.this, new AuthUtil.FaceAuthListener() {
            @Override
            public void authSucces() {
                requestCameraPerm(side);
            }
        });
    }

    private void requestCameraPerm(int side) {
        mSide = side;
        if (Build.VERSION.SDK_INT >= M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        EXTERNAL_STORAGE_REQ_CAMERA_CODE);
            } else {
                startOcr(side);
            }
        } else {
            startOcr(side);
        }
    }

    private void startOcr(int side) {
        Intent intent = new Intent(IdCardVerifyActivity.this, MegviiOcr2Activity.class);
        intent.putExtra("side", side);
        startActivityForResult(intent, INTO_IDCARDSCAN_PAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == EXTERNAL_STORAGE_REQ_CAMERA_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted
                DialogUtil.displayForActivity(this, "获取相机权限失败");
            } else {
                startOcr(mSide);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTO_IDCARDSCAN_PAGE && resultCode == RESULT_OK) {

            IDCardAttr.IDCardSide idcardSide = data.getIntExtra("side", 0) == 0
                    ? IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT
                    : IDCardAttr.IDCardSide.IDCARD_SIDE_BACK;

            byte[] idcardImgData = data.getByteArrayExtra("idcardImg");
            idcardBmp = BitmapFactory.decodeByteArray(idcardImgData, 0, idcardImgData.length);

            if (idcardSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {
                byte[] portraitImgData = data.getByteArrayExtra("portraitImg");
                Bitmap portraitImgsBmp = BitmapFactory.decodeByteArray(portraitImgData, 0, portraitImgData.length);


                File[] files = new File[2];
                files[0] = Util.bmp2File(idcardBmp, P1 + "_" + DateUtil.getDate() + ".jpg");//扣出图像中身份证的部分。
                files[1] = Util.bmp2File(portraitImgsBmp, P5 + "_" + DateUtil.getDate() + ".jpg");//扣出身份证中人脸的部分。
                upload(true, files);
            } else {
                File[] files = new File[1];
                files[0] = Util.bmp2File(idcardBmp, P2 + "_" + DateUtil.getDate() + ".jpg");//扣出图像中身份证的部分。
                upload(false, files);
            }
        }
    }

    @Override
    protected View onNavigateBar() {
        return new NavigateBarBlue.Builder(this)
                .setTitle(getString(R.string.nav_id_card))
                .setTitleSize(17)
                .getView();
    }

    private void idCardInfoConfirm(String icName, String icNumber) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();

        View view = LayoutInflater.from(this).inflate(R.layout.layout_id_card_confirm, null);
        TextView idCardName = view.findViewById(R.id.idcard_confirm_name);
        TextView idCardNumber = view.findViewById(R.id.idcard_confirm_number);
        idCardName.setText(icName);
        idCardNumber.setText(icNumber);
        view.findViewById(R.id.idcard_confirm_sure)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Map<String, String> request = new HashMap<>();
                        request.put("custId", UserManage.get(IdCardVerifyActivity.this).custId());
                        request.put("mobileNo", UserManage.get(IdCardVerifyActivity.this).phoneNo());
                        request.put("certNo", certNo);
                        new ApiRealCall(IdCardVerifyActivity.this, ServiceCode.IDCARD_VERIFY_CONFIRM)
                                .request(request, BasicResponse.class, new ApiCallback<BasicResponse>() {
                                    @Override
                                    public void onSuccess(BasicResponse response) {
                                        startActivity(new Intent(IdCardVerifyActivity.this, MainActivity.class));
                                        IdCardVerifyActivity.this.finish();

                                        // 更新用户信息缓存
                                        UserManage userManage = UserManage.get(IdCardVerifyActivity.this);
                                        LoginInResponse user = userManage.getUser();
                                        user.setUserName(certName);
                                        user.setCustCert(certNo);
                                        user.setIdentityResult("Y");
                                        userManage.loginIn(user);

                                        dialog.dismiss();
                                        new BuriedStatistics.Builder().addContext(IdCardVerifyActivity.this)
                                                .addOperElementName("提交")
                                                .addOperElementType("button")
                                                .addOperPageName("身份证验证页面")
                                                .addErrorInfo("身份验证确定上传成功")
                                                .addSessionId("")
                                                .build();

                                    }

                                    @Override
                                    public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                                        Toast.makeText(IdCardVerifyActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        setFrontEnable(true);
                                        setBackEnable(true);
                                        takeFront.setBackgroundResource(R.drawable.id_card_bg);
                                        takeBack.setBackgroundResource(R.drawable.id_card_bg);
                                        new BuriedStatistics.Builder().addContext(IdCardVerifyActivity.this)
                                                .addOperElementName("提交")
                                                .addOperElementType("button")
                                                .addOperPageName("身份证验证页面")
                                                .addErrorInfo("身份验证确定上传失败")
                                                .addSessionId("")
                                                .build();
                                    }
                                });

                    }
                });

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null)
            window.setContentView(view);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //点击完返回键，执行的动作
            moveTaskToBack(false);
            return true;
        }
        return true;
    }
    private void setFrontEnable(boolean enable) {
        takeFront.setEnabled(enable);
    }

    private void setBackEnable(boolean enable) {
        takeBack.setEnabled(enable);
    }

    private boolean isFrontEnable() {
        return takeFront.isEnabled();
    }

    private boolean isBackEnable() {
        return takeBack.isEnabled();
    }
}
