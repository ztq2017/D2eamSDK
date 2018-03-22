package com.overseas.exports;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.overseas.exports.common.TwitterRestClient;
import com.overseas.exports.platfrom.FacebookSdkApi;
import com.overseas.exports.platfrom.GoogleSdkApi;
import com.overseas.exports.platfrom.RePayHelper;
import com.overseas.exports.sdk.SDKCallBackListener;
import com.overseas.exports.sdk.SDKCallbackListenerNullException;
import com.overseas.exports.task.SdkAsyncHttpStandardResponseHandler;
import com.overseas.exports.utils.ReadTxtContentUtil;
import com.overseas.exports.utils.SdkUrl;
import com.overseas.exports.utils.UserCookies;
import com.overseas.exports.utils.Utils;
import com.overseas.exports.view.InitView;
import com.overseas.exports.view.user.LoginMainDialog;

import org.json.JSONObject;

import java.util.Map;

public class SdkManager {
    private FragmentActivity mActivity;
    private static SdkManager sdkManager;
    private SdkInitSetting mSdkInitSetting;
    private SDKCallBackListener mOnLogoutListener;
    private SDKCallBackListener mOnInitListener;
    private SDKCallBackListener mOnLoginListener;
    private SDKCallBackListener mOnPayListener;
    private SDKCallBackListener mOnBindListener;
    private SDKCallBackListener mOnShareListener;
    private String mLanguage = "language_zh_cn";
    public static String OverseasTag = "OverseasSDK";
    public static ReadTxtContentUtil readTxtUtils;
    public static String mGUid = "";
    public static String mAccList = "";
    private LoginMainDialog mLoginMainDialog;
    private long mLastPayClickTime;
    private int sdkInitState = 0;
    private RePayHelper mRePayHelper;
    /**
     * SDK版本号名称
     */
    public static final String SDK_VERSION_NAME = "v1.0.1";
    // SDK版本号code（只要SDK_VERSION_NAME有增加，此字段必需累加）
    public static final int SDK_VERSION_CODE = 2;
    /**
     * 支付点击间隔时间
     */
    private static final long PAY_CLICK_INTERVAL_TIME = 3 * 1000;

    private int amount;
    private String productId;
    private String productName;
    private String roleId;
    private int serverId;
    private String extInfo;

    public static SdkManager defaultSDK() {
        if (sdkManager == null) {
            synchronized (SdkManager.class) {
                if (sdkManager == null) {
                    sdkManager = new SdkManager();
                }
            }
        }
        return sdkManager;
    }

    /**
     * 初始化SDK
     *
     * @param activity         Activity
     * @param sdkInitSetting   游戏SDK配置参数
     * @param callBackListener 初始化结果回调监听
     * @throws SDKCallbackListenerNullException
     */
    public void initSDK(FragmentActivity activity, SdkInitSetting sdkInitSetting, SDKCallBackListener callBackListener)
            throws SDKCallbackListenerNullException {
        if (callBackListener == null) {
            throw new SDKCallbackListenerNullException("回调侦听器（listener）为空");
        }

        if (activity == null) {
            callBackListener.callBack(SDKStatusCode.INIT_FAIL, "Context 上下文不存在");
            return;
        }

        if (null == sdkInitSetting) {
            callBackListener.callBack(SDKStatusCode.INIT_FAIL, "SdkInitSetting不能为空");
            return;
        }
        if (sdkInitState != 0) {
            Log.e(OverseasTag, "initSdk 重复初始化");
            sdkInitState = 0;
            return;
        }
        mOnInitListener = callBackListener;
        mSdkInitSetting = sdkInitSetting;
        mActivity = activity;
        sdkInitState = -1;
        Utils.getInstance().checkNet(mActivity);
        FacebookSdkApi.defaultSDK().initSdk(mActivity);
        GoogleSdkApi.defaultSDK().initGoogleSdk(mActivity);
        mLanguage = sdkInitSetting.getLanguage();
        readTxtUtils = new ReadTxtContentUtil(activity, "sdk_text_config.txt");
        new InitView(mActivity, mSdkInitSetting, new InitView.OnInitCompleteListener() {
            @Override
            public void onInitComplete(String accList, String gUid) {
                if (!gUid.isEmpty() || !gUid.equals("")) {
                    //获取到uid
                    Log.e(OverseasTag, "data = " + gUid);
                    mGUid = gUid;
                    mAccList = accList;
                    sdkInitState = 1;
                } else {
                    sdkInitState = 0;
                    Toast.makeText(mActivity, "guid is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 登录
     *
     * @param activity Activity
     * @param listener 登录结果回调监听
     * @throws SDKCallbackListenerNullException
     */
    public void login(final FragmentActivity activity, SDKCallBackListener listener)
            throws SDKCallbackListenerNullException {
        mActivity = activity;
        if (sdkInitState != 1) {
            return;
        }
        if (listener == null) {
            throw new SDKCallbackListenerNullException("登录回调侦听器（listener）为空！");
        }
        mOnLoginListener = listener;
        if (Utils.getInstance().isEntity(mSdkInitSetting.getChannelId())
                || Utils.getInstance().isEntity(mSdkInitSetting.getAppKey())
                || Utils.getInstance().isEntity(mSdkInitSetting.getLanguage())) {
            mOnLoginListener.callBack(SDKStatusCode.LOGIN_ERROR, getLanguageContent(112));
            return;
        }

        if (!Utils.getInstance().checkNet(activity)) {
            mOnLoginListener.callBack(SDKStatusCode.LOGIN_ERROR, getLanguageContent(221));
            return;
        }
        if (mGUid.isEmpty() || mGUid.equals("")) {
            mOnLoginListener.callBack(SDKStatusCode.LOGIN_ERROR, getLanguageContent(112));
            initSDK(mActivity, getSdkInitSetting(), new SDKCallBackListener() {
                @Override
                public void callBack(int code, String msg) {

                }
            });
            return;
        }
        mLoginMainDialog = new LoginMainDialog(mActivity, mAccList, mGUid, "login");
        mLoginMainDialog.show();
    }

    /**
     * 账号中心
     *
     * @param activity Activity
     * @param listener 登录结果回调监听
     * @throws SDKCallbackListenerNullException
     */
    public void showUserCenter(final FragmentActivity activity, SDKCallBackListener listener)
            throws SDKCallbackListenerNullException {
        if (sdkInitState != 1) return;
        mActivity = activity;
        if (listener == null) {
            throw new SDKCallbackListenerNullException("登录回调侦听器（listener）为空！");
        }
        mOnBindListener = listener;
        mOnLogoutListener = listener;
        if (Utils.getInstance().isEntity(mSdkInitSetting.getChannelId())
                || Utils.getInstance().isEntity(mSdkInitSetting.getAppKey())
                || Utils.getInstance().isEntity(mSdkInitSetting.getLanguage())) {
            mOnBindListener.callBack(SDKStatusCode.BIND_FAIL, getLanguageContent(112));
            return;
        }

        if (!Utils.getInstance().checkNet(activity)) {
            mOnBindListener.callBack(SDKStatusCode.BIND_FAIL, getLanguageContent(221));
            return;
        }
        if (!UserCookies.getInstance(mActivity).isLogin()) {
            mOnBindListener.callBack(SDKStatusCode.BIND_FAIL, getLanguageContent(233));
            return;
        }
        if (mGUid.isEmpty() || mGUid.equals("")) {
            initSDK(mActivity, getSdkInitSetting(), new SDKCallBackListener() {
                @Override
                public void callBack(int code, String msg) {

                }
            });
            return;
        }
        mLoginMainDialog = new LoginMainDialog(mActivity, mAccList, mGUid, "showUserCenter");
        mLoginMainDialog.show();
    }

    public void pay(final FragmentActivity activity, final int amount, final String productId, final String productName, final String roleId, final int serverId, final String extInfo, SDKCallBackListener sdkCallBackListener) throws SDKCallbackListenerNullException {
        mOnPayListener = sdkCallBackListener;
        mRePayHelper = new RePayHelper(mActivity);
        this.amount = amount;
        this.productId = productId;
        this.productName = productName;
        this.serverId = serverId;
        this.roleId = roleId;
        this.extInfo = extInfo;
        if (sdkInitState != 1) return;
        // 防连续点击
        if (System.currentTimeMillis() - mLastPayClickTime < PAY_CLICK_INTERVAL_TIME) {
            return;
        }
        mLastPayClickTime = System.currentTimeMillis();

        if (sdkCallBackListener == null) {
            throw new SDKCallbackListenerNullException("回调侦听器（listener）为空");
        }

        if (activity == null) {
            sdkCallBackListener.callBack(SDKStatusCode.PAY_ERROR, "Context 上下文不存在");
            return;
        }

        mActivity = activity;
        if (!Utils.getInstance().checkNet(activity)) {
            sdkCallBackListener.callBack(SDKStatusCode.PAY_ERROR, getLanguageContent(221));
            return;
        }
        if (!UserCookies.getInstance(activity).isLogin()) {
            sdkCallBackListener.callBack(SDKStatusCode.PAY_ERROR, getLanguageContent(233));
            return;
        }

        if (mGUid.isEmpty() || mGUid.equals("")) {
            initSDK(mActivity, getSdkInitSetting(), new SDKCallBackListener() {
                @Override
                public void callBack(int code, String msg) {

                }
            });
            return;
        }

        if (!mRePayHelper.hasReOrder()) {
            // 有临时订单，需要进行订单操作
            googlePay();
        } else {
            new AlertDialog.Builder(mActivity)
                    .setTitle(getLanguageContent(602))
                    .setMessage(getLanguageContent(605))
                    .setCancelable(false)
                    .setNegativeButton(getLanguageContent(603), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            handlerRepay();
                        }
                    }).create().show();
        }

    }


    private void googlePay() {

        try {
            JSONObject ojson = new JSONObject();
            ojson.put("guid", mGUid);
            ojson.put("userid", UserCookies.getInstance(mActivity).getUserID());
            ojson.put("channelId", mSdkInitSetting.getChannelId());
            ojson.put("payChannel", "googlepay");
            ojson.put("productId", productId);
            ojson.put("productName", productName);
            ojson.put("price", amount);
            ojson.put("count", 1);
            ojson.put("roleId", roleId);
            ojson.put("serverId", serverId);
            if (SdkUrl.PAY_URL == null || SdkUrl.PAY_URL.length() == 0) {
                Log.e("chuizi", "strPayUrl is null");
                mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, "{err=[[strPayUrl is null]]}");
                return;
            }
            Log.e(OverseasTag, SdkUrl.PAY_URL + "/GenOrderId" + "?" + ojson.toString());
            TwitterRestClient.post(SdkUrl.PAY_URL + "/GenOrderId", ojson.toString(), new SdkAsyncHttpStandardResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {
                    Log.e(OverseasTag, "pay:new String(response)" + new String(response));
                    String msg = "";
                    if (statusCode == 200) {
                        try {
                            JSONObject json = new JSONObject(new String(response));
                            if (!json.has("ret")) {
                                Utils.getInstance().toast(mActivity, "{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                return;
                            }
                            int ret = Integer.parseInt(json.getString("ret"));
                            if (ret == 1) {
                                //获取订单号成功
                                final String orderId = json.getString("orderId");
                                if (!json.has("argvPay")) {
                                    return;
                                }
                                String argvPay = json.getString("argvPay");
                                JSONObject payInfo = new JSONObject(argvPay);
                                final String productId = payInfo.getString("productId");

                                GoogleSdkApi.defaultSDK().pay(mActivity, productId, orderId, new GoogleSdkApi.PlatPaySdkCallBackListener() {
                                    @Override
                                    public void onPlatSdkPaySuccess(String msg) {
                                        verifyPay(productId, orderId, amount, msg);
                                    }

                                    @Override
                                    public void onPlatSdkPayFail(String msg) {
                                        mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, msg);
                                        //  Toast.makeText(mActivity, "msg = " + msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (ret == 0) {
                                if (json.has("errinfo")) {
                                    msg = json.getString("errinfo");
                                }
                                mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, msg);
                            } else if (ret == 2008) {
                                new InitView(mActivity, SdkManager.defaultSDK().getSdkInitSetting(), new InitView.OnInitCompleteListener() {
                                    @Override
                                    public void onInitComplete(String accList, String gUid) {
                                        mGUid = gUid;
                                        mAccList = accList;
                                    }
                                });
                            } else {
                                mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, SdkManager.defaultSDK().getLanguageContent(601) + "：" + new String(response));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, SdkManager.defaultSDK().getLanguageContent(601) + "：" + e.getMessage());
                        }
                    } else {
                        mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, SdkManager.defaultSDK().getLanguageContent(601));
                    }

                }

                @Override
                public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                    mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, SdkManager.defaultSDK().getLanguageContent(113));
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyPay(String productId, String orderId, int amount, String ret) {
        try {
            JSONObject getPayJson = new JSONObject(ret);
            String base64EncodedPublicKey = getPayJson.getString("base64EncodedPublicKey");
            String purchaseData = getPayJson.getString("purchaseData");
            String dataSignature = getPayJson.getString("dataSignature");

            JSONObject verifyPayJson = new JSONObject();
            verifyPayJson.put("productId", productId);
            verifyPayJson.put("orderId", orderId);
            verifyPayJson.put("amount", amount);
            verifyPayJson.put("base64EncodedPublicKey", base64EncodedPublicKey);
            verifyPayJson.put("purchaseData", purchaseData);
            verifyPayJson.put("dataSignature", dataSignature);
            Log.e(OverseasTag, "pay:base64EncodedPublicKey" + base64EncodedPublicKey);
            Log.e(OverseasTag, "pay:purchaseData" + purchaseData);
            Log.e(OverseasTag, "pay:dataSignature" + dataSignature);
            Log.e(OverseasTag, "pay:verifyPayJson" + verifyPayJson.toString());
            // 先保存临时订单
            mRePayHelper.saveOrder(orderId, amount, productId, base64EncodedPublicKey, purchaseData, dataSignature);
            TwitterRestClient.post(SdkUrl.PAY_URL + "/google", verifyPayJson.toString(), new SdkAsyncHttpStandardResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {

                    Log.e(OverseasTag, "verifyPay:new String(response)" + new String(response));
                    if (statusCode == 200) {
                        if (new String(response).equals("ok")) {
                            Utils.getInstance().toast(mActivity, getLanguageContent(600));
                            String ret = String.format("{ret = '%s', msg = '%s',}",
                                    1, "ok");
                            mOnPayListener.callBack(SDKStatusCode.PAY_SUCCESS, ret.replace("'", "\""));
                        } else {
                            mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, SdkManager.defaultSDK().getLanguageContent(601));
                        }
                    } else {
                        mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, SdkManager.defaultSDK().getLanguageContent(601));
                    }

                }

                @Override
                public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                    mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, SdkManager.defaultSDK().getLanguageContent(113));
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handlerRepay() {
        try {
            JSONObject ojson = new JSONObject();
            ojson.put("productId", mRePayHelper.getProductId());
            ojson.put("orderId", mRePayHelper.getOrderNum());
            ojson.put("amount", mRePayHelper.getMoney());
            ojson.put("base64EncodedPublicKey", mRePayHelper.getBase64EncodedPublicKey());
            ojson.put("purchaseData", mRePayHelper.getPurchaseData());
            ojson.put("dataSignature", mRePayHelper.getDataSignature());
            mRePayHelper.repay(SdkUrl.PAY_URL + "/google", ojson.toString(), new SdkAsyncHttpStandardResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {
                    //Utils.getInstance().dismissProgress();
                    try {
                        String retJson = new String(response);
                        //JSONObject jsonObj = new JSONObject(retJson);
                        if (retJson.equals("ok")) {
                            // 清除临时订单
                            mRePayHelper.clearReOrder();
                            new AlertDialog.Builder(mActivity)
                                    .setTitle(getLanguageContent(602))
                                    .setMessage(getLanguageContent(606))
                                    .setCancelable(false)
                                    .setNegativeButton(getLanguageContent(604), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).setPositiveButton(getLanguageContent(603), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    googlePay();
                                }
                            }).create().show();
                        } else {
                            mOnPayListener.callBack(SDKStatusCode.PAY_ERROR, getLanguageContent(601));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                    // Utils.getInstance().dismissProgress();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareMessage(int type, String url, String description, final SDKCallBackListener shareCallListener) throws SDKCallbackListenerNullException {
        mOnShareListener = shareCallListener;

        if (shareCallListener == null) {
            throw new SDKCallbackListenerNullException("回调侦听器（listener）为空");
        }
        if (!Utils.getInstance().checkNet(mActivity)) {
            shareCallListener.callBack(SDKStatusCode.PAY_ERROR, getLanguageContent(221));
            return;
        }
        if (!UserCookies.getInstance(mActivity).isLogin()) {
            shareCallListener.callBack(SDKStatusCode.PAY_ERROR, getLanguageContent(233));
            return;
        }

        if (mGUid.isEmpty() || mGUid.equals("")) {
            initSDK(mActivity, getSdkInitSetting(), new SDKCallBackListener() {
                @Override
                public void callBack(int code, String msg) {

                }
            });
            return;
        }
        FacebookSdkApi.defaultSDK().sharedMessage(type, url, description, new FacebookSdkApi.PlatSdkShareCallBackListener() {
            @Override
            public void onPlatSdkShareSuccess(String msg) {
                mOnShareListener.callBack(SDKStatusCode.SHARE_SUCCESS, getLanguageContent(701));
            }

            @Override
            public void onPlatSdkShareFail(String msg) {
                mOnShareListener.callBack(SDKStatusCode.SHARE_FAIL, getLanguageContent(702));
            }

            @Override
            public void onPlatSdkShareCancel() {
                mOnShareListener.callBack(SDKStatusCode.SHARE_CANCEL, getLanguageContent(703));
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            mLoginMainDialog.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void onPause() {
        try {
            GoogleSdkApi.defaultSDK().onPause();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void onDestroy() {
        try {
            GoogleSdkApi.defaultSDK().onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getLanguageContent(int code) {
        return readTxtUtils.getItemContent(code, mLanguage);
    }

    public SdkInitSetting getSdkInitSetting() {
        return mSdkInitSetting;
    }

    public SDKCallBackListener getOnInitListener() {
        return mOnInitListener;
    }


    public SDKCallBackListener getOnLoginListener() {
        return mOnLoginListener;
    }

    public SDKCallBackListener getOnBindListener() {
        return mOnBindListener;
    }

    public SDKCallBackListener getOnPayListener() {
        return mOnPayListener;
    }

    public SDKCallBackListener getOnLogoutListener() {
        return mOnLogoutListener;
    }


}
