package com.overseas.exports.view.user;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.util.Map;

/**
 * 手机号登录
 */
public class UserLoginView extends FrameLayout implements View.OnClickListener {
    private FragmentActivity mActivity;
    private TextView mTvAreaSelect; //地区选择
    private TextView mTvForgetPassword;
    private TextView mTvChangePassword;
    private Button mBtn9453PlayLogin;//账号登录
    private LinearLayout mLLRegPhoneNew;//注册新账号
    private EditText mEdtLoginPhone, mEdtPhonePassword;
    private CheckBox mSwitchPasswordShow;
    private OnLoginSuccessListener mOnLoginSuccessListener;
    private SpinnerPopWindow mSpinnerPopWindow;
    private String mGUid;
    /**
     * 忘记密码
     */
    public static final int LOGIN_USER_FORGET_PASSWORD = 6;
    /**
     * 新注册用户
     */
    public static final int LOGIN_USER_REG_NEW_PHONE = 7;
    /**
     * 修改密码
     */
    public static final int LOGIN_USER_CHANGE_PASSWORD = 8;

    private OnLoginClickListener mOnLoginClickListener;


    public UserLoginView(FragmentActivity activity, String gUid, OnLoginClickListener onLoginClickListener, OnLoginSuccessListener onLoginSuccessListener) {
        super(activity);
        this.mActivity = activity;
        mOnLoginSuccessListener = onLoginSuccessListener;
        mOnLoginClickListener = onLoginClickListener;
        mGUid = gUid;
        initViews();
    }

    public void initViews() {
        inflate(mActivity, UtilResources.getLayoutId("login_mobile_view"), this);
        TextView mTvMainLoginTitle = (TextView) findViewById(UtilResources.getId("tv_mainLoginTitle"));
        mTvMainLoginTitle.setText(SdkManager.defaultSDK().getLanguageContent(201));
        TextView mTvMainRegTitle = (TextView) findViewById(UtilResources.getId("tv_mainRegTitle"));
        mTvMainRegTitle.setText(SdkManager.defaultSDK().getLanguageContent(202));
        //手机账号
        mEdtLoginPhone = (EditText) findViewById(UtilResources.getId("edt_phone"));
        mEdtLoginPhone.setHint(SdkManager.defaultSDK().getLanguageContent(203));
        //密码
        mEdtPhonePassword = (EditText) findViewById(UtilResources.getId("edt_phonePassword"));
        mEdtPhonePassword.setHint(SdkManager.defaultSDK().getLanguageContent(204));
        //获取地区选择
        mTvAreaSelect = (TextView) findViewById(UtilResources.getId("tv_areaSelect"));
        mTvAreaSelect.setOnClickListener(this);
        mTvAreaSelect.setText(SdkManager.defaultSDK().getLanguageContent(215));

        TextView mTvShowPassword = (TextView) findViewById(UtilResources.getId("tv_showPassword"));
        mTvShowPassword.setText(SdkManager.defaultSDK().getLanguageContent(205));
        //是否显示明文密码
        mSwitchPasswordShow = (CheckBox) findViewById(UtilResources.getId("switch_pwdShow"));
        mSwitchPasswordShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mSwitchPasswordShow.isChecked()) {
                    //do something
                    mEdtPhonePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //do something else
                    mEdtPhonePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        //忘记密码
        mTvForgetPassword = (TextView) findViewById(UtilResources.getId("tv_forgetPassword"));
        mTvForgetPassword.setText(SdkManager.defaultSDK().getLanguageContent(206));
        mTvForgetPassword.setOnClickListener(this);

        //忘记密码
        mTvChangePassword = (TextView) findViewById(UtilResources.getId("tv_changePassword"));
        mTvChangePassword.setText(SdkManager.defaultSDK().getLanguageContent(207));
        mTvChangePassword.setOnClickListener(this);

        //注册新账号
        mLLRegPhoneNew = (LinearLayout) findViewById(UtilResources.getId("ll_regNewAccount"));
        mLLRegPhoneNew.setOnClickListener(this);

        mBtn9453PlayLogin = (Button) findViewById(UtilResources.getId("btn_9453playLogin"));
        mBtn9453PlayLogin.setText(SdkManager.defaultSDK().getLanguageContent(101));
        mBtn9453PlayLogin.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        if (view == mTvAreaSelect) {
            //地区选择
            mSpinnerPopWindow.setWidth(mTvAreaSelect.getWidth());
            mSpinnerPopWindow.showAsDropDown(mTvAreaSelect);
            setTextImage(UtilResources.getDrawableId("icon_up"));
        } else if (view == mTvForgetPassword) {
            //忘记密码
            mOnLoginClickListener.onLoginClickListener(LOGIN_USER_FORGET_PASSWORD);
        } else if (view == mLLRegPhoneNew) {
            // 调用手机账号注册接口，生成新的手机账号
            mOnLoginClickListener.onLoginClickListener(LOGIN_USER_REG_NEW_PHONE);
        } else if (view == mTvChangePassword) {
            // 调用修改密码接口
            mOnLoginClickListener.onLoginClickListener(LOGIN_USER_CHANGE_PASSWORD);
        } else if (view == mBtn9453PlayLogin) {
            doLogin();
        }

    }

    private void doLogin() {
        String areaCode = mTvAreaSelect.getText().toString().replace("+", "");
        if (!Utils.getInstance().isNumeric(areaCode)) {
            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(200));
            return;
        }
        final String bindphone = areaCode + mEdtLoginPhone.getText().toString();
        final String bindpass = mEdtPhonePassword.getText().toString();
        if (!Utils.getInstance().checkNet(mActivity)) {
            return;
        }
        if (!Utils.getInstance().formatUser(mActivity, mEdtLoginPhone.getText().toString())) {
            return;
        }
        if (!Utils.getInstance().formatPass(mActivity, bindpass)) {
            return;
        }
        if (mGUid.isEmpty() || mGUid.equals("")) {
            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(112));
            return;
        }
        try {
            Utils.getInstance().showProgress(mActivity, SdkManager.defaultSDK().getLanguageContent(226));
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("guid", mGUid);
            jsonObj.put("bindphone", bindphone);
            jsonObj.put("bindpass", bindpass);
            TwitterRestClient.post(SdkUrl.getSdkLoginUrl(SdkUrl.USER_LOGIN_BIND_PHONE), jsonObj.toString(), new SdkAsyncHttpStandardResponseHandler() {
                @Override
                public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                    Utils.getInstance().dismissProgress();
                    Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(113));
                }

                @Override
                public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {
                    Utils.getInstance().dismissProgress();
                    String msg = "";
                    if (statusCode == 200) {
                        try {
                            JSONObject json = new JSONObject(new String(response));
                            if (!json.has("ret")) {
                                mOnLoginSuccessListener.onLoginFail("{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                return;
                            }

                            int ret = Integer.parseInt(json.getString("ret"));
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
                                String result = String.format("{userId = '%s', token = '%s', userType = '%s'}", id, token, "9453PLAY");
                                UserCookies.getInstance(mActivity).saveUserInfo(id, bindphone, token, "9453PLAY", bindpass, false, false);
                                mOnLoginSuccessListener.onLoginSuccess(result.replace("'", "\""));
                            } else if (ret == 0) {
                                if (json.has("errinfo")) {
                                    msg = json.getString("errinfo");
                                }
                                mOnLoginSuccessListener.onLoginFail(msg);
                            } else if (ret == 2008) {
                                SdkManager.defaultSDK().initSDK(mActivity, SdkManager.defaultSDK().getSdkInitSetting(), new SDKCallBackListener() {
                                    @Override
                                    public void callBack(int code, String msg) {

                                    }
                                });
                            } else if (ret == 1104) {
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235) + "," + SdkManager.defaultSDK().getLanguageContent(1104));
                            } else if (ret == 1103) {
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235) + "," + SdkManager.defaultSDK().getLanguageContent(1103));
                            } else {
                                mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235) + "：" + new String(response));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(235) + "：" + e.getMessage());
                        }
                    } else {
                        mOnLoginSuccessListener.onLoginFail(SdkManager.defaultSDK().getLanguageContent(224));
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 给TextView右边设置图片
     *
     * @param resId
     */
    private void setTextImage(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        mTvAreaSelect.setCompoundDrawables(null, null, drawable, null);
    }

    public interface OnLoginSuccessListener {
        void onLoginSuccess(String msg);

        void onLoginFail(String msg);
    }

    public interface OnLoginClickListener {
        void onLoginClickListener(int loginMode);
    }
}
