package com.squarefong.notisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ConfigFileActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener{

    public static String action = "com.squarefong.notisync.ConfigFileActivity";

    private static final String TAG = "ConfigFileActivity";
    ConfigItem item;

    Boolean isNew;
    SwitchCompat mSwitch;
    EditText remarks;
    EditText address;
    EditText ports;
    EditText uuid;
    Spinner workingMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_file);

        mSwitch = findViewById(R.id.sc_run);
        remarks = findViewById(R.id.et_remarks);
        address = findViewById(R.id.et_address);
        ports = findViewById(R.id.et_ports);
        uuid = findViewById(R.id.et_UUID);
        workingMode = findViewById(R.id.sp_workingMode);


        if (mSwitch != null) {
            mSwitch.setOnCheckedChangeListener(this);
        }
        Intent intent = getIntent();
        isNew = intent.getBooleanExtra("isNew", false);
        Log.d(TAG, "onCreate: Get intent extra \'isNew\' " + isNew);
        if(isNew){
            item = new ConfigItem(-1, "Untitled", "");
            mSwitch.setChecked(false);
            item.isRun=0;
        }
        else {
            Integer number = intent.getIntExtra("id", -1);
            item = ConfigsManager.getConfigItemByID(number);
            mSwitch.setChecked(item.isRun > 0);
            remarks.setText(item.remarks);
            address.setText(item.address);
            ports.setText(item.ports.toString());
            uuid.setText(item.uuid);
            workingMode.setSelection(item.mode.getCode());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_config_file, menu);
        return true;
    }

    @SuppressLint("ShowToast")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_config:
                Toast.makeText(ConfigFileActivity.this, "You Click Config File",
                        Toast.LENGTH_SHORT).show();
                saveConfig();
                break;
            case R.id.del_config:
                Toast.makeText(ConfigFileActivity.this, "You Click Delete File",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveConfig(){

        Integer p = workingMode.getSelectedItemPosition();
        WorkingMode mode = (p == 0 ? WorkingMode.Receiver: WorkingMode.Sender);

        //TODO 数据格式检查



        item.remarks = remarks.getText().toString();
        item.address = address.getText().toString();
        item.ports = Integer.parseInt(ports.getText().toString());
        item.uuid = uuid.getText().toString();
        item.mode = mode;

        ConfigsManager configsManager = new ConfigsManager(this);
        if (isNew)
            configsManager.insert(item);
        else {
            configsManager.update(item);
            //发送广播以更新主界面
            Intent intent = new Intent(action);
            sendBroadcast(intent);
        }

        finish();
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "The Switch is " + (isChecked ? "on" : "off"),
                Toast.LENGTH_SHORT).show();
        item.isRun = (isChecked?1:0);
    }
}
