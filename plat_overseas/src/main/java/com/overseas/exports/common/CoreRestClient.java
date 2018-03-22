package com.overseas.exports.common;


import com.overseas.exports.common.volley.Request;
import com.overseas.exports.common.volley.VolleyError;
import com.overseas.exports.common.volley.custom.JsonStringRequest;
import com.overseas.exports.common.volley.custom.OnResponseListener;
import com.overseas.exports.common.volley.custom.QueryParameterRequest;

import java.util.Map;

/**
 * RESTful 核心请求类，使用Volley
 *
 * @author wanggang
 */
public class CoreRestClient {
    private static final VolleySingleton mVolleySingleton = VolleySingleton.getInstance();

    /**
     * 执行GET请求
     *
     * @param url
     * @param responseHandler
     */
    public static void executeGet(String url, BaseHttpResponseHandler responseHandler) {
        executeGet(url, null, responseHandler);
    }

    /**
     * 执行GET请求
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void executeGet(String url, Map<String, String> params, BaseHttpResponseHandler responseHandler) {
        if (null != responseHandler) responseHandler.onStart();
        mVolleySingleton.addRequestQueue(new QueryParameterRequest(url, params, new CustomOnResponseListener(responseHandler)));
    }

    /**
     * 执行POST请求
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void executePostQueryParameter(String url, Map<String, String> params, BaseHttpResponseHandler responseHandler) {
        if (null != responseHandler) responseHandler.onStart();
        mVolleySingleton.addRequestQueue(new QueryParameterRequest(Request.Method.POST, url, params, new CustomOnResponseListener(responseHandler)));
    }

    /**
     * 执行POST请求
     *
     * @param url
     * @param jsonStr
     * @param responseHandler
     */
    public static void executePostJson(String url, String jsonStr, BaseHttpResponseHandler responseHandler) {
        executePostJson(url, null, jsonStr, responseHandler);
    }

    /**
     * 执行POST请求
     *
     * @param url
     * @param headerMap
     * @param jsonStr
     * @param responseHandler
     */
    public static void executePostJson(String url, Map<String, String> headerMap, String jsonStr, BaseHttpResponseHandler responseHandler) {
        if (null != responseHandler) responseHandler.onStart();
        mVolleySingleton.addRequestQueue(new JsonStringRequest(url, headerMap, jsonStr, new CustomOnResponseListener(responseHandler)));
    }

    private static class CustomOnResponseListener implements OnResponseListener<byte[]> {
        private BaseHttpResponseHandler mResponseHandler;

        public CustomOnResponseListener(BaseHttpResponseHandler responseHandler) {
            this.mResponseHandler = responseHandler;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (null == mResponseHandler) return;

            // 回调失败
            mResponseHandler.onFailure(-1, null, null, error);
            // 回调成功
            mResponseHandler.onFinish();
        }

        @Override
        public void onResponse(int statusCode, Map<String, String> headers, byte[] data) {
            if (null == mResponseHandler) return;

            // 获取返回信息
            if (statusCode >= 200 && statusCode < 300) {
                mResponseHandler.onSuccess(statusCode, headers, data);
            } else {
                // 回调失败
                mResponseHandler.onFailure(statusCode, headers, data, null);
            }
            mResponseHandler.onFinish();
        }
    }

}
