package com.squarefong.notisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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


        //TODO test 测试JSON用，记得注释掉
        try {
            JSONObject ojb = NetworkUtil.notificationToJson(
                    "7517e18a-40a6-4902-a7c9-23bd0ef7f00f",
                    new NotificationItem("com.v2ray.ang",
                            "ggc-hk",
                            "这是一条测试信息abc"));
            NetworkUtil.sendPOSTRequest("192.168.50.151", 9090, ojb, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.d(TAG, "onFinish: " + response);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initConfigs() {
        configList = configsManager.getConfigList();
        //TODO 测试用 最后删掉
        if (configList.size() == 0 ){
            ConfigItem item = new ConfigItem(
                    -1, 1,
                    "Untitled Configuration",
                    "192.168.50.151", 9090,"000-000",
                    WorkingMode.Sender.getCode(),0);
            configsManager.insert(item);
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
