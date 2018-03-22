package com.overseas.exports.view.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * @author songjian:
 * @version 创建时间：2015-7-31 下午2:12:55 类说明
 */

public class OverseasWebChromeClient extends WebChromeClient {
    public static final int WEB_SEL_FILE_BY_OPEN_FILE_CHOOSER = 53001;
    public static final int WEB_SEL_FILE_BY_ON_SHOW_FILE_CHOOSER = 53002;

    private Context context;
    private ValueCallback<Uri> mUploadMsgByOpenFileChooser;
    private ValueCallback<Uri[]> mUploadMsgByOnShowFileChooser;

    public OverseasWebChromeClient(Context context) {
        this.context = context;
    }

    public final boolean onConsoleMessage(ConsoleMessage paramConsoleMessage) {
        return true;
    }

    public final void onReceivedTitle(WebView paramWebView, String paramString) {
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        Builder b2 = new Builder(context)
                .setTitle("提示").setMessage(message)
                .setPositiveButton("确定", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
        b2.setCancelable(false);
        b2.create();
        b2.show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        Builder builder = new Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("确定 ", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                result.cancel();
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        result.confirm();
        return super.onJsPrompt(view, url, message, message, result);
    }

    // 打开文件上传 (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (隐藏方法)
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, null);
    }

    // 打开文件上传 (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (隐藏方法)
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooser(uploadMsg, acceptType, null);
    }

    // 打开文件上传 (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (隐藏方法)
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openImageFileInput(uploadMsg, null, false, WEB_SEL_FILE_BY_OPEN_FILE_CHOOSER);
    }

    // 打开文件上传 (Android 5.0 (API level 21) +) (隐藏方法)
    @SuppressWarnings("all")
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (Build.VERSION.SDK_INT >= 21) {
            openImageFileInput(null, filePathCallback, fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE, WEB_SEL_FILE_BY_ON_SHOW_FILE_CHOOSER);
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("NewApi")
    protected void openImageFileInput(ValueCallback<Uri> uploadMsgByOpenFileChooser, ValueCallback<Uri[]> uploadMsgByOnShowFileChooser, boolean allowMultiple, int requestCode) {
        if (null == context || !(context instanceof Activity)) return;

        if (mUploadMsgByOpenFileChooser != null) {
            uploadMsgByOpenFileChooser.onReceiveValue(null);
        }
        mUploadMsgByOpenFileChooser = uploadMsgByOpenFileChooser;

        if (mUploadMsgByOnShowFileChooser != null) {
            mUploadMsgByOnShowFileChooser.onReceiveValue(null);
        }
        mUploadMsgByOnShowFileChooser = uploadMsgByOnShowFileChooser;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (allowMultiple && Build.VERSION.SDK_INT >= 18) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setType("image/*");
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, "选择图片"), requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == WEB_SEL_FILE_BY_OPEN_FILE_CHOOSER && null != mUploadMsgByOpenFileChooser) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                mUploadMsgByOpenFileChooser.onReceiveValue(data.getData());
                mUploadMsgByOpenFileChooser = null;
            } else {
                mUploadMsgByOpenFileChooser.onReceiveValue(null);
                mUploadMsgByOpenFileChooser = null;
            }
        } else if (requestCode == WEB_SEL_FILE_BY_ON_SHOW_FILE_CHOOSER && null != mUploadMsgByOnShowFileChooser) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                Uri[] dataUris = null;
                try {
                    if (!TextUtils.isEmpty(data.getDataString())) {
                        dataUris = new Uri[]{Uri.parse(data.getDataString())};
                    } else {
                        if (Build.VERSION.SDK_INT >= 16) {
                            if (data.getClipData() != null) {
                                int numSelectedFiles = data.getClipData().getItemCount();
                                dataUris = new Uri[numSelectedFiles];
                                for (int i = 0; i < numSelectedFiles; i++) {
                                    dataUris[i] = data.getClipData().getItemAt(i).getUri();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
                mUploadMsgByOnShowFileChooser.onReceiveValue(dataUris);
                mUploadMsgByOnShowFileChooser = null;
            } else {
                mUploadMsgByOnShowFileChooser.onReceiveValue(null);
                mUploadMsgByOnShowFileChooser = null;
            }
        }
    }

}
