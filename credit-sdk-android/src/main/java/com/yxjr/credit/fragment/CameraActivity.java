package com.yxjr.credit.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yxjr.credit.R;
import com.yxjr.credit.fragment.deprecated.OldCamera;
import com.yxjr.credit.fragment.deprecated.YxPictureUtil;
//import com.yxjr.credit.utils.DensityUtil;
//import com.yxjr.credit.utils.ToastUtil;

@Deprecated
public class CameraActivity extends Activity {

//    private String cameraXML = "yxjr_credit_camera";
//    private String textureID = "yx_credit_tv_texture";
//    private String previewID = "yx_credit_iv_preview";
//    private String cancelID = "yx_credit_tv_cancel";
//    private String selectID = "yx_credit_iv_select";
//    private String takeID = "yx_credit_iv_take";
//    private String againID = "yx_credit_iv_again";
//    private String changeID = "yx_credit_iv_change";
//
//    private String rlPreviewID = "yx_credit_rlPreview";
//    private String rlRightFunctionID = "yx_credit_rl_right";
//    private String rlCancelSelectID = "yxcredit_rl_cancel_select";
//    private String rlNoselectChangeID = "yx_credit_no_change";
//    private String rlAssistID = "yx_credit_assist";

    private TextureView mTextureView;
    private TextView mCancel;
    private ImageView mTake;
    private ImageView mAgain;
    private ImageView mSelect;
    private ImageView mPreview;
    private ImageView mChange;

    private String mFilePath = null;
    private String mPicName = null;

    private OldCamera mICamera;// 照相机工具类
    private int mCameraId = 0;// 0表示后置，1表示前置
    private boolean mIsVertical = false;//是否竖屏
    private boolean mIsChange;//是否可切换前后摄像头

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.yxjr_credit_camera);
        find();
        init();
    }

    private void find() {
        mSelect = (ImageView) findViewById(R.id.yx_credit_iv_select);
        mSelect.setVisibility(View.GONE);
        mSelect.setOnClickListener(clickListener);
        mAgain = (ImageView) findViewById(R.id.yx_credit_iv_again);
        mAgain.setVisibility(View.GONE);
        mAgain.setOnClickListener(clickListener);
        mTake = (ImageView) findViewById(R.id.yx_credit_iv_take);
        mTake.setOnClickListener(clickListener);
        mTextureView = (TextureView) findViewById(R.id.yx_credit_tv_texture);
        mTextureView.setSurfaceTextureListener(surfaceTextureListener);
        mTextureView.setOnClickListener(clickListener);
        mTextureView.setKeepScreenOn(true);
        mCancel = (TextView) findViewById(R.id.yx_credit_tv_cancel);
        mCancel.setOnClickListener(clickListener);
        mPreview = (ImageView) findViewById(R.id.yx_credit_iv_preview);
        mPreview.setScaleType(ScaleType.FIT_XY);
        mChange = (ImageView) findViewById(R.id.yx_credit_iv_change);
        mChange.setVisibility(View.GONE);

    }

    @SuppressLint("NewApi")
    private void init() {
        mPicName = getIntent().getStringExtra("picName");
        if (mPicName == null) {
            mPicName = "123456.jpg";
        }
        mIsVertical = getIntent().getBooleanExtra("isvertical", false);//默认横屏
        //这里需要切换为竖屏且重新写布局逻辑
        if (mIsVertical) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            RelativeLayout rlPreview = (RelativeLayout) findViewById(R.id.yx_credit_rlPreview);
            RelativeLayout rightFunction = (RelativeLayout) findViewById(R.id.yx_credit_rl_right);
            RelativeLayout cancelSelect = (RelativeLayout) findViewById(R.id.yxcredit_rl_cancel_select);
            RelativeLayout noselectChange = (RelativeLayout) findViewById(R.id.yx_credit_no_change);
            ImageView assist = (ImageView) findViewById(R.id.yx_credit_assist);

            //功能部分
            RelativeLayout.LayoutParams lprightFunction = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            lprightFunction.setMargins(0, DensityUtil.dp2Px(this, 30), 0, DensityUtil.dp2Px(this, 30));
            lprightFunction.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rightFunction.setLayoutParams(lprightFunction);
            //取消/选择
            RelativeLayout.LayoutParams lpcancelSelect = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            lpcancelSelect.rightMargin = DensityUtil.dp2Px(this, 30);
            lpcancelSelect.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lpcancelSelect.addRule(RelativeLayout.CENTER_VERTICAL);
            cancelSelect.setLayoutParams(lpcancelSelect);
            //不选择/切换
//            RelativeLayout.LayoutParams lpnoselectChange = new RelativeLayout.LayoutParams(DensityUtil.dp2Px(this, 30), DensityUtil.dp2Px(this, 30));
//            lpnoselectChange.leftMargin = DensityUtil.dp2Px(this, 30);
//            lpnoselectChange.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            lpnoselectChange.addRule(RelativeLayout.CENTER_VERTICAL);
//            noselectChange.setLayoutParams(lpnoselectChange);
            //预览部分
            RelativeLayout.LayoutParams lprlPreview = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lprlPreview.addRule(RelativeLayout.ABOVE, rightFunction.getId());
            lprlPreview.setMargins(0, 0, 0, 0);
            rlPreview.setLayoutParams(lprlPreview);
            //
            RelativeLayout.LayoutParams lpmPreview = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lpmPreview.setMargins(0, 0, 0, 0);
            mPreview.setLayoutParams(lpmPreview);
            //
            RelativeLayout.LayoutParams lpmTextureView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lpmTextureView.setMargins(0, 0, 0, 0);
            mTextureView.setLayoutParams(lpmTextureView);
            //
            int width = this.getWindowManager().getDefaultDisplay().getWidth();
            RelativeLayout.LayoutParams lpassist = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (width * 1.14));//辅助框高度为，宽度(屏幕宽度)*1.14，比例是图片的比例，防止变形
            lpassist.addRule(RelativeLayout.CENTER_VERTICAL);
            assist.setLayoutParams(lpassist);
            assist.setVisibility(View.VISIBLE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        mIsChange = getIntent().getBooleanExtra("isChange", false);
        if (mIsChange) {
            mChange.setVisibility(View.VISIBLE);
            mChange.setOnClickListener(clickListener);
        }
        mICamera = new OldCamera(mIsVertical);
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            int textureViewID = R.id.yx_credit_tv_texture;
            int cancelID = R.id.yx_credit_tv_cancel;
            int takeID = R.id.yx_credit_iv_take;
            int againID = R.id.yx_credit_iv_again;
            int selectID = R.id.yx_credit_iv_select;
            int changeID = R.id.yx_credit_iv_change;

            if (id == textureViewID) {//相机预览
                mICamera.autoFocus();
            } else if (id == cancelID) {//取消按钮
                CameraActivity.this.finish();
            } else if (id == takeID) {//拍照按钮
                if (!mWaitForTakePhoto) {
                    return;
                }
                if (mChange.getVisibility() == View.VISIBLE) {
                    mChange.setVisibility(View.GONE);
                }
                mTake.setVisibility(View.INVISIBLE);
                mICamera.takePicture(pictureCallback);
                mCancel.setVisibility(View.GONE);
                mSelect.setVisibility(View.VISIBLE);
                mAgain.setVisibility(View.VISIBLE);
            } else if (id == againID) {//取消重来(X按钮)
                if (mIsChange && mChange.getVisibility() == View.GONE) {
                    mChange.setVisibility(View.VISIBLE);
                }
                mAgain.setVisibility(View.GONE);
                mICamera.openCamera(CameraActivity.this, mCameraId);
                doPreview();
                mWaitForTakePhoto = true;
                mTake.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.VISIBLE);
                mSelect.setVisibility(View.GONE);
                if (mFilePath != null) {//取消删去原先拍的照片
                    YxPictureUtil.deleteTempFile(mFilePath);
                }
            } else if (id == selectID) {//选择(√按钮)
                if (mFilePath != null) {
                    setResult(Activity.RESULT_OK, new Intent().putExtra("picPath", mFilePath));
                    CameraActivity.this.finish();
                } else {
//                    ToastUtil.showToast(CameraActivity.this, "无法创建图片");//原因：1、手机没有存储空间；2、没有存储权限；3、其他异常
                }
            } else if (id == changeID) {
                int cameraCount = 0;
                CameraInfo cameraInfo = new CameraInfo();
                cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数
                for (int i = 0; i < cameraCount; i++) {
                    Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
                    if (mCameraId == 1) {
                        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {// 现在是后置，变更为前置
                            mICamera.closeCamera();//释放camera，方便其他应用调用
                            mCameraId = 0;
                            CameraActivity.this.onResume();
                            doPreview();
                            mWaitForTakePhoto = true;
                            break;
                        }
                    } else {
                        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {// 现在是前置， 变更为后置
                            mICamera.closeCamera();//释放camera，方便其他应用调用
                            mCameraId = 1;
                            CameraActivity.this.onResume();
                            doPreview();
                            mWaitForTakePhoto = true;
                            break;
                        }
                    }
                }
            }
        }
    };

    private SurfaceTextureListener surfaceTextureListener = new SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mHasSurface = false;
            return false;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mHasSurface = true;
            doPreview();
        }
    };

    boolean mWaitForTakePhoto = true;

    @Override
    protected void onResume() {
        super.onResume();
        Camera mCamera = mICamera.openCamera(this, mCameraId);
        if (mCamera != null) {
            //			RelativeLayout.LayoutParams layout_params = mICamera.getLayoutParam(this);
            //			mTextureView.setLayoutParams(layout_params);
        } else {
//            if (PermissionUtil.isCameraPer(this).equals(PermissionUtil.UNAUTHORIZED)) {//为授权，友情提示
//                mDialogUtil.showDialogForActivity("请开启相机权限!");
//            } else if (PermissionUtil.isCameraPer(this).equals(PermissionUtil.NOT_GET)) {//无法获取，友情提示
//                mDialogUtil.showDialogForActivity("打开摄像头失败！\n" + "\r\r\r\r失败原因可能有：\n" + "\r\r\r\r1、未开启相机权限；\n" + "\r\r\r\r2、相机异常！");
//            } else if (PermissionUtil.isCameraPer(this).equals(PermissionUtil.AUTHORIZED)) {//已授权，说明相机有问题
//                mDialogUtil.showDialogForActivity("打开摄像头失败！\n" + "\r\r\r\r失败原因可能有：\n" + "\r\r\r\r1、未开启相机权限；\n" + "\r\r\r\r2、相机异常！");
//            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mICamera.closeCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mICamera.closeCamera();
    }

    private void doPreview() {
        if (!mHasSurface) {
            return;
        }
        mICamera.startPreview(mTextureView.getSurfaceTexture());
    }

    private boolean mHasSurface = false;

    private PictureCallback pictureCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //			int imageWidth = mICamera.cameraWidth;
            //			int imageHeight = mICamera.cameraHeight;
            //			旋转照片、横屏不会旋转，竖屏旋转90度，但是旋转90度会报下标越界，因此干脆去除
            //			byte[] imgDate = RotaterUtil.rotate(data, imageWidth, imageHeight, mICamera.getCameraAngle(CameraActivity.this));
            //			mFilePath = mICamera.onTakePhoto(imgDate, mPreview, mPicName);
            mFilePath = mICamera.onTakePhoto(data, mPreview, mPicName);
            mWaitForTakePhoto = false;
        }
    };

}
