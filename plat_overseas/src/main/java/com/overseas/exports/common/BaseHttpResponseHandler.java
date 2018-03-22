package com.overseas.exports.common;

import java.util.Map;

/**
 * http响应回调基类
 *
 * @author wanggang
 */
public interface BaseHttpResponseHandler {

    void onStart();

    void onRetry(int retryNo);

    void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error);

    void onSuccess(int statusCode, Map<String, String> headers, byte[] response);

    void onFinish();

}
