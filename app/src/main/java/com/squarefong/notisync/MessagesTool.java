package com.squarefong.notisync;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.Vector;

/*
 * 获取所有短信的类，使用方法：
 * Vector<MessageItem> items =  MessagesTool.getAllMessages(this);
 * 使用前务必使用checkPermission以申请权限。
 * */

public class MessagesTool {
    static public void sendMessage(String number, String content){
        SmsManager massage = SmsManager.getDefault();
        massage.sendTextMessage(number, null, content, null, null);
    }

    static public Vector<MessageItem> getAllMessages(Context context){
        Vector<MessageItem> allMessages = new Vector<>();
        final Uri SMS_URI_ALL = Uri.parse("content://sms/");
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = cr.query(SMS_URI_ALL, projection, null, null,
                "date desc");

        if (null == cur) {
            Log.i("ooc", "************cur == null");
            return allMessages;
        }


        int add = cur.getColumnIndex("address");
        int per = cur.getColumnIndex("person");
        int bod = cur.getColumnIndex("body");
        int dat = cur.getColumnIndex("date");
        int typ = cur.getColumnIndex("type");
        while (cur.moveToNext()) {
            String number = cur.getString(add); //手机号
            String name = cur.getString(per);   //联系人姓名
            String body = cur.getString(bod);   //短信内容
            String date = cur.getString(dat);
            String type = cur.getString(typ);
            allMessages.add(new MessageItem(number, name, body, date,type));
        }
        cur.close();
        return allMessages;
    }

    public static void checkPermission(Activity parent) {
        /**
         * 第 1 步: 检查是否有相应的权限
         */

        boolean isAllGranted = true;
        String[] permissions = new String[] {
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
        };
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(parent, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                isAllGranted = false;
                break;
            }
        }
        // 如果这3个权限全都拥有, 则直接执行读取短信代码
        if (isAllGranted) {
        } else {

            int MY_PERMISSION_REQUEST_CODE = 1000;

            parent.requestPermissions(
                    new String[]{
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS,
                    },
                    MY_PERMISSION_REQUEST_CODE
            );
        }
    }
}

//1表示接收，2表示发送
class MessageItem {
    String Number;
    String Name;
    String Body;
    String Date;
    String Type;
    public MessageItem(String Number, String Name, String Body, String Date, String Type){
        this.Number = Number;
        this.Name = Name;
        this.Body = Body;
        this.Date = Date;
        this.Type = Type;
    }

    public MessageItem() {

    }
}