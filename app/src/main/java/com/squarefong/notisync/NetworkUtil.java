package com.squarefong.notisync;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class NetworkUtil {
    public static JSONObject notificationToJson(String uuid, NotificationItem n) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("UUID", uuid);
        json.put("Time", n.time.toString());
        JSONObject data = new JSONObject();
        data.put("Time", n.time.toString());
        data.put("PackageName", n.packageName);
        data.put("Title", n.title);
        data.put("Content", n.content);
        JSONArray array =new JSONArray();
        array.put(data);
        json.put("Data", array);
        return  json;
    }

    public static void sendPOSTRequest(final String address, final int ports,
                                       final JSONObject json, final HttpCallbackListener listener) {
        //新线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn=null;
                try {

                    URL url = new URL("http",address,ports,"send");

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("accept", "application/json");
//                    conn.setReadTimeout(8000);
//                    conn.setConnectTimeout(8000);
                    if (json != null){
                        byte[] writeBytes = json.toString().getBytes();
                        conn.setRequestProperty("Content-Length", String.valueOf(writeBytes.length));
                        OutputStream outputStream = conn.getOutputStream();
                        outputStream.write(writeBytes);
                        outputStream.flush();
                        outputStream.close();
                        Log.d(TAG, "sendPOSTRequest: conn" + conn.getResponseCode());
                    }
                    String result = "";
                    if (conn.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        result = reader.readLine();
                    }

                    if (listener != null) {
                        //回调onfinish方法
                        listener.onFinish(result);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void sendNotification(NotificationItem item){
        //遍历配置列表，选择所有发送的配置执行
        for (ConfigItem cfg:ConfigsManager.configList) {
            if (cfg.isRun > 0 && cfg.mode.equals(WorkingMode.Sender)) {
                try {
                    JSONObject ojb = NetworkUtil.notificationToJson(
                            cfg.uuid,
                            item);
                    NetworkUtil.sendPOSTRequest(cfg.address,
                            Integer.parseInt(cfg.address),
                            ojb,
                            new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            Log.d(TAG, "NetworkUtil:sendNotification: onFinish: " + response);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



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
