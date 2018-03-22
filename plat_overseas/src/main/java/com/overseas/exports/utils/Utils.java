package com.overseas.exports.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.overseas.exports.SdkManager;
import com.overseas.exports.common.util.CommonUtils;
import com.overseas.exports.dialog.WaitingDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {
    public static final String SDCARD_NAME = "overseas_game_sdk";
    private static Utils sdkUtils;
    private static long lastShowTime;
    private WaitingDialog waitingDialog;
    public static List<String> mAreaNameList, mAreaCodeList;

    private Utils() {
    }

    public static Utils getInstance() {
        if (sdkUtils == null) {
            synchronized (Utils.class) {
                if (sdkUtils == null) {
                    sdkUtils = new Utils();
                }
            }
        }
        return sdkUtils;
    }

    public void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public boolean isEntity(String str) {
        if (str != null && !"".equals(str)) {
            return false;
        }
        return true;
    }

    public static boolean isPhone(String paramString) {
        if (!TextUtils.isEmpty(paramString))
            return paramString.matches("1+[0-9]+[0-9]{9}");
        return false;
    }

    public boolean formatUser(Context context, String username) {
        if (TextUtils.isEmpty(username)) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(211));
            return false;
        }
        if (username.contains(" ") || username.length() > 14) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(214));
            return false;
        }
        return true;
    }

    public boolean formatPhoneCode(Context context, String code) {
        if (TextUtils.isEmpty(code)) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(228));
            return false;
        }
        if (code.length() <= 4 && code.length() >= 6) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(229));
            return false;
        }
        return true;
    }

    public boolean formatPhonePassword(Context context, String password) {
        if (TextUtils.isEmpty(password)) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(212));
            return false;
        }
        if (password.length() < 6 || password.length() > 12) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(213));
            return false;
        }
        return true;
    }

    public boolean checkNet(Context context) {
        if (hasConnectedNetwork(context))
            return true;
        if (System.currentTimeMillis() - lastShowTime > 3000) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(221));
            lastShowTime = System.currentTimeMillis();
        }
        return false;
    }

    public boolean hasConnectedNetwork(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = null;

            if ((mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE)) == null) {
                return false;
            }
            return mConnectivityManager.getActiveNetworkInfo() != null;
        } else {
            return false;
        }

    }

    public String getDeviceId(Context context) {
        String deviceId = UtilSharedPreferences.getInstance(context).getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = CommonUtils.getInstance().getDeviceId(context);
            UtilSharedPreferences.getInstance(context).savaDeviceId(context, deviceId);
        }
        return deviceId;
    }

    /**
     * 获取SDK统一WaitingDialog
     *
     * @param context
     * @param paramString
     * @return
     */
    public WaitingDialog getWaitingDialog(Context context, String paramString) {
        WaitingDialog mWaitingDialog = new WaitingDialog(context);
        mWaitingDialog.setText(paramString);
        return mWaitingDialog;
    }

    public void showProgress(Context context, String paramString) {
        this.waitingDialog = new WaitingDialog(context);
        this.waitingDialog.setText(paramString);
        if (!this.waitingDialog.isShowing())
            this.waitingDialog.show();
    }

    public void dismissProgress() {
        if ((this.waitingDialog != null) && (this.waitingDialog.isShowing()))
            this.waitingDialog.dismiss();
    }

    public boolean equalStr(String numOrStr) {
        boolean flag = true;
        char str = numOrStr.charAt(0);
        for (int i = 0; i < numOrStr.length(); i++) {
            if (str != numOrStr.charAt(i)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public boolean formatPass(Context context, String password) {
        if (TextUtils.isEmpty(password)) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(212));
            return false;
        }
        if (password.contains(" ")) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(222));
            return false;
        }
        if (password.length() < 6 || password.length() > 12) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(213));
            return false;
        }
        if (equalStr(password)) {
            Utils.getInstance().toast(context, SdkManager.defaultSDK().getLanguageContent(223));
            return false;
        }
        return true;
    }

    public boolean formatPassAgain(Context context, String password,
                                   String passwordAgain) {
        if (TextUtils.isEmpty(password)) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(212));
            return false;
        }
        if (password.contains(" ")) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(222));
            return false;
        }
        if (password.length() < 6 || password.length() > 12) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(213));
            return false;
        }
        if (!passwordAgain.equals(password)) {
            toast(context, SdkManager.defaultSDK().getLanguageContent(309));
            return false;
        }

        return true;
    }

    public int getScreenOrientation(Activity activity) {
        if (activity.getResources().getConfiguration().orientation == 2)
            return 2;
        if (activity.getResources().getConfiguration().orientation == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * 初始化地区数据
     */
    public void getAreaData(Activity activity) {
        try {
            mAreaNameList = new ArrayList<String>();
            mAreaCodeList = new ArrayList<String>();
            String area_code = ReadAssetTextUtil.readTextFromAsset(activity, "sdk_area_code.txt");
            JSONObject jsonObject = new JSONObject(area_code);
            if (!jsonObject.has("data")) {
                toast(activity, "{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                return;
            }
            String data = jsonObject.getString("data");
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject areaInfo = jsonArray.getJSONObject(i);
                mAreaNameList.add(areaInfo.getString("area"));
                mAreaCodeList.add(areaInfo.getString("code"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
