package com.overseas.exports.common.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;

public class UtilDPI {

    public static int SCREEN_WIDTH = LayoutParams.FILL_PARENT;
    public static int SCREEN_HEIGHT = LayoutParams.FILL_PARENT;
    private static float SCALE_X = -1.0F;
    private static float SCALE_Y = -1.0F;
    private static float DENSITY = -1.0F;
    private static long lastClickTime;

    public static int getInt(Context paramContext, int paramInt) {
        return getIntForScalX(paramContext, paramInt);
    }

    public static int getIntY(Context paramContext, int paramInt) {
        return getIntForScalY(paramContext, paramInt);
    }

    private static int getIntForScalX(Context paramContext, int paramInt) {
        return (int) (paramInt * getSCALE_X(paramContext));
    }

    private static int getIntForScalY(Context paramContext, int paramInt) {
        return (int) (paramInt * getSCALE_Y(paramContext));
    }

    public static float getSCALE_X(Context paramContext) {
        if (SCALE_X > 0.0F)
            return SCALE_X;
        init(paramContext);
        return SCALE_X;
    }

    public static float getSCALE_Y(Context paramContext) {
        if (SCALE_Y > 0.0F)
            return SCALE_Y;
        init(paramContext);
        return SCALE_Y;
    }

    public static void init(Context paramContext) {

        DisplayMetrics displayMetrics = paramContext.getResources()
                .getDisplayMetrics();
        SCREEN_WIDTH = displayMetrics.widthPixels;
        SCREEN_HEIGHT = displayMetrics.heightPixels;
        if (SCREEN_WIDTH > SCREEN_HEIGHT) {
            int i = SCREEN_WIDTH;
            SCREEN_WIDTH = SCREEN_HEIGHT;
            SCREEN_HEIGHT = i;
        }
        DENSITY = displayMetrics.density;
        SCALE_X = SCREEN_WIDTH / 480.0F;
        SCALE_Y = SCREEN_HEIGHT / 800.0F;
    }

    public static int dip2px(int paramInt) {
        return (int) (paramInt * DENSITY + 0.5F);
    }

    public static int px2dip(int paramInt) {
        return (int) (paramInt / DENSITY + 0.5F);
    }

    public static int getScreenWidth(Context paramContext) {
        if (SCREEN_WIDTH > 0)
            return SCREEN_WIDTH;
        init(paramContext);
        return SCREEN_WIDTH;
    }

    public static int getScreenHeight(Context paramContext) {
        if (SCREEN_HEIGHT > 0)
            return SCREEN_HEIGHT;
        init(paramContext);
        return SCREEN_HEIGHT;
    }

    public static boolean isProt(Context paramContext) {
        return paramContext.getResources().getConfiguration().orientation != 2;
    }

    public static int getTextSize(Context paramContext, int paramInt) {
        return (int) (paramInt * getSCALE_X(paramContext) / DENSITY);
    }

    public static boolean isPhone(String paramString) {
        if (!TextUtils.isEmpty(paramString))
            return paramString.matches("1+[0-9]+[0-9]{9}");
        return false;
    }

    public static boolean isFastDoubleClick() {
        long l1;
        long l2 = (l1 = System.currentTimeMillis()) - lastClickTime;
        if ((0L < l2) && (l2 < 500L))
            return true;
        lastClickTime = l1;
        return false;
    }

    public static int px2dip(Context paramContext, float paramFloat) {
        final float scale = paramContext.getResources().getDisplayMetrics().density;
        return (int) (paramFloat / scale + 0.5F);
    }

    public static int dip2px(Context paramContext, float paramFloat) {
        final float scale = paramContext.getResources().getDisplayMetrics().density;
        return (int) (paramFloat * scale + 0.5F);
    }

    public static int px2sp(Context paramContext, float paramFloat) {
        final float scale = paramContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (paramFloat / scale + 0.5F);
    }

    public static int sp2px(Context paramContext, float paramFloat) {
        final float scale = paramContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (paramFloat * scale + 0.5F);
    }
}