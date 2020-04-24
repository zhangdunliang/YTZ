//package com.yxjr.credit.fragment;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.text.Editable;
//import android.text.InputFilter;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.yxjr.credit.R;
//import com.yxjr.credit.WebActivity;
//import com.yxjr.credit.constants.ActivityCode;
//import com.yxjr.credit.constants.BridgeCode;
//import com.yxjr.credit.constants.HttpService;
//import com.yxjr.credit.constants.YxjrParam;
//import com.yxjr.credit.fragment.deprecated.YxBmpFactory;
//import com.yxjr.credit.fragment.deprecated.YxPictureUtil;
//import com.yxjr.credit.net.HttpRequest;
//import com.yxjr.credit.net.RequestCallBack;
//import com.yxjr.credit.net.upload.FileCallBack;
//import com.yxjr.credit.net.upload.FileUpload;
//import com.yxjr.credit.third.event.InvokeJsEvent;
//import com.yxjr.credit.utils.DataUtil;
//import com.yxjr.credit.utils.EmojiFilterUtil;
//import com.yxjr.credit.utils.LoadingUtil;
//import com.yxjr.credit.utils.StringUtil;
//import com.yxjr.credit.utils.ToastUtil;
//import com.yxjr.credit.widget.WheelDate;
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
//import java.util.Calendar;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.regex.PatternSyntaxException;
//
//import static android.view.View.inflate;
//
///**
// * Created by xiaochangyou on 2018/4/18.
// * <p>
// * 旧版本-添加车产
// */
//public class CarFragment extends Fragment implements WebActivity.IAssetFragment {
//
//    // * 返回按钮
//    private RelativeLayout carBack;
//    // *标题
//    private TextView carTitle;
//    // *车辆型号
//    private EditText carType;
//    // *车牌号
//    private EditText carPlate;
//    // *发动机编号
//    private EditText carEngineNum;
//    // *添加行驶证注册日期
//    private RelativeLayout carDLRegisterDateRl;
//    // *行驶证注册日期
//    private TextView carDLRegisterDate;
//    // *添加行驶证发证日期
//    private RelativeLayout carDLSendDateRl;
//    // *行驶证发证日期
//    private TextView carDLSendDate;
//    // *添加行驶证照片按钮
//    private ImageView carCredentialPicBtn;
//    // *显示行驶证照片和重拍整块
//    private FrameLayout carCredentialPicFl;
//    // *显示行驶证照片
//    private ImageView carCredentialPic;
//    // *重拍行驶证照片
//    private TextView carCredentialPic_Again;
//    // *提交车产信息
//    private Button carSubmit;
//    // *个人车辆信息
//    private TextView carText1;
//    // *行驶证照片
//    private TextView carText2;
//    // *信息一经提交则无法更改，请仔细核对，以免影响您的申请
//    private TextView carText3;
//
//    private String carDLRegisterDate_text = "行驶证注册日期";
//    private String carDLSendDate_text = "行驶证发证日期";
//
//    private Activity mActivity;
//    private String certId;
//    private String categoryCode;
//    public YxBmpFactory bmpFactory;
//    private String carBmpPath = null;
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
//        View view = inflate(mActivity, R.layout.yxjr_credit_fragment_asset_car, null);
////        layout.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
//        carBack = view.findViewById(R.id.yx_credit_asset_car_Back);
//        carTitle = view.findViewById(R.id.yx_credit_asset_car_Title);
//        carTitle.setText("添加车产");
//        carType = view.findViewById(R.id.yx_credit_asset_car_Type);
//        carType.setHint("输入车辆型号");
//        carType.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
//        carPlate = view.findViewById(R.id.yx_credit_asset_car_Plate);
//        carPlate.setHint("输入车牌号");
//        carPlate.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
//        carEngineNum = view.findViewById(R.id.yx_credit_asset_car_EngineNum);
//        carEngineNum.setHint("输入发动机编号");
//        carEngineNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
//        carDLRegisterDateRl = view.findViewById(R.id.yx_credit_asset_car_DLRegisterDate_rl);
//        carDLRegisterDate = view.findViewById(R.id.yx_credit_asset_car_DLRegisterDate);
//        carDLRegisterDate.setText(carDLRegisterDate_text);
//        carDLSendDateRl = view.findViewById(R.id.yx_credit_asset_car_DLSendDate_rl);
//        carDLSendDate = view.findViewById(R.id.yx_credit_asset_car_DLSendDate);
//        carDLSendDate.setText(carDLSendDate_text);
//        carCredentialPicBtn = view.findViewById(R.id.yx_credit_asset_car_CredentialPic_btn);
//        carCredentialPicBtn.setImageResource(R.drawable.yx_credit_photograph_btn);
//        carCredentialPicFl = view.findViewById(R.id.yx_credit_asset_car_CredentialPic_fl);
//        carCredentialPicFl.setVisibility(View.GONE);
//        carCredentialPic = view.findViewById(R.id.yx_credit_asset_car_CredentialPic);
//        carCredentialPic_Again = view.findViewById(R.id.yx_credit_asset_car_CredentialPic_Again);
//        carSubmit = view.findViewById(R.id.yx_credit_asset_car_Submit);
//        carSubmit.setText("提交");
//        carText1 = view.findViewById(R.id.yx_credit_asset_car_Text1);
//        carText1.setText("个人车辆信息");
//        carText2 = view.findViewById(R.id.yx_credit_asset_car_Text2);
//        carText2.setText("行驶证照片");
//        carText3 = view.findViewById(R.id.yx_credit_asset_car_Text3);
//        carText3.setText("*信息一经提交则无法更改，请仔细核对，以免影响您的申请");
//
//
//        setEditTextWatcher();
//        setEditOnFocusChange();
//        setOnClick();
////        setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
//
//        bmpFactory = new YxBmpFactory(mActivity);
//
//        return view;
//    }
//
//    private String stringFilter(String str) throws PatternSyntaxException {
//        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";// 只允许字母、数字和汉字
//        Pattern p = Pattern.compile(regEx);
//        Matcher m = p.matcher(str);
//        return m.replaceAll("").trim();
//    }
//
//    private void setEditTextWatcher() {
//        carEngineNum.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String editableCarEngineNum = carEngineNum.getText().toString();
//                String strCarEngineNum = stringFilter(editableCarEngineNum.toString());
//                if (!editableCarEngineNum.equals(strCarEngineNum)) {
//                    carEngineNum.setText(strCarEngineNum);
//                    carEngineNum.setSelection(strCarEngineNum.length());// 设置新的光标所在位置
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//        carPlate.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String editableCarPlate = carPlate.getText().toString();
//                String strCarPlate = stringFilter(editableCarPlate.toString());
//                if (!editableCarPlate.equals(strCarPlate)) {
//                    carPlate.setText(strCarPlate);
//                    carPlate.setSelection(strCarPlate.length());// 设置新的光标所在位置
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//        carType.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String editableCarType = carType.getText().toString();
//                String strCarType = stringFilter(editableCarType.toString());
//                if (!editableCarType.equals(strCarType)) {
//                    carType.setText(strCarType);
//                    carType.setSelection(strCarType.length());// 设置新的光标所在位置
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//    }
//
//    private void setEditOnFocusChange() {
//        carEngineNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {// 没得到焦点时
//                    carEngineNum.clearFocus();// 失去焦点
//                    InputMethodManager mInputMethodManager = (InputMethodManager) mActivity
//                            .getSystemService(Context.INPUT_METHOD_SERVICE);
//                    mInputMethodManager.hideSoftInputFromWindow(carEngineNum.getWindowToken(), 0);
//                }
//            }
//        });
//        carPlate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {// 没得到焦点时
//                    carPlate.clearFocus();// 失去焦点
//                    InputMethodManager mInputMethodManager = (InputMethodManager) mActivity
//                            .getSystemService(Context.INPUT_METHOD_SERVICE);
//                    mInputMethodManager.hideSoftInputFromWindow(carPlate.getWindowToken(), 0);
//                }
//            }
//        });
//        carType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {// 没得到焦点时
//                    carType.clearFocus();// 失去焦点
//                    InputMethodManager mInputMethodManager = (InputMethodManager) mActivity
//                            .getSystemService(Context.INPUT_METHOD_SERVICE);
//                    mInputMethodManager.hideSoftInputFromWindow(carType.getWindowToken(), 0);
//                }
//            }
//        });
//    }
//
//    private void setOnClick() {
//        carBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                mCallBack.removeAssetCar();
//                mActivity.onBackPressed();
//            }
//        });
//        carDLRegisterDateRl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                if (imm.hideSoftInputFromWindow(carType.getWindowToken(), 0)) {// 软键盘已弹出
//                    imm.showSoftInput(carType, 0);
//                }
//                if (imm.hideSoftInputFromWindow(carEngineNum.getWindowToken(), 0)) {// 软键盘已弹出
//                    imm.showSoftInput(carEngineNum, 0);
//                }
//                if (imm.hideSoftInputFromWindow(carPlate.getWindowToken(), 0)) {// 软键盘已弹出
//                    imm.showSoftInput(carPlate, 0);
//                }
//                WheelDate mChangeBirthDialog = new WheelDate(mActivity);
//                Calendar c = Calendar.getInstance();
//                mChangeBirthDialog.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
//                        c.get(Calendar.DAY_OF_MONTH));
//                mChangeBirthDialog.show();
//                mChangeBirthDialog.setSelectDateListener(new WheelDate.OnSelectDateListener() {
//                    @Override
//                    public void onClick(String year, String month, String day) {
//                        carDLRegisterDate.setText(year + "-" + month + "-" + day);
//                    }
//                });
//            }
//        });
//        carDLSendDateRl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                if (imm.hideSoftInputFromWindow(carType.getWindowToken(), 0)) {// 软键盘已弹出
//                    imm.showSoftInput(carType, 0);
//                }
//                if (imm.hideSoftInputFromWindow(carEngineNum.getWindowToken(), 0)) {// 软键盘已弹出
//                    imm.showSoftInput(carEngineNum, 0);
//                }
//                if (imm.hideSoftInputFromWindow(carPlate.getWindowToken(), 0)) {// 软键盘已弹出
//                    imm.showSoftInput(carPlate, 0);
//                }
//                WheelDate mChangeBirthDialog = new WheelDate(mActivity);
//                Calendar c = Calendar.getInstance();
//                mChangeBirthDialog.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
//                        c.get(Calendar.DAY_OF_MONTH));
//                mChangeBirthDialog.show();
//                mChangeBirthDialog.setSelectDateListener(new WheelDate.OnSelectDateListener() {
//
//                    @Override
//                    public void onClick(String year, String month, String day) {
//                        carDLSendDate.setText(year + "-" + month + "-" + day);
//                    }
//                });
//            }
//        });
//        carCredentialPicBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bmpFactory.openCamera(ActivityCode.Request.CAR_CREDENTIAL);
//            }
//        });
//        carCredentialPic_Again.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bmpFactory.openCamera(ActivityCode.Request.CAR_CREDENTIAL);
//            }
//        });
//        carSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitCredential();
//            }
//        });
//    }
//
////    public void setCarCredentialPic(String carBmpPath) {
////        if (carBmpPath != null) {
////            this.carBmpPath = carBmpPath;
////            carCredentialPic.setImageBitmap(YxPictureUtil.getSmallBitmap(carBmpPath));
////            carCredentialPic_Again.setText("点击重拍");
////            carCredentialPicBtn.setVisibility(View.GONE);
////            carCredentialPicFl.setVisibility(View.VISIBLE);
////        }
////        carCredentialPic.setScaleType(ImageView.ScaleType.FIT_XY);// 填充图片
////    }
//
//    @Override
//    public YxBmpFactory getBmp() {
//        return bmpFactory;
//    }
//
//    @Override
//    public void setPath(int type, String carBmpPath) {
//        if (carBmpPath != null) {
//            this.carBmpPath = carBmpPath;
//            carCredentialPic.setImageBitmap(YxPictureUtil.getSmallBitmap(carBmpPath));
//            carCredentialPic_Again.setText("点击重拍");
//            carCredentialPicBtn.setVisibility(View.GONE);
//            carCredentialPicFl.setVisibility(View.VISIBLE);
//        }
//        carCredentialPic.setScaleType(ImageView.ScaleType.FIT_XY);// 填充图片
//    }
//
//    @Override
//    public Fragment getFragment() {
//        return this;
//    }
//
//    private void submitCredential() {
//        if (StringUtil.isEmpty(carType.getText().toString())) {
//            ToastUtil.showToast(mActivity, "请填写车辆型号");
//            return;
//        }
//        if (StringUtil.isEmpty(carPlate.getText().toString())) {
//            ToastUtil.showToast(mActivity, "请填写车牌号");
//            return;
//        }
//        if (StringUtil.isEmpty(carEngineNum.getText().toString())) {
//            ToastUtil.showToast(mActivity, "请填写发动机编号");
//            return;
//        }
//        if (EmojiFilterUtil.containsEmoji(carType.getText().toString())) {
//            ToastUtil.showToast(mActivity, "请输入合法数据[包含表情]");
//            return;
//        }
//        if (EmojiFilterUtil.containsEmoji(carPlate.getText().toString())) {
//            ToastUtil.showToast(mActivity, "请输入合法数据[包含表情]");
//            return;
//        }
//        if (EmojiFilterUtil.containsEmoji(carEngineNum.getText().toString())) {
//            ToastUtil.showToast(mActivity, "请输入合法数据[包含表情]");
//            return;
//        }
//        if (carDLRegisterDate.getText().toString().equals(carDLRegisterDate_text)) {
//            ToastUtil.showToast(mActivity, "请选择" + carDLRegisterDate_text);
//            return;
//        }
//        if (carDLSendDate.getText().toString().equals(carDLSendDate_text)) {
//            ToastUtil.showToast(mActivity, "请选择" + carDLSendDate_text);
//            return;
//        }
//        if (carCredentialPicBtn.getVisibility() == View.VISIBLE) {
//            ToastUtil.showToast(mActivity, "请添加行驶证照片");
//            return;
//        }
//        List<Bitmap> bmpList = new ArrayList<Bitmap>();
//        if (null != carBmpPath) {
//            bmpList.add(YxPictureUtil.getSmallBitmap(carBmpPath));
//        }
//
//        BufferedOutputStream bos = null;
//        FileOutputStream fos = null;
//        File[] files = new File[1];
//        try {
//            for (int i = 0; i < bmpList.size(); i++) {
//                String picName = YxjrParam.FileName.IMG_A4 + DataUtil.getCurrentTime2() + ".jpg";
//                files[i] = new File(YxPictureUtil.getAlbumDir(), picName);
//                fos = new FileOutputStream(files[i]);
//                bos = new BufferedOutputStream(fos);
//                bmpList.get(i).compress(Bitmap.CompressFormat.JPEG, YxjrParam.Common.PICTURE_COMPRESS, bos);// compress是压缩率,50表示压缩50%;如果不压缩是100,表示压缩率为0%
//                fos.flush();
//                bos.flush();
//            }
//        } catch (IOException e) {
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
//
//        final File[] uploadFiles = files;
////        final DialogLoading dialogLoading = new DialogLoading(mActivity, "上传中...");
////        dialogLoading.show();
////        new RequestEngine(mActivity).upload(certId, uploadFiles, new UploadCallBack() {
////            @Override
////            public void onSucces(String result) {
////                String carTypeValue = carType.getText().toString();
////                String carPlateValue = carPlate.getText().toString();
////                String carEngineNumValue = carEngineNum.getText().toString();
////                String carDLRegisterDateValue = carDLRegisterDate.getText().toString();
////                String carDLSendDateValue = carDLSendDate.getText().toString();
////                JSONObject params = new JSONObject();
////                try {
////                    params.put("carModel", carTypeValue);
////                    params.put("carNo", carPlateValue);
////                    params.put("engineNo", carEngineNumValue);
////                    params.put("vehicleRegisterDate", carDLRegisterDateValue);
////                    params.put("vehicleGrantDate", carDLSendDateValue);
////                    params.put("vehiclePhoto", result);
////                    params.put("categoryCode", categoryCode);
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
////                if (categoryCode.equals("")) {
////                    submitCarAsset(HttpConstant.Request.CAR_ASSET_APPROVE, params);
////                } else {
////                    submitCarAsset(HttpConstant.Request.CAR_ASSET_APPROVE_PATCH, params);
////                }
////                super.onSucces(result);
////            }
////
////            @Override
////            public void onFailure(String errorCode, String errorMsg) {
////                ToastUtil.showToast(mActivity, "行驶证上传失败！");
////                super.onFailure(errorCode, errorMsg);
////            }
////
////            @Override
////            public void onFinish() {
////                dialogLoading.cancel();
////                for (int i = 0; i < uploadFiles.length; i++) {
////                    if (uploadFiles[i].getAbsolutePath() != null) {
////                        YxPictureUtil.deleteTempFile(uploadFiles[i].getAbsolutePath());
////                    }
////                }
////                super.onFinish();
////            }
////
////            @Override
////            public void onProgress(long currentLength, long totalLength, int index) {
////                super.onProgress(currentLength, totalLength, index);
////            }
////        });
//
//        LoadingUtil.display(mActivity);
//        new FileUpload.Builder()
//                .appNo(certId)
//                .files(uploadFiles)
//                .upload(new FileCallBack() {
//                    @Override
//                    public void onSucces(String result) {
//                        super.onSucces(result);
//                        String carTypeValue = carType.getText().toString();
//                        String carPlateValue = carPlate.getText().toString();
//                        String carEngineNumValue = carEngineNum.getText().toString();
//                        String carDLRegisterDateValue = carDLRegisterDate.getText().toString();
//                        String carDLSendDateValue = carDLSendDate.getText().toString();
//                        JSONObject params = new JSONObject();
//                        try {
//                            params.put("carModel", carTypeValue);
//                            params.put("carNo", carPlateValue);
//                            params.put("engineNo", carEngineNumValue);
//                            params.put("vehicleRegisterDate", carDLRegisterDateValue);
//                            params.put("vehicleGrantDate", carDLSendDateValue);
//                            params.put("vehiclePhoto", result);
//                            params.put("categoryCode", categoryCode);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        if (StringUtil.isEmpty(categoryCode)) {
//                            submitCarAsset(HttpService.CAR_ASSET_APPROVE, params);
//                        } else {
//                            submitCarAsset(HttpService.CAR_ASSET_APPROVE_PATCH, params);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String errorCode, String errorMsg) {
//                        super.onFailure(errorCode, errorMsg);
//                        ToastUtil.showToast(mActivity, "行驶证上传失败！");
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        super.onFinish();
//                        for (int i = 0; i < uploadFiles.length; i++) {
//                            if (uploadFiles[i].getAbsolutePath() != null) {
//                                YxPictureUtil.deleteTempFile(uploadFiles[i].getAbsolutePath());
//                            }
//                        }
//                        LoadingUtil.dismiss();
//                    }
//                });
//    }
//
//    private void submitCarAsset(String serviceId, JSONObject carAsset) {
////        final DialogLoading dialogLoading = new DialogLoading(mActivity);
////        dialogLoading.show();
////        new RequestEngine(mActivity).execute(serviceId, carAsset, new RequestCallBack(mActivity) {
////            @Override
////            public void onSucces(String result) {
////                dialogLoading.cancel();
////                mCallBack.loadUrl(JsConstant.H5_REFRESH, null);
////                mCallBack.removeAssetCar();
////                ToastUtil.showToast(mActivity, "提交成功！");
////                YxLog.d("车产认证成功执行后！");
////                // 删除临时原图片
////                if (null != carBmpPath) {
////                    YxPictureUtil.deleteTempFile(carBmpPath);
////                }
////                super.onSucces(result);
////            }
////
////            @Override
////            public void onFailure(String errorCode, String errorMsg) {
////                dialogLoading.cancel();
////                super.onFailure(errorCode, errorMsg);
////            }
////        });
//
//        new HttpRequest.Builder(mActivity)
//                .serviceId(serviceId)
//                .params(carAsset)
//                .execute(new RequestCallBack(mActivity) {
//                    @Override
//                    public void onSucces(String result) {
////                        mCallBack.loadUrl(JsConstant.H5_REFRESH, null);
////                        mCallBack.removeAssetCar();
//                        EventBus.getDefault().post(new InvokeJsEvent(BridgeCode.H5_REFRESH, null));
//                        mActivity.onBackPressed();
//                        ToastUtil.showToast(mActivity, "提交成功！");
//                        // 删除临时原图片
//                        if (null != carBmpPath) {
//                            YxPictureUtil.deleteTempFile(carBmpPath);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String errorCode, String errorMsg) {
//                    }
//                });
//    }
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
//}
