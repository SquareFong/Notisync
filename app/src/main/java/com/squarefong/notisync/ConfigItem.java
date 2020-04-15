package com.squarefong.notisync;

enum WorkingMode {
    Receiver("Receiver", 0), Sender("Sender", 1);

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
    Integer isRun;//1运行 0不运行
    String remarks;
    String address;
    Integer ports;
    String uuid;
    WorkingMode mode;
    ConfigItem(Integer number, String remarks, String address){
        this.number = number;
        this.remarks = remarks;
        this.address = address;
        this.isRun = 0;
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
