package com.squarefong.notisync;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import android.util.Base64;

import static android.util.Base64.encodeToString;

public class StrTool {
    public static String toUTF8(String androidContent){
        try {
            return URLDecoder.decode(androidContent, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String toBase64(String rawContent){
        return encodeToString(rawContent.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
    }

    public static String fromBase64(String rawBase64) {
        return new String(Base64.decode(rawBase64.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
    }
}
