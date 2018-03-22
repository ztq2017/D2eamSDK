package com.overseas.exports;

import android.app.Application;

import com.overseas.exports.utils.D2eamApplicationUtils;

/**
 * Created by Administrator on 2018/1/9.
 */

public class D2eamApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        D2eamApplicationUtils.onCreate(this);
    }
}
