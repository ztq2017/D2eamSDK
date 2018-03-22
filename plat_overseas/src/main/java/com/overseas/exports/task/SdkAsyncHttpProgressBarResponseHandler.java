package com.overseas.exports.task;

import java.util.Map;

import android.content.Context;

import com.overseas.exports.common.BaseHttpResponseHandler;
import com.overseas.exports.utils.Utils;

public abstract class SdkAsyncHttpProgressBarResponseHandler implements BaseHttpResponseHandler {

    private Context context;

    public SdkAsyncHttpProgressBarResponseHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onFailure(int statusCode, Map<String, String> headers,
                          byte[] responseBody, Throwable error) {
        if (responseBody != null) {
            System.out.println("onFailure:" + new String(responseBody));
        }
        Utils.getInstance().toast(context, "网络连接失败，请重试");
    }

    @Override
    public void onRetry(int retryNo) {
        Utils.getInstance().toast(context, "网络连接失败，请重试");
    }

    @Override
    public void onFinish() {
        Utils.getInstance().dismissProgress();
    }

}
