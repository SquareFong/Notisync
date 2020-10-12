package com.squarefong.notisync;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
/*
 * 获取手机详细信息的工具类，使用方法
 * DetailItem item = PhoneDetails.getPhoneDetails();
 *
 * */

public class PhoneDetails {

    private static final int BUFFER_SIZE = 100;

    @SuppressLint("DefaultLocale")
    static String memUsage(){
        try {
            String cmd = "grep MemTotal /proc/meminfo";
            Process p = Runtime.getRuntime().exec(cmd);
            InputStream inputStream = null;
            if (p.waitFor() == 0) {
                inputStream = p.getInputStream();
            } else {
                inputStream = p.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream),
                    BUFFER_SIZE);
            String total = br.readLine();
            br.close();

            cmd = "grep MemAvailable /proc/meminfo";
            p = Runtime.getRuntime().exec(cmd);
            inputStream = null;
            if (p.waitFor() == 0) {
                inputStream = p.getInputStream();
            } else {
                inputStream = p.getErrorStream();
            }
            br = new BufferedReader(new InputStreamReader(inputStream),
                    BUFFER_SIZE);
            String available = br.readLine();
            br.close();

            String[] tmp = total.split(" ");
            total = tmp[tmp.length - 2];
            tmp = available.split(" ");
            available = tmp[tmp.length - 2];

            long totalMem = Long.parseLong(total)/1024;
            long availableMem = Long.parseLong(available)/1024;

            if (totalMem < 1024){
                return  String.format("%d MB / %d MB", availableMem, totalMem);
            }else {
                return  String.format("%.2f GB / %.2f GB", ((double)availableMem / 1024), ((double)totalMem / 1024));
            }
        }catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }

    @SuppressLint("DefaultLocale")
    static String externalStorageUsage(){
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long bytesAvailable;
        bytesAvailable = statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
        long available = bytesAvailable / (1024 * 1024);

        long storageSize = statFs.getTotalBytes() / (1024 * 1024);
        if(storageSize < 1024)
            return available + "MB / " + storageSize + "MB";
        else
            return  String.format("%.2f GB / %.2f GB", ((double)available / 1024), ((double)storageSize / 1024));
    }

    static String osVersion(){
        return "Android" + " " + android.os.Build.VERSION.RELEASE;
    }

    static String model(){
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    static String uptime(){
        long elapsedRealtime = SystemClock.elapsedRealtime();
        String sysUptime = String.format(Locale.getDefault(),
                "%d day(s), %d hour(s)",
                TimeUnit.MILLISECONDS.toDays(elapsedRealtime),
                TimeUnit.MILLISECONDS.toHours(elapsedRealtime));
        return sysUptime;
    }

    static String kernelVersion(){
        try {
            Process p = Runtime.getRuntime().exec("uname -r");
            InputStream inputStream = null;
            if (p.waitFor() == 0) {
                inputStream = p.getInputStream();
            } else {
                inputStream = p.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream),
                    BUFFER_SIZE);
            String line = br.readLine();
            br.close();
            return line;
        } catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }

    static String cpuModel(){
        try {
            String cmd = "grep Hardware /proc/cpuinfo";
            Process p = Runtime.getRuntime().exec(cmd);
            InputStream inputStream = null;
            if (p.waitFor() == 0) {
                inputStream = p.getInputStream();
            } else {
                inputStream = p.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream),
                    BUFFER_SIZE);
            String line = br.readLine();
            br.close();

            if(line == null){
                cmd = "grep name /proc/cpuinfo";
                p = Runtime.getRuntime().exec(cmd);
                inputStream = null;
                if (p.waitFor() == 0) {
                    inputStream = p.getInputStream();
                } else {
                    inputStream = p.getErrorStream();
                }
                br = new BufferedReader(new InputStreamReader(inputStream),
                        BUFFER_SIZE);
                line = br.readLine();
                br.close();
            }

            line = line.split(":", 2)[1];

            return line;
        } catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }

    public static DetailItem getPhoneDetails(){
        DetailItem item = new DetailItem(osVersion(), model(), kernelVersion(),
                uptime(), cpuModel(), memUsage(), externalStorageUsage());
        return item;
    }
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