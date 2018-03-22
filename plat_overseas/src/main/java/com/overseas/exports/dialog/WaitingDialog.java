package com.overseas.exports.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overseas.exports.R;
import com.overseas.exports.common.util.UtilDPI;
import com.overseas.exports.common.util.UtilResources;

public final class WaitingDialog extends Dialog {
    private TextView textView;
    private RelativeLayout relativeLayout;

    public WaitingDialog(Context context) {
        this(context, false);
    }

    @SuppressLint("ResourceType")
    public WaitingDialog(Context context, boolean isCancelable) {
        super(context, UtilResources.getStyleId("wait_dialog_style"));
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        RelativeLayout localRelativeLayout = new RelativeLayout(context);
        setContentView(localRelativeLayout);

        RelativeLayout localObject;
        (localObject = new RelativeLayout(context))
                .setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        ((RelativeLayout) localObject).setGravity(Gravity.CENTER);
        localRelativeLayout.addView(localObject);

        int i = UtilDPI.getInt(context, 310);
        int j = UtilDPI.getInt(context, 70);
        this.relativeLayout = new RelativeLayout(context);
        this.relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(i, j));
        this.relativeLayout.setBackgroundResource(UtilResources.getDrawableId("wait_progress_bg_dialog"));
        ((RelativeLayout) localObject).addView(this.relativeLayout);
        i = UtilDPI.getInt(context, 30);

        View layoutInflater = View.inflate(context, UtilResources.getLayoutId("wait_layout_progress"), null);
        layoutInflater.setId(1001);
        RelativeLayout.LayoutParams localLayoutParams;
        (localLayoutParams = new RelativeLayout.LayoutParams(i, i)).addRule(15);
        localLayoutParams.leftMargin = UtilDPI.getInt(context, 20);
        ((View) layoutInflater).setLayoutParams(localLayoutParams);
        this.relativeLayout.addView((View) layoutInflater);
        this.textView = new TextView(context);
        this.textView.setTextSize(UtilDPI.getTextSize(context, 20));
        this.textView.setTextColor(-1);
        this.textView.setSingleLine();
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(15);
        layoutParams.addRule(11);
        layoutParams.addRule(1, 1001);
        this.textView.setLayoutParams(layoutParams);
        this.relativeLayout.addView(this.textView);
    }

    public final void setText(String paramString) {
        if (TextUtils.isEmpty(paramString))
            return;
        this.textView.setText("    " + paramString + "    ");
    }
}
