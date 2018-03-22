package com.overseas.exports.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UtilSharedPreferences {
    private static UtilSharedPreferences instance;
    private static SharedPreferences sharedPreferences;

    public UtilSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("6LGAMESDKSP", Context.MODE_PRIVATE);
    }

    public static synchronized UtilSharedPreferences getInstance(Context context) {
        if (instance == null || sharedPreferences == null) {
            instance = new UtilSharedPreferences(context);
        }
        return instance;
    }

    public void savaStatsUserLog(Context context, String time) {
        sharedPreferences.edit().putString("6l_game_sdk_userstatslog", time)
                .commit();
    }

    public String getStatsUserLog() {
        if (sharedPreferences == null)
            return null;
        return sharedPreferences.getString("6l_game_sdk_userstatslog", "");
    }

    public void savaDeviceId(Context context, String deviceId) {
        sharedPreferences.edit().putString("6l_game_sdk_deviceId", deviceId) .commit();
    }

    public String getDeviceId() {
        if (sharedPreferences == null)
            return null;
        return sharedPreferences.getString("6l_game_sdk_deviceId", "");
    }

    public void savaXiaomiNotify(Context context) {
        sharedPreferences.edit().putBoolean("6l_game_sdk_xiaomi_notify", true)
                .commit();
    }

    public boolean getXiaomiNotify() {
        if (sharedPreferences == null)
            return false;
        return sharedPreferences.getBoolean("6l_game_sdk_xiaomi_notify", false);
    }

    /**
     * 设置闪屏图片
     *
     * @param isPortrait true：竖屏, false：横屏
     * @param value
     */
    public void setSplashPicUrl(boolean isPortrait, String value) {
        String key = isPortrait ? "splash_pic_url_p" : "splash_pic_url_h";
        sharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 获取闪屏图片
     *
     * @param isPortrait true：竖屏, false：横屏
     */
    public String getSplashPicUrl(boolean isPortrait) {
        String key = isPortrait ? "splash_pic_url_p" : "splash_pic_url_h";
        return sharedPreferences.getString(key, null);
    }
}
