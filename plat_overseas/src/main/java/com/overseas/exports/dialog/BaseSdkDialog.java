package com.overseas.exports.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

public class BaseSdkDialog extends Dialog {

    private LoginDialogBackListener backListener;

    public BaseSdkDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public final void setDialogBackListener(
            LoginDialogBackListener dialogBackListener) {
        this.backListener = dialogBackListener;
    }

    @Override
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {

        if ((paramInt == KeyEvent.KEYCODE_BACK) && (this.backListener != null)) {
            if (!this.backListener.isBackView()) {
                cancel();
                return super.onKeyDown(paramInt, paramKeyEvent);
            }
            return false;
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    public abstract interface LoginDialogBackListener {
        public abstract boolean isBackView();
    }
}
