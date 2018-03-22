package com.overseas.exports.view.user;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.overseas.exports.SDKStatusCode;
import com.overseas.exports.SdkManager;
import com.overseas.exports.common.TwitterRestClient;
import com.overseas.exports.common.util.UtilResources;
import com.overseas.exports.dialog.BaseSdkDialog;
import com.overseas.exports.dialog.TipsDialog;
import com.overseas.exports.platfrom.FacebookSdkApi;
import com.overseas.exports.platfrom.GoogleSdkApi;
import com.overseas.exports.task.SdkAsyncHttpStandardResponseHandler;
import com.overseas.exports.utils.SdkUrl;
import com.overseas.exports.utils.UserCookies;
import com.overseas.exports.utils.Utils;
import com.overseas.exports.view.InitView;
import com.overseas.exports.widget.LoginDialogBackImpl;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.overseas.exports.utils.SdkUrl.USER_LOGIN_PLAT_ACC;


public class LoginMainDialog extends BaseSdkDialog implements View.OnClickListener {
    private static final int VIEW_MODE_INITIALIZE = 0;
    private static final int VIEW_MODE_CHOOSE_LOGIN_VIEW = 1;
    private static final int VIEW_MODE_VISITOR_LOGIN_VIEW = 2; //  游客登录
    private static final int VIEW_MODE_9453PLAY_LOGIN_VIEW = 3; // 手机号登录
    private static final int VIEW_MODE_FACEBOOK_LOGIN_VIEW = 4; // FACEBOOK
    private static final int VIEW_MODE_GOOGLE_LOGIN_VIEW = 5; //google
    private static final int VIEW_MODE_FORGET_PASSWORD = 6; //忘记密码
    private static final int VIEW_MODE_REG_NEW_PHONE = 7; //注册新账号
    private static final int VIEW_MODE_CHANGE_PASSWORD = 8; //修改密码
    private static final int VIEW_MODE_ACCOUNT_INFO_VIEW = 9; //帳號資訊
    private static final int VIEW_MODE_BIND_NEW_PHONE = 10; //绑定新账号
    private static final int VIEW_MODE_AUTO_LOGIN_VIEW = 11;

    @SuppressLint("StaticFieldLeak")
    private static FragmentActivity mActivity;
    private FrameLayout mFrameLayoutSubView;
    private ChooseLoginView mChooseLoginView;
    private UserLoginView mUserLoginView;
    private ForgetPasswordView mForgetPasswordView;
    private RegPhoneView mRegPhoneView;
    private BindPhoneAccView mBindPhoneAccView;
    private ChangePasswordView mChangePasswordView;
    private int mCurViewMode = VIEW_MODE_INITIALIZE;
    private boolean isLoginDialogAutoCancel = false;
    private ImageView mClose;//mBack;
    private String mGUid, mAccList;
    private String mType;
    private String mGuestUserId;
    private static int loginType = 1;
    private TipsDialog tipsDialog;

    static class AccInfo {
        String gameid;
        String bindAcc;

    }

    private ArrayList<AccInfo> mGameIDList = new ArrayList<>();
    private LinearLayout.LayoutParams llLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    public LoginMainDialog(FragmentActivity activity, String accList, String gUid, String type) {
        super(activity, UtilResources.getStyleId("dialog_style"));
        mActivity = activity;
        mType = type;
        mGUid = gUid;
        mAccList = accList;
        initViews();
    }

    private void initViews() {
        mCurViewMode = VIEW_MODE_CHOOSE_LOGIN_VIEW;
        View mMainRootView = View.inflate(mActivity, UtilResources.getLayoutId("login_dialog_main_view"), null);
        setContentView(mMainRootView);
        mClose = (ImageView) findViewById(UtilResources.getId("iv_closeIcon"));
        mClose.setOnClickListener(this);
        // 获取屏幕的宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams layoutParams;
        //根据横竖屏显示Dialog大小
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutParams = new FrameLayout.LayoutParams((int) (screenHeight * 0.9f), (int) (screenHeight * 0.9f));//(int) (screenHeight * 0.85f)
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(0, 0, 0, (int) ((screenHeight - (int) (screenHeight * 0.9f)) * 0.2f));
        } else {
            layoutParams = new FrameLayout.LayoutParams((int) (screenWidth * 0.94f), (int) (screenWidth * 0.94f));
            layoutParams.gravity = Gravity.CENTER;
            //layoutParams.setMargins(0, (int) ((screenHeight - (int) (screenHeight * 0.8f)) * 0.8f), 0, (int) ((screenHeight - (int) (screenHeight * 0.8f)) * 0.2f));
        }
        layoutParams.gravity = Gravity.CENTER;
        mMainRootView.setLayoutParams(layoutParams);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setCanceledOnTouchOutside(false);

        // 判断登录token是否过期
        String loginTokenTime = UserCookies.getInstance(mActivity).getLoginTokenTime();
        long saveLoginTokenTime = Long.parseLong(loginTokenTime);
        long currentTime = System.currentTimeMillis();
        long tokenTime = (currentTime - saveLoginTokenTime) / (1000 * 60 * 60 * 24);


        mFrameLayoutSubView = (FrameLayout) findViewById(UtilResources.getId("fl_subView"));
        loginType = 1;
        mChooseLoginView = new ChooseLoginView(mActivity, mAccList, loginType, onLoginClickListener);
        // 判断是否是登录，是，弹出选择登录的页面；否，则弹出切换用户界面
        // 判断是否是第一次登录，是，弹出选择登录的页面；否，则自动登录
        if (mType.equals("login") && !UserCookies.getInstance(mActivity).isLogin()) {
            mCurViewMode = VIEW_MODE_CHOOSE_LOGIN_VIEW;
            mFrameLayoutSubView.addView(mChooseLoginView);
        } else if (mType.equals("showUserCenter") && UserCookies.getInstance(mActivity).isLogin()) {
            if (!UserCookies.getInstance(mActivity).isLogin()) {
                Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(233));
            } else {
                UserCenterView userCenterView = new UserCenterView(mActivity, new UserCenterView.OnBindLoginListener() {
                    @Override
                    public void onBindLoginListener(String userId) {
                        mGuestUserId = userId;
                        loginType = 2;
                        mChooseLoginView = new ChooseLoginView(mActivity, mAccList, loginType, onLoginClickListener);
                        toChooseLoginView();
                    }
                }, new UserCenterView.OnSwitchAccountListener() {
                    @Override
                    public void onSwitchAccountListener() {
                        UserCookies.getInstance(mActivity).clearUserInfo();
                        SdkManager.defaultSDK().getOnLogoutListener().callBack(SDKStatusCode.LOGOUT, SdkManager.defaultSDK().getLanguageContent(237));
                        cancel();
                    }
                });
                mCurViewMode = VIEW_MODE_ACCOUNT_INFO_VIEW;
                mFrameLayoutSubView.removeAllViews();
                mFrameLayoutSubView.addView(userCenterView);
            }
        } else if (UserCookies.getInstance(mActivity).isLogin()) {
            if (tokenTime > 7) {
                mCurViewMode = VIEW_MODE_CHOOSE_LOGIN_VIEW;
                UserCookies.getInstance(mActivity).clearUserInfo();
                Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(115));
                toChooseLoginView();
            } else {
                mCurViewMode = VIEW_MODE_AUTO_LOGIN_VIEW;
                mMainRootView.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SdkUrl.PAY_URL = UserCookies.getInstance(mActivity).getPayUrl();
                        String result = String.format("{userId = '%s', token = '%s', userType = %s}", UserCookies.getInstance(mActivity).getUserID(), UserCookies.getInstance(mActivity).getToken(), UserCookies.getInstance(mActivity).getLoginWay());
                        loginSuccess(result.replace("'", "\""));
                    }
                }, 3000);
            }
        }
        setDialogBackListener(new LoginDialogBackImpl(this));
        setOnDismissListener(new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!isLoginDialogAutoCancel) {
                    // 退出登录
                    // Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(114));
                    dismiss();
                }
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mCurViewMode == VIEW_MODE_AUTO_LOGIN_VIEW || mCurViewMode == VIEW_MODE_VISITOR_LOGIN_VIEW) {
                        // 自动登录中，让实体返回键失效
                        return true;
                    }

                    if (mCurViewMode != VIEW_MODE_INITIALIZE && mCurViewMode != VIEW_MODE_CHOOSE_LOGIN_VIEW) {
                        // 非初始化、选择登录方式界面时，使用回到选择登录方式界面
                        toChooseLoginView();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mClose) {
            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(114));
            dismiss();
        }
    }

    private void toChooseLoginView() {
        mCurViewMode = VIEW_MODE_CHOOSE_LOGIN_VIEW;
        mFrameLayoutSubView.removeAllViews();
        mFrameLayoutSubView.addView(mChooseLoginView);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FacebookSdkApi.defaultSDK().onActivityResult(requestCode, resultCode, data);
        GoogleSdkApi.defaultSDK().onActivityResult(requestCode, resultCode, data);
    }

    private ChooseLoginView.OnLoginClickListener onLoginClickListener = new ChooseLoginView.OnLoginClickListener() {
        @Override
        public void onLoginClickListener(int loginMode) {
            if (loginMode == ChooseLoginView.LOGIN_TYPE_VISITOR) {
                mCurViewMode = VIEW_MODE_VISITOR_LOGIN_VIEW;
                Utils.getInstance().showProgress(mActivity, SdkManager.defaultSDK().getLanguageContent(226));
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("guid", mGUid);
                    TwitterRestClient.post(SdkUrl.getSdkLoginUrl(SdkUrl.USER_LOGIN_QUICK), jsonObj.toString(), new SdkAsyncHttpStandardResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                            Utils.getInstance().dismissProgress();
                            SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(113));
                        }

                        @Override
                        public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {
                            Utils.getInstance().dismissProgress();
                            if (statusCode == 200 && response != null) {
                                try {
                                    final JSONObject json = new JSONObject(new String(response));
                                    if (!json.has("ret")) {
                                        Utils.getInstance().toast(mActivity, "{err=[[" + SdkManager.defaultSDK().getLanguageContent(221) + "_1002]]}");
                                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, "{err=[[" + SdkManager.defaultSDK().getLanguageContent(221) + "_1002]]}");
                                        return;
                                    }
                                    if (json.getInt("ret") != 1) {
                                        return;
                                    }
                                    int ret = Integer.parseInt(json.getString("ret"));
                                    if (ret == 1) {
                                        tipsDialog = new TipsDialog(mActivity, SdkManager.defaultSDK().getLanguageContent(116), new TipsDialog.OnSureListener() {
                                            @Override
                                            public void onSureListener() {
                                                //登录成功
                                                tipsDialog.dismiss();
                                                String token = "";
                                                try {
                                                    String id = json.getString("gameid");
                                                    String acc = "null";
                                                    if (json.has("bindacc"))
                                                        acc = json.getString("bindacc");
                                                    if (json.has("token")) {
                                                        token = json.getString("token");
                                                    }
                                                    if (json.has("payurl"))
                                                        SdkUrl.PAY_URL = json.getString("payurl");
                                                    UserCookies.getInstance(mActivity).savePayUrl(json.getString("payurl"));
                                                    UserCookies.getInstance(mActivity).saveTokenTime(System.currentTimeMillis() + "");
                                                    SortAccList(id, acc);
                                                    @SuppressLint("DefaultLocale")
                                                    String result = String.format("{userId = '%s', token = '%s', userType = %s}", id, token, "GUEST");
                                                    UserCookies.getInstance(mActivity).saveUserInfo(id, id, token, "GUEST", "", true, false);
                                                    loginSuccess(result.replace("'", "\""));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        tipsDialog.show();

                                    } else if (ret == 2008) {
                                        new InitView(mActivity, SdkManager.defaultSDK().getSdkInitSetting(), new InitView.OnInitCompleteListener() {
                                            @Override
                                            public void onInitComplete(String accList, String gUid) {
                                                mGUid = gUid;
                                                mAccList = accList;
                                            }
                                        });
                                    } else {
                                        Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(224) + "：" + new String(response));
                                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224) + "：" + new String(response));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.getInstance().toast(mActivity, "登录失败，请重试(1)...");
                            }
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            Utils.getInstance().dismissProgress();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (loginMode == ChooseLoginView.LOGIN_TYPE_9453PLAY) {
                // 手机登录
                if (loginType == 2) {
                    //手机号注册
                    if (null == mBindPhoneAccView) {
                        mBindPhoneAccView = new BindPhoneAccView(mActivity, mGUid, new BindPhoneAccView.OnBindPhoneClickListener() {
                            @Override
                            public void onBindPhoneClickListener(String msg) {
                                bindAcc(msg);
                            }
                        });
                    }
                    mCurViewMode = VIEW_MODE_BIND_NEW_PHONE;
                    mFrameLayoutSubView.removeAllViews();
                    mFrameLayoutSubView.addView(mBindPhoneAccView);
                } else {
                    if (null == mUserLoginView) {
                        mUserLoginView = new UserLoginView(mActivity, mGUid, userOnLoginClickListener, new UserLoginView.OnLoginSuccessListener() {
                            @Override
                            public void onLoginSuccess(String msg) {
                                loginSuccess(msg);
                            }

                            @Override
                            public void onLoginFail(String msg) {

                                showTipsDialog(SdkManager.defaultSDK().getLanguageContent(224) + "：" + msg);
                                SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224) + "：" + msg);
                            }
                        });
                    }
                    mCurViewMode = VIEW_MODE_9453PLAY_LOGIN_VIEW;
                    mFrameLayoutSubView.removeAllViews();
                    mFrameLayoutSubView.addView(mUserLoginView);
                }
            } else if (loginMode == ChooseLoginView.LOGIN_TYPE_FACEBOOK) {
                mCurViewMode = VIEW_MODE_FACEBOOK_LOGIN_VIEW;
                if (mGUid.isEmpty() || mGUid.equals("")) {
                    Utils.getInstance().showProgress(mActivity, SdkManager.defaultSDK().getLanguageContent(112));
                    return;
                }
                Utils.getInstance().showProgress(mActivity, SdkManager.defaultSDK().getLanguageContent(226));
                FacebookSdkApi.defaultSDK().login(new FacebookSdkApi.PlatSdkCallBackListener() {
                    @Override
                    public void onPlatSdkLoginSuccess(final String userId, final String mToken) {
                        if (userId != null) {
                            if (loginType == 2) {
                                String result = String.format("{userType = '%s',platacc = '%s',excode = '%s'}", "facebook", userId, mToken);
                                bindAcc(result.replace("'", "\""));

                            } else {
                                try {
                                    JSONObject jsonObj = new JSONObject();
                                    jsonObj.put("guid", mGUid);
                                    jsonObj.put("platacc", userId);
                                    jsonObj.put("excode", mToken);
                                    TwitterRestClient.post(SdkUrl.getSdkLoginUrl(USER_LOGIN_PLAT_ACC + "FaceBook"), jsonObj.toString(), new SdkAsyncHttpStandardResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {
                                            Utils.getInstance().dismissProgress();
                                            String msg = "";
                                            if (statusCode == 200) {
                                                try {
                                                    JSONObject json = new JSONObject(new String(response));
                                                    Log.e("ztq", "sss = " + json.toString());
                                                    if (!json.has("ret")) {
                                                        Utils.getInstance().toast(mActivity, "{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, "{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                                        return;
                                                    }

                                                    int ret = Integer.parseInt(json.getString("ret"));
                                                    String token = "";
                                                    if (ret == 1) {
                                                        //登录成功
                                                        String id = json.getString("gameid");
                                                        String acc = "null";
                                                        if (json.has("bindacc"))
                                                            acc = json.getString("bindacc");
                                                        if (json.has("token")) {
                                                            token = json.getString("token");
                                                        }
                                                        if (json.has("payurl"))
                                                            SdkUrl.PAY_URL = json.getString("payurl");
                                                        UserCookies.getInstance(mActivity).savePayUrl(json.getString("payurl"));
                                                        UserCookies.getInstance(mActivity).saveTokenTime(System.currentTimeMillis() + "");
                                                        SortAccList(id, acc);
                                                        @SuppressLint("DefaultLocale")
                                                        String result = String.format("{userId = '%s', token = '%s', userType = '%s'}", id, token, "FACEBOOK");
                                                        loginSuccess(result.replace("'", "\""));
                                                        UserCookies.getInstance(mActivity).saveUserInfo(id, acc, token, "FACEBOOK", "", false, true);
                                                    } else if (ret == 2801) {
                                                        Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(224) + "：" + SdkManager.defaultSDK().getLanguageContent(12801));
                                                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224) + "：" + SdkManager.defaultSDK().getLanguageContent(12801));
                                                    } else if (ret == 0) {
                                                        if (json.has("errinfo")) {
                                                            msg = json.getString("errinfo");
                                                        }
                                                        Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(224) + "：" + msg);
                                                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224) + "：" + msg);
                                                    } else if (ret == 2008) {
                                                        new InitView(mActivity, SdkManager.defaultSDK().getSdkInitSetting(), new InitView.OnInitCompleteListener() {
                                                            @Override
                                                            public void onInitComplete(String accList, String gUid) {
                                                                mGUid = gUid;
                                                                mAccList = accList;
                                                            }
                                                        });
                                                    } else {
                                                        Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(224) + "：" + new String(response));
                                                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224) + "：" + new String(response));
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(224) + "：" + e.getMessage());
                                                    SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224) + "：" + e.getMessage());
                                                }
                                            } else {
                                                Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(224));
                                                SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224));
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                                            SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(113) + new String(responseBody));
                                        }

                                        @Override
                                        public void onFinish() {
                                            super.onFinish();
                                            Utils.getInstance().dismissProgress();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onPlatSdkLoginFail(String msg) {
                        Utils.getInstance().dismissProgress();
                        Utils.getInstance().toast(mActivity, "FaceBook " + SdkManager.defaultSDK().getLanguageContent(1106));
                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, "FaceBook " + SdkManager.defaultSDK().getLanguageContent(1106));
                    }

                    @Override
                    public void onPlatSdkLoginCancel() {
                        Utils.getInstance().dismissProgress();
                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_CANCEL, "FaceBook " + SdkManager.defaultSDK().getLanguageContent(234));
                    }
                });

            } else if (loginMode == ChooseLoginView.LOGIN_TYPE_GOOGLE) {
                mCurViewMode = VIEW_MODE_GOOGLE_LOGIN_VIEW;
                Utils.getInstance().showProgress(mActivity, SdkManager.defaultSDK().getLanguageContent(226));
                GoogleSdkApi.defaultSDK().login(mActivity, new GoogleSdkApi.PlatLoginSdkCallBackListener() {
                    @Override
                    public void onPlatSdkLoginSuccess(final String userId, final String mToken) {
                        if (userId != null) {
                            if (loginType == 2) {
                                String result = String.format("{userType = '%s',platacc = '%s',excode = '%s'}", "google", userId, mToken);
                                bindAcc(result.replace("'", "\""));
                            } else {
                                try {
                                    JSONObject jsonObj = new JSONObject();
                                    jsonObj.put("guid", mGUid);
                                    jsonObj.put("platacc", userId);
                                    jsonObj.put("excode", mToken);
                                    TwitterRestClient.post(SdkUrl.getSdkLoginUrl(USER_LOGIN_PLAT_ACC + "Google"), jsonObj.toString(), new SdkAsyncHttpStandardResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {
                                            Utils.getInstance().dismissProgress();
                                            String msg = "";
                                            if (statusCode == 200) {
                                                try {
                                                    JSONObject json = new JSONObject(new String(response));
                                                    if (!json.has("ret")) {
                                                        Utils.getInstance().toast(mActivity, "{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, "{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                                        return;
                                                    }

                                                    int ret = Integer.parseInt(json.getString("ret"));
                                                    String token = "";
                                                    if (ret == 1) {
                                                        //登录成功
                                                        String id = json.getString("gameid");
                                                        String acc = "null";
                                                        if (json.has("bindacc"))
                                                            acc = json.getString("bindacc");
                                                        String isnew = "0";
                                                        if (json.has("new"))
                                                            isnew = json.getString("new");
                                                        if (json.has("token")) {
                                                            token = json.getString("token");
                                                        }
                                                        if (json.has("payurl"))
                                                            SdkUrl.PAY_URL = json.getString("payurl");
                                                        UserCookies.getInstance(mActivity).savePayUrl(json.getString("payurl"));
                                                        UserCookies.getInstance(mActivity).saveTokenTime(System.currentTimeMillis() + "");
                                                        SortAccList(id, acc);
                                                        @SuppressLint("DefaultLocale")
                                                        String result = String.format("{userId = '%s', token = '%s', userType = '%s'}", id, token, "GOOGLE");
                                                        loginSuccess(result.replace("'", "\""));
                                                        UserCookies.getInstance(mActivity).saveUserInfo(id, acc, token, "GOOGLE", "", false, true);
                                                    } else if (ret == 0) {
                                                        if (json.has("errinfo")) {
                                                            msg = json.getString("errinfo");
                                                        }
                                                        Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(224) + "：" + msg);
                                                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224) + "：" + msg);
                                                    } else if (ret == 2008) {
                                                        new InitView(mActivity, SdkManager.defaultSDK().getSdkInitSetting(), new InitView.OnInitCompleteListener() {
                                                            @Override
                                                            public void onInitComplete(String accList, String gUid) {
                                                                mGUid = gUid;
                                                                mAccList = accList;
                                                            }
                                                        });
                                                    } else {
                                                        Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(224) + "：" + new String(response));
                                                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224) + "：" + new String(response));
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224) + "：" + e.getMessage());
                                                }
                                            } else {
                                                Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(224));
                                                SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(224));
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                                            SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(113));
                                        }

                                        @Override
                                        public void onFinish() {
                                            super.onFinish();
                                            Utils.getInstance().dismissProgress();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onPlatSdkLoginFail(String msg) {
                        Utils.getInstance().dismissProgress();
                        Utils.getInstance().toast(mActivity, "Google " + SdkManager.defaultSDK().getLanguageContent(1106));
                        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, "Google " + SdkManager.defaultSDK().getLanguageContent(1106));
                    }
                });
            }
        }
    };
    private UserLoginView.OnLoginClickListener userOnLoginClickListener = new UserLoginView.OnLoginClickListener() {
        @Override
        public void onLoginClickListener(int loginMode) {
            if (loginMode == UserLoginView.LOGIN_USER_FORGET_PASSWORD) {
                //忘记密码
                if (null == mForgetPasswordView) {
                    mForgetPasswordView = new ForgetPasswordView(mActivity, mGUid, new ForgetPasswordView.OnChangePassSuccessListener() {
                        @Override
                        public void onChangePassSuccess(String msg) {
                            tipsDialog = new TipsDialog(mActivity, SdkManager.defaultSDK().getLanguageContent(310), new TipsDialog.OnSureListener() {
                                @Override
                                public void onSureListener() {
                                    tipsDialog.dismiss();
                                    mCurViewMode = VIEW_MODE_9453PLAY_LOGIN_VIEW;
                                    mFrameLayoutSubView.removeAllViews();
                                    mFrameLayoutSubView.addView(mUserLoginView);
                                }
                            });
                            tipsDialog.show();
                        }

                        @Override
                        public void onChangePassFail(String msg) {
                            showTipsDialog(msg);
                        }
                    });
                }
                mCurViewMode = VIEW_MODE_FORGET_PASSWORD;
                mFrameLayoutSubView.removeAllViews();
                mFrameLayoutSubView.addView(mForgetPasswordView);

            } else if (loginMode == UserLoginView.LOGIN_USER_REG_NEW_PHONE) {
                //手机号注册
                if (null == mRegPhoneView) {
                    mRegPhoneView = new RegPhoneView(mActivity, mGUid, new RegPhoneView.OnLoginClickListener() {
                        @Override
                        public void onLoginClickListener(int loginMode) {
                            if (loginMode == ChooseLoginView.LOGIN_TYPE_9453PLAY) {
                                // 手机登录
                                if (null == mUserLoginView) {
                                    mUserLoginView = new UserLoginView(mActivity, mGUid, userOnLoginClickListener, new UserLoginView.OnLoginSuccessListener() {
                                        @Override
                                        public void onLoginSuccess(String msg) {
                                            loginSuccess(msg);
                                        }

                                        @Override
                                        public void onLoginFail(String msg) {
                                            showTipsDialog(SdkManager.defaultSDK().getLanguageContent(235) + msg);
                                            SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(235) + msg);
                                        }
                                    });
                                }
                                mCurViewMode = VIEW_MODE_9453PLAY_LOGIN_VIEW;
                                mFrameLayoutSubView.removeAllViews();
                                mFrameLayoutSubView.addView(mUserLoginView);
                            }
                        }
                    }, new RegPhoneView.OnLoginSuccessListener() {
                        @Override
                        public void onLoginSuccess(String msg) {
                            loginSuccess(msg);
                        }

                        @Override
                        public void onLoginFail(String msg) {
                            showTipsDialog(msg);
                            SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_ERROR, SdkManager.defaultSDK().getLanguageContent(235) + msg);
                        }
                    });
                }
                mCurViewMode = VIEW_MODE_REG_NEW_PHONE;
                mFrameLayoutSubView.removeAllViews();
                mFrameLayoutSubView.addView(mRegPhoneView);
            } else if (loginMode == UserLoginView.LOGIN_USER_CHANGE_PASSWORD) {
                //修改密码
                if (null == mChangePasswordView) {
                    mChangePasswordView = new ChangePasswordView(mActivity, mGUid, new ChangePasswordView.OnChangePassSuccessListener() {
                        @Override
                        public void onChangePassSuccess() {
                            toChooseLoginView();
                            showTipsDialog(SdkManager.defaultSDK().getLanguageContent(310));
                        }

                        @Override
                        public void onChangePassFail(String errMsg) {
                            toChooseLoginView();
                            showTipsDialog(errMsg);
                        }
                    });
                }
                mCurViewMode = VIEW_MODE_CHANGE_PASSWORD;
                mFrameLayoutSubView.removeAllViews();
                mFrameLayoutSubView.addView(mChangePasswordView);

            }
        }
    };

    private void showTipsDialog(final String message) {
        try {
            tipsDialog = new TipsDialog(mActivity, message, new TipsDialog.OnSureListener() {
                @Override
                public void onSureListener() {
                    tipsDialog.dismiss();
                }
            });
            tipsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void bindAcc(String msg) {
        try {
            JSONObject infoJson = new JSONObject(msg);
            String userType = infoJson.getString("userType");
            if (userType.equals("phone")) {
                String bindPhone = infoJson.getString("bindPhone");
                String phoneCode = infoJson.getString("phoneCode");
                String bindPass = infoJson.getString("bindpass");
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("guid", mGUid);
                jsonObj.put("userid", mGuestUserId);
                jsonObj.put("bindPhone", bindPhone);
                jsonObj.put("code", phoneCode);
                jsonObj.put("bindpass", bindPass);
                TwitterRestClient.post(SdkUrl.getSdkLoginUrl("/BindPhoneAcc"), jsonObj.toString(), new SdkAsyncHttpStandardResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                        Utils.getInstance().dismissProgress();
                        showTipsDialog(SdkManager.defaultSDK().getLanguageContent(113) + "，please try again(2)...");
                    }

                    @Override
                    public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {
                        Utils.getInstance().dismissProgress();
                        if (statusCode == 200 && response != null) {
                            try {
                                JSONObject json = new JSONObject(new String(response));
                                if (!json.has("ret")) {
                                    showTipsDialog("{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, "{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                    return;
                                }
                                int ret = Integer.parseInt(json.getString("ret"));
                                if (ret == 1) {
                                    cancel();
                                    UserCookies.getInstance(mActivity).clearUserInfo();
                                    showTipsDialog(SdkManager.defaultSDK().getLanguageContent(404));
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_SUCCESS, SdkManager.defaultSDK().getLanguageContent(404));
                                } else if (ret == 1101) {
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, SdkManager.defaultSDK().getLanguageContent(405) + "," + SdkManager.defaultSDK().getLanguageContent(1101));
                                } else if (ret == 1102) {
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, SdkManager.defaultSDK().getLanguageContent(405) + "," + SdkManager.defaultSDK().getLanguageContent(1102));
                                } else if (ret == 1104) {
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, SdkManager.defaultSDK().getLanguageContent(405) + "," + SdkManager.defaultSDK().getLanguageContent(1104));
                                } else if (ret == 1103) {
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, SdkManager.defaultSDK().getLanguageContent(405) + "," + SdkManager.defaultSDK().getLanguageContent(1103));
                                } else if (ret == 0) {
                                    showTipsDialog(SdkManager.defaultSDK().getLanguageContent(405) + "，" + json.getString("errinfo"));
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, SdkManager.defaultSDK().getLanguageContent(405) + "，" + json.getString("errinfo"));
                                } else if (ret == 2601) {
                                    showTipsDialog(SdkManager.defaultSDK().getLanguageContent(405) + "，" + SdkManager.defaultSDK().getLanguageContent(2601));
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, SdkManager.defaultSDK().getLanguageContent(405) + "，" + SdkManager.defaultSDK().getLanguageContent(2601));
                                } else if (ret == 2801) {
                                    showTipsDialog(SdkManager.defaultSDK().getLanguageContent(405) + "，" + SdkManager.defaultSDK().getLanguageContent(408));
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, SdkManager.defaultSDK().getLanguageContent(405) + "，" + SdkManager.defaultSDK().getLanguageContent(2601));
                                } else if (ret == 2008) {
                                    new InitView(mActivity, SdkManager.defaultSDK().getSdkInitSetting(), new InitView.OnInitCompleteListener() {
                                        @Override
                                        public void onInitComplete(String accList, String gUid) {
                                            mGUid = gUid;
                                            mAccList = accList;
                                        }
                                    });
                                } else {
                                    Utils.getInstance().toast(mActivity, new String(response));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(405) + "，" + e.getMessage());
                            }
                        } else {
                            showTipsDialog(SdkManager.defaultSDK().getLanguageContent(405) + "，please try again(1)...");
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        Utils.getInstance().dismissProgress();
                    }
                });
            } else {
                String platacc = infoJson.getString("platacc");
                String excode = infoJson.getString("excode");
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("guid", mGUid);
                jsonObj.put("userid", mGuestUserId);
                jsonObj.put("platacc", platacc);
                jsonObj.put("excode", excode);
                TwitterRestClient.post(SdkUrl.getSdkLoginUrl(SdkUrl.USER_LOGIN_BIND_PLAT_ACC), jsonObj.toString(), new SdkAsyncHttpStandardResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                        Utils.getInstance().dismissProgress();
                        showTipsDialog(SdkManager.defaultSDK().getLanguageContent(113) + "，Please try again(2)...");
                    }

                    @Override
                    public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {
                        Utils.getInstance().dismissProgress();
                        if (statusCode == 200) {
                            try {
                                Log.e("D2eam", "bind_response = " + new String(response));
                                JSONObject json = new JSONObject(new String(response));
                                if (!json.has("ret")) {
                                    showTipsDialog("{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, "{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                    return;
                                }
                                int ret = Integer.parseInt(json.getString("ret"));
                                if (ret == 1) {
                                    cancel();
                                    UserCookies.getInstance(mActivity).clearUserInfo();
                                    showTipsDialog(SdkManager.defaultSDK().getLanguageContent(404));
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_SUCCESS, SdkManager.defaultSDK().getLanguageContent(404));
                                } else if (ret == 0) {
                                    showTipsDialog(SdkManager.defaultSDK().getLanguageContent(405) + "，" + json.getString("errinfo"));
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, SdkManager.defaultSDK().getLanguageContent(405) + "，" + json.getString("errinfo"));
                                } else if (ret == 2601) {
                                    showTipsDialog(SdkManager.defaultSDK().getLanguageContent(405) + "，" + SdkManager.defaultSDK().getLanguageContent(2601));
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, SdkManager.defaultSDK().getLanguageContent(405) + "，" + SdkManager.defaultSDK().getLanguageContent(2601));
                                } else if (ret == 2801) {
                                    showTipsDialog(SdkManager.defaultSDK().getLanguageContent(405) + "，" + SdkManager.defaultSDK().getLanguageContent(408));
                                    SdkManager.defaultSDK().getOnBindListener().callBack(SDKStatusCode.BIND_FAIL, SdkManager.defaultSDK().getLanguageContent(405) + "，" + SdkManager.defaultSDK().getLanguageContent(2801));
                                } else if (ret == 2008) {
                                    new InitView(mActivity, SdkManager.defaultSDK().getSdkInitSetting(), new InitView.OnInitCompleteListener() {
                                        @Override
                                        public void onInitComplete(String accList, String gUid) {
                                            mGUid = gUid;
                                            mAccList = accList;
                                        }
                                    });
                                } else {
                                    Utils.getInstance().toast(mActivity, new String(response));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(405) + "，" + e.getMessage());
                            }
                        } else {
                            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(405) + "，Please try again(1)...");
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        Utils.getInstance().dismissProgress();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loginSuccess(String msg) {
        isLoginDialogAutoCancel = true;
        SdkManager.defaultSDK().getOnLoginListener().callBack(SDKStatusCode.LOGIN_SUCCESS, msg);
        cancel();
        // dismiss();
    }

    private void SortAccList(String gameid, String acc) {
        AccInfo guset = null;
        int guset_N = 0;
        for (int n = 0; n < mGameIDList.size(); n++) {
            if (mGameIDList.get(n).gameid.equals(gameid)) {
                mGameIDList.remove(n);
                break;
            }

            if (mGameIDList.get(n).bindAcc.equals("null")) {
                if (n >= 5) {
                    guset = mGameIDList.get(n);
                    guset_N = n;
                }
            }
        }
        AccInfo info = new AccInfo();
        info.gameid = gameid;
        info.bindAcc = acc;
        mGameIDList.add(0, info);
        String lastGameID = gameid;

        if (guset_N > 4) {
            mGameIDList.remove(guset_N);
            mGameIDList.add(4, guset);
        }
    }

    private void SortAccListEx() {
        AccInfo guset = null;
        int guset_N = 0;
        String ccc = "null";
        for (int n = 0; n < mGameIDList.size(); n++) {
            if (mGameIDList.get(n).bindAcc.equals("null")) {
                if (n >= 5) {
                    guset = mGameIDList.get(n);
                    guset_N = n;
                }
            }
        }

        if (guset_N > 4) {
            mGameIDList.remove(guset_N);
            mGameIDList.add(4, guset);
        }
    }

}
