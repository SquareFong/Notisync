package com.squarefong.notisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private List<ConfigItem> configList;

    public ConfigsManager configsManager = new ConfigsManager(this);
    public static NotificationListener listener = new NotificationListener();

    static Boolean isFirst = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化主界面 和 recyclerView
        initConfigs();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ConfigAdapter adapter = new ConfigAdapter(this, configList);
        recyclerView.setAdapter(adapter);

        if(isFirst) {
            //打开通知监听服务
            listener.attachBaseContext(this);
            Intent intent = new Intent(MainActivity.this, NotificationListener.class);
            startService(intent);

            isFirst = false;
        }

        //注册广播接收器以更新界面
        IntentFilter filter = new IntentFilter(ConfigFileActivity.action);
        registerReceiver(broadcastReceiver, filter);

        Intent i = new Intent(this, FetchNotiService.class);
        startService(i);

    }

    private void initConfigs() {
        configList = configsManager.getConfigList();
        SharedPreferences settings = getSharedPreferences("Initialize", 0);
        if (!settings.getBoolean("initialized", false)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("initialized", true);
            editor.apply();
            if (configList.size() == 0) {
                ConfigItem item = new ConfigItem(
                        -1, 1,
                        "Untitled Configuration",
                        "127.0.0.1", 2020, "00000000-0000-0000-0000-000000000000",
                        WorkingMode.Sender.getCode(), 0);
                configsManager.insert(item);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }


    //菜单选中
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            //启动Activity，手动输入配置
            case R.id.type_manually:
                Intent intent = new Intent(MainActivity.this,
                        ConfigFileActivity.class);
                intent.putExtra("isNew", true);
                startActivity(intent);
                break;
            case R.id.import_from_clip_board:
                Toast.makeText(MainActivity.this, "You Click Import",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.notification_setting:
                listener.applyAccessSetting();
                break;
            case R.id.caught_notifications:
                Intent intent0 = new Intent(MainActivity.this,
                        NotificationsActivity.class);
                startActivity(intent0);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //unregisterReceiver(this);
            recreate();
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(this.broadcastReceiver);
        super.onDestroy();
    }
}
