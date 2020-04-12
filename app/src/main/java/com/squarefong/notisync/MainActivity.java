package com.squarefong.notisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<ConfigItem> configList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initConfigs();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ConfigAdapter adapter = new ConfigAdapter(configList);
        recyclerView.setAdapter(adapter);

    }

    private void initConfigs() {
        ConfigItem a = new ConfigItem(1, "123","Share 1");
        configList.add(a);
        ConfigItem b = new ConfigItem(2, "234","Share 2");
        configList.add(b);
        ConfigItem c = new ConfigItem(3, "345","Share 3");
        configList.add(c);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.type_manually:
                Intent intent = new Intent(MainActivity.this, ConfigFileActivity.class);
                startActivity(intent);
                break;
            case R.id.import_from_clip_board:
                Toast.makeText(MainActivity.this, "You Click Import",
                        Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }
}
