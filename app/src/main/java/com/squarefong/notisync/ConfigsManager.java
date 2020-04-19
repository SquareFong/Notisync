package com.squarefong.notisync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

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
    private static ConfigsHelper helper;
    ConfigsManager(Context context){
        helper = new ConfigsHelper(context);
    }

    static List<ConfigItem> configList=new ArrayList<>();

    static ConfigItem getConfigItemByID(Integer number){
        for (ConfigItem item : configList) {
            if (item.number.equals(number)) {
                return item;
            }
        }
        Log.d(TAG, "getConfigItemByID: 根据ID查找失败");
        return (ConfigItem)configList.iterator();
    }

    List<ConfigItem> getConfigList() {
        if(configList.size() == 0){
            String QUERY = "SELECT * FROM " + ConfigsHelper.TABLE_NAME;
            String[] selectionArgs = { "id", "isRun", "remarks",
                    "address", "ports", "uuid", "mode" };
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor = db.query(ConfigsHelper.TABLE_NAME, null, null,
                    null, null ,null, "id");
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
            cursor.close();
        }
        return configList;
    }

    public void insert(ConfigItem item){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isRun", item.isRun);
        values.put("remarks", item.remarks);
        values.put("address", item.address);
        values.put("ports", item.ports);
        values.put("uuid", item.uuid);
        values.put("mode", item.mode.getCode());
        //result 即为rowID
        Long result = db.insert(ConfigsHelper.TABLE_NAME, null, values);
        if(result < 0) {
            Log.d(TAG, "update: db.insert occurred an error");
        }
        if(item.number == -1){
            //从数据库更新item的ID然后插入configList
            item.number = result.intValue();
            configList.add(item);
        }
    }

    public void delete(ConfigItem item){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] args = {item.number.toString()};
        db.delete(ConfigsHelper.TABLE_NAME, "id=?", args);
        configList.remove(item);
    }

    public void update(ConfigItem item){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", item.number);
        values.put("isRun", item.isRun);
        values.put("remarks", item.remarks);
        values.put("address", item.address);
        values.put("ports", item.ports);
        values.put("uuid", item.uuid);
        values.put("mode", item.mode.getCode());
        long result = db.replace(ConfigsHelper.TABLE_NAME, null, values);
        if(result < 0) {
            Log.d(TAG, "update: db.replace occurred an error");
        }
    }
}
