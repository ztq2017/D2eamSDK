package com.overseas.exports.common;

import android.util.Log;

import java.util.Map;

public class TwitterRestClient extends CoreRestClient {

    public static void get(String url, Map<String, String> params, BaseHttpResponseHandler responseHandler) {
        executeGet(url, params, responseHandler);
    }

    public static void post(String url, Map<String, String> params, BaseHttpResponseHandler responseHandler) {
        executePostQueryParameter(url, params, responseHandler);
    }

    public static void post(String url, String jsonStr, BaseHttpResponseHandler responseHandler) {
        executePostJson(url, jsonStr, responseHandler);
    }
}
