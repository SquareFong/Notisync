package com.squarefong.notisync;

public class PhoneDetails {
}


class DetailItem{
    String OsVersion;
    String Model;
    String Kernel;
    String Uptime;
    String Processor;
    String MemoryUsage;
    String StorageUsage;
    public DetailItem(String osVersion, String model, String kernel,
                      String uptime, String processor,
                      String memoryUsage, String storageUsage){
        OsVersion = osVersion;
        Model = model;
        Kernel = kernel;
        Uptime = uptime;
        Processor = processor;
        MemoryUsage = memoryUsage;
        StorageUsage = storageUsage;
    }
}