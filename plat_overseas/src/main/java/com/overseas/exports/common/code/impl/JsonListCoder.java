package com.overseas.exports.common.code.impl;

import com.overseas.exports.common.code.StringCoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonListCoder implements StringCoder<Collection<?>, Void> {

    @Override
    public Collection<?> decode(String input, Void condition) {
        List<Object> decodedList;
        try {
            JSONArray jsonArray = new JSONArray(input);
            decodedList = new ArrayList<Object>();

            for (int i = 0; i < jsonArray.length(); i++) {

                Object value = jsonArray.get(i);

                if (value instanceof JSONObject) {
                    decodedList.add(new JsonObjectCoder().decode(
                            value.toString(), condition));

                } else if (value instanceof JSONArray) {
                    decodedList.add(decode(value.toString(), condition));
                } else {
                    decodedList.add(value);
                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
            return null;
        }
        return decodedList;
    }

    @Override
    public String encode(Collection<?> input, Void condition) {

        return new JSONArray(input).toString();
    }

}
