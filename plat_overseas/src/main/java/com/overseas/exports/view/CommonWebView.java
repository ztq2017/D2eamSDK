package com.overseas.exports.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.overseas.exports.view.web.OverseasWebChromeClient;
import com.overseas.exports.view.web.OverseasWebViewClient;
import com.overseas.exports.view.web.OverseasWebViewDownLoadListener;


public class CommonWebView extends WebView {
    private Activity mActivity;
    private String mUrl;
    private OverseasWebChromeClient mOverseasWebChromeClient;

    public CommonWebView(Activity activity, String url) {
        super(activity);
        mActivity = activity;
        mUrl = url;
        initView();
    }

    private void initView() {
        configWebView();
        loadUrl(mUrl);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void configWebView() {
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);

        setWebChromeClient(mOverseasWebChromeClient = new OverseasWebChromeClient(mActivity));
        setWebViewClient(new OverseasWebViewClient(mActivity, true, true));
        setDownloadListener(new OverseasWebViewDownLoadListener(mActivity));
        setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (getUrl().contains("_6lyx_init_home")) {
                        mActivity.finish();
                    } else {
                        goBack();
                    }
                    return true;
                }
                return false;
            }
        });
        requestFocus();
        clearCache(true);
        clearHistory();
        clearFormData();
    }

    /**
     * onActivityResult
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != mOverseasWebChromeClient) {
            mOverseasWebChromeClient.onActivityResult(requestCode, resultCode, data);
        }
    }


}
