package com.fiveoneofly.cgw.net.api;

import android.support.annotation.NonNull;

/**
 * Created by xiaochangyou on 2017/6/14.
 */

public abstract class ApiCallback<P> {

    public void onStart() {
    }

    public abstract void onSuccess(P p);

    public abstract void onFailure(@NonNull String errorCode, @NonNull String errorMessage);
}
