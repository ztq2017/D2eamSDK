package com.overseas.exports.common.volley.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.overseas.exports.common.volley.VolleyError;
import com.overseas.exports.common.volley.toolbox.ImageLoader;
import com.overseas.exports.common.volley.toolbox.ImageLoader.ImageContainer;
import com.overseas.exports.common.volley.toolbox.Volley;

/**
 * 图片加载辅助类（核心实例使用的Volley，风格参照Glide）
 * Created by wanggang on 2016/6/8
 * E-Mail: wanggang@6lapp.com
 */
public class ImageHelper {
	private static ImageLoader mImageLoader;

	public static ImageLoadManager with(Context context) {
		if (mImageLoader == null) {
			synchronized (ImageHelper.class) {
				if (mImageLoader == null) {
					mImageLoader = new ImageLoader(Volley.newRequestQueue(context), new ImageMemoryCache());
				}
			}
		}
		
		return new ImageLoadManager(mImageLoader);
	}
	
	public static class ImageLoadManager {
		private ImageLoader mImageLoader;
		private int mPlaceholderResId, mErrorResId;
		private String mPicUrl;
		
		public ImageLoadManager(ImageLoader imageLoader) {
			mImageLoader = imageLoader;  
		}
		
		public ImageLoadManager load(String url) {
			mPicUrl = url;
			return this;
		}
		
		public ImageLoadManager placeholder(int resourceId) {
			mPlaceholderResId = resourceId;
			return this;
		}
		
		public ImageLoadManager error(int resourceId) {
			mErrorResId = resourceId;
			return this;
		}
		
		public void into(ImageView iamgeView) {
			if (null == iamgeView) return;

			if (mPlaceholderResId != 0) {
				iamgeView.setImageResource(mPlaceholderResId);
			}

			if (TextUtils.isEmpty(mPicUrl)) {
				if (0 != mErrorResId) {
					iamgeView.setImageResource(mErrorResId);
				}
				return;
			}

			mImageLoader.get(mPicUrl, ImageLoader.getImageListener(iamgeView, mPlaceholderResId, mErrorResId));
		}
		
		public void download() {
			download(null); 
		}
		
		public void download(final OnDownloadCompleteListener onDownloadCompleteListener) {
			mImageLoader.get(mPicUrl, new ImageLoader.ImageListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					onDownloadCompleteListener.onError(error);
				}
				
				@Override
				public void onResponse(ImageContainer response, boolean isImmediate) {
					if (null != onDownloadCompleteListener && null != response.getBitmap()) {
						onDownloadCompleteListener.onSuccess(response.getBitmap());
					}
				}
			});  
		}
	}
	
	
	public interface OnDownloadCompleteListener {
		void onError(Exception e);
		
		void onSuccess(Bitmap bitmap);
	}
	
}
