package com.fiveoneofly.cgw.web.indicator;

/**
 * Created by xiaochangyou on 2018/6/13.
 */

public class IndicatorController extends WebIndicator.Controller {

    private WebIndicator mWebIndicator;

    public IndicatorController(WebIndicator webIndicator) {
        this.mWebIndicator = webIndicator;
    }

    @Override
    public void show() {
        if (mWebIndicator != null)
            mWebIndicator.show();
    }

    @Override
    public void hide() {
        if (mWebIndicator != null)
            mWebIndicator.hide();
    }

    @Override
    public void reset() {
        if (mWebIndicator != null)
            mWebIndicator.reset();
    }

    @Override
    public void progress(int pro) {
        if (mWebIndicator == null)
            return;

        if (pro == 0) {
            reset();
        } else if (pro > 0 && pro <= 10) {
            show();
        } else if (pro > 10 && pro < 95) {
            mWebIndicator.setProgress(pro);
        } else {
            mWebIndicator.setProgress(pro);
            hide();
        }
    }
}
