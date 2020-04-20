package com.squarefong.notisync;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.content.ContentValues.TAG;

public class NetworkUtil {
    public static Context context;

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

    public static void sendNotification(final NotificationItem item){
        //遍历配置列表，选择所有发送的配置执行
        new Thread(new Runnable(){
            @Override
            public void run() {
                for (ConfigItem cfg:ConfigsManager.configList) {
                    if (cfg.isRun > 0 && cfg.mode.equals(WorkingMode.Sender)) {
                        try {
                            JSONObject ojb = NetworkUtil.notificationToJson(
                                    cfg.uuid,
                                    item);
                            NetworkUtil.sendPOSTRequest(cfg.address,
                                    cfg.ports,
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
        }).start();
    }

    public static void sendGETRequest(final String address, final int ports,
                                      final String uuid, final Long time, final HttpCallbackListener listener) {
        //新线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    String link = "http://" + address + ":" + ports + "/"
                            + "get";
                    link += "?" + "UUID=" + uuid + "&Time=" + time;
                    URL url = new URL(link);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("Accept-Charset", "UTF-8");
                    conn.connect();

                    String result = "";
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        InputStreamReader inputStreamReader = new
                                InputStreamReader(inputStream,"utf-8");
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null){
                            content.append(line);
                        }
                        result = content.toString();
                    }

                    if (result.length() > 0) {
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

    class Item {
        String PackageName;
        String Title;
        String Content;
        Long Time;
    }

    public static void getNotifications(){
        for (final ConfigItem cfg:ConfigsManager.configList) {
            if (cfg.isRun > 0 && cfg.mode.equals(WorkingMode.Receiver)) {
                sendGETRequest(cfg.address, cfg.ports,
                        cfg.uuid, cfg.lastUpdate.longValue(), new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        //TODO 更新最新通知时间
                        Log.d(TAG, "onFinish: " + response);
                        Gson gson = new Gson();
                        List<Item> notificationItemList= gson.fromJson(response,
                                new TypeToken<List<Item>>(){}.getType());
                        for(Item item : notificationItemList){
                            Log.d(TAG, "onFinish: GotNotification: Time:" + item.Time);
                            Log.d(TAG, "onFinish: GotNotification: packageName:" + item.PackageName);
                            Log.d(TAG, "onFinish: GotNotification: title:" + item.Title);
                            Log.d(TAG, "onFinish: GotNotification: content:" + item.Content);
                            FetchNotiService.postNotification(item.Title, item.Content);
                            if (item.Time.compareTo(cfg.lastUpdate.longValue()) > 0){
                                cfg.lastUpdate = item.Time.intValue();
                                //以后考虑更优雅方式
                                ConfigsManager configsManager = new ConfigsManager(ConfigsManager.context);
                                configsManager.update(cfg);
                                Log.d(TAG, "onFinish: 更新最新时间" + cfg.lastUpdate);
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        }
    }
}
