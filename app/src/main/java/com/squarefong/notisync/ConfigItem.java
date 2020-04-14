package com.squarefong.notisync;

enum WorkingMode {
    Receiver("Receiver", 1), Sender("Sender", 2);

    private final String name;
    private final Integer code;
    WorkingMode(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName(){
        return name;
    }

    public Integer getCode(){
        return code;
    }

}

class ConfigItem {
    Integer number;
    Integer isRun;
    String remarks;
    String address;
    Integer ports;
    String uuid;
    WorkingMode mode;
    ConfigItem(Integer number, String remarks, String address){
        this.number = number;
        this.remarks = remarks;
        this.address = address;
    }

    ConfigItem(Integer number, Integer isRun, String remarks,
               String address, Integer ports, String uuid,
               Integer mode) {
        this.number = number;
        this.isRun = isRun;
        this.remarks = remarks;
        this.address = address;
        this.ports = ports;
        this.uuid = uuid;
        this.mode = (mode==1? WorkingMode.Receiver:WorkingMode.Sender);
    }
}
