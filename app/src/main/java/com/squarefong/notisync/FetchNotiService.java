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
    private int delay = 1000*3;
    private int launchDelay = 3000;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationChannel channel = new NotificationChannel(
                channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        context = getApplicationContext();

        //创建获取通知的进程
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (ConfigItem cfg : ConfigsManager.configList) {
                    if (cfg.isRun > 0 && cfg.mode.equals(WorkingMode.Receiver)) {
                        NetworkUtil.getNotifications(cfg);
                    }
                }

                handler.postDelayed(this, delay);//每隔3s执行
            }
        };
        handler.postDelayed(runnable, launchDelay);//延时启动定时器

        //创建心跳指令进程
        final Handler heartBeatHandler = new Handler();
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                for (ConfigItem cfg : ConfigsManager.configList) {
                    if (cfg.isRun > 0 && cfg.mode.equals(WorkingMode.Sender)) {
                        NetworkUtil.getCommand(cfg);
                    }
                }
                heartBeatHandler.postDelayed(this, 1000);//每隔1s执行
            }
        };
        heartBeatHandler.postDelayed(runnable1, launchDelay);

    }
}
