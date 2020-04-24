package com.yxjr.credit.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.yxjr.credit.R;
import com.yxjr.credit.widget.wheel.AbstractWheelTextAdapter;
import com.yxjr.credit.widget.wheel.OnWheelChangedListener;
import com.yxjr.credit.widget.wheel.OnWheelScrollListener;
import com.yxjr.credit.widget.wheel.WheelView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * All rights Reserved, Designed By ClareShaw
 *
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午10:57:20
 * @描述:TODO[地区选择器]
 */
public class WheelAddress implements View.OnClickListener {

    private WheelView wvProvince;
    private WheelView wvCitys;
    private WheelView wvAreas;
    private View lyChangeAddress;
    private View lyChangeAddressChild;
    private TextView btnSure;
    private TextView btnCancel;

    private JSONObject mJsonObj;
    private String[] mProvinceDatas;
    private Map<String, String[]> mCitysDatasMap = new HashMap<String, String[]>();
    private Map<String, String[]> mAreasDatasMap = new HashMap<String, String[]>();

    private ArrayList<String> arrProvinces = new ArrayList<String>();
    private ArrayList<String> arrCitys = new ArrayList<String>();
    private ArrayList<String> arrAreas = new ArrayList<String>();
    private AddressTextAdapter provinceAdapter;
    private AddressTextAdapter cityAdapter;
    private AddressTextAdapter areaAdapter;

    private String strProvince = "上海";
    private String strCity = "上海";
    private String strArea = "黄浦区";
    private OnAddressCListener onAddressCListener;

    private int maxsize = 24;
    private int minsize = 14;

    private Context mContext;
    private Activity mActivity;
    private LayoutInflater mInflater = null;
    private PopupWindow menuWindow;

    public WheelAddress(Activity mActivity) {
        this.mContext = mActivity;
        this.mActivity = mActivity;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void show() {
        setPopwindow(setDataPick(), mActivity);
    }

    private void setPopwindow(View view, final Activity mActivity) {
        this.menuWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        menuWindow.setFocusable(true);
        ColorDrawable cd = new ColorDrawable(0x000000);
        menuWindow.setBackgroundDrawable(cd);
        menuWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();// 产生背景变暗效果
        lp.alpha = 0.4f;
        mActivity.getWindow().setAttributes(lp);
        menuWindow.setOutsideTouchable(true);
        menuWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                menuWindow = null;
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().setAttributes(lp);
            }
        });
    }

    private View setDataPick() {
        View view = mInflater.inflate(R.layout.yxjr_credit_wheel_address, null);

        wvProvince = (WheelView) view.findViewById(R.id.wv_address_province);
        wvCitys = (WheelView) view.findViewById(R.id.wv_address_city);
        wvAreas = (WheelView) view.findViewById(R.id.wv_address_area);
        lyChangeAddress = view.findViewById(R.id.ly_myinfo_changeaddress);
        lyChangeAddressChild = view.findViewById(R.id.ly_myinfo_changeaddress_child);
        btnSure = (TextView) view.findViewById(R.id.btn_myinfo_sure);
        btnCancel = (TextView) view.findViewById(R.id.btn_myinfo_cancel);

        lyChangeAddress.setOnClickListener(this);
        lyChangeAddressChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        initJsonData();
        initDatas();

        initProvinces();
        provinceAdapter = new AddressTextAdapter(mContext, arrProvinces, getProvinceItem(strProvince), maxsize, minsize);
        wvProvince.setVisibleItems(5);
        wvProvince.setViewAdapter(provinceAdapter);
        wvProvince.setCurrentItem(getProvinceItem(strProvince));

        initCitys(mCitysDatasMap.get(strProvince));
        cityAdapter = new AddressTextAdapter(mContext, arrCitys, getCityItem(strCity), maxsize, minsize);
        wvCitys.setVisibleItems(5);
        wvCitys.setViewAdapter(cityAdapter);
        wvCitys.setCurrentItem(getCityItem(strCity));

        initAreas(mAreasDatasMap.get(strCity));
        areaAdapter = new AddressTextAdapter(mContext, arrAreas, getAreaItem(strArea), maxsize, minsize);
        wvAreas.setVisibleItems(5);
        wvAreas.setViewAdapter(areaAdapter);
        wvAreas.setCurrentItem(getAreaItem(strArea));

        wvProvince.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) provinceAdapter.getItemText(wheel.getCurrentItem());
                strProvince = currentText;
                setTextviewSize(currentText, provinceAdapter);

                String[] citys = mCitysDatasMap.get(currentText);
                initCitys(citys);
                cityAdapter = new AddressTextAdapter(mContext, arrCitys, 0, maxsize, minsize);
                wvCitys.setVisibleItems(5);
                wvCitys.setViewAdapter(cityAdapter);
                wvCitys.setCurrentItem(0);

                String[] areas = mAreasDatasMap.get(arrCitys.get(0));
                initAreas(areas);
                areaAdapter = new AddressTextAdapter(mContext, arrAreas, 0, maxsize, minsize);
                wvAreas.setVisibleItems(5);
                wvAreas.setViewAdapter(areaAdapter);
                wvAreas.setCurrentItem(0);

            }
        });

        wvProvince.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) provinceAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, provinceAdapter);
            }
        });

        wvCitys.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
                strCity = currentText;
                setTextviewSize(currentText, cityAdapter);
                String[] areas = mAreasDatasMap.get(currentText);
                initAreas(areas);
                areaAdapter = new AddressTextAdapter(mContext, arrAreas, 0, maxsize, minsize);
                wvAreas.setVisibleItems(5);
                wvAreas.setViewAdapter(areaAdapter);
                wvAreas.setCurrentItem(0);
            }
        });

        wvCitys.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, cityAdapter);
            }
        });

        wvAreas.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) areaAdapter.getItemText(wheel.getCurrentItem());
                strArea = currentText;
                setTextviewSize(currentText, areaAdapter);
            }
        });

        wvAreas.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) areaAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, areaAdapter);
            }
        });
        return view;
    }

    private class AddressTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected AddressTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.yxjr_credit_wheel_item, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.yx_credit_tv_temp_value);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, AddressTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(24);
            } else {
                textvew.setTextSize(14);
            }
        }
    }

    public void setAddresskListener(OnAddressCListener onAddressCListener) {
        this.onAddressCListener = onAddressCListener;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btnSure) {
            if (onAddressCListener != null) {
                onAddressCListener.onClick(strProvince, strCity, strArea);
                menuWindow.dismiss();
            }
        } else if (v == btnCancel) {
            menuWindow.dismiss();
        } else if (v == lyChangeAddressChild) {
            return;
        } else {
            menuWindow.dismiss();
        }
    }

    /**
     * 回调接口
     */
    public interface OnAddressCListener {
        public void onClick(String province, String city, String area);
    }

    /**
     * 从文件中读取地址数据
     */
    private void initJsonData() {
        try {
            String data = null;
            InputStream is = mContext.getAssets().open("yxjr_credit_province.json");
            byte[] buf = new byte[is.available()];
            is.read(buf);
            data = new String(buf, "UTF-8");
            is.close();
            mJsonObj = new JSONObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析数据
     */
    private void initDatas() {
        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("list");
            mProvinceDatas = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonProvince = jsonArray.getJSONObject(i);
                String province = jsonProvince.getString("province");// 省份
                mProvinceDatas[i] = province;

                JSONArray jsonCityList = null;
                try {
                    jsonCityList = jsonProvince.getJSONArray("city");// 城市
                } catch (Exception e1) {
                    continue;
                }
                String[] mCityesDatas = new String[jsonCityList.length()];

                for (int j = 0; j < jsonCityList.length(); j++) {
                    JSONObject jsonCity = jsonCityList.getJSONObject(j);
                    String city = jsonCity.getString("name");// 城市名称
                    mCityesDatas[j] = city;

                    JSONArray jsonAreas = null;
                    try {
                        jsonAreas = jsonCity.getJSONArray("area");// 城市区域
                    } catch (Exception e) {
                        continue;
                    }
                    String[] mAreasDatas = new String[jsonAreas.length()];
                    for (int k = 0; k < jsonAreas.length(); k++) {
                        String area = jsonAreas.getString(k);
                        mAreasDatas[k] = area;
                    }
                    mAreasDatasMap.put(city, mAreasDatas);
                }
                mCitysDatasMap.put(province, mCityesDatas);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }

    /**
     * 初始化省会
     */
    public void initProvinces() {
        int length = mProvinceDatas.length;
        for (int i = 0; i < length; i++) {
            arrProvinces.add(mProvinceDatas[i]);
        }
    }

    /**
     * 根据省会，生成该省会的所有城市
     *
     * @param citys
     */
    public void initCitys(String[] citys) {
        if (citys != null) {
            if (arrCitys != null)
                arrCitys.clear();
            int length = citys.length;
            for (int i = 0; i < length; i++) {
                arrCitys.add(citys[i]);
            }
        } else {
            String[] city = mCitysDatasMap.get("湖南");
            arrCitys.clear();
            int length = city.length;
            for (int i = 0; i < length; i++) {
                arrCitys.add(city[i]);
            }
        }
        if (arrCitys != null && arrCitys.size() > 0 && !arrCitys.contains(strCity)) {
            strCity = arrCitys.get(0);
        }
    }

    /**
     * 根据城市生成该城市所有区域
     */
    public void initAreas(String[] areas) {
        if (areas != null) {
            if (arrAreas != null)
                arrAreas.clear();
            int length = areas.length;
            for (int i = 0; i < length; i++) {
                arrAreas.add(areas[i]);
            }
        } else {
            String[] area = mAreasDatasMap.get("长沙");
            arrAreas.clear();
            int length = area.length;
            for (int i = 0; i < length; i++) {
                arrAreas.add(area[i]);
            }
        }
        if (arrAreas != null && arrAreas.size() > 0 && !arrAreas.contains(strArea)) {
            strArea = arrAreas.get(0);
        }
    }

    /**
     * 初始化地点
     *
     * @param province
     * @param city
     */
    public void setAddress(String province, String city, String area) {
        if (province != null && province.length() > 0) {
            this.strProvince = province;
        }
        if (city != null && city.length() > 0) {
            this.strCity = city;
        }
        if (area != null && area.length() > 0) {
            this.strArea = area;
        }
    }

    /**
     * 返回省会索引，没有就返回默认“上海”
     *
     * @param province
     * @return
     */
    public int getProvinceItem(String province) {
        int size = arrProvinces.size();
        int provinceIndex = 0;
        boolean noprovince = true;
        for (int i = 0; i < size; i++) {
            if (province.equals(arrProvinces.get(i))) {
                noprovince = false;
                return provinceIndex;
            } else {
                provinceIndex++;
            }
        }
        if (noprovince) {
            strProvince = "上海";
            return 8;
        }
        return provinceIndex;
    }

    /**
     * 得到城市索引，没有返回默认“上海”
     *
     * @param city
     * @return
     */
    public int getCityItem(String city) {
        int size = arrCitys.size();
        int cityIndex = 0;
        boolean nocity = true;
        for (int i = 0; i < size; i++) {
//            YxLog.d("=========city=========" + arrCitys.get(i));
            if (city.equals(arrCitys.get(i))) {
                nocity = false;
                return cityIndex;
            } else {
                cityIndex++;
            }
        }
        if (nocity) {
            strCity = "上海";
            return 0;
        }
        return cityIndex;
    }

    /**
     * 得到区域索引，没有返回默认“黄浦区”
     *
     * @param area
     * @return
     */
    public int getAreaItem(String area) {
        int size = arrAreas.size();
        int areaIndex = 0;
        boolean noarea = true;
        for (int i = 0; i < size; i++) {
//            YxLog.d("=========area=========" + arrAreas.get(i));
            if (area.equals(arrAreas.get(i))) {
                noarea = false;
                return areaIndex;
            } else {
                areaIndex++;
            }
        }
        if (noarea) {
            strArea = "黄浦区";
            return 0;
        }
        return areaIndex;
    }

}