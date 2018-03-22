package com.overseas.exports.common.code;

public interface StringCoder<I, C> extends Coder<I, C, String> {

    public abstract String encode(I input, C condition);

    public abstract I decode(String input, C condition);
}