package com.overseas.exports.view.user;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.overseas.exports.SdkManager;
import com.overseas.exports.common.util.UtilResources;
import com.overseas.exports.utils.UserCookies;

/**
 * 账号中心
 */
public class UserCenterView extends FrameLayout implements View.OnClickListener {
    private Activity mActivity;
    private Button mBtnSwitchAccount, mBtnBindAccount;
    private TextView mTvLoginWay;
    private TextView mTvLoginAccount;
    private OnBindLoginListener mOnBindLoginListener;
    private OnSwitchAccountListener mOnSwitchAccountListener;
    String loginUserId, loginWay;

    public UserCenterView(Activity activity, OnBindLoginListener onBindLoginListener, OnSwitchAccountListener onSwitchAccountListener) {
        super(activity);
        mActivity = activity;
        mOnBindLoginListener = onBindLoginListener;
        mOnSwitchAccountListener = onSwitchAccountListener;
        initView();
    }

    private void initView() {
        inflate(mActivity, UtilResources.getLayoutId("login_bind_view"), this);
        loginUserId = UserCookies.getInstance(mActivity).getUserID();
        loginWay = UserCookies.getInstance(mActivity).getLoginWay();
        mTvLoginWay = (TextView) findViewById(UtilResources.getId("tv_loginWay"));
        mTvLoginAccount = (TextView) findViewById(UtilResources.getId("tv_loginAccount"));
        mBtnSwitchAccount = (Button) findViewById(UtilResources.getId("btn_SwitchAccount"));
        mBtnSwitchAccount.setText(SdkManager.defaultSDK().getLanguageContent(402));
        mBtnSwitchAccount.setOnClickListener(this);
        mBtnBindAccount = (Button) findViewById(UtilResources.getId("btn_bindAccount"));
        mBtnBindAccount.setText(SdkManager.defaultSDK().getLanguageContent(403));
        mBtnBindAccount.setOnClickListener(this);
        if (loginWay.equals("GUEST")) {
            mBtnBindAccount.setVisibility(VISIBLE);
            loginWay = SdkManager.defaultSDK().getLanguageContent(105);
        } else {
            mBtnBindAccount.setVisibility(GONE);
        }
        mTvLoginWay.setText(String.format(SdkManager.defaultSDK().getLanguageContent(401), loginWay));
        mTvLoginAccount.setText("ID：" + loginUserId);

    }

    @Override
    public void onClick(View view) {
        if (view == mBtnSwitchAccount) {
            mOnSwitchAccountListener.onSwitchAccountListener();
        } else if (view == mBtnBindAccount) {
            mOnBindLoginListener.onBindLoginListener(loginUserId);
        }
    }

    public interface OnBindLoginListener {
        void onBindLoginListener(String userId);
    }

    public interface OnSwitchAccountListener {
        void onSwitchAccountListener();
    }
}
