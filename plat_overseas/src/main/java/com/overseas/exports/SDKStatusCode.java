package com.overseas.exports;

/**
 * SDK回调代码
 */

public class SDKStatusCode {
    /**
     * 登录成功
     */
    public static final int LOGIN_SUCCESS = 210;
    /**
     * 登录失败
     */
    public static final int LOGIN_ERROR = 211;
    /**
     * 取消登录
     */
    public static final int LOGIN_CANCEL = 212;

    /**
     * 账号登录
     */
    public static final int LOGOUT = 215;

    public static final int INIT_SUCCESS = 101;// 初始化成功
    public static final int INIT_FAIL = 103;// 初始化失败

    /**
     * 取消支付
     */
    public static final int PAY_CANCEL = 221;
    /**
     * 支付失败
     */
    public static final int PAY_ERROR = 222;
    /**
     * 支付成功
     */
    public static final int PAY_SUCCESS = 223;
    /**
     * 绑定成功
     */
    public static final int BIND_SUCCESS = 251;
    /**
     * 取消绑定
     */
    public static final int BIND_CANCEL = 252;
    /**
     * 绑定失败
     */
    public static final int BIND_FAIL = 253;
    /**
     * 分享成功
     */
    public static final int SHARE_SUCCESS = 251;
    /**
     * 取消分享
     */
    public static final int SHARE_CANCEL = 252;
    /**
     * 分享失败
     */
    public static final int SHARE_FAIL = 253;
}
