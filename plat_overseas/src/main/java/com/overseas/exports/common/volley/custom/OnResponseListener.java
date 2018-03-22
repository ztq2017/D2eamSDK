package com.overseas.exports.common.volley.custom;

import com.overseas.exports.common.volley.Response;

import java.util.Map;

/**
 * 自定义Volley返回监听类（原生需要两个回调类，用这个合并统一成一个）
 * Created by wanggang on 2016/6/8
 * E-Mail: wanggang@6lapp.com
 */
public interface OnResponseListener<T> extends Response.ErrorListener {
	
	public void onResponse(int statusCode, Map<String, String> headers, T data);

}
