package com.squarefong.notisync;

import android.net.UrlQuerySanitizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpUtil {
    public static void sendGETRequest(final String address, final String method, final
        HttpCallbackListener listener) {
        //新线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection=null;
                try {
                    URL url = new URL(address);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod(method);
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        //回调onfinish方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
