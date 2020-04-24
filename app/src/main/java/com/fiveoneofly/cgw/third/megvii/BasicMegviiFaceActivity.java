package com.fiveoneofly.cgw.third.megvii;

import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fiveoneofly.cgw.app.basic.BasicActivity;
import com.fiveoneofly.cgw.statistics.BuriedStatistics;
import com.fiveoneofly.cgw.third.megvii.util.IDetection;
import com.fiveoneofly.cgw.third.megvii.util.IMediaPlayer;
import com.fiveoneofly.cgw.third.megvii.util.MegviiCamera;
import com.fiveoneofly.cgw.third.megvii.util.MegviiUtil;
import com.fiveoneofly.cgw.third.megvii.util.SensorUtil;
import com.fiveoneofly.cgw.third.megvii.widget.CircleProgressBar;
import com.fiveoneofly.cgw.third.megvii.widget.FaceMask;
import com.fiveoneofly.cgw.utils.DialogUtil;
import com.fiveoneofly.cgw.utils.StringUtil;
import com.megvii.livenessdetection.DetectionConfig;
import com.megvii.livenessdetection.DetectionFrame;
import com.megvii.livenessdetection.Detector;
import com.megvii.livenessdetection.FaceQualityManager;
import com.megvii.livenessdetection.bean.FaceInfo;

import java.util.List;

/**
 * Created by xiaochangyou on 2018/5/15.
 */
public class BasicMegviiFaceActivity extends BasicActivity implements Camera.PreviewCallback, TextureView.SurfaceTextureListener, Detector.DetectionListener {

    public TextureView mCameraPreview;
    public FaceMask mFaceMask;// 画脸位置的类（调试时会用到）
    public ProgressBar mProgressBar;// 网络上传请求验证时出现的ProgressBar
    public RelativeLayout mRootView;// 根视图
    public TextView mTimeOutText;// 倒计时文字
    public CircleProgressBar mTimeOutBar;// 倒计时控件
    public TextView mPromptText;// 错误提示

    protected Detector mDetector;// 活体检测器
    private MegviiCamera mICamera;// 照相机工具类
    private Handler mHandler;
    private Handler mainHandler;
    private HandlerThread mHandlerThread = new HandlerThread("videoEncoder");
    protected IMediaPlayer mIMediaPlayer;// 多媒体工具类
    private IDetection mIDetection;// 实体验证工具类
    private FaceQualityManager mFaceQualityManager;
    private SensorUtil mSensorUtil;

    private int mCurStep = 0;// 检测动作的次数
    private int mFailFrame = 0;// 埋点十次一次
    private boolean mHasSurface = false;// TextureView是否可用
    private boolean isHandleStart;// 是否开始检测

    protected FaceUI mFaceUI;
    protected FaceServer mFaceServer;


//    private String mAppNo = "";
//    private String mCategoryCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.yxjr.credit.R.layout.yxjr_credit_face_liveness);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏锁定
        setStatusBarColor();

        mFaceUI = new FaceUI();
        mFaceUI.findView(this);

        mSensorUtil = new SensorUtil(this);
        mainHandler = new Handler();
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mIMediaPlayer = new IMediaPlayer(this);
        mIDetection = new IDetection(this, mRootView);
        mICamera = new MegviiCamera();

        mFaceUI.initializeLayout(this);

        // 初始化活体检测器
        DetectionConfig config = new DetectionConfig.Builder()
//                .setBlur(arg0, arg1)
                .build();//配置识别参数
        mDetector = new Detector(this, config);

        boolean initSuccess = mDetector.init(this, MegviiUtil.readModel(this, com.yxjr.credit.R.raw.meglivemodel), "");
        if (!initSuccess) {
            DialogUtil.displayForActivity(this, "检测器初始化失败");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isHandleStart = false;
        Camera mCamera = mICamera.openCamera(this, MegviiCamera.FRONT_CAMERA);
        if (mCamera != null) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(MegviiCamera.FRONT_CAMERA, cameraInfo);
            mFaceMask.setFrontal(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
            /*
             * 获取到相机分辨率对应的显示大小，并把这个值复制给camerapreview
             * 这版，没有使用相应比例，因为设计为全屏，但是魅族等其他手机不可能铺满，因此不是用比例
             * 如果碰到 人脸变形、拉伸，说明此原因，并重新设计，不能使用全屏。
             * 建议正方形、或圆形，
             */
            RelativeLayout.LayoutParams camera_layout_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            camera_layout_params.addRule(RelativeLayout.BELOW, mFaceUI.navBarId);
            camera_layout_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mCameraPreview.setLayoutParams(camera_layout_params);// 相机预览
            // 初始化人脸质量检测管理类
            mFaceQualityManager = new FaceQualityManager(1 - 0.5f, 0.5f);
            mIDetection.mCurShowIndex = -1;
        } else {
            DialogUtil.displayForActivity(this, "打开前置摄像头失败");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainHandler.removeCallbacksAndMessages(null);
        mICamera.closeCamera();
        mIMediaPlayer.close();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDetector != null)
            mDetector.release();
        mIDetection.onDestroy();
        mIMediaPlayer.close();
        release();
    }

    /**
     * 「Camera.PreviewCallback」
     * <p>
     * 照相机预览数据回调
     */
    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        Camera.Size previewsize = camera.getParameters().getPreviewSize();

        // 活体检测器检测
        mDetector.doDetection(
                data,
                previewsize.width,
                previewsize.height,
                360 - mICamera.getCameraAngle(this));
    }

    /**
     * 「TextureView.SurfaceTextureListener」
     * TextureView启动成功后 启动相机预览和添加活体检测回调
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mHasSurface = true;

        doPreview();

        mDetector.setDetectionListener(this);// 添加活体检测回调「DetectionListener」
        mICamera.actionDetect(this);// 添加相机预览回调「PreviewCallback」
    }

    /**
     * 「TextureView.SurfaceTextureListener」
     */
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    /**
     * 「TextureView.SurfaceTextureListener」
     * TextureView销毁后
     */
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mHasSurface = false;
        return false;
    }

    /**
     * 「TextureView.SurfaceTextureListener」
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    /**
     * 「Detector.DetectionListener」
     * <p>
     * 活体验证成功
     */
    @Override
    public Detector.DetectionType onDetectionSuccess(final DetectionFrame validFrame) {
        mIMediaPlayer.reset();
        mCurStep++;
        mFaceMask.setFaceInfo(null);

        if (mCurStep == mIDetection.mDetectionSteps.size()) {
            handleResult(com.yxjr.credit.R.string.face_verify_success, com.yxjr.credit.R.raw.face_meglive_success);
            new BuriedStatistics.Builder().addContext(BasicMegviiFaceActivity.this)
                    .addOperElementName("")
                    .addOperElementType("button")
                    .addOperPageName("人脸识别页面")
                    .addErrorInfo("活体检测成功")
                    .addSessionId("")
                    .build();
        } else {
            changeType(mIDetection.mDetectionSteps.get(mCurStep));
        }

        // 检测器返回值：
        // 如果 不希望 检测器检测动作；则返回 DetectionType.DONE
        // 如果 希望 检测器检测动作；则返回 要检测的动作
        return mCurStep >= mIDetection.mDetectionSteps.size()
                ?
                Detector.DetectionType.DONE
                :
                mIDetection.mDetectionSteps.get(mCurStep);
    }

    /**
     * 「Detector.DetectionListener」
     * 活体检测失败
     */
    @Override
    public void onDetectionFailed(final Detector.DetectionFailedType type) {
        int resID = com.yxjr.credit.R.string.face_liveness_detection_failed;
        int rawID = com.yxjr.credit.R.raw.face_meglive_failed;
        switch (type) {
            case ACTIONBLEND:// 混入了其他不应该出现的动作
                resID = com.yxjr.credit.R.string.face_liveness_detection_failed_action_blend;
                break;
            case NOTVIDEO:// 使用非连续的图像进行活体攻击
                resID = com.yxjr.credit.R.string.face_liveness_detection_failed_not_video;
                break;
            case TIMEOUT:// 检测超时
                resID = com.yxjr.credit.R.string.face_liveness_detection_failed_timeout;
                break;
            case FACELOSTNOTCONTINUOUS:// 人脸时不时的丢失，被算法判定为非连续|不适合提示用户

                break;
            case FACENOTCONTINUOUS:// 由于人脸动作过快导致的非连续|不适合提示用户
                break;
            case MASK:// 面具攻击 |不适合提示用户
                break;
            case TOOMANYFACELOST:// 人脸从拍摄区域消失时间过长|不适合提示用户
                break;
        }
        new BuriedStatistics.Builder().addContext(BasicMegviiFaceActivity.this)
                .addOperElementName("")
                .addOperElementType("button")
                .addOperPageName("人脸识别页面")
                .addErrorInfo("活体检测失败"+":"+resID)
                .addSessionId("")
                .build();

        handleResult(resID, rawID);
    }

    /**
     * 「Detector.DetectionListener」
     * 活体验证中（这个方法会持续不断的回调，返回照片detection信息）
     */
    @Override
    public void onFrameDetected(final long timeout, DetectionFrame detectionFrame) {
        if (mSensorUtil.isVertical()) {

            faceOcclusion(detectionFrame);

            if (timeout > 0) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTimeOutText.setText(timeout / 1000 + "");
                        mTimeOutBar.setProgress((int) (timeout / 100));
                    }
                });
            }

            mFaceMask.setFaceInfo(detectionFrame);
        } else {
            if (mSensorUtil.Y == 0) {
                showErrorTxt("请打开手机读取运动数据权限");
            } else {
                showErrorTxt("请竖直握紧手机");
            }
        }
    }

    // ============================================================================================================================================================
    // ============================================================================================================================================================
    // ============================================================================================================================================================

    /**
     * 照镜子环节
     * 流程：
     * 1,先从返回的DetectionFrame中获取FaceInfo。在FaceInfo中可以先判断这张照片上的人脸是否有被遮挡的状况,入股有直接return
     * 2,如果没有遮挡就把SDK返回的DetectionFramed传入人脸质量检测管理类mFaceQualityManager中获取FaceQualityErrorType的list
     * 3.通过返回的list来判断这张照片上的人脸是否合格。如果返回list为空或list中FaceQualityErrorType的对象数量为0则表示这张照片合格开始进行活体检测
     */
    private void faceOcclusion(DetectionFrame detectionFrame) {
        mFailFrame++;
        if (detectionFrame != null) {
            FaceInfo faceInfo = detectionFrame.getFaceInfo();
            if (faceInfo != null) {
                if (faceInfo.eyeLeftOcclusion > 0.5 || faceInfo.eyeRightOcclusion > 0.5) {
                    showFail("请勿用手遮挡眼睛");
                    return;
                }
                if (faceInfo.mouthOcclusion > 0.5) {
                    showFail("请勿用手遮挡嘴巴");
                    return;
                }
                boolean faceTooLarge = faceInfo.faceTooLarge;
                mIDetection.checkFaceTooLarge(faceTooLarge);
            }
        }
        // 从人脸质量检测管理类中获取错误类型list
        faceInfoChecker(mFaceQualityManager.feedFrame(detectionFrame));
    }

    private void faceInfoChecker(List<FaceQualityManager.FaceQualityErrorType> errorTypeList) {
        if (errorTypeList == null || errorTypeList.size() == 0) {
            if (isHandleStart) {
                return;
            }
            isHandleStart = true;
            mFaceUI.handleStart(this);
            mainHandler.post(mTimeoutRunnable);// 开始活体检测
        } else {
            String infoStr = "";
            FaceQualityManager.FaceQualityErrorType errorType = errorTypeList.get(0);
            if (errorType == FaceQualityManager.FaceQualityErrorType.FACE_NOT_FOUND) {
                infoStr = "请让我看到您的正脸";
            } else if (errorType == FaceQualityManager.FaceQualityErrorType.FACE_POS_DEVIATED) {
                infoStr = "请让我看到您的正脸";
            } else if (errorType == FaceQualityManager.FaceQualityErrorType.FACE_NONINTEGRITY) {
                infoStr = "请让我看到您的正脸";
            } else if (errorType == FaceQualityManager.FaceQualityErrorType.FACE_TOO_DARK) {
                infoStr = "请让光线再亮点";
            } else if (errorType == FaceQualityManager.FaceQualityErrorType.FACE_TOO_BRIGHT) {
                infoStr = "请让光线再暗点";
            } else if (errorType == FaceQualityManager.FaceQualityErrorType.FACE_TOO_SMALL) {
                infoStr = "请再靠近一些";
            } else if (errorType == FaceQualityManager.FaceQualityErrorType.FACE_TOO_LARGE) {
                infoStr = "请再离远一些";
            } else if (errorType == FaceQualityManager.FaceQualityErrorType.FACE_TOO_BLURRY) {
                infoStr = "请避免侧光和背光";
            } else if (errorType == FaceQualityManager.FaceQualityErrorType.FACE_OUT_OF_RECT) {
                infoStr = "请保持脸在人脸框中";
            }

            if (StringUtil.isNotEmpty(infoStr)) {
                showFail(infoStr);
            }
        }
    }

    private Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            // 倒计时开始
            initDetecteSession();
            if (mIDetection.mDetectionSteps != null)
                changeType(mIDetection.mDetectionSteps.get(0));
        }
    };

    private void initDetecteSession() {
        if (mICamera.mCamera == null)
            return;

        mProgressBar.setVisibility(View.INVISIBLE);
        mIDetection.detectionTypeInit();// 初始化动作集

        mCurStep = 0;
        mDetector.reset();
        mDetector.changeDetectionType(mIDetection.mDetectionSteps.get(0));//
    }

    private void showFail(String str) {
        if (mFailFrame > 10) {
            mFailFrame = 0;
            showErrorTxt(str);
        }
    }

    private void showErrorTxt(String error) {
//        mFaceServer.sendError(error, "无");
        mPromptText.setText(error);
    }

    //
    private void doPreview() {
        if (!mHasSurface)
            return;

        mICamera.startPreview(mCameraPreview.getSurfaceTexture());
    }

    // 切换检测类型
    private void changeType(Detector.DetectionType detectiontype) {

        mIDetection.changeType(detectiontype);// 动画切换
        mFaceMask.setFaceInfo(null);

        // 语音播放
        if (mCurStep == 0) {
            mIMediaPlayer.doPlay(mIMediaPlayer.getSoundRes(detectiontype));
        } else {
            mIMediaPlayer.doPlay(com.yxjr.credit.R.raw.face_meglive_well_done);
            mIMediaPlayer.setOnCompletionListener(detectiontype);
        }
    }


    protected void release() {
//         if (mDetector != null)//跳转至结果页时，释放此处报空指针异常
//             mDetector.release();
        mSensorUtil.release();
        mICamera.closeCamera();
    }

    // 处理验证结果
    protected void handleResult(int resID, int rawID) {
    }

}

