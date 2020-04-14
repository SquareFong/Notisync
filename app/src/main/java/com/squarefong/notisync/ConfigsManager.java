package com.squarefong.notisync;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

class ConfigsHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notisync.db";
    public static final String TABLE_NAME = "Configs";


    ConfigsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db = getWritableDatabase();
        String CREATE_TABLE = "create table if not exists " +
                TABLE_NAME +
                " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "isRun INTEGER, " +
                "remarks TEXT, " +
                "address TEXT, " +
                "ports INTEGER, " +
                "uuid TEXT, " +
                "mode INTEGER" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

public class ConfigsManager {

    private SQLiteDatabase db;
    ConfigsManager(Context context){
        ConfigsHelper helper = new ConfigsHelper(context);
        db = helper.getWritableDatabase();
    }

    private List<ConfigItem> configList=new ArrayList<>();

    public List<ConfigItem> getConfigList() {
        if(configList.size() == 0){
            String QUERY = "SELECT * FROM " + ConfigsHelper.TABLE_NAME;
            String[] selectionArgs = { "id", "isRun", "remarks",
                    "address", "ports", "uuid", "mode" };
            @SuppressLint("Recycle") Cursor cursor = db.rawQuery(QUERY, selectionArgs);
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ConfigItem item = new ConfigItem(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getInt(4),
                            cursor.getString(5),
                            cursor.getInt(6));
                    configList.add(item);
                }
            }
        }
        return configList;
    }

    public void insert(ConfigItem item){
        ContentValues values = new ContentValues();
        values.put("isRun", item.isRun);
        values.put("remarks", item.remarks);
        values.put("address", item.address);
        values.put("ports", item.ports);
        values.put("uuid", item.uuid);
        values.put("mode", item.mode.getCode());
        db.insert(ConfigsHelper.TABLE_NAME, null, values);
    }
}
