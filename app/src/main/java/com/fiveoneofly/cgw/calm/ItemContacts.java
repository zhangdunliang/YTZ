package com.fiveoneofly.cgw.calm;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.fiveoneofly.cgw.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemContacts {

    /**
     * 姓名长度限制：50 15
     */
    public static final int mNameMaxLength = 15;
    /**
     * 手机长度限制：20 20
     */
    public static final int mPhoneNumberMaxLength = 20;
    /**
     * 国家长度限制：30 10
     */
    private final int mCountryMaxLength = 10;
    /**
     * 省长度限制：50 16
     */
    private final int mStateMaxLength = 16;
    /**
     * 市长度限制：30 10
     */
    private final int mCityMaxLength = 10;
    /**
     * 街道长度限制：100 33
     */
    private final int mStreetMaxLength = 33;
    /**
     * 邮编长度限制：20 20
     */
    private final int mZipMaxLength = 20;
    /**
     * 邮箱长度限制：50 25
     */
    private final int mEmailContentMaxLength = 25;
    /**
     * 公司名称长度限制：200 60
     */
    private final int mCompanyMaxLength = 60;

    public JSONArray getData(Context context) {
        return GetContacts(context);
    }

    /**
     * 从本地手机中获取全部|查询全部字段
     */
    @SuppressLint({"SimpleDateFormat", "InlinedApi"})
    public JSONArray GetContacts(Context context) {
        JSONArray contacts = new JSONArray();
        Cursor contactsCursor = null;
        ContentResolver cr = context.getContentResolver();// 得到ContentResolver对象
        try {
            contactsCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "sort_key COLLATE LOCALIZED asc"); // 取得电话本中开始一项的光标
            if (null != contactsCursor && contactsCursor.getCount() > 0) {
                if (contactsCursor.moveToFirst()) {// 查询结果是否为空
                    int idContactColumn = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID);// 取得联系人ID列
                    int nameColumn = contactsCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);// 取得联系人名字列
                    int updateTimeColumn = contactsCursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP);//最后一次更新时间
                    do {
                        JSONObject contact = new JSONObject();
                        String contactId = contactsCursor.getString(idContactColumn);// 获取联系人ID
                        String displayName = contactsCursor.getString(nameColumn);// 获取联系人姓名
                        String updateTime = contactsCursor.getString(updateTimeColumn);// 获取联系人更新时间
                        //联系人更新时间
                        if (null != updateTime) {
                            if (!updateTime.equals("0")) {
                                String updateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(updateTime)));
                                contact.put("updateTime", updateTimeFormat);
                            }
                        }
                        //联系人显示的姓名
                        if (null != displayName) {
                            if (displayName.length() > mNameMaxLength) {// 姓名长度限制
                                contact.put("name", displayName.substring(0, mNameMaxLength));
                            } else {
                                contact.put("name", displayName);
                            }
                        }
                        // 获得该联系人号码
                        queryPhone(cr, contactsCursor, contact, contactId, displayName);
                        // 获得该联系人的email信息
                        queryEmails(cr, contactId, contact);
                        // 获取该联系人地址
                        queryAddresses(cr, contactId, contact);
                        // 获取该联系人组织
                        queryCompany(cr, contactId, contact);
//                        LogUtil.json("Calm---Contact", contact.toString());
                        contacts.put(contact);
                    } while (contactsCursor.moveToNext());
                }
            }
        } catch (SecurityException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (contactsCursor != null) {
                contactsCursor.close();
            }
        }
        LogUtil.d("Calm---Contacts", " query end  ====> Contacts");
        return contacts;
    }

    /**
     * 获得该联系人号码
     */
    private void queryPhone(ContentResolver cr, Cursor contactsCursor, JSONObject contact, String contactId, String displayName) throws JSONException {
        //联系人号码个数
        int phoneCount = contactsCursor.getInt(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));// 查看联系人有多少个号码，如果没有号码，返回0
        if (phoneCount > 0) {
            // 取得电话号码(可能存在多个号码)
            Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);// 在类ContactsContract.CommonDataKinds.Phone中根据查询相应id联系人的所有电话；
            if (null != phoneCursor && phoneCursor.moveToFirst()) {// phoneCursor.moveToFirst()查询结果是否为空
                JSONArray phones = new JSONArray();
                do {
                    String PhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    JSONObject phone = new JSONObject();
                    if (null != PhoneNumber) {
                        if (PhoneNumber.length() > mPhoneNumberMaxLength) {// 号码长度限制
                            phone.put("phoneContent", PhoneNumber.substring(0, mPhoneNumberMaxLength));
                        } else {
                            phone.put("phoneContent", PhoneNumber);
                        }
                    }
                    String phoneType = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    phoneType = phoneType == null ? "" : phoneType;
                    if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM + "")) {// CUSTOM自定义电话
                        phone.put("phoneLabel", "自定义");
                    } else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_HOME + "")) {// HOME家庭电话
                        phone.put("phoneLabel", "家庭");
                    } else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + "")) {// MOBILE手机电话
                        phone.put("phoneLabel", "手机");
                    } else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_WORK + "")) {// WORK工作电话
                        phone.put("phoneLabel", "工作");
                    } else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK + "")) {// FAX_WORK工作传真
                        phone.put("phoneLabel", "工作传真");
                    } else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME + "")) {// FAX_HOME家庭传真
                        phone.put("phoneLabel", "家庭传真");
                    } else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_PAGER + "")) {// PAGER寻呼机
                        phone.put("phoneLabel", "寻呼机");
                    } else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER + "")) {// OTHER其他电话
                        phone.put("phoneLabel", "其他");
                    } else {// 未知
                        phone.put("phoneLabel", "未知");// 未知电话
                    }
                    phones.put(phone);
                } while (phoneCursor.moveToNext());
                contact.put("phoneCount", phones.length());//联系人号码个数
                contact.put("phones", phones);
            }
            if (null != phoneCursor) {
                phoneCursor.close();
            }
        }
    }

    /**
     * 获得该联系人的email信息
     */
    private void queryEmails(ContentResolver cr, String contactId, JSONObject contact) throws JSONException {
        Cursor emailsCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactId, null, null);
        if (null != emailsCursor && emailsCursor.moveToFirst() && emailsCursor.getCount() > 0) {// emailsCursor.moveToFirst()查询结果是否为空
            JSONArray emails = new JSONArray();
            int emailIndex = emailsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            do {
                String emailContent = emailsCursor.getString(emailIndex);
                String emailType = emailsCursor.getString(emailsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                JSONObject email = new JSONObject();
                if (null != emailContent) {
                    if (emailContent.length() > mEmailContentMaxLength) {// 邮箱长度限制
                        emailContent = emailContent.substring(0, mEmailContentMaxLength);
                    }
                }
                email.put("mailContent", emailContent);
                if (emailType.equals(ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM + "")) {// CUSTOM自定义
                    email.put("mailLabel", "自定义邮箱");
                } else if (emailType.equals(ContactsContract.CommonDataKinds.Email.TYPE_HOME + "")) {// HOME家庭
                    email.put("mailLabel", "家庭邮箱");
                } else if (emailType.equals(ContactsContract.CommonDataKinds.Email.TYPE_WORK + "")) {// WORK工作
                    email.put("mailLabel", "工作邮箱");
                } else if (emailType.equals(ContactsContract.CommonDataKinds.Email.TYPE_OTHER + "")) {// OTHER其他
                    email.put("mailLabel", "其他邮箱");
                } else if (emailType.equals(ContactsContract.CommonDataKinds.Email.TYPE_MOBILE + "")) {// MOBILE手机
                    email.put("mailLabel", "手机邮箱");
                } else {// FAX_HOME家庭传真
                    email.put("mailLabel", "未知邮箱");
                }
                emails.put(email);
            } while (emailsCursor.moveToNext());
            contact.put("emails", emails);
        }
        if (null != emailsCursor) {
            emailsCursor.close();
        }
    }

    /**
     * 获取该联系人地址
     */
    private void queryAddresses(ContentResolver cr, String contactId, JSONObject contact) throws JSONException {
        Cursor addressCursor = cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
        if (null != addressCursor && addressCursor.moveToFirst() && addressCursor.getCount() > 0) {// addressCursor.moveToFirst()查询结果是否为空
            JSONArray addresses = new JSONArray();
            do {
                String street = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String addressLabel = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                String city = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String zip = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                String state = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                String country = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                JSONObject address = new JSONObject();
                if (null != street) {// 街道
                    if (street.length() > mStreetMaxLength) {// 街道长度限制
                        street = street.substring(0, mStreetMaxLength);
                    }
                    address.put("street", street);
                }
                if (null != addressLabel) {// 地址类型
                    if (addressLabel.equals(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM + "")) {// CUSTOM自定义
                        address.put("addressLabel", "自定义地址");
                    } else if (addressLabel.equals(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME + "")) {// HOME家庭
                        address.put("addressLabel", "家庭地址");
                    } else if (addressLabel.equals(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK + "")) {// WORK工作
                        address.put("addressLabel", "工作地址");
                    } else if (addressLabel.equals(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER + "")) {// OTHER其他
                        address.put("addressLabel", "其他地址");
                    } else {// MOBILE手机
                        address.put("addressLabel", "未知地址");
                    }
                }
                if (null != city) {// 城市
                    if (city.length() > mCityMaxLength) {// 城市长度限制
                        city = city.substring(0, mCityMaxLength);
                    }
                    address.put("city", city);
                }
                if (null != zip) {// 邮编
                    if (zip.length() > mZipMaxLength) {// 邮编长度限制
                        zip = zip.substring(0, mZipMaxLength);
                    }
                    address.put("zip", zip);
                }
                if (null != state) {// 省份
                    if (state.length() > mStateMaxLength) {// 省份长度限制
                        state = state.substring(0, mStateMaxLength);
                    }
                    address.put("state", state);
                }
                if (null != country) {// 国家
                    if (country.length() > mCountryMaxLength) {// 国家长度限制
                        country = country.substring(0, mCountryMaxLength);
                    }
                    address.put("country", country);
                }
                // 国家代码(Android获取不到)
                // address.put("countryCode", "");
                addresses.put(address);
            } while (addressCursor.moveToNext());
            contact.put("addresses", addresses);
        }
        if (null != addressCursor) {
            addressCursor.close();
        }
    }

    /**
     * 获取该联系人组织(公司名)
     */
    private void queryCompany(ContentResolver cr, String contactId, JSONObject contact) throws JSONException {
        Cursor organizationsCursor = cr.query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Organization.COMPANY, ContactsContract.CommonDataKinds.Organization.TITLE},
                ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "'", new String[]{contactId}, null);
        if (null != organizationsCursor && organizationsCursor.moveToFirst() && organizationsCursor.getCount() > 0) {
            do {
                String company = organizationsCursor.getString(organizationsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));// 公司
                if (null != company) {
                    if (company.length() > mCompanyMaxLength) {
                        company = company.substring(0, mCompanyMaxLength);
                    }
                    contact.put("company", company);
                }
            } while (organizationsCursor.moveToNext());
        }
        if (null != organizationsCursor) {
            organizationsCursor.close();
        }
    }

}

