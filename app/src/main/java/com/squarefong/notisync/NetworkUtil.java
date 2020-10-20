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
import java.util.Vector;

import static android.content.ContentValues.TAG;

public class NetworkUtil {
    public static Context context;

    private static JSONObject messageToJson(String uuid, MessageItem item) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("UUID", uuid);
        json.put("Time", String.valueOf(System.currentTimeMillis()));
        json.put("Type","Message");

        JSONObject data = new JSONObject();
        data.put("Number", item.Number);
        data.put("Name", item.Name);
        data.put("Body", item.Body);
        data.put("Date", item.Date);
        data.put("Type", item.Type);

        Log.d(TAG, "messageToJson: " + data.toString());
        json.put("Data", StrTool.toBase64(data.toString()));
        return  json;
    }

    private static JSONObject allMessagesToJson(String uuid, Vector<MessageItem> n) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("UUID", uuid);
        json.put("Time", String.valueOf(System.currentTimeMillis()));
        json.put("Type","Message");


        JSONArray array =new JSONArray();

        for (MessageItem item:
                n) {
            JSONObject data = new JSONObject();
            data.put("Number", item.Number);
            data.put("Name", item.Name);
            data.put("Body", item.Body);
            data.put("Date", item.Date);
            data.put("Type", item.Type);
            array.put(data);
        }

        Log.d(TAG, "allMessagesToJson: " + array.toString());
        json.put("Data", StrTool.toBase64(array.toString()));
        return  json;
    }

    private static JSONObject phoneDetailsToJson(String uuid, DetailItem item) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("UUID", uuid);
        json.put("Time", String.valueOf(System.currentTimeMillis()));
        json.put("Type","Detail");

        JSONObject data = new JSONObject();
        data.put("OsVersion", String.valueOf(System.currentTimeMillis()));
        data.put("Model", item.Model);
        data.put("Kernel", item.Kernel);
        data.put("Uptime", item.Uptime);
        data.put("Processor", item.Processor);
        data.put("MemoryUsage", item.MemoryUsage);
        data.put("StorageUsage", item.StorageUsage);

        Log.d(TAG, "phoneDetailsToJson: " + data.toString());
        json.put("Data", StrTool.toBase64(data.toString()));
        return  json;
    }

    private static JSONObject notificationToJson(String uuid, NotificationItem n) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("UUID", uuid);
        json.put("Time", n.time.toString());
        json.put("Type","Notification");

        JSONObject data = new JSONObject();
        data.put("Time", n.time.toString());
        data.put("PackageName", n.packageName);
        data.put("Title", n.title);
        data.put("Content", n.content);
        JSONArray array =new JSONArray();
        array.put(data);
        Log.d(TAG, "notificationToJson: " + array.toString());
        json.put("Data", StrTool.toBase64(array.toString()));
        return  json;
    }

    private static JSONObject allNotificationToJson(String uuid, List<NotificationItem> n) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("UUID", uuid);
        json.put("Time", String.valueOf(System.currentTimeMillis()));
        json.put("Type","Notification");


        JSONArray array =new JSONArray();

        for (NotificationItem item:
             n) {
            JSONObject data = new JSONObject();
            data.put("Time", item.time.toString());
            data.put("PackageName", item.packageName);
            data.put("Title", item.title);
            data.put("Content", item.content);
            array.put(data);
        }

        Log.d(TAG, "allNotificationToJson: " + array.toString());
        json.put("Data", StrTool.toBase64(array.toString()));
        return  json;
    }

    private static void sendPOSTRequest(final String address, final int ports,
                                       final JSONObject json, final HttpCallbackListener listener) {
        //新线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
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

    private static void sendGETRequest(final String address, final int ports,
                                      final String uuid, final Long time,
                                       final HttpCallbackListener listener) {
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

    // 通知项结构体
    class Item {
        String PackageName;
        String Title;
        String Content;
        Long Time;
    }

    public static void getNotifications(final ConfigItem cfg){
        if (cfg.isRun > 0 && cfg.mode.equals(WorkingMode.Receiver)) {
            sendGETCommandRequest(cfg.address, cfg.ports,
                    cfg.uuid, cfg.lastUpdate.longValue(),"Notification", new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
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

    private static void sendGETCommandRequest(final String address, final int ports,
                                       final String uuid, final Long time, final String commandType,
                                       final HttpCallbackListener listener) {
        //新线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    String link = "http://" + address + ":" + ports + "/"
                            + "get";
                    link += "?" + "UUID=" + uuid + "&Time=" + time + "&Type=" + commandType;
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

    static class communicateStruct{
        String UUID;
        String Time;
        String Type;
        String Data;
    }
    public static void getCommand(final ConfigItem cfg){
        if (cfg.isRun > 0 && cfg.mode.equals(WorkingMode.Sender)) {
            sendGETCommandRequest(cfg.address, cfg.ports,
                    cfg.uuid, cfg.lastUpdate.longValue(), "Command", new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            Gson gson = new Gson();
                            communicateStruct s = gson.fromJson(response, communicateStruct.class);
                            if(s.UUID.equals(cfg.uuid)){
                                switch (s.Type) {
                                    case "active": {
                                        //修改ShortMessageReceiver的变量使其自动发送新短信
                                        if (!ShortMessageReceiver.active)
                                            ShortMessageReceiver.active = true;
                                        //与此同时，主动汇报手机detail
                                        DetailItem detailItem = PhoneDetails.getPhoneDetails();
                                        JSONObject ojb = null;
                                        try {
                                            ojb = NetworkUtil.phoneDetailsToJson(cfg.uuid, detailItem);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        NetworkUtil.sendPOSTRequest(cfg.address,
                                                cfg.ports,
                                                ojb,
                                                new HttpCallbackListener() {
                                                    @Override
                                                    public void onFinish(String response) {
                                                        Log.d(TAG, "NetworkUtil:sendPhoneDetails: onFinish: " + response);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {

                                                    }
                                                });
                                        break;
                                    }
                                    case "all": {
                                        //主动上传所有短信
                                        Vector<MessageItem> allMessages = MessagesTool.getAllMessages(context);
                                        JSONObject object = null;
                                        try {
                                            object = NetworkUtil.allMessagesToJson(cfg.uuid, allMessages);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        NetworkUtil.sendPOSTRequest(cfg.address,
                                                cfg.ports,
                                                object,
                                                new HttpCallbackListener() {
                                                    @Override
                                                    public void onFinish(String response) {
                                                        Log.d(TAG, "NetworkUtil:sendAllMessages: onFinish: " + response);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {

                                                    }
                                                });

                                        //修改ShortMessageReceiver的变量使其自动发送新短信
                                        if (!ShortMessageReceiver.active)
                                            ShortMessageReceiver.active = true;
                                        //与此同时，主动汇报手机detail
                                        DetailItem detailItem = PhoneDetails.getPhoneDetails();
                                        JSONObject ojb = null;
                                        try {
                                            ojb = NetworkUtil.phoneDetailsToJson(cfg.uuid, detailItem);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        NetworkUtil.sendPOSTRequest(cfg.address,
                                                cfg.ports,
                                                ojb,
                                                new HttpCallbackListener() {
                                                    @Override
                                                    public void onFinish(String response) {
                                                        Log.d(TAG, "NetworkUtil:sendPhoneDetails: onFinish: " + response);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {

                                                    }
                                                });
                                        break;
                                    }
                                    case "dead": {
                                        //修改ShortMessageReceiver的变量使其 不要 自动发送新短信
                                        if (ShortMessageReceiver.active)
                                            ShortMessageReceiver.active = false;
                                        break;
                                    }
                                    case "newSMS": {
                                        String data = StrTool.fromBase64(s.Data);
                                        MessageItem item = gson.fromJson(data, MessageItem.class);
                                        MessagesTool.sendMessage(item.Number, item.Body);
                                        break;
                                    }
                                    default:
                                        Log.d(TAG, "getCommand onFinish: command type error");
                                        break;
                                }
                            }
                            else {
                                Log.d(TAG, "getCommand onFinish: " + "UUID错误");
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

        }
    }
}
