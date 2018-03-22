package com.overseas.exports.common.code.impl;

import com.overseas.exports.common.code.StringCoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 描述：json解析器
 *
 * @author songjian
 */
public class JsonObjectCoder implements StringCoder<Map<String, ?>, Void> {

    @Override
    public Map<String, ?> decode(String input, Void condition) {
        Map<String, Object> decodedMap = null;
        try {
            decodedMap = new HashMap<String, Object>();
            JSONObject jsonObject = new JSONObject(input);
            for (Iterator<String> keysIterator = jsonObject.keys(); keysIterator
                    .hasNext(); ) {
                String key = keysIterator.next();

                Object value = jsonObject.get(key);

                if (value instanceof JSONObject) {
                    decodedMap.put(key, decode(value.toString(), condition));

                } else if (value instanceof JSONArray) {
                    decodedMap.put(key, new JsonListCoder().decode(
                            value.toString(), condition));
                } else {
                    decodedMap.put(key, value);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return decodedMap;
    }

    @Override
    public String encode(Map<String, ?> input, Void condition) {

        return new JSONObject(input).toString();
    }

}
