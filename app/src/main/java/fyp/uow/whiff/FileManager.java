package fyp.uow.whiff;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  This helper class provides the directory and files management used in this application.
 * */
public class FileManager {

    private static final File Directory = new File(Environment.getExternalStorageDirectory(),"MyCurious");

    public static void init() {

        if (!Directory.isDirectory()){
            Directory.delete();
            Directory.mkdir();
        }
    }

    public static File createNewPacketFile() {
        File file = new File(Directory, createNewFileName());
        return file;
    }

    public static File[] listPacketFiles(){
        return Directory.listFiles();
    }

    private static String createNewFileName(){
        return new SimpleDateFormat("yyyyMMddHHmmss'.pcap'").format(new Date());
    }
}
