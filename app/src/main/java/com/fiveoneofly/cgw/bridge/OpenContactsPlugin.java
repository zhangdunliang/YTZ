package com.fiveoneofly.cgw.bridge;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.fiveoneofly.cgw.calm.CalmController;
import com.fiveoneofly.cgw.calm.ItemContacts;
import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;
import com.yxjr.credit.constants.ActivityCode;
import com.fiveoneofly.cgw.utils.EmojiFilterUtil;
import com.fiveoneofly.cgw.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class OpenContactsPlugin extends Plugin {

    private String mId;
    private ICallback mResponseCallback;

    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) {
        mId = id;
        mResponseCallback = responseCallback;

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(Uri.parse("content://contacts/people"));
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        mOnBridgeListener.startActivityForResult(this, intent, ActivityCode.Request.CONTACTS_REQUEST_CODE);
        CalmController.getInstance().executeContacts(mOnBridgeListener.getActivity());// 抓通讯录
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityCode.Request.CONTACTS_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {// 选择联系人

//            if (PermissionUtil.checkContacts(mContext).equals(PermissionUtil.STATUS_UNAUTHORIZED)) {
//                ToastUtil.showToast(mContext, "请开启读取联系人权限后重试!");
//                return;
//            }
            if (data == null) {
                return;
            }
            // 处理返回的data,获取选择的联系人信息
            Uri uri = data.getData();
            getPhoneContacts(uri);
        }
    }

    private void getPhoneContacts(Uri uri) {

        String[] contacts = new String[2];
        ContentResolver cr = mContext.getContentResolver();// 得到ContentResolver对象
        Cursor cursor = cr.query(uri, null, null, null, null);// 取得电话本中开始一项的光标
        if (cursor != null && cursor.moveToFirst()) {
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);// 取得联系人姓名
            contacts[0] = cursor.getString(nameFieldColumnIndex);
            Cursor phoneCursor = cr.query(uri, null, null, null, null);
            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                contacts[1] = phoneCursor
                        .getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneCursor.close();
            } else {
                contacts[1] = null;
            }
            cursor.close();
        } else {
            contacts[0] = null;
            contacts[1] = null;
        }

        String contactName = contacts[0];
        String contactNum = contacts[1];

        JSONObject contact = new JSONObject();
        if (null == contactName || null == contactNum) {
            ToastUtil.showToast(mContext, "联系人姓名和号码不能为空！");
            return;
        }
        if ("".equals(contactName) || "".equals(contactNum)) {
            ToastUtil.showToast(mContext, "联系人姓名和号码不能为空！");
            return;
        }

        try {
            contact.put("name", contactName);
            contact.put("number", contactNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (contactName.length() > ItemContacts.mNameMaxLength) {
            ToastUtil.showToast(mContext, "联系人姓名过长！");
            return;
        }
        if (contactNum.length() > ItemContacts.mPhoneNumberMaxLength) {
            ToastUtil.showToast(mContext, "联系人号码过长！");
            return;
        }
        if (EmojiFilterUtil.containsEmoji(contactName)) {
            ToastUtil.showToast(mContext, "联系人姓名不能包含表情！");
            return;
        }

        mResponseCallback.callback(mId, mPluginCode, contact.toString());
    }
}
