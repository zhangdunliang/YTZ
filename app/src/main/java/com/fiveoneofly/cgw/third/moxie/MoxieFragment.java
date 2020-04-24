package com.fiveoneofly.cgw.third.moxie;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.moxie.client.manager.StatusViewListener;

import org.json.JSONObject;

public class MoxieFragment extends Fragment implements StatusViewListener {

    MoxieLoading mMoxieLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout topLayout = new RelativeLayout(this.getActivity());
        topLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        mMoxieLoading = new MoxieLoading(this.getActivity());

        mMoxieLoading.show();
        return topLayout;
    }

    @Override
    public void onDestroy() {
        mMoxieLoading.dismiss();
        super.onDestroy();
    }

    @Override
    public void updateProgress(JSONObject arg0) {
    }
}
