package com.overseas.exports.platfrom;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.overseas.exports.utils.IabHelper;
import com.overseas.exports.utils.IabResult;
import com.overseas.exports.utils.Inventory;
import com.overseas.exports.utils.Purchase;
import com.overseas.exports.utils.Security;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class GoogleSdkApi implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static GoogleSdkApi googleSdkApi;
    private String TAG = "googleSdk";
    public int REQUEST_CODE = 1001;
    private FragmentActivity mActivity;
    public GoogleSignInOptions gso;
    public GoogleApiClient mGoogleApiClient;
    public GoogleApiClient.OnConnectionFailedListener listener;
    private PlatLoginSdkCallBackListener mPlatLoginSdkCallBackListener;
    private PlatPaySdkCallBackListener mPlatPaySdkCallBackListener;
    private String mIdToken;


    private IInAppBillingService mService;
    private IabHelper mHelper;
    /**
     * Google是否初始化成功：
     */
    private boolean iap_is_ok = false;
    //商品id
    private String mProductId = "";
    private String payload = "";
    // (arbitrary) request code for the purchase flow
    //购买请求回调requestcode
    private static final int RC_REQUEST = 1002;
    //base64EncodedPublicKey是在Google开发者后台复制过来的：要集成的应用——>服务和API——>此应用的许可密钥（自己去复制）
    private String base64EncodedPublicKey = "";
    private String mUserId = "";

    public static GoogleSdkApi defaultSDK() {
        if (googleSdkApi == null) {
            synchronized (GoogleSdkApi.class) {
                if (googleSdkApi == null) {
                    googleSdkApi = new GoogleSdkApi();
                }
            }
        }
        return googleSdkApi;
    }

    public void initGoogleSdk(final FragmentActivity activity) {
        mActivity = activity;
        try {
            ApplicationInfo appInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(),
                    PackageManager.GET_META_DATA);
            mIdToken = appInfo.metaData.getString("REQUEST_ID_TOKEN");
            base64EncodedPublicKey = appInfo.metaData.getString("GOOGLE_BASE64ENCODEDPUBLICKEY");
            Log.e(TAG, "requestIdToken=" + mIdToken + " ,base64EncodedPublicKey =" + base64EncodedPublicKey);

            //初始化谷歌登录服务
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestId()
                    .requestIdToken(mIdToken)
                    .requestProfile()
                    .build();

            // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .enableAutoManage(activity, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(final FragmentActivity activity, final PlatLoginSdkCallBackListener platLoginSdkCallBackListener) {
        mPlatLoginSdkCallBackListener = platLoginSdkCallBackListener;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                activity.startActivityForResult(signInIntent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void pay(FragmentActivity activity, final String productId, final String orderId, PlatPaySdkCallBackListener platPaySdkCallBackListener) {
        mPlatPaySdkCallBackListener = platPaySdkCallBackListener;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //绑定谷歌支付服务
                    Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
                    serviceIntent.setPackage("com.android.vending");
                    mActivity.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
                    mProductId = productId.replace(" ", "");
                    payload = orderId;
                    Log.e("plat_google", "mProductId=" + mProductId + " _ orderId=" + payload);
                    mHelper = new IabHelper(mActivity, base64EncodedPublicKey);
                    // enable debug logging (for a production application, you should set this to false).
                    mHelper.enableDebugLogging(true);
                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                        public void onIabSetupFinished(IabResult result) {
                            Log.i(TAG, "初始化完成.");
                            if (!result.isSuccess()) {
                                // Oh noes, there was a problem.
                                String ret = String.format("{error = '%s'}", "Problem setting up in-app billing:初始化失败 " + result);
                                mPlatPaySdkCallBackListener.onPlatSdkPayFail(ret);
                                return;
                            }
                            iap_is_ok = true;
                            if (mHelper == null) return;
                            Log.i(TAG, "Google初始化成功.");
                            if (iap_is_ok) {
                                try {
                                    List<String> additionalSkuList = new ArrayList<String>();
                                    additionalSkuList.add(productId);
                                    mHelper.queryInventoryAsync(true, additionalSkuList, null, mGotInventoryListener);
                                } catch (IabHelper.IabAsyncInProgressException e) {
                                    String ret = String.format("{error = '%s'}", "Problem setting up in-app billing:初始化失败 " + result);
                                }
                            } else {
                                String ret = String.format("{error = '%s'}", "Google Play初始化失败,当前无法进行支付，请确定您所在地区支持Google Play支付或重启游戏再试！");
                                mPlatPaySdkCallBackListener.onPlatSdkPayFail(ret);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.i(TAG, "查询库存完成.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                String ret = String.format("{error = '%s'}", "查询库存失败: " + result);
                Log.e(TAG, ret);
                mPlatPaySdkCallBackListener.onPlatSdkPayFail(ret);
                return;
            }
            Log.i(TAG, "查询库存成功.");
            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */
            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            //查询你的产品是否存在没有消耗的，要是没有消耗，先去消耗，再购买
            Purchase gasPurchase = inventory.getPurchase(mProductId);
            if (gasPurchase != null) {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(mProductId), mConsumeFinishedListener);
                } catch (Exception e) {
                    String ret = String.format("{error = '%s'}", "Error consuming gas. Another async operation in progress.");
                    Log.e(TAG, ret);
                    mPlatPaySdkCallBackListener.onPlatSdkPayFail(ret);
                }
                return;
            }
            Log.i(TAG, "初始库存查询完成；启用主用户界面.");
            toBuyGooglepay();
        }
    };

    /**
     * 去购买Google产品
     * productId  Google产品id
     * payload  购买商品订单号
     * <p>
     * 点击购买的时候，才去初始化产品，看看是否有这个产品，是否消耗
     */
    private void toBuyGooglepay() {
        // launch the gas purchase UI flow.
        // We will be notified of completion via mPurchaseFinishedListener
        Log.i(TAG, "开始购买");

        /* for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        //这个payload是要给Google发送的备注信息，自定义参数，购买完成之后的订单中也有该字段
        //<br style="padding: 0px;" />一，调用In-app Billing中的getBuyIntent方法，会传几个参数，第一个参数 3 代表的是当前所用的支付API的版本，第二个参数是你的包名，第三个参数就是你内购商品的ID,第四个参数是这次购买的类型，“inapp”和"subs",我们用的是第一个，第二个是只能购买一次的类型，第五个参数是订单号。需要讲的只有第三个和第五个参数。

        try {
            mHelper.launchPurchaseFlow(mActivity, mProductId, RC_REQUEST, mPurchaseFinishedListener, payload);
        } catch (Exception e) {
            String ret = String.format("{error = '%s'}", "无法完成谷歌支付");
            Log.e(TAG, ret);
            mPlatPaySdkCallBackListener.onPlatSdkPayFail(ret);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.i(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.getResponse() == -1005) {
                Log.e(TAG, result.getMessage());
                return;
            }

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (!verifyDeveloperPayload(purchase)) {
                return;
            }

            if (result.isFailure()) {
                String ret = String.format("{error = '%s'}", "Error purchasing: " + result);
                Log.e(TAG, ret);
                mPlatPaySdkCallBackListener.onPlatSdkPayFail(ret);
                return;
            }
            //购买完成时候就能获取到订单的详细信息：purchase.getOriginalJson(),要是想要什么就去purchase中get
            //根据获取到产品的Id去判断是哪一项产品
            if (purchase.getSku().equals(mProductId)) {

                Log.i(TAG, "购买的是" + purchase.getSku());
                try {
                    //购买完成之后去消耗产品
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (Exception e) {
                    String ret = String.format("{error = '%s'}", "Error consuming gas. Another async operation in progress.");
                    Log.e(TAG, ret);
                    mPlatPaySdkCallBackListener.onPlatSdkPayFail(ret);
                    return;
                }
            }

        }
    };

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Called when consumption is complete 消耗产品的回调
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;
            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                String ret = String.format("{succ = '%s'}", "消费成功。Provisioning");
                Log.i(TAG, ret);
            } else {
                String ret = String.format("{succ = '%s'}", "Error while consuming: " + result);
                Log.i(TAG, ret);
            }

        }
    };


    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //谷歌登录成功回调
        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        if (mHelper == null) return;
        // Pass on the activity result to the helper for handling

        try {
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to in-app
                // billing...
                googleSdkApi.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.i(TAG, "onActivityResult handled by IABUtil.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (requestCode == RC_REQUEST) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            //订单信息
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            Log.i(TAG, "purchaseData：： " + purchaseData);
            Log.i(TAG, "dataSignature：： " + dataSignature);

            if (resultCode == mActivity.RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    //订单Id
                    String sku = jo.getString("productId");
                    String ret = String.format("{base64EncodedPublicKey = '%s', purchaseData = '%s', dataSignature = '%s'}",
                            base64EncodedPublicKey, purchaseData, dataSignature);
                    mPlatPaySdkCallBackListener.onPlatSdkPaySuccess(ret);
                    Log.i(TAG, "You have bought the " + sku + ". Excellent choice,adventurer!");
                } catch (JSONException e) {
                    System.out.println("Failed to parse purchase data.");
                    e.printStackTrace();
                }
            } else {
                mPlatPaySdkCallBackListener.onPlatSdkPayFail("{error = 'pay fail'}");
            }
        }

    }

    /**
     * 退出登录
     */
    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (mPlatLoginSdkCallBackListener != null) {
                                //mPlatLoginSdkCallBackListener.p;
                            }
                        } else {
                            if (mPlatLoginSdkCallBackListener != null) {
                                //mPlatLoginSdkCallBackListener.googleLogoutFail();
                            }
                        }
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            //登录成功
            GoogleSignInAccount acct = result.getSignInAccount();
            String res = "登录成功"
                    + "用户名为：" + acct.getDisplayName()
                    + "  邮箱为：" + acct.getEmail()
                    + " token为：" + acct.getIdToken()
                    + " 头像地址为：" + acct.getPhotoUrl()
                    + " Id为：" + acct.getId()
                    + " GrantedScopes为：" + acct.getGrantedScopes();
            Log.e("res", "res:" + res);
            if (mPlatLoginSdkCallBackListener != null) {
                mPlatLoginSdkCallBackListener.onPlatSdkLoginSuccess(acct.getId(), "google");
            }
        } else {
            // Signed out, show unauthenticated UI.
            // res = "-1";  //-1代表用户退出登录了 ， 可以自定义
            if (mPlatLoginSdkCallBackListener != null) {
                mPlatLoginSdkCallBackListener.onPlatSdkLoginFail("google login fail");
            }
        }
    }

    public interface PlatLoginSdkCallBackListener {
        void onPlatSdkLoginSuccess(String userId, String token);

        void onPlatSdkLoginFail(String msg);

    }

    public interface PlatPaySdkCallBackListener {
        void onPlatSdkPaySuccess(String msg);

        void onPlatSdkPayFail(String msg);

    }

    public void onPause() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(mActivity);
            mGoogleApiClient.disconnect();
        }
    }

    public void onDestroy() {
        if (mHelper != null) {
            try {
                mHelper.disposeWhenFinished();
                mHelper = null;
                if (mService != null) {
                    mActivity.unbindService(mServiceConn);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
