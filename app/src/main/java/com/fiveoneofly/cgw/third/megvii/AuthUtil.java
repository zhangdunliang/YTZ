package com.fiveoneofly.cgw.third.megvii;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.utils.AndroidUtil;
import com.fiveoneofly.cgw.utils.DialogUtil;
import com.megvii.idcardquality.IDCardQualityLicenseManager;
import com.megvii.licensemanager.Manager;
import com.megvii.livenessdetection.LivenessLicenseManager;
import com.fiveoneofly.cgw.third.megvii.util.MegviiUtil;


/**
 * Created by xiaochangyou on 2018/5/14.
 */

public class AuthUtil {


    private static final int OCR_AUTH_SUCCES = 1;// ocr联网授权成功
    private static final int OCR_AUTH_FAIL = 2;// ocr联网授权失败
    private static final int FACE_AUTH_SUCCES = 3;// face联网授权成功
    private static final int FACE_AUTH_FAIL = 4;// face联网授权失败
    private static AuthDialog mAuthDialog = null;

    private static Handler mAuthHandler = null;

    private static FaceAuthListener mAuthListener;
    private static Context mContext;

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == OCR_AUTH_SUCCES) {
                mAuthDialog.showAuthSuccess();
                mAuthDialog = null;

                if (mAuthListener != null)
                    mAuthListener.authSucces();

                mAuthHandler = null;

            } else if (msg.what == OCR_AUTH_FAIL) {
//                final String message = msg.obj != null ? msg.obj.toString() : "";
                mAuthDialog.showAuthFail(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mAuthDialog.dismiss();

                        if (mContext != null && mAuthListener != null)
                            netOcrAuth(mContext, mAuthListener);

                    }
                });
            } else if (msg.what == FACE_AUTH_SUCCES) {
                mAuthDialog.showAuthSuccess();
                mAuthDialog = null;

                if (mAuthListener != null)
                    mAuthListener.authSucces();

                mAuthHandler = null;
            } else if (msg.what == FACE_AUTH_FAIL) {
//                final String message = msg.obj != null ? msg.obj.toString() : "";
                mAuthDialog.showAuthFail(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mAuthDialog.dismiss();

                        if (mContext != null && mAuthListener != null)
                            netFaceAuth(mContext, mAuthListener);

                    }
                });
            }
        }
    }


    public static void netOcrAuth(final Context context, FaceAuthListener authListener) {
        mAuthListener = authListener;
        mContext = context;


        if (mAuthHandler == null)
            mAuthHandler = new MyHandler();

        if (mAuthDialog == null)
            mAuthDialog = new AuthDialog(mContext);

        final IDCardQualityLicenseManager idCardLicenseManager = new IDCardQualityLicenseManager(mContext);

        if (idCardLicenseManager.checkCachedLicense() > 0) {
            mAuthHandler.sendEmptyMessage(OCR_AUTH_SUCCES);
        } else {
            DialogUtil.display(
                    mContext,
                    String.format(mContext.getString(R.string.auth_message_ocr), AndroidUtil.getAppName(mContext)),
                    mContext.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    mContext.getString(R.string.auth),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String uuid = MegviiUtil.getUUIDString(mContext);
                            mAuthDialog.showAuth();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Manager manager = new Manager(mContext);
                                    manager.registerLicenseManager(idCardLicenseManager);
                                    manager.takeLicenseFromNetwork(uuid);
                                    if (idCardLicenseManager.checkCachedLicense() > 0) {
                                        mAuthHandler.sendEmptyMessage(OCR_AUTH_SUCCES);
                                    } else {
//                                        Message msg = new Message();
//                                        msg.what = OCR_AUTH_FAIL;
//                                        msg.obj = message;
//                                        mAuthHandler.sendMessage(msg);
                                        mAuthHandler.sendEmptyMessage(OCR_AUTH_FAIL);
                                    }
                                }
                            }).start();
                        }
                    });
        }
    }

    public static void netFaceAuth(final Context context, FaceAuthListener authListener) {

        mAuthListener = authListener;
        mContext = context;


        if (mAuthHandler == null)
            mAuthHandler = new MyHandler();

        if (mAuthDialog == null)
            mAuthDialog = new AuthDialog(mContext);

        final LivenessLicenseManager livenessLicenseManager = new LivenessLicenseManager(mContext);

        if (livenessLicenseManager.checkCachedLicense() > 0) {
            mAuthHandler.sendEmptyMessage(FACE_AUTH_SUCCES);
        } else {
            DialogUtil.display(
                    mContext,
                    String.format(mContext.getString(R.string.auth_message_face), AndroidUtil.getAppName(mContext)),
                    mContext.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    mContext.getString(R.string.auth),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String uuid = MegviiUtil.getUUIDString(mContext);
                            mAuthDialog.showAuth();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Manager manager = new Manager(mContext);
                                    manager.registerLicenseManager(livenessLicenseManager);
                                    manager.takeLicenseFromNetwork(uuid);
                                    if (livenessLicenseManager.checkCachedLicense() > 0) {
                                        mAuthHandler.sendEmptyMessage(FACE_AUTH_SUCCES);
                                    } else {
                                        mAuthHandler.sendEmptyMessage(FACE_AUTH_FAIL);
                                    }
                                }
                            }).start();
                        }
                    });
        }
    }

    public interface FaceAuthListener {
        void authSucces();
    }

}



