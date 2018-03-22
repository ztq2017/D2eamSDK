package com.overseas.exports.view;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.overseas.exports.SDKStatusCode;
import com.overseas.exports.SdkInitSetting;
import com.overseas.exports.SdkManager;
import com.overseas.exports.common.TwitterRestClient;
import com.overseas.exports.common.util.CommonUtils;
import com.overseas.exports.task.SdkAsyncHttpResponseHandler;
import com.overseas.exports.utils.SdkUrl;
import com.overseas.exports.utils.Utils;

import org.json.JSONObject;

import java.util.Map;

import static com.overseas.exports.SdkManager.OverseasTag;
import static com.overseas.exports.SdkManager.mGUid;
import static com.overseas.exports.common.util.CommonUtils.szAndroidID;
import static com.overseas.exports.common.util.CommonUtils.szBTMAC;
import static com.overseas.exports.common.util.CommonUtils.szDevIDShort;
import static com.overseas.exports.common.util.CommonUtils.szWLANMAC;

/**
 * 初始化sdk操作
 */
public class InitView {
    private Activity mActivity;
    private SdkInitSetting sdkInitSetting;
    private OnInitCompleteListener mOnInitCompleteListener;
    private String gUid = null;
    private String accList = "";
    private int sdkInitState = 0;

    public InitView(Activity activity, SdkInitSetting sdkInitSetting, OnInitCompleteListener onInitCompleteListener) {
        if (sdkInitState != 0) {
            Log.e(SdkManager.OverseasTag, "initSdk 重复初始化");
            sdkInitState = 0;
            return;
        }
        this.mActivity = activity;
        this.sdkInitSetting = sdkInitSetting;
        sdkInitState = -1;
        mOnInitCompleteListener = onInitCompleteListener;
        getData();
    }

    private void getData() {
        if (Utils.getInstance().isEntity(SdkManager.defaultSDK().getSdkInitSetting().getChannelId())) {
            SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, "ChannelId is null!!");
        }
        if (Utils.getInstance().isEntity(SdkManager.defaultSDK().getSdkInitSetting().getAppKey())) {
            SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, "AppKey is null !!");
        }
        if (Utils.getInstance().isEntity(SdkManager.defaultSDK().getSdkInitSetting().getLanguage())) {
            SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, "Language is null !");
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GetSDKUrl();
            }
        });
    }


    private void GetSDKUrl() {
        final JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("channelid", sdkInitSetting.getChannelId());
            jsonObj.put("appkey", sdkInitSetting.getAppKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        TwitterRestClient.post(SdkUrl.getSdkUrl(SdkUrl.USER_CHECKURL), jsonObj.toString(), new SdkAsyncHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                String errInfo = null != error ? " _ " + error.getMessage() : "";
                Toast.makeText(mActivity, SdkManager.defaultSDK().getLanguageContent(221), Toast.LENGTH_LONG).show();
                Log.e(OverseasTag, "errInfo = " + errInfo);
                SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, SdkManager.defaultSDK().getLanguageContent(113));
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, final byte[] response) {
                if (null == SdkManager.defaultSDK().getSdkInitSetting()) {
                    Toast.makeText(mActivity, SdkManager.defaultSDK().getLanguageContent(111) + "SdkInitSetting is null", Toast.LENGTH_LONG).show();
                    return;
                }
                if (statusCode == 200) {
                    try {
                        JSONObject json = new JSONObject(new String(response));
                        Log.e(OverseasTag, "json = " + json.toString());
                        if (json.has("logicurl")) {
                            SdkUrl.SDK_LOGIN_URL = json.getString("logicurl");
                            initSdk();
                        } else {
                            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(221));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, SdkManager.defaultSDK().getLanguageContent(111));
                }
            }
        });
    }

    private void initSdk() {
        if (sdkInitState != -1) {
            return;
        }
        // GET给服务器
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("channelid", sdkInitSetting.getChannelId());
            jsonObj.put("devicetype", java.net.URLEncoder.encode(android.os.Build.MODEL, "utf-8"));
            jsonObj.put("packver", CommonUtils.getVersionName(mActivity));
            jsonObj.put("packname", mActivity.getPackageName());
            jsonObj.put("appkey", sdkInitSetting.getAppKey());
            jsonObj.put("imei", CommonUtils.getInstance().getDeviceId());
            CommonUtils.getInstance().prepareDeviceInfo(mActivity);
            if (szDevIDShort != null && !szDevIDShort.equals("null"))
                jsonObj.put("devidshort", java.net.URLEncoder.encode(szDevIDShort, "utf-8"));
            else
                jsonObj.put("devidshort", "null");

            if (szAndroidID != null && !szAndroidID.equals("null"))
                jsonObj.put("androidid", java.net.URLEncoder.encode(szAndroidID, "utf-8"));
            else
                jsonObj.put("androidid", "null");
            if (szWLANMAC != null && !szWLANMAC.equals("null"))
                jsonObj.put("wlanmac", java.net.URLEncoder.encode(szWLANMAC, "utf-8"));
            else
                jsonObj.put("wlanmac", "null");
            if (szBTMAC != null && !szBTMAC.equals("null"))
                jsonObj.put("btmac", java.net.URLEncoder.encode(szBTMAC, "utf-8"));
            else
                jsonObj.put("btmac", "null");

        } catch (Exception e) {
            sdkInitState = 0;
            mGUid = "";
            e.printStackTrace();
            SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, "init fail1");
        }
        sdkInitState = -1;
        TwitterRestClient.post(SdkUrl.getSdkLoginUrl(SdkUrl.USER_INIT), jsonObj.toString(), new SdkAsyncHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                String errInfo = null != error ? " _ " + error.getMessage() : "";
                Toast.makeText(mActivity, SdkManager.defaultSDK().getLanguageContent(221), Toast.LENGTH_LONG).show();
                Log.e(OverseasTag, "errInfo = " + errInfo);
                SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, SdkManager.defaultSDK().getLanguageContent(113));
                GetSDKUrl();
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, final byte[] response) {
                if (null == SdkManager.defaultSDK().getSdkInitSetting()) {
                    Toast.makeText(mActivity, SdkManager.defaultSDK().getLanguageContent(111) + " SdkInitSetting is null", Toast.LENGTH_LONG).show();
                    return;
                }
                if (statusCode == 200) {
                    try {

                        JSONObject jsonObject = new JSONObject(new String(response));
                        Log.e("ztq", "jsonObject = " + jsonObject.toString());
                        if (jsonObject.getInt("ret") != 1) {
                            if (jsonObject.has("errInfo")) {
                                SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, SdkManager.defaultSDK().getLanguageContent(111) + jsonObject.getString("errInfo"));
                            } else if (jsonObject.has("errinfo")) {
                                SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, SdkManager.defaultSDK().getLanguageContent(111) + jsonObject.getString("errinfo"));
                            } else {
                                SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, SdkManager.defaultSDK().getLanguageContent(225));
                            }
                        } else {
                            sdkInitState = 1;
                            gUid = jsonObject.getString("guid");
                            if (jsonObject.has("acclist")) {
                                accList = jsonObject.getString("acclist");
                            }
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (null != mOnInitCompleteListener) {
                                        mOnInitCompleteListener.onInitComplete(accList, gUid);
                                    }
                                    SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_SUCCESS, SdkManager.defaultSDK().getLanguageContent(110));
                                }
                            }, 2000);
                        }
                    } catch (Exception e) {
                        sdkInitState = 0;
                        mGUid = "";
                        e.printStackTrace();
                        SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, "init fail2.");
                    }
                } else {
                    Toast.makeText(mActivity, "init fail statusCode(2) = " + statusCode, Toast.LENGTH_LONG).show();
                    SdkManager.defaultSDK().getOnInitListener().callBack(SDKStatusCode.INIT_FAIL, SdkManager.defaultSDK().getLanguageContent(111));
                }
            }
        });

    }

    public interface OnInitCompleteListener {
        void onInitComplete(String accList, String gUid);
    }
}
