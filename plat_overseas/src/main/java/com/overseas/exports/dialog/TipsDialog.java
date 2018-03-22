package com.overseas.exports.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overseas.exports.R;
import com.overseas.exports.common.util.UtilResources;

/**
 * 提示Dialog
 */

public class TipsDialog extends Dialog {
    private Activity mActivity;
    private String msg;
    private OnSureListener mOnSureListener;

    public TipsDialog(Activity activity, String msg, OnSureListener onSureListener) {
        super(activity, UtilResources.getStyleId("dialog_style"));
        mActivity = activity;
        this.msg = msg;
        mOnSureListener = onSureListener;
        initView();
    }

    private void initView() {
        View mMainTipsView = View.inflate(mActivity, UtilResources.getLayoutId("change_password_success"), null);
        setContentView(mMainTipsView);
        TextView mTvTipsInfo = (TextView) mMainTipsView.findViewById(UtilResources.getId("tv_changeSuccess"));
        Button mBtnSure = (Button) mMainTipsView.findViewById(UtilResources.getId("btn_changeSuccess"));

        mTvTipsInfo.setText(msg);
        mBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnSureListener.onSureListener();
            }
        });
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
            layoutParams = new FrameLayout.LayoutParams((int) (screenWidth * 0.5f), (int) (screenHeight * 0.5f));//(int) (screenHeight * 0.85f)
        } else {
            layoutParams = new FrameLayout.LayoutParams((int) (screenHeight * 0.5f), (int) (screenHeight * 0.5f));
        }
        layoutParams.gravity = Gravity.CENTER;
        mMainTipsView.setLayoutParams(layoutParams);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setCanceledOnTouchOutside(false);

    }

    public interface OnSureListener {
        void onSureListener();
    }

}
