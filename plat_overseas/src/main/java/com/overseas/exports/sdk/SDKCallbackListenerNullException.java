package com.overseas.exports.sdk;

public class SDKCallbackListenerNullException extends Exception {
    public SDKCallbackListenerNullException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public SDKCallbackListenerNullException(String detailMessage) {
        super(detailMessage);
    }

    public SDKCallbackListenerNullException(Throwable throwable) {
        super(throwable);
    }

    public SDKCallbackListenerNullException() {
    }
}
