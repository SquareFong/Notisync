package com.squarefong.notisync;

import android.app.Activity;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NotificationListener extends NotificationListenerService {

    public void applyAccessSetting(){
        gotoNotificationAccessSetting(this.getApplicationContext());
    }

    //当有新通知
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG,"Enter onNotificationPosted ");
        Notification notification = sbn.getNotification();
        String pkgName = sbn.getPackageName();
        String title = notification.extras.getString(Notification.EXTRA_TITLE);
        String content = notification.extras.getString(Notification.EXTRA_TEXT);
        Log.d(TAG, pkgName + " : " + title + content);
    }

    private boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;

        } catch (ActivityNotFoundException e) {//普通情况下找不到的时候需要再特殊处理找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings",
                        "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Toast.makeText(context, "对不起，您的手机暂不支持", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    private boolean isNotificationListenersEnabled() {
        String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
        String pkgName = getPackageName();
        String flat = Settings.Secure.getString(getContentResolver(),   ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

}
