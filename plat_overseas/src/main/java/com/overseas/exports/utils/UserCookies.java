package com.overseas.exports.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.overseas.exports.common.util.Md5;


/**
 * 用户本地化信息
 */
public class UserCookies {
    private static final String SPF_KEY_USER_ID = "user_id";
    private static final String SPF_KEY_LOGIN_WAY = "login_way";
    private static final String SPF_KEY_TOKEN = "user_token";
    private static final String SPF_KEY_ACCOUNT = "user_account";
    private static final String SPF_KEY_PWD = "user_password";
    private static final String SPF_KEY_IS_PLATFORM_LOGIN = "is_platform_login"; // 是否是平台登录
    private static final String SPF_KEY_IS_VISITOR = "is_visitor"; // 是否是游客
    private static final String SPF_KEY_PAY_URL = "pay_url"; // 支付地址
    private static final String SPF_KEY_LOGIN_TOKEN_TIME = "login_token_time"; // 支付地址

    private static UserCookies mUserCookies;
    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private String mUserID;
    private String mToken;
    private String mAccount;
    private String mLoginWay;
    private String mPwd;
    private String mPayUrl;
    private boolean isPlatformLogin;
    private boolean isVisitor;
    private String mLoginTokenTime;

    private UserCookies(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences("user_cookies", Context.MODE_PRIVATE);
        mUserID = mSharedPreferences.getString(SPF_KEY_USER_ID, null);
        mToken = mSharedPreferences.getString(SPF_KEY_TOKEN, null);
        mAccount = mSharedPreferences.getString(SPF_KEY_ACCOUNT, null);
        mLoginWay = mSharedPreferences.getString(SPF_KEY_LOGIN_WAY, null);
        mPwd = mSharedPreferences.getString(SPF_KEY_PWD, null);
        isPlatformLogin = mSharedPreferences.getBoolean(SPF_KEY_IS_PLATFORM_LOGIN, false);
        isVisitor = mSharedPreferences.getBoolean(SPF_KEY_IS_VISITOR, false);
        mPayUrl = mSharedPreferences.getString(SPF_KEY_PAY_URL, null);
        mLoginTokenTime = mSharedPreferences.getString(SPF_KEY_LOGIN_TOKEN_TIME, "0");
    }

    public static UserCookies getInstance(Context context) {
        if (null == mUserCookies) {
            synchronized (UserCookies.class) {
                if (null == mUserCookies) {
                    mUserCookies = new UserCookies(context);
                }
            }
        }
        return mUserCookies;
    }

    /**
     * 保存用户信息
     *
     * @param userID    user id
     * @param account   account
     * @param pwd       pwd
     * @param isVisitor 是否是游客
     */
    public void saveUserInfo(String userID, String account, String token, String loginWay, String pwd, boolean isVisitor, boolean isPlatformLogin) {
        if (TextUtils.isEmpty(userID)) {
            return;
        }
        mUserID = userID;
        mToken = token;
        mAccount = account;
        mLoginWay = loginWay;
        mPwd = pwd;
        this.isVisitor = isVisitor;
        this.isPlatformLogin = isPlatformLogin;
        mSharedPreferences
                .edit()
                .putString(SPF_KEY_USER_ID, userID)
                .putString(SPF_KEY_TOKEN, mToken)
                .putString(SPF_KEY_ACCOUNT, account)
                .putString(SPF_KEY_LOGIN_WAY, loginWay)
                .putString(SPF_KEY_PWD, pwd)
                .putBoolean(SPF_KEY_IS_PLATFORM_LOGIN, isPlatformLogin)
                .putBoolean(SPF_KEY_IS_VISITOR, isVisitor)
                .apply();
    }

    /**
     * pwd变更
     *
     * @param newPwd new pwd
     */
    public void pwdChange(String newPwd) {
        if (TextUtils.isEmpty(newPwd) || newPwd.equals(mPwd)) return;

        mPwd = newPwd;
        mSharedPreferences
                .edit()
                .putString(SPF_KEY_PWD, newPwd)
                .apply();
    }

    /**
     * pwd变更
     *
     * @param payUrl 支付地址
     */
    public void savePayUrl(String payUrl) {
        if (TextUtils.isEmpty(payUrl) || payUrl.equals(mPayUrl)) return;

        mPayUrl = payUrl;
        mSharedPreferences
                .edit()
                .putString(SPF_KEY_PAY_URL, payUrl)
                .apply();
    }

    /**
     * 登录保存token 时间
     *
     * @param tokenTime 支付地址
     */
    public void saveTokenTime(String tokenTime) {
        if (TextUtils.isEmpty(tokenTime) || tokenTime.equals(mPayUrl)) return;

        mLoginTokenTime = tokenTime;
        mSharedPreferences
                .edit()
                .putString(SPF_KEY_LOGIN_TOKEN_TIME, tokenTime)
                .apply();
    }

    /**
     * 清空用户信息
     */
    public void clearUserInfo() {
        mUserID = null;
        mToken = null;
        mAccount = null;
        mLoginWay = null;
        mPwd = null;
        mPayUrl = null;
        mSharedPreferences.edit().clear().apply();
    }

    /**
     * 是否已经登录
     *
     * @return true: 已登录，false：未登录
     */
    public boolean isLogin() {
        return !TextUtils.isEmpty(mUserID);
    }

    public String getUserID() {
        return mUserID;
    }

    public String getToken() {
        return mToken;
    }

    public String getAccount() {
        return mAccount;
    }

    public String getLoginWay() {
        return mLoginWay;
    }


    public String getPwd() {
        return mPwd;
    }

    public String getPayUrl() {
        return mPayUrl;
    }

    public String getLoginTokenTime() {
        return mLoginTokenTime;
    }

    /**
     * 是否是平台登录
     *
     * @return boolean
     */
    public boolean isPlatformLogin() {
        return isPlatformLogin;
    }

    /**
     * 是否是游客
     *
     * @return boolean
     */
    public boolean isVisitor() {
        return isVisitor;
    }

    /**
     * 是否用户设置了不显示该类型的提示Dialog
     *
     * @return boolean
     */
    public boolean isNotShowTipsDialog(int type) {
        return mSharedPreferences.getBoolean(getNotShowTipsDialogKeyByType(type), false);
    }

    /**
     * 用户设置是否不提示该类型的Dialog
     */
    public void setNotShowTipsDialog(int type, boolean isNotShow) {
        mSharedPreferences
                .edit()
                .putBoolean(getNotShowTipsDialogKeyByType(type), isNotShow)
                .apply();
    }

    // 获取是否不显示该类型的提示Dialog的SharedPreferences key
    private String getNotShowTipsDialogKeyByType(int type) {
        return "not_show_tips_dialog_by_type_" + type;
    }

}
