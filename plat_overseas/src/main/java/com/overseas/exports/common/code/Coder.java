package com.overseas.exports.common.code;

public interface Coder<I, C, O> {

	public O encode(I input, C condition);

	public I decode(O input, C condition);
}
