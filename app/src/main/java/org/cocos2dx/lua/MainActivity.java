package org.cocos2dx.lua;

import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.d2eam.base.proj.R;
import com.overseas.exports.SDKStatusCode;
import com.overseas.exports.SdkInitSetting;
import com.overseas.exports.SdkManager;
import com.overseas.exports.sdk.SDKCallBackListener;

import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends FragmentActivity {
    private HashMap<String, String> payData;
    private Button mBtnLogin, mBtnPay, mBtnBindAccount;
    private boolean sdkInitStatus = false;
    private boolean sdkLoginStatus = false;
    private static PowerManager powerManager = null;
    private static PowerManager.WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initSdk();
        initView();

    }

    private void initSdk() {

        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SdkInitSetting sdkInitSetting = new SdkInitSetting();
                        sdkInitSetting.setChannelId("1001");
                        sdkInitSetting.setAppKey("5a64454887c086ecfde6bc814788adf2edfe823c6460bb61043d4bf188920995aa093f21ca728d2049ff27bded26ffda41a2f5d4c1fefdb6c496b007ca134329");
                        sdkInitSetting.setLanguage("language_zh_tw");// 设置语言 language_zh_tw 繁体，language_zh_cn 简体
                        SdkManager.defaultSDK().initSDK(MainActivity.this, sdkInitSetting, new SDKCallBackListener() {
                            @Override
                            public void callBack(int code, String msg) {
                                switch (code) {
                                    case SDKStatusCode.INIT_SUCCESS:
                                        // 初始化成功
                                        sdkInitStatus = true;
                                        showToast("msg = " + msg);
                                        // 登录
                                        break;
                                    case SDKStatusCode.INIT_FAIL:
                                        // 初始化失败
                                        sdkInitStatus = false;
                                        showToast(SdkManager.defaultSDK().getLanguageContent(111) + "：code= " + code + " _ msg=" + msg);
                                        break;
                                    default:
                                        // 初始化失败（其它错误）
                                        sdkInitStatus = false;
                                        String pMsg = String.format(Locale.getDefault(), "初始化失败（其它）：code=%d _ msg=%s", code, msg);
                                        showToast(pMsg);
                                        break;
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mBtnLogin = (Button) findViewById(R.id.login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sdkInitStatus) {
                    doLogin();
                } else {
                    showToast(SdkManager.defaultSDK().getLanguageContent(112));
                    initSdk();
                }
            }
        });

        mBtnPay = (Button) findViewById(R.id.pay);
        mBtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sdkLoginStatus) {
                    doPay();
                } else {
                    showToast(SdkManager.defaultSDK().getLanguageContent(233));
                }
            }
        });

        mBtnBindAccount = (Button) findViewById(R.id.btn_switchAccount);
        mBtnBindAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sdkLoginStatus) {
                    doBind();
                } else {
                    showToast(SdkManager.defaultSDK().getLanguageContent(233));
                }

            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

    }

    private boolean doLogin() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    SdkManager.defaultSDK().login(MainActivity.this, new SDKCallBackListener() {
                        @Override
                        public void callBack(int code, String msg) {
                            Log.e("D2eam", msg);
                            if (code == SDKStatusCode.LOGIN_SUCCESS) {
                                sdkLoginStatus = true;
                                mBtnLogin.setClickable(false);
                                Toast.makeText(MainActivity.this, SdkManager.defaultSDK().getLanguageContent(236), Toast.LENGTH_SHORT).show();
                            } else if (code == SDKStatusCode.LOGIN_CANCEL) {
                                sdkLoginStatus = false;
                                mBtnLogin.setClickable(true);
                                // Toast.makeText(MainActivity.this, "取消登录", Toast.LENGTH_SHORT).show();
                            } else {
                                sdkLoginStatus = false;
                                mBtnLogin.setClickable(true);
                                // Toast.makeText(MainActivity.this, "登录失败,msg = " + msg, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    private void doPay() {
        Log.e("ztq", "支付");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    SdkManager.defaultSDK().pay(MainActivity.this, 1, "dda", "300元宝", "1", 1, "asaa", new SDKCallBackListener() {
                        @Override
                        public void callBack(int code, String msg) {
                            switch (code) {
                                case SDKStatusCode.PAY_SUCCESS:
                                    Toast.makeText(MainActivity.this,
                                            "支付成功：" + msg, Toast.LENGTH_LONG).show();
                                    break;
                                case SDKStatusCode.PAY_ERROR:
                                    Toast.makeText(MainActivity.this,
                                            "支付失败：" + msg, Toast.LENGTH_LONG).show();
                                    break;
                                case SDKStatusCode.PAY_CANCEL:
                                    Toast.makeText(MainActivity.this, "支付已取消",
                                            Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doBind() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    SdkManager.defaultSDK().showUserCenter(MainActivity.this, new SDKCallBackListener() {
                        @Override
                        public void callBack(int code, String msg) {
                            switch (code) {
                                case SDKStatusCode.BIND_SUCCESS:
                                    Log.e("MainActivity", msg);
                                    mBtnLogin.setClickable(true);
                                    showToast(msg);
                                    break;
                                case SDKStatusCode.BIND_FAIL:
                                    Log.e("MainActivity", msg);
                                    mBtnLogin.setClickable(true);
                                    showToast(msg);
                                    break;
                                case SDKStatusCode.BIND_CANCEL:
                                    mBtnLogin.setClickable(true);
                                    Log.e("MainActivity", msg);
                                    showToast(msg);
                                    break;
                                case SDKStatusCode.LOGOUT:
                                    Log.e("MainActivity", msg);
                                    showToast(msg);
                                    mBtnLogin.setClickable(true);
                                    sdkLoginStatus = false;
                                    break;
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SdkManager.defaultSDK().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SdkManager.defaultSDK().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SdkManager.defaultSDK().onDestroy();
    }
}
