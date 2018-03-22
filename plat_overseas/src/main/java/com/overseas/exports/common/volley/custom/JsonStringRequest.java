/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.overseas.exports.common.volley.custom;

import com.overseas.exports.common.volley.AuthFailureError;
import com.overseas.exports.common.volley.NetworkResponse;
import com.overseas.exports.common.volley.Response;
import com.overseas.exports.common.volley.toolbox.HttpHeaderParser;
import com.overseas.exports.common.volley.toolbox.JsonRequest;

import java.util.Map;

/**
 * query参数请求封闭类（如get下：http://xxx/server?name=iWgang&city=cd） post下的body为name=iWgang&city=cd
 * Created by wanggang on 2016/6/8
 * E-Mail: wanggang@6lapp.com
 */
public class JsonStringRequest extends JsonRequest<byte[]> {
	private OnResponseListener<byte[]> mOnResponseListener;
	private Map<String, String> mResponseHeaders;
    private int mStatusCode;
    private Map<String, String> mHeaderMap;

    public JsonStringRequest(String url, Map<String, String> headerMap, String requestBody, OnResponseListener<byte[]> onResponseListener) {
        this(Method.POST, url, headerMap, requestBody, onResponseListener);
    }
    
    public JsonStringRequest(int method, String url, Map<String, String> headerMap, String requestBody, OnResponseListener<byte[]> onResponseListener) {
        super(method, url, requestBody, null, onResponseListener);
        mOnResponseListener = onResponseListener;
        mHeaderMap = headerMap;
    }
    
    @Override
    protected void onFinish() {
        super.onFinish();
        mOnResponseListener = null;
    }
    
    @Override
    protected void deliverResponse(byte[] response) {
        if (mOnResponseListener != null) {
        	mOnResponseListener.onResponse(mStatusCode, mResponseHeaders, response);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return null == mHeaderMap ? super.getHeaders() : mHeaderMap;
    }

    @Override
	protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
		mStatusCode = response.statusCode;
		mResponseHeaders = response.headers;
		return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
	}
    
}