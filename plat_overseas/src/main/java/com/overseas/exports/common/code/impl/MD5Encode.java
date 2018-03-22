package com.overseas.exports.common.code.impl;

import android.text.TextUtils;

import com.overseas.exports.common.util.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encode {

    public String authEncode(String $string, String key) {
        if (TextUtils.isEmpty(key)) {
        }
        return uc_authcode($string, "ENCODE", key, 0);
    }

    public String authEncode(String $string, String key, int $expiry) {
        if (TextUtils.isEmpty(key)) {
        }
        return uc_authcode($string, "ENCODE", key, $expiry);
    }
    /**
     *
     * 描述：FF平台加密
     *
     * @param $string
     *            原文
     * @return 加密后的字符
     */
//	public String authEncode(String $string) {
//		return uc_authcode($string, "ENCODE", MD5Lib.GetMd5Key(), 0);
//	}

    /**
     * 描述：FF平台加密
     *
     * @param $string 原文
     * @param $expiry 有效期 ,单位秒
     * @return
     */
//	public String authEncode(String $string, int $expiry) {
//		return uc_authcode($string, "ENCODE", MD5Lib.GetMd5Key(), $expiry);
//	}

    // public String uc_authcode(String $string, String $operation, String $key)
    // {
    // return uc_authcode($string, "ENCODE", $key, 0);
    // }
    public String uc_authcode(String $string, String $operation, String $key,
                              int $expiry) {
        try {
            $string = URLEncoder.encode($string, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }// 支持中文
        int $ckey_length = 4; // note 随机密钥长度 取值 0-32;
        // note
        // 加入随机密钥，可以令密文无任何规律，即便是原文和密钥完全相同，加密结果也会每次不同，增大破解难度。
        // note 取值越大，密文变动规律越大，密文变化 = 16 的 $ckey_length
        // 次方
        // note 当此值为 0 时，则不产生随机密钥

        $key = md5($key != null ? $key : "ffgame");
        String $keya = md5(substr($key, 0, 16));
        String $keyb = md5(substr($key, 16, 16));
        String $keyc = $ckey_length > 0 ? ($operation.equals("DECODE") ? substr(
                $string, 0, $ckey_length) : substr(md5(microtime()),
                -$ckey_length))
                : "";

        String $cryptkey = $keya + md5($keya + $keyc);
        int $key_length = $cryptkey.length();

        $string = $operation.equals("DECODE") ? base64_decode(substr($string,
                $ckey_length)) : sprintf("%010d", $expiry > 0 ? $expiry
                + time() : 0)
                + substr(md5($string + $keyb), 0, 16) + $string;

        int $string_length = $string.length();

        StringBuffer $result1 = new StringBuffer();

        int[] $box = new int[256];
        for (int i = 0; i < 256; i++) {
            $box[i] = i;
        }

        int[] $rndkey = new int[256];
        for (int $i = 0; $i <= 255; $i++) {
            $rndkey[$i] = (int) $cryptkey.charAt($i % $key_length);
        }

        int $j = 0;
        for (int $i = 0; $i < 256; $i++) {
            $j = ($j + $box[$i] + $rndkey[$i]) % 256;
            int $tmp = $box[$i];
            $box[$i] = $box[$j];
            $box[$j] = $tmp;
        }

        $j = 0;
        int $a = 0;
        for (int $i = 0; $i < $string_length; $i++) {
            $a = ($a + 1) % 256;
            $j = ($j + $box[$a]) % 256;
            int $tmp = $box[$a];
            $box[$a] = $box[$j];
            $box[$j] = $tmp;
            $result1.append((char) (((int) $string.charAt($i)) ^ ($box[($box[$a] + $box[$j]) % 256])));

        }

        if ($operation.equals("DECODE")) {
            String $result = $result1.substring(0, $result1.length());
            if ((Integer.parseInt(substr($result.toString(), 0, 10)) == 0 || Long
                    .parseLong(substr($result.toString(), 0, 10)) - time() > 0)
                    && substr($result.toString(), 10, 16).equals(
                    substr(md5(substr($result.toString(), 26) + $keyb),
                            0, 16))) {
                return substr($result.toString(), 26);
            } else {
                return "";
            }
        } else {
            return $keyc
                    + base64_encode($result1.toString()).replaceAll("=", "");
        }
    }

    protected String base64_encode(String input) {
        try {
            String jString = new String(Base64.encode(input
                    .getBytes("iso-8859-1")));
            return new String(jString.getBytes(), "UTF-8");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    protected String substr(String input, int begin, int length) {
        return input.substring(begin, begin + length);
    }

    protected String substr(String input, int begin) {
        if (begin > 0) {
            return input.substring(begin);
        } else {
            return input.substring(input.length() + begin);
        }
    }

    protected String base64_decode(String input) {
        try {
            String jString = new String(Base64.decode(input.toCharArray()),
                    "iso-8859-1");
            return new String(jString.getBytes(), "UTF-8");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    protected String md5(String input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return byte2hex(md.digest(input.getBytes()));
    }

    protected String md5(long input) {
        return md5(String.valueOf(input));
    }

    protected String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
        }
        return hs.toString();
    }

    protected long microtime() {
        return System.currentTimeMillis();
    }

    protected long time() {
        return System.currentTimeMillis() / 1000;
    }

    protected String sprintf(String format, long input) {
        String temp = "0000000000" + input;
        return temp.substring(temp.length() - 10);
    }
}
