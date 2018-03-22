package com.overseas.exports.task;

import com.overseas.exports.common.BaseHttpResponseHandler;

import java.util.Map;


public abstract class SdkAsyncHttpResponseHandler implements BaseHttpResponseHandler {

    @Override
    public void onStart() {
    }

    @Override
    public void onFailure(int statusCode, Map<String, String> headers,
                          byte[] responseBody, Throwable error) {
        if (responseBody != null) {
            System.out.println("onFailure:" + new String(responseBody));
        }
    }

    @Override
    public void onRetry(int retryNo) {

    }

    @Override
    public void onFinish() {
    }

}
