package com.squarefong.notisync;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    public static List<NotificationItem> notifications = new ArrayList<>();
    NotificationManager(){
        //TODO 考虑做持久化
    }

    public static void add(String packageName, String title, String content){
        notifications.add(new NotificationItem(packageName,title,content));
    }

    public static List<NotificationItem> getNotifications() {
        return notifications;
    }
}

class NotificationItem {
    String packageName;
    String title;
    String content;
    Long time;
    NotificationItem(String packageName, String title, String content){
        this.packageName = packageName;
        this.title = title;
        this.content = content;
        this.time = System.currentTimeMillis() / 1000;
    }
}