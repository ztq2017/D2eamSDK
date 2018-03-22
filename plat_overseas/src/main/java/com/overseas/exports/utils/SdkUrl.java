package com.overseas.exports.utils;

import com.overseas.exports.common.util.Md5;

import java.util.Map;
import java.util.TreeMap;


public class SdkUrl {


    private static final String SDK_URL = "https://gs888.9453play.com";
    public static final String GET_CODE_URL = "http://gs888.9453play.com";
    public static String SDK_LOGIN_URL = "";
    /* 用户 Start */

    public static final String USER_CHECKURL = "/CheckUrl"; // 获取服务器地址
    public static final String USER_INIT = "/InitSdk"; // 初始化
    public static final String USER_LOGIN_QUICK = "/login_Quick"; // 游客登录
    public static final String USER_LOGIN_BIND_PHONE = "/login_BindPhoneLogin"; //會員帳號登入
    public static final String USER_LOGIN_BIND_PHONE_REG = "/login_BindPhoneReg"; //會員註冊
    public static final String USER_LOGIN_PLAT_ACC = "/login_"; //會員帳號登入
    public static final String USER_LOGIN_BIND_PLAT_ACC = "/bind_Platacc";  // 绑定账号
    public static final String USER_LOGIN_RESET_PHONE_PASS = "/Reset_PhonePass";  // 重置手機密碼
    public static final String USER_LOGIN_FORGET_PWD_CODE = "/BindPhoneAcc"; // 忘記密碼檢核 - 获取手机验证码
    public static final String USER_LOGIN_FORGET_PHONE_PASS = "/forget_PhonePass"; // 忘記密碼申請

    /* 用户 End */

    public static String PAY_URL = ""; // 支付接口


    public static String getSdkUrl(String cmd) {
        return SDK_URL + cmd;
    }

    public static String getSdkLoginUrl(String cmd) {
        return SDK_LOGIN_URL + cmd;
    }


    public static String genParamsStr(Map<String, String> params, String key) {
        StringBuilder baseStrBuilder = new StringBuilder();
        boolean isFist = true;
        for (Map.Entry<String, String> p : params.entrySet()) {
            if (isFist) {
                baseStrBuilder.append(p.getKey()).append("=").append(p.getValue());
                isFist = false;
            } else {
                baseStrBuilder.append("&").append(p.getKey()).append("=").append(p.getValue());
            }
        }
        String sign = getSign(params, key);
        baseStrBuilder.append("&sign=").append(sign);
        return baseStrBuilder.toString();
    }

    public static String getSign(Map<String, String> params, String key) {
        TreeMap<String, String> signMap = new TreeMap<>();
        signMap.putAll(params);
        signMap.put("key", key);
        StringBuilder signStrBuilder = new StringBuilder();
        boolean isFist = true;
        for (Map.Entry<String, String> p : signMap.entrySet()) {
            if (isFist) {
                signStrBuilder.append(p.getKey()).append("=").append(p.getValue());
                isFist = false;
            } else {
                signStrBuilder.append("&").append(p.getKey()).append("=").append(p.getValue());
            }
        }
        return Md5.MD5(signStrBuilder.toString());
    }
}
