package com.overseas.exports.platfrom;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;

public class FacebookSdkApi {
    private static FacebookSdkApi facebookSdkApi;
    private CallbackManager callbackManager;
    private PlatSdkCallBackListener mPlatSdkCallBackListener;
    private FragmentActivity mActivity;
    private static final String PERMISSION = "public_profile";
    private ShareDialog shareDialog;
    private PlatSdkShareCallBackListener cbShareResult = null;

    public static FacebookSdkApi defaultSDK() {
        if (facebookSdkApi == null) {
            synchronized (FacebookSdkApi.class) {
                if (facebookSdkApi == null) {
                    facebookSdkApi = new FacebookSdkApi();
                }
            }
        }
        return facebookSdkApi;
    }

    public void initSdk(FragmentActivity activity) {
        mActivity = activity;
        FacebookSdk.sdkInitialize(activity);
        AppEventsLogger.activateApp(activity);
        callbackManager = CallbackManager.Factory.create();
    }

    public void login(PlatSdkCallBackListener platSdkCallBackListener) {
        mPlatSdkCallBackListener = platSdkCallBackListener;
        final LoginManager loginManager = LoginManager.getInstance();
        loginManager.setDefaultAudience(loginManager.getDefaultAudience());
        loginManager.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        loginManager.logInWithReadPermissions(mActivity, Arrays.asList(PERMISSION));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        String userId = accessToken.getUserId();
                        String token = accessToken.getToken();
                        //拿到userId和token，传给游戏服务器校验
                        Log.e("plat_Facebook", "userId = " + userId + ",,,token = " + token);
                        mPlatSdkCallBackListener.onPlatSdkLoginSuccess(userId, "facebook");

                    }

                    @Override
                    public void onCancel() {
                        Log.e("FaceBookSdk", "取消登录");
                        mPlatSdkCallBackListener.onPlatSdkLoginCancel();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                mPlatSdkCallBackListener.onPlatSdkLoginFail("");
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                });
    }

    private void loginOut() {
        LoginManager.getInstance().logOut();
    }

    //shareToFaceBook:(int)type // 1.链接，2。图片  url:(NSString*)contentUrl // 分享地址 quote:(NSString*)quot // 描述 cbEnd:(int)cbEnd // 回调
    public void sharedMessage(int type, String url, String description, final PlatSdkShareCallBackListener platSdkShareCallBackListener) {
        cbShareResult = platSdkShareCallBackListener;
        shareDialog = new ShareDialog(mActivity);

        if (type == 1) {
            //分享链接
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(url))
                    .setContentDescription(description)
                    .build();
            shareDialog.show(linkContent);
        } else {
            //分享图片
            SharePhoto photo = new SharePhoto.Builder()
                    .setImageUrl(Uri.parse(url))
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            shareDialog.show(content);
        }
        shareDialog.registerCallback(callbackManager, shareCallback);
    }

    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.e("Facebook", "Canceled");
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cbShareResult.onPlatSdkShareCancel();
                }
            });
        }

        @Override
        public void onError(FacebookException error) {
            Log.e("Facebook", String.format("Error: %s", error.toString()));
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String ret = String.format("{ret = '%s', ret_string = '%s'}",
                            -1, "分享失敗");
                    cbShareResult.onPlatSdkShareFail(ret.replace("'", "\""));
                }
            });
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String ret = String.format("{ret = '%s', ret_string = '%s'}",
                            1, "分享成功");
                    cbShareResult.onPlatSdkShareSuccess(ret.replace("'", "\""));
                }
            });
        }
    };


    public interface PlatSdkCallBackListener {
        void onPlatSdkLoginSuccess(String userId, String token);

        void onPlatSdkLoginFail(String msg);

        void onPlatSdkLoginCancel();
    }

    public interface PlatSdkShareCallBackListener {
        void onPlatSdkShareSuccess(String token);

        void onPlatSdkShareFail(String msg);

        void onPlatSdkShareCancel();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
