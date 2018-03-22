package com.overseas.exports.view.user;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.overseas.exports.SdkManager;
import com.overseas.exports.common.TwitterRestClient;
import com.overseas.exports.common.util.UtilResources;
import com.overseas.exports.sdk.SDKCallBackListener;
import com.overseas.exports.task.SdkAsyncHttpStandardResponseHandler;
import com.overseas.exports.utils.SdkUrl;
import com.overseas.exports.utils.UserCookies;
import com.overseas.exports.utils.Utils;
import com.overseas.exports.view.SpinnerPopWindow;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 手机号注册
 */
public class RegPhoneView extends FrameLayout implements View.OnClickListener {
    private FragmentActivity mActivity;
    private TextView mTvAreaSelect; //地区选择
    private Button mBtnSureRegPhone, mBtnGetAuthCode;//确认註冊,获取手机验证码
    private LinearLayout mLLLoginAccount;//注册新账号
    private EditText mEdtRegPhone, mEdtRegPassword, mEdtAgainPassword, mEdtPhoneAuth;
    private TextView mTvAgreeRules, mTvMainLoginTitle, mTvMainRegTitle;
    private OnLoginSuccessListener mOnLoginSuccessListener;
    private SpinnerPopWindow mSpinnerPopWindow;
    private String mGUid = "";
    /**
     * 新注册用户
     */
    public static final int VIEW_MODE_9453PLAY_LOGIN_VIEW = 2;
    private OnLoginClickListener mOnLoginClickListener;

    public RegPhoneView(FragmentActivity activity, String gUid, OnLoginClickListener onLoginClickListener, OnLoginSuccessListener onLoginSuccessListener) {
        super(activity);
        this.mActivity = activity;
        mGUid = gUid;
        mOnLoginSuccessListener = onLoginSuccessListener;
        mOnLoginClickListener = onLoginClickListener;
        initViews();
    }

    public void initViews() {
        inflate(mActivity, UtilResources.getLayoutId("reg_mobile_view"), this);
        mTvMainLoginTitle = (TextView) findViewById(UtilResources.getId("tv_mainLoginTitle"));
        mTvMainLoginTitle.setText(SdkManager.defaultSDK().getLanguageContent(201));
        mTvMainRegTitle = (TextView) findViewById(UtilResources.getId("tv_mainRegTitle"));
        mTvMainRegTitle.setText(SdkManager.defaultSDK().getLanguageContent(202));
        //手机账号
        mEdtRegPhone = (EditText) findViewById(UtilResources.getId("edt_phone"));
        mEdtRegPhone.setHint(SdkManager.defaultSDK().getLanguageContent(203));
        //密码
        mEdtRegPassword = (EditText) findViewById(UtilResources.getId("edt_password"));
        mEdtRegPassword.setHint(SdkManager.defaultSDK().getLanguageContent(204));
        //密码
        mEdtAgainPassword = (EditText) findViewById(UtilResources.getId("edt_againPassword"));
        mEdtAgainPassword.setHint(SdkManager.defaultSDK().getLanguageContent(208));
        //获取地区选择
        mTvAreaSelect = (TextView) findViewById(UtilResources.getId("tv_areaSelect"));
        mTvAreaSelect.setText(SdkManager.defaultSDK().getLanguageContent(215));
        mTvAreaSelect.setOnClickListener(this);
        //注册新账号
        mLLLoginAccount = (LinearLayout) findViewById(UtilResources.getId("ll_loginAccount"));
        mLLLoginAccount.setOnClickListener(this);
        //同意管理規章
        mTvAgreeRules = (TextView) findViewById(UtilResources.getId("tv_agreeRules"));
        mTvAgreeRules.setText(SdkManager.defaultSDK().getLanguageContent(209));
        //手机验证码
        mEdtPhoneAuth = (EditText) findViewById(UtilResources.getId("edt_auth"));
        mEdtPhoneAuth.setHint(SdkManager.defaultSDK().getLanguageContent(302));
        //获取手机验证码
        mBtnGetAuthCode = (Button) findViewById(UtilResources.getId("btn_getAuthCode"));
        mBtnGetAuthCode.setText(SdkManager.defaultSDK().getLanguageContent(303));
        mBtnGetAuthCode.setOnClickListener(this);
        //确认绑定手机
        mBtnSureRegPhone = (Button) findViewById(UtilResources.getId("btn_sureRegPhone"));
        mBtnSureRegPhone.setText(SdkManager.defaultSDK().getLanguageContent(210));
        mBtnSureRegPhone.setOnClickListener(this);

        Utils.getInstance().getAreaData(mActivity);
        mSpinnerPopWindow = new SpinnerPopWindow(mActivity, Utils.mAreaNameList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mSpinnerPopWindow.dismiss();
                mTvAreaSelect.setText(Utils.mAreaCodeList.get(position));
            }
        });
        mSpinnerPopWindow.setOnDismissListener(dismissListener);

    }


    /**
     * 监听popupwindow取消
     */
    private PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            setTextImage(UtilResources.getDrawableId("icon_down"));
        }
    };

    /**
     * 给TextView右边设置图片
     *
     * @param resId 图片资源id
     */
    private void setTextImage(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        mTvAreaSelect.setCompoundDrawables(null, null, drawable, null);
    }

    @Override
    public void onClick(View view) {
        if (view == mTvAreaSelect) {
            //地区选择
            mSpinnerPopWindow.setWidth(mTvAreaSelect.getWidth());
            mSpinnerPopWindow.showAsDropDown(mTvAreaSelect);
            setTextImage(UtilResources.getDrawableId("icon_up"));
        } else if (view == mTvAgreeRules) {
            // TODO 游戏规章制度
        } else if (view == mBtnSureRegPhone) {
            sureRegPhone();
        } else if (view == mLLLoginAccount) {
            mOnLoginClickListener.onLoginClickListener(VIEW_MODE_9453PLAY_LOGIN_VIEW);
        } else if (view == mBtnGetAuthCode) {
            getCode();
        }
    }

    public void getCode() {
        String areaCode = mTvAreaSelect.getText().toString().replace("+", "");
        if (!Utils.getInstance().isNumeric(areaCode)) {
            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(200));
            return;
        }
        String phone = areaCode + mEdtRegPhone.getText().toString();
        if (!Utils.getInstance().checkNet(mActivity)) {
            return;
        }
        if (!Utils.getInstance().formatUser(mActivity, mEdtRegPhone.getText().toString())) {
            return;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("phone", phone);
        Utils.getInstance().showProgress(mActivity, SdkManager.defaultSDK().getLanguageContent(232));
        TwitterRestClient.get(SdkUrl.GET_CODE_URL + SdkUrl.USER_LOGIN_FORGET_PWD_CODE, hashMap, new SdkAsyncHttpStandardResponseHandler() {
            @Override
            public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                Utils.getInstance().dismissProgress();
                Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(113));
            }

            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {
                Utils.getInstance().dismissProgress();
                Log.e(SdkManager.OverseasTag, "new String(response)" + new String(response));
                if (new String(response).equals("ok")) {
                    Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(230));
                } else {
                    Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(231));
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Utils.getInstance().dismissProgress();
            }
        });

    }

    private void sureRegPhone() {
        String areaCode = mTvAreaSelect.getText().toString().replace("+", "");
        if (!Utils.getInstance().isNumeric(areaCode)) {
            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(200));
            return;
        }
        final String phone = mEdtRegPhone.getText().toString();
        String bindPhone = areaCode + phone;
        String phoneCode = mEdtPhoneAuth.getText().toString();
        final String bindPass = mEdtRegPassword.getText().toString();
        String bindAgainPass = mEdtAgainPassword.getText().toString();
        if (!Utils.getInstance().checkNet(mActivity)) {
            return;
        }
        if (!Utils.getInstance().formatUser(mActivity, phone)) {
            return;
        }

        if (!Utils.getInstance().formatPhoneCode(mActivity, phoneCode)) {
            return;
        }
        if (!Utils.getInstance().formatPass(mActivity, bindPass)) {
            return;
        }
        if (!Utils.getInstance().formatPassAgain(mActivity, bindPass, bindAgainPass)) {
            return;
        }
        if (mGUid.isEmpty() || mGUid.equals("")) {
            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(112));
            return;
        }
        try {
            Utils.getInstance().showProgress(mActivity, SdkManager.defaultSDK().getLanguageContent(227));
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("guid", mGUid);
            jsonObj.put("bindphone", bindPhone);
            jsonObj.put("bindpass", bindPass);
            jsonObj.put("code", phoneCode);
            TwitterRestClient.post(SdkUrl.getSdkLoginUrl(SdkUrl.USER_LOGIN_BIND_PHONE_REG), jsonObj.toString(), new SdkAsyncHttpStandardResponseHandler() {
                @Override
                public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                    Utils.getInstance().dismissProgress();
                    Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(113));
                }

                @Override
                public void onSuccess(int statusCode, Map<String, String> headers, byte[] response)  {
                    Utils.getInstance().dismissProgress();
                    String msg = "";
                    if (statusCode == 200) {
                        try {
                            JSONObject json = new JSONObject(new String(response));
                            if (!json.has("ret")) {
                                mOnLoginSuccessListener.onLoginFail("{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                return;
                            }

                            int ret = json.getInt("ret");
                            String token = "";
                            if (ret == 1) {
                                //登录成功
                                String id = json.getString("gameid");
                                if (json.has("token")) {
                                    token = json.getString("token");
                                }
                                if (json.has("payurl"))
                                    SdkUrl.PAY_URL = json.getString("payurl");
                                UserCookies.getInstance(mActivity).savePayUrl(json.getString("payurl"));
                                UserCookies.getInstance(mActivity).saveTokenTime(System.currentTimeMillis() + "");
                                @SuppressLint("DefaultLocale")
                                String result = String.format("{userId = '%s', token = '%s', userType = %s}", id, token, "9453PLAY");
                                UserCookies.getInstance(mActivity).saveUserInfo(id, phone, token, "9453PLAY", bindPass, false, false);
                                mOnLoginSuccessListener.onLoginSuccess(result.replace("'", "\""));
                            } else if (ret == 1101) {
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235) + "," + SdkManager.defaultSDK().getLanguageContent(1101));
                            } else if (ret == 1102) {
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235) + "," + SdkManager.defaultSDK().getLanguageContent(1102));
                            } else if (ret == 1104) {
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235) + "," + SdkManager.defaultSDK().getLanguageContent(1104));
                            } else if (ret == 1103) {
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235) + "," + SdkManager.defaultSDK().getLanguageContent(1103));
                            }else if (ret == 1208) {
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(1208));
                            } else if (ret == 1105) {
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(1105));
                            } else if (ret == 0) {
                                if (json.has("errinfo")) {
                                    msg = json.getString("errinfo");
                                }
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235) + "：" + msg);
                            } else if (ret == 2008) {
                                SdkManager.defaultSDK().initSDK(mActivity, SdkManager.defaultSDK().getSdkInitSetting(), new SDKCallBackListener() {
                                    @Override
                                    public void callBack(int code, String msg) {

                                    }
                                });
                            } else {
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235) + "：" + new String(response));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(235) + "：" + e.getMessage());
                        }
                    } else {
                        mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnLoginSuccessListener {

        void onLoginSuccess(String msg);

        void onLoginFail(String msg);
    }

    public interface OnLoginClickListener {
        void onLoginClickListener(int loginMode);
    }
}
