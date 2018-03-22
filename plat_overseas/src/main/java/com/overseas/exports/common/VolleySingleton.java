package com.overseas.exports.common;

import android.annotation.SuppressLint;
import android.content.Context;

import com.overseas.exports.common.volley.DefaultRetryPolicy;
import com.overseas.exports.common.volley.HTTPSTrustManager;
import com.overseas.exports.common.volley.Request;
import com.overseas.exports.common.volley.RequestQueue;
import com.overseas.exports.common.volley.toolbox.Volley;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQuene;

    public static VolleySingleton initialize(Context context) {
        if (mInstance == null) {
            synchronized (VolleySingleton.class) {
                if (mInstance == null) {
                    mInstance = new VolleySingleton(context);
                }
            }
        }
        return mInstance;
    }

    public static VolleySingleton getInstance() {
        if (null == mInstance) throw new IllegalArgumentException("no initialize");
        return mInstance;
    }

    private VolleySingleton(Context context) {
        HTTPSTrustManager.allowAllSSL();//信任所有证书
        mRequestQuene = Volley.newRequestQueue(context);
        HTTPSTrustManager.handleSSLHandshake();
    }

    public RequestQueue getRequestQuene() {
        return mRequestQuene;
    }

    public <T> void addRequestQueue(Request<T> request) {
        // 统一设置超时时间及重试次数
        request.setRetryPolicy(new DefaultRetryPolicy(8 * 1000, 1, 1.0f));
        getRequestQuene().add(request);
    }

    public void cancelRequest(Object object) {
        getRequestQuene().cancelAll(object);
    }

}