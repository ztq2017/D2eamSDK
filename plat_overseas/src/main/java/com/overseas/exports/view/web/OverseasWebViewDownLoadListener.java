package com.overseas.exports.view.web;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;

public class OverseasWebViewDownLoadListener implements DownloadListener {
    private Activity activity;

    public OverseasWebViewDownLoadListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onDownloadStart(String url, String userAgent,
                                String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        this.activity.startActivity(intent);
    }

}
