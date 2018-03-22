package com.overseas.exports.utils;

import android.app.Activity;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2018/1/19.
 */

public class ReadAssetTextUtil {

    public static String readTextFromAsset(Activity activity, String filename) throws Exception {
        AssetManager am = activity.getAssets();
        InputStream is = am.open(filename);
        InputStreamReader reader = new InputStreamReader(is, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }

        return buffer.toString();
    }

}
