package com.squarefong.notisync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Vector;

import static android.content.ContentValues.TAG;

public class ShortMessageReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //判断广播消息
        if (action.equals(SMS_RECEIVED_ACTION)){
            Bundle bundle = intent.getExtras();
            //如果不为空
            if (bundle!=null){
                //将pdus里面的内容转化成Object[]数组
                Object[] pdusData = (Object[]) bundle.get("pdus");// pdus ：protocol data unit  ：
                if(pdusData != null) {
                    //解析短信
                    SmsMessage[] msg = new SmsMessage[pdusData.length];
                    String format = bundle.getString("format");
                    Vector<MessageItem> messageItems = new Vector<>();
                    for (Object pdusObj : pdusData){
                        SmsMessage sms = SmsMessage.createFromPdu((byte[])pdusObj, format);MessageItem item = new MessageItem();
                        item.body = sms.getMessageBody();
                        item.number = sms.getOriginatingAddress();
                        item.type = String.valueOf(1);
                        item.date = String.valueOf(System.currentTimeMillis());
                        messageItems.add(item);

                        Log.d(TAG, "onReceive: " + "Number: " + item.number);
                        Log.d(TAG, "onReceive: " + "Name: " + item.name);
                        Log.d(TAG, "onReceive: " + "Body: " + item.body);
                        Log.d(TAG, "onReceive: " + "Date: " + item.date);
                        Log.d(TAG, "onReceive: " + "Type: " + item.type);
                    }
                }
            }
        }
    }
}
