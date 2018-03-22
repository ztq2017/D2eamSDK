package com.overseas.exports.view.user;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.overseas.exports.SdkManager;
import com.overseas.exports.common.TwitterRestClient;
import com.overseas.exports.common.util.UtilResources;
import com.overseas.exports.task.SdkAsyncHttpStandardResponseHandler;
import com.overseas.exports.utils.SdkUrl;
import com.overseas.exports.utils.Utils;
import com.overseas.exports.view.SpinnerPopWindow;

import java.util.HashMap;
import java.util.Map;

/**
 * 绑定手机账号
 */

public class BindPhoneAccView extends FrameLayout implements View.OnClickListener {
    private FragmentActivity mActivity;
    private TextView mTvAreaSelect; //地区选择
    private Button mBtnSureRegPhone, mBtnGetAuthCode;//确认註冊
    private EditText mEdtRegPhone;
    private EditText mEdtRegPassword;
    private EditText mEdtAgainPassword;
    private EditText mEdtPhoneAuth;
    private TextView mTvMainLoginTitle;
    private SpinnerPopWindow mSpinnerPopWindow;
    private String mGUid = "";
    private OnBindPhoneClickListener mOnBindPhoneClickListener;

    public BindPhoneAccView(FragmentActivity activity, String gUid, OnBindPhoneClickListener onBindPhoneClickListener) {
        super(activity);
        this.mActivity = activity;
        mGUid = gUid;
        mOnBindPhoneClickListener = onBindPhoneClickListener;
        initViews();
    }

    public void initViews() {
        inflate(mActivity, UtilResources.getLayoutId("bind_phone_acc_view"), this);
        mTvMainLoginTitle = (TextView) findViewById(UtilResources.getId("tv_mainTitle"));
        mTvMainLoginTitle.setText(SdkManager.defaultSDK().getLanguageContent(403));
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

        //手机验证码
        mEdtPhoneAuth = (EditText) findViewById(UtilResources.getId("edt_auth"));
        mEdtPhoneAuth.setHint(SdkManager.defaultSDK().getLanguageContent(302));
        //获取手机验证码
        mBtnGetAuthCode = (Button) findViewById(UtilResources.getId("btn_getAuthCode"));
        mBtnGetAuthCode.setText(SdkManager.defaultSDK().getLanguageContent(303));
        mBtnGetAuthCode.setOnClickListener(this);
        //确认绑定手机
        mBtnSureRegPhone = (Button) findViewById(UtilResources.getId("btn_sureBindPhone"));
        mBtnSureRegPhone.setText(SdkManager.defaultSDK().getLanguageContent(406));
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
        } else if (view == mBtnGetAuthCode) {
            getCode();
        } else if (view == mBtnSureRegPhone) {
            String areaCode = mTvAreaSelect.getText().toString().replace("+", "");
            if (!Utils.getInstance().isNumeric(areaCode)) {
                Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(200));
                return;
            }
            final String bindphone = areaCode + mEdtRegPhone.getText().toString();
            final String bindpass = mEdtRegPassword.getText().toString();
            final String bindAgainPassword = mEdtAgainPassword.getText().toString();

            String phoneCode = mEdtPhoneAuth.getText().toString();

            if (!Utils.getInstance().checkNet(mActivity)) {
                return;
            }
            if (!Utils.getInstance().formatUser(mActivity, bindphone)) {
                return;
            }

            if (!Utils.getInstance().formatPhoneCode(mActivity, phoneCode)) {
                return;
            }
            if (!Utils.getInstance().formatPass(mActivity, bindpass)) {
                return;
            }
            if (!Utils.getInstance().formatPassAgain(mActivity, bindpass, bindAgainPassword)) {
                return;
            }
            if (mGUid.isEmpty() || mGUid.equals("")) {
                Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(112));
                return;
            }
            String result = String.format("{ userType = '%s',bindPhone = '%s',phoneCode = '%s',bindpass = '%s'}", "phone", bindphone, phoneCode, bindpass);
            mOnBindPhoneClickListener.onBindPhoneClickListener(result.replace("'", "\""));
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

    public interface OnBindPhoneClickListener {
        void onBindPhoneClickListener(String msg);
    }
}
