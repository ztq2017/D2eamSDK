package com.overseas.exports.common.volley.custom;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.overseas.exports.common.volley.toolbox.ImageLoader.ImageCache;

/**
 * 图片加载内存缓存（采用的LruCahce）
 * Created by wanggang on 2016/6/13
 * E-Mail: wanggang@6lapp.com
 */
public class ImageMemoryCache implements ImageCache {
	private LruCache<String, Bitmap> mCache;

	public ImageMemoryCache() {
		int maxSize = (int) Runtime.getRuntime().maxMemory() / 8;
		mCache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getHeight() * value.getRowBytes();
			}
		};
	}

	@Override
	public Bitmap getBitmap(String key) {
		return mCache.get(key);
	}

	@Override
	public void putBitmap(String key, Bitmap value) {
		mCache.put(key, value);
	}
}