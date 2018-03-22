package com.overseas.exports.view.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.overseas.exports.dialog.WaitingDialog;


/**
 * @author songjian:
 * @version 创建时间：2015-7-31 下午2:17:36 支付webview com.downjoy.b.c.be
 */
public class OverseasWebViewClient extends WebViewClient {
    private WaitingDialog waitingDialog;
    private Context context;
    private boolean isShowProgress;
    private boolean isProgressCancelable;

    public OverseasWebViewClient(Context context, boolean isShowProgress, boolean isProgressCancelable) {
        this.context = context;
        this.isShowProgress = isShowProgress;
        this.isProgressCancelable = isProgressCancelable;
    }

    private void showProgress(Context context, String paramString) {
        if (!isShowProgress) return;

        if (this.waitingDialog == null) {
            this.waitingDialog = new WaitingDialog(context, isProgressCancelable);
        }

        this.waitingDialog.setText(paramString);
        if (!this.waitingDialog.isShowing()) {
            this.waitingDialog.show();
        }
    }

    private void dismissProgress() {
        if (!isShowProgress) return;

        if ((this.waitingDialog != null) && (this.waitingDialog.isShowing()))
            this.waitingDialog.dismiss();
    }

    @Override
    public void onLoadResource(WebView paramWebView, String paramString) {
        super.onLoadResource(paramWebView, paramString);
    }

    @Override
    public void onReceivedError(WebView paramWebView, int paramInt, String paramString1, String paramString2) {
        super.onReceivedError(paramWebView, paramInt, paramString1, paramString2);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        dismissProgress();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        showProgress(context, "加载中");
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 处理电话
        if (url.startsWith("tel:") && null != context && context instanceof Activity) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            context.startActivity(intent);
            return true;
        }

        // 处理微信
        if (url.startsWith("weixin://") && null != context && context instanceof Activity) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
            return true;
        }

        return super.shouldOverrideUrlLoading(view, url);
    }
}
