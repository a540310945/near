package com.hitoncloud.near.utils;


import android.text.TextUtils;

/**
 *  负责人：蒋凌
 *  说明：用于各种验证的工具类
 */
public class VerificationUtils  {

    //判断是否是手机号
    public static boolean isCellPhoneNumber(String CellPhoneNumber) {
        String telRegex = "[1]\\d{10}";
        if (TextUtils.isEmpty(CellPhoneNumber)) return false;
        else return CellPhoneNumber.matches(telRegex);
    }

}
