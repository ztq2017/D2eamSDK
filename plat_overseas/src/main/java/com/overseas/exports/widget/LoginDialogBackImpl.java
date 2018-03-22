package com.overseas.exports.widget;


import com.overseas.exports.dialog.BaseSdkDialog;
import com.overseas.exports.view.user.LoginMainDialog;

public class LoginDialogBackImpl implements BaseSdkDialog.LoginDialogBackListener {


    private LoginMainDialog mLoginMainDialog;


    public LoginDialogBackImpl(LoginMainDialog loginMainDialog) {
        this.mLoginMainDialog = loginMainDialog;
    }

    @Override
    public boolean isBackView() {
        if (mLoginMainDialog == null)
            return true;
        return false;
    }

}
