package com.overseas.exports.view.user;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.overseas.exports.SdkManager;
import com.overseas.exports.common.util.UtilResources;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 选择登录方式
 */

public class ChooseLoginView extends FrameLayout implements View.OnClickListener {
    public int loginType = 1;
    /**
     * 游客登录
     */
    public static final int LOGIN_TYPE_VISITOR = 1;
    /**
     * 賬號登录
     */
    public static final int LOGIN_TYPE_9453PLAY = 2;
    /**
     * FaceBook登录
     */
    public static final int LOGIN_TYPE_FACEBOOK = 3;
    /**
     * google登录
     */
    public static final int LOGIN_TYPE_GOOGLE = 4;

    private Activity mActivity;
    private Button mBtn9453playLogin, mBtnFaceBookLogin, mBtnGoogleLogin, mBtnVisitorLogin;
    TextView mTvAgreeRules;
    private OnLoginClickListener mOnLoginClickListener;
    private String mAccList;
    private ArrayList<AccInfo> mGameIDList = new ArrayList<>();
    private ArrayList<String> plataccList = new ArrayList<>();
    private ArrayList<String> bindaccList = new ArrayList<>();
    private String gameid = "";
    private String bindacc = "";
    private String platacc = "";

    static class AccInfo {
        public String gameid;
        public String bindAcc;
    }

    public ChooseLoginView(Activity activity, String accList, int loginType, OnLoginClickListener onLoginClickListener) {
        super(activity);
        mActivity = activity;
        this.loginType = loginType;
        mAccList = accList;
        mOnLoginClickListener = onLoginClickListener;
        initViews();
    }

    private void initViews() {
        try {
            inflate(mActivity, UtilResources.getLayoutId("login_choose_view"), this);
            mBtn9453playLogin = (Button) findViewById(UtilResources.getId("btn_9453playLogin"));
            mBtnFaceBookLogin = (Button) findViewById(UtilResources.getId("btn_faceBookLogin"));
            mBtnGoogleLogin = (Button) findViewById(UtilResources.getId("btn_googleLogin"));
            mBtnVisitorLogin = (Button) findViewById(UtilResources.getId("btn_visitorLogin"));
            mTvAgreeRules = (TextView) findViewById(UtilResources.getId("tv_agreeRules"));
            mBtn9453playLogin.setOnClickListener(this);
            mBtnFaceBookLogin.setOnClickListener(this);
            mBtnGoogleLogin.setOnClickListener(this);
            mBtnVisitorLogin.setOnClickListener(this);
            mTvAgreeRules.setOnClickListener(this);
            mBtn9453playLogin.setText(SdkManager.defaultSDK().getLanguageContent(101));
            mBtnFaceBookLogin.setText(SdkManager.defaultSDK().getLanguageContent(102));
            mBtnGoogleLogin.setText(SdkManager.defaultSDK().getLanguageContent(103));
            mBtnVisitorLogin.setText(SdkManager.defaultSDK().getLanguageContent(104));
            mTvAgreeRules.setText(SdkManager.defaultSDK().getLanguageContent(109));

            if (!mAccList.equals("") && !mAccList.isEmpty()) {
                JSONArray jsonArray = new JSONArray(mAccList);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    gameid = json.getString("gameid");
                    bindacc = json.getString("bindacc");
                    platacc = json.getString("platacc");
                    if (!platacc.isEmpty() && !platacc.equals("") && !platacc.equals("null")) {
                        plataccList.add(platacc);
                    }
                    if (!bindacc.isEmpty() && !bindacc.equals("") && !bindacc.equals("null")) {
                        bindaccList.add(bindacc);
                    }
                    SortAccList(gameid, bindacc);
                }
            }
            if (loginType == 2) {
                mBtnVisitorLogin.setVisibility(View.GONE);
            } else {
                mBtnVisitorLogin.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        if (guset_N > 4) {
            mGameIDList.remove(guset_N);
            mGameIDList.add(4, guset);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mBtn9453playLogin) {
            mOnLoginClickListener.onLoginClickListener(LOGIN_TYPE_9453PLAY);
        } else if (view == mBtnFaceBookLogin) {
            mOnLoginClickListener.onLoginClickListener(LOGIN_TYPE_FACEBOOK);
        } else if (view == mBtnGoogleLogin) {
            mOnLoginClickListener.onLoginClickListener(LOGIN_TYPE_GOOGLE);
        } else if (view == mBtnVisitorLogin) {
            mOnLoginClickListener.onLoginClickListener(LOGIN_TYPE_VISITOR);
        } else if (view == mTvAgreeRules) {
            //TODO 游戏规章制度
        }
    }

    public interface OnLoginClickListener {
        void onLoginClickListener(int loginMode);
    }

}

