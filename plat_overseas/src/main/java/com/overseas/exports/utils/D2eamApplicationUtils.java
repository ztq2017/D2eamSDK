package com.overseas.exports.utils;

import android.app.Application;

import com.overseas.exports.common.VolleySingleton;
import com.overseas.exports.common.util.UtilResources;
import com.overseas.exports.crash.CrashHandler;


public final class D2eamApplicationUtils {

    public static void onCreate(Application application) {
        // 初始化Volley框架
        VolleySingleton.initialize(application);
        // 初始化Crash
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(application);
        UtilResources.initResourcesContext(application);
    }

}
