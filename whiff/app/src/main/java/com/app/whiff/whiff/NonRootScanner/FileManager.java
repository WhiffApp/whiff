package com.app.whiff.whiff.NonRootScanner;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.app.whiff.whiff.NonRootScanner.Common.JDateTimeTransform;

/**
 *
 * This helper class provides the directory and files management used in this application.
 *
 * @author Yeo Pei Xuan
 */

public class FileManager {

    private static final int FileExtensionLength = 5;
    private static final File DbDirectory    = new File(Environment.getExternalStorageDirectory(),"WhiffDB");
    private static final File FilesDirectory = new File(Environment.getExternalStorageDirectory(),"Whiff");

    public static void init() {

        if (!FilesDirectory.isDirectory()){
            FilesDirectory.delete();
            FilesDirectory.mkdir();
        }

        if (!DbDirectory.isDirectory()){
            DbDirectory.delete();
            DbDirectory.mkdir();
        }
    }

    public static String getApplicationFilePath(String fileName) {
        File file = new File(FilesDirectory, fileName);
        return file.getAbsolutePath();
    }

    public static String getAppDatabaseFilePath(String fileName) {
        File file = new File(DbDirectory, fileName);
        return file.getAbsolutePath();
    }

    public static String generateNewFileName(){
        //return new SimpleDateFormat("yyyyMMddHHmmss'.pcap'").format(new Date());
        //return new SimpleDateFormat("yyyy-MM-dd@HH_mm_ss'.pcap'").format(new Date());
        return new SimpleDateFormat("yyyyMMdd_hhmmss'.pcap'").format(new Date());
    }

    public static File createNewPacketFile() {
        File file = new File(FilesDirectory, generateNewFileName());
        return file;
    }

    public static File[] listPacketFiles(){
        File[] files = FilesDirectory.listFiles();
        return files;
    }

    public static String getFormattedTimestampFromFileName(String filename) {

        String fn = "";

        try {

            String time = filename.substring(0, filename.length() - FileExtensionLength);
            String name = new JDateTimeTransform().parse("yyyyMMdd_hhmmss", time).toString("yyyy-MM-dd hh:mm:ss");
            fn = name;

        } catch(Exception e) {
            fn = filename;
        }
        return fn;
    }

    public static String getFileNameWithoutExtension(String filename) {

        String nameOnly = "";

        try {
            nameOnly = filename.substring(0, filename.length() - FileExtensionLength);
        } catch(Exception e) {
            nameOnly = filename;
        }
        return nameOnly;
    }
}
