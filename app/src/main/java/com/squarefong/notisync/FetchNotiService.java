package com.squarefong.notisync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static android.content.ContentValues.TAG;

public class FetchNotiService extends Service {
    static String channelID = "FetchNotifications";
    static String channelName = "通知同步";
    static NotificationManager manager;
    static Context context;
    static int id = 0;
    public FetchNotiService() {
    }

    static void postNotification(String title, String content){
        Notification notification = new Notification.Builder(context, channelID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true).build();
        manager.notify(++id, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
        //return null;
    }

    private Handler handler;
    private int delay = 1000*3;//周期时间
    private int launchDelay = 5000;// 这是8小时的毫秒数 为了少消耗流量和电量，8小时自动更新一次

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationChannel channel = new NotificationChannel(
                channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        context = getApplicationContext();

        handler = new Handler();
        // handler自带方法实现定时器
        //每隔3s执行
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                // handler自带方法实现定时器
                Log.d(TAG, "run: ##############33333333333333333333################");

                for (ConfigItem cfg : ConfigsManager.configList) {
                    if (cfg.isRun > 0 && cfg.mode.equals(WorkingMode.Receiver)) {
                        NetworkUtil.getNotifications();
                    }
                }

                handler.postDelayed(this, delay);//每隔3s执行
            }
        };
        handler.postDelayed(runnable, launchDelay);//延时多长时间启动定时器
    }
}
