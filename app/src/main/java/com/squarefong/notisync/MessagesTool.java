package com.squarefong.notisync;

public class MessagesTool {
}

class MessageItem {
    String number;
    String name;
    String body;
    String date;
    String type;
    public MessageItem(String Number, String Name, String Body, String Date, String Type){
        number = Number;
        name = Name;
        body = Body;
        date = Date;
        type = Type;
    }

    public MessageItem() {

    }
}