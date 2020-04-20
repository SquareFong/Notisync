package com.squarefong.notisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "com.squarefong.notisync.NotificationsActivity";
    public static String action = "com.squarefong.notisync.NotificationsActivity";
    public static boolean isFirst = true;

    NotificationAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        RecyclerView recyclerView = findViewById(R.id.rv_notifications);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new NotificationAdapter(NotificationsManager.notifications);
        adapter.parentContext = this;
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "进入 onCreate");

        //注册广播接收器以更新界面
        IntentFilter filter = new IntentFilter(NotificationsActivity.action);
        registerReceiver(adapter.broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(adapter.broadcastReceiver);
        super.onDestroy();
    }
}
