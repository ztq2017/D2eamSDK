
package com.overseas.exports.common.volley.custom;

import com.overseas.exports.common.volley.AuthFailureError;
import com.overseas.exports.common.volley.HTTPSTrustManager;
import com.overseas.exports.common.volley.NetworkResponse;
import com.overseas.exports.common.volley.Request;
import com.overseas.exports.common.volley.Response;
import com.overseas.exports.common.volley.toolbox.HttpHeaderParser;

import java.net.HttpURLConnection;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class QueryParameterRequest extends Request<byte[]> {
    private OnResponseListener<byte[]> mOnResponseListener;
    private Map<String, String> mQueryParameterMap;
    private Map<String, String> mResponseHeaders;
    private Map<String, String> mHeaders;
    private int mStatusCode;
    private String mReqUrl;
    private int mMethod;

    public QueryParameterRequest(String url, Map<String, String> queryParameterMap, OnResponseListener<byte[]> onResponseListener) {
        this(Method.GET, url, queryParameterMap, onResponseListener);
    }

    public QueryParameterRequest(int method, String url, Map<String, String> queryParameterMap, OnResponseListener<byte[]> onResponseListener) {
        super(method, url, onResponseListener);
        mMethod = method;
        mReqUrl = url;

        // 预处理value为空的内容，为空时使用空字符串
        if (null != queryParameterMap && !queryParameterMap.isEmpty()) {
            for (Map.Entry<String, String> kv : queryParameterMap.entrySet()) {
                if (kv.getValue() == null) {
                    queryParameterMap.put(kv.getKey(), "");
                }
            }
        }
        mQueryParameterMap = queryParameterMap;
        mOnResponseListener = onResponseListener;
    }

    @Override
    public String getUrl() {
        if (mMethod == Request.Method.GET) {
            if (null != mQueryParameterMap && mQueryParameterMap.size() > 0) {
                int index = 1;
                StringBuilder urlBuilder = new StringBuilder(mReqUrl);
                boolean isUrlContainsQuestionMark = mReqUrl.contains("?");
                for (Map.Entry<String, String> kv : mQueryParameterMap.entrySet()) {
                    urlBuilder.append((index == 1 && !isUrlContainsQuestionMark) ? "?" : "&");
                    urlBuilder.append(kv.getKey()).append("=").append(kv.getValue());
                    index++;
                }
                mReqUrl = urlBuilder.toString();
            }
        }
        return mReqUrl;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mQueryParameterMap;
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
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        mStatusCode = response.statusCode;
        mResponseHeaders = response.headers;
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

}