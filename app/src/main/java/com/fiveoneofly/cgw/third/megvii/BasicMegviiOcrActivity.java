package com.fiveoneofly.cgw.third.megvii;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fiveoneofly.cgw.statistics.BuriedStatistics;
import com.fiveoneofly.cgw.third.megvii.util.MegviiCamera;
import com.fiveoneofly.cgw.third.megvii.util.MegviiUtil;
import com.fiveoneofly.cgw.third.megvii.util.Util;
import com.fiveoneofly.cgw.third.megvii.widget.IDCardNewIndicator;
import com.fiveoneofly.cgw.utils.DialogUtil;
import com.megvii.idcardquality.IDCardQualityAssessment;
import com.megvii.idcardquality.IDCardQualityResult;
import com.megvii.idcardquality.bean.IDCardAttr;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


public class BasicMegviiOcrActivity extends Activity implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {

    public TextureView mTextureView;// 照相机预览控件
    public IDCardNewIndicator mIDCardNewIndicator;// 自定义的UI-身份证框
    public TextView mPrompt;// 错误提示
    public MegviiCamera mICamera;// 照相机工具类
    private IDCardQualityAssessment mIdCardQualityAssessment = null;// 身份证质量检测类
    protected IDCardAttr.IDCardSide mSide;// 身份证的正反面
    protected OcrServer mOcrServer;
    private DecodeThread mDecoder = null;

    private boolean mHasSurface = false;// SurfaceTexture是否有效
    private BlockingQueue<byte[]> mFrameDataQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
        setContentView(com.yxjr.credit.R.layout.yxjr_credit_face_idcard);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            mICamera = new MegviiCamera();
            mFrameDataQueue = new LinkedBlockingDeque<byte[]>(1);

            OcrUI ocrUI = new OcrUI();
            ocrUI.findView(this);
            ocrUI.initializeLayout(this, mSide);

            if (mDecoder == null) {
                mDecoder = new DecodeThread();
            }
            if (!mDecoder.isAlive()) {
                mDecoder.start();
            }

            initializeOcr();
        }
    }

    // 开始扫描
    private void initializeOcr() {
        // 参数配置
        mIdCardQualityAssessment = new IDCardQualityAssessment.Builder()
//                .setClear(0)//判断身份证是否清晰的阈值。默认值为0.5
//                .setInBound(0)//判断身份证是否在引导框内的阈值。默认值为0.5
//                .setIsIdcard(0)//判断拍摄的是否为身份证的阈值。默认值为0.5
//                .setIsIgnoreShadow(true)//是否忽略阴影判断，如果为真则表示忽略阴影。默认为假
//                .setIsIgnoreHighlight(false)//是否忽略光斑判断，如果为真则表示忽略光斑。默认为假
                .build();
        boolean initSuccess = mIdCardQualityAssessment.init(this, MegviiUtil.readModel(this, com.yxjr.credit.R.raw.face_model_idcardquality));
        if (!initSuccess) {
            DialogUtil.displayForActivity(this, "检测器初始化失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mDecoder != null) {
                mDecoder.interrupt();
                mDecoder.join();
                mDecoder = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mIdCardQualityAssessment != null) {
            mIdCardQualityAssessment.release();
            mIdCardQualityAssessment = null;
        }
        mICamera.closeCamera();
    }

    private void doPreview() {
        if (!mHasSurface) {
            return;
        }
        mICamera.startPreview(mTextureView.getSurfaceTexture());
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        Camera camera = mICamera.openCamera(this, MegviiCamera.REAR_CAMERA);
        if (camera != null) {
            RelativeLayout.LayoutParams layout_params = mICamera.getLayoutParam(this);
            mTextureView.setLayoutParams(layout_params);
            mIDCardNewIndicator.setLayoutParams(layout_params);
        } else {
            DialogUtil.displayForActivity(this, "打开摄像头失败");
        }

        mHasSurface = true;
        doPreview();

        mICamera.actionDetect(this);// 开始检测并setPreviewCallback

        mIDCardNewIndicator.startScanAnimator();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mICamera.closeCamera();
        mHasSurface = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        mFrameDataQueue.offer(data);
    }

    private class DecodeThread extends Thread {
        boolean mHasSuccess = false;
        private IDCardQualityResult.IDCardFailedType mLstErrType;

        @Override
        public void run() {
            byte[] imgData;
            try {
                while ((imgData = mFrameDataQueue.take()) != null) {
                    if (mHasSuccess) {
                        return;
                    }
                    int imageWidth = mICamera.mCameraWidth;
                    int imageHeight = mICamera.mCameraHeight;
                    RectF rectF = mIDCardNewIndicator.getPosition();// 没有考虑竖屏
                    Rect roi = new Rect();
                    roi.left = (int) (rectF.left * imageWidth);
                    roi.top = (int) (rectF.top * imageHeight);
                    roi.right = (int) (rectF.right * imageWidth);
                    roi.bottom = (int) (rectF.bottom * imageHeight);
                    if (!isEven01(roi.left))
                        roi.left = roi.left + 1;
                    if (!isEven01(roi.top))
                        roi.top = roi.top + 1;
                    if (!isEven01(roi.right))
                        roi.right = roi.right - 1;
                    if (!isEven01(roi.bottom))
                        roi.bottom = roi.bottom - 1;

                    final IDCardQualityResult result = mIdCardQualityAssessment.getQuality(
                            imgData,
                            imageWidth,
                            imageHeight,
                            mSide,
                            roi);

                    if (result.isValid()) {// 是否合格
                        mHasSuccess = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mIDCardNewIndicator.stopScanAnimator();
                                handleSuccess(result);

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<IDCardQualityResult.IDCardFailedType> failTypes = result.fails;
                                if (failTypes != null) {

                                    IDCardQualityResult.IDCardFailedType errType = result.fails.get(0);
                                    if (errType != mLstErrType) {
                                        mPrompt.setText(Util.errorType2HumanStr(result.fails.get(0), mSide));
                                        String message=mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT?"身份证正面":"身份证反面";
                                        new BuriedStatistics.Builder().addContext(BasicMegviiOcrActivity.this)
                                                .addOperElementName(message+"拍照")
                                                .addOperElementType("button")
                                                .addOperPageName("身份证拍照页面")
                                                .addErrorInfo(message+Util.errorType2HumanStr(result.fails.get(0), mSide))
                                                .addSessionId("")
                                                .build();
//                                        mOcrServer.showError(mSide, Util.errorType2HumanStr(result.fails.get(0), mSide), "");
                                        mLstErrType = errType;
                                    }
                                }
                            }
                        });
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 取余运算
        private boolean isEven01(int num) {
            if (num % 2 == 0) {
                return true;
            } else {
                return false;
            }
        }

    }


    protected void handleSuccess(IDCardQualityResult result) {
    }

}