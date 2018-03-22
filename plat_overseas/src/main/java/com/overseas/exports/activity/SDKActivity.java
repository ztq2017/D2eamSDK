package com.overseas.exports.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.overseas.exports.utils.SdkPosition;
import com.overseas.exports.view.CommonWebView;

public class SDKActivity extends Activity {
    public static final String INTENT_KEY_WEB_VIEW_URL = "intent_key_web_view_url";

    private CommonWebView mCommonWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle paramBundle = getIntent().getExtras();
        int keyPosition = paramBundle.getInt(SdkPosition.KEY_POSITION);

        switch (keyPosition) {
            case SdkPosition.POSITION_LOGIN: // 游戏登录
                //new LoginMainDialog(SDKActivity.this).show();
                break;
            case SdkPosition.POSITION_PAY_GAME_MAIN: // 游戏充值
                break;
            case SdkPosition.POSITION_COMMON_WEBVIEW: // 公共WebView

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mCommonWebView = new CommonWebView(this, paramBundle.getString(INTENT_KEY_WEB_VIEW_URL));
                setContentView(mCommonWebView);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != mCommonWebView) {
            mCommonWebView.onActivityResult(requestCode, resultCode, data);
        }
    }

}
