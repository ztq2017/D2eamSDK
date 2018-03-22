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
import com.overseas.exports.sdk.SDKCallBackListener;
import com.overseas.exports.task.SdkAsyncHttpStandardResponseHandler;
import com.overseas.exports.utils.SdkUrl;
import com.overseas.exports.utils.Utils;
import com.overseas.exports.view.SpinnerPopWindow;

import org.json.JSONObject;

import java.util.Map;

/**
 * 修改手机密码
 */
public class ChangePasswordView extends FrameLayout implements View.OnClickListener {
    private FragmentActivity mActivity;
    private TextView mTvAreaSelect; //地区选择
    private Button mBtnSureChangePassword;//确认绑定手机按钮
    private EditText mEdtPhone, mEdtOldPassword, mEdtNewPassword, mEdtAgainNewPassword;
    private OnChangePassSuccessListener mOnChangePassSuccessListener;
    private SpinnerPopWindow mSpinnerPopWindow;
    private String mGUid;

    public ChangePasswordView(FragmentActivity activity, String gUid, OnChangePassSuccessListener onChangePassSuccessListener) {
        super(activity);
        this.mActivity = activity;
        mOnChangePassSuccessListener = onChangePassSuccessListener;
        mGUid = gUid;
        initViews();
    }

    public void initViews() {
        inflate(mActivity, UtilResources.getLayoutId("sure_change_password_view"), this);
        TextView mTvMainTitle = (TextView) findViewById(UtilResources.getId("tv_mainTitle"));
        mTvMainTitle.setText(SdkManager.defaultSDK().getLanguageContent(308));
        mEdtPhone = (EditText) findViewById(UtilResources.getId("edt_phone"));
        mEdtPhone.setHint(SdkManager.defaultSDK().getLanguageContent(203));
        // 旧密码
        mEdtOldPassword = (EditText) findViewById(UtilResources.getId("edt_oldPassword"));
        mEdtOldPassword.setHint(SdkManager.defaultSDK().getLanguageContent(307));
        //新密码
        mEdtNewPassword = (EditText) findViewById(UtilResources.getId("edt_newPassword"));
        mEdtNewPassword.setHint(SdkManager.defaultSDK().getLanguageContent(304));
        //再次输入新密码
        mEdtAgainNewPassword = (EditText) findViewById(UtilResources.getId("edt_againNewPassword"));
        mEdtAgainNewPassword.setHint(SdkManager.defaultSDK().getLanguageContent(305));

        //获取地区选择
        mTvAreaSelect = (TextView) findViewById(UtilResources.getId("tv_areaSelect"));
        mTvAreaSelect.setOnClickListener(this);
        mTvAreaSelect.setText(SdkManager.defaultSDK().getLanguageContent(215));

        //确认修改
        mBtnSureChangePassword = (Button) findViewById(UtilResources.getId("btn_sureChangePassword"));
        mBtnSureChangePassword.setText(SdkManager.defaultSDK().getLanguageContent(306));
        mBtnSureChangePassword.setOnClickListener(this);

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
     * 给TextView右边设置图片
     *
     * @param resId
     */
    private void setTextImage(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        mTvAreaSelect.setCompoundDrawables(null, null, drawable, null);
    }


    /*监听popupwindow取消
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
        } else if (view == mBtnSureChangePassword) {
            doChangePwd();
        }
    }

    private void doChangePwd() {
        String areaCode = mTvAreaSelect.getText().toString().replace("+", "");
        if (!Utils.getInstance().isNumeric(areaCode)) {
            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(200));
            return;
        }
        String phone = areaCode + mEdtPhone.getText().toString();
        String oldPassword = mEdtOldPassword.getText().toString();
        final String newPassword = mEdtNewPassword.getText().toString();
        final String againNewPassword = mEdtAgainNewPassword.getText().toString();

        if (!Utils.getInstance().formatUser(mActivity, mEdtPhone.getText().toString())) {
            return;
        }

        if (!Utils.getInstance().formatPass(mActivity, oldPassword)) {
            return;
        }
        if (!Utils.getInstance().formatPass(mActivity, newPassword)) {
            return;
        }
        if (!againNewPassword.equals(newPassword)) {
            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(309));
            return;
        }
        if (mGUid.isEmpty() || mGUid.equals("")) {
            Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(112));
            return;
        }
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("guid", mGUid);
            jsonObj.put("bindphone", phone);
            jsonObj.put("old_bindpass", oldPassword);
            jsonObj.put("new_bindpass", newPassword);

            mEdtPhone.setText("");
            mEdtOldPassword.setText("");
            mEdtNewPassword.setText("");
            mEdtAgainNewPassword.setText("");
            mTvAreaSelect.setText("");
            Utils.getInstance().showProgress(mActivity, SdkManager.defaultSDK().getLanguageContent(312));
            TwitterRestClient.post(SdkUrl.getSdkLoginUrl(SdkUrl.USER_LOGIN_RESET_PHONE_PASS), jsonObj.toString(), new SdkAsyncHttpStandardResponseHandler() {
                @Override
                public void onFailure(int statusCode, Map<String, String> headers, byte[] responseBody, Throwable error) {
                    Utils.getInstance().dismissProgress();
                    Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(113));
                }

                @Override
                public void onSuccess(int statusCode, Map<String, String> headers, byte[] response) {
                    Utils.getInstance().dismissProgress();
                    if (statusCode == 200 && response != null) {
                        try {
                            Log.e("D2eam", "reset_response = " + new String(response));
                            JSONObject json = new JSONObject(new String(response));
                            if (!json.has("ret")) {
                                mOnChangePassSuccessListener.onChangePassFail("{err=[[" + SdkManager.defaultSDK().getLanguageContent(225) + "_1002]]}");
                                return;
                            }
                            int ret = Integer.parseInt(json.getString("ret"));
                            if (ret == 200) {
                                mOnChangePassSuccessListener.onChangePassSuccess();
                            } else if (ret == 2008) {
                                SdkManager.defaultSDK().initSDK(mActivity, SdkManager.defaultSDK().getSdkInitSetting(), new SDKCallBackListener() {
                                    @Override
                                    public void callBack(int code, String msg) {

                                    }
                                });
                            } else if (ret == 1104) {
                                mOnChangePassSuccessListener.onChangePassFail(SdkManager.defaultSDK().getLanguageContent(311) + "," + SdkManager.defaultSDK().getLanguageContent(1104));
                            } else if (ret == 1103) {
                                mOnChangePassSuccessListener.onChangePassFail(SdkManager.defaultSDK().getLanguageContent(311) + "," + SdkManager.defaultSDK().getLanguageContent(1103));
                            } else {
                                if (json.has("errinfo")) {
                                    mOnChangePassSuccessListener.onChangePassFail(json.getString("errinfo"));
                                } else if (json.has("errInfo")) {
                                    mOnChangePassSuccessListener.onChangePassFail(json.getString("errInfo"));
                                } else {
                                    Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(225));
                                }
                            }
                        } catch (Exception e) {
                            Log.e("D2eam", "e = " + e.getMessage());
                        }
                    } else {
                        Utils.getInstance().toast(mActivity, SdkManager.defaultSDK().getLanguageContent(311));
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
    }

    public interface OnChangePassSuccessListener {
        void onChangePassSuccess();

        void onChangePassFail(String errMsg);
    }

}
