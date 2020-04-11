package com.squarefong.notisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;

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

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }
}
