package com.overseas.exports.common.util;

import android.content.Context;

public class UtilResources {
	private static Context mContext;
	
	public static void initResourcesContext(Context context) {
		mContext = context;
	}
	
	public static void destroy() {
		mContext = null;
	}
	
	public static int getLayoutId(String paramString) {
		return mContext.getResources().getIdentifier(paramString, "layout", mContext.getPackageName());
	}

	public static int getStringId(String paramString) {
		return mContext.getResources().getIdentifier(paramString, "string", mContext.getPackageName());
	}

	public static int getDrawableId(String paramString) {
		return mContext.getResources().getIdentifier(paramString, "drawable", mContext.getPackageName());
	}

	public static int getStyleId(String paramString) {
		return mContext.getResources().getIdentifier(paramString, "style", mContext.getPackageName());
	}

	public static int getId(String paramString) {
		return mContext.getResources().getIdentifier(paramString, "id", mContext.getPackageName());
	}

	public static int getColorId(String paramString) {
		return mContext.getResources().getIdentifier(paramString, "color", mContext.getPackageName());
	}

	public static int getRawId(String paramString) {
		return mContext.getResources().getIdentifier(paramString, "raw", mContext.getPackageName());
	}

	public static int getAnimId(String paramString) {
		return mContext.getResources().getIdentifier(paramString, "anim", mContext.getPackageName());
	}
	
}
