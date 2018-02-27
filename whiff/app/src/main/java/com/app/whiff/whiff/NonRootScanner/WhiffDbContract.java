package com.app.whiff.whiff.NonRootScanner;

import android.provider.BaseColumns;

/**
 *
 *  This class defines the table structure used to store
 *  the packet captured information in the database
 *
 * @author Yeo Pei Xuan
 */

public final class WhiffDbContract {

    private WhiffDbContract() {}


    /* Inner class that defines the table contents */
    public static class CaptureTable implements BaseColumns {
        public static final String TABLE_NAME               = "capture";
        public static final String COLUMN_NAME_NAME         = "name";
        public static final String COLUMN_NAME_DESCRIPTION  = "desc";
        public static final String COLUMN_NAME_FILENAME     = "file_name";
        public static final String COLUMN_NAME_FILESIZE     = "file_size";
        public static final String COLUMN_NAME_STARTTIME    = "start_time";
        public static final String COLUMN_NAME_ENDTIME      = "end_time";
    }

    public static class CaptureItemTable implements BaseColumns {
        public static final String TABLE_NAME               = "capture_data";
        public static final String COLUMN_NAME_CAPTUREID    = "capture_id";
        public static final String COLUMN_NAME_TIMESTAMP    = "timestamp";
        public static final String COLUMN_NAME_SRCADDRESS   = "src_addr";
        public static final String COLUMN_NAME_SRCPORT      = "src_port";
        public static final String COLUMN_NAME_DSTADDRESS   = "dst_addr";
        public static final String COLUMN_NAME_DSTPORT      = "dst_port";
        public static final String COLUMN_NAME_PROTOCOL     = "protocol";
        public static final String COLUMN_NAME_LENGTH       = "length";
        public static final String COLUMN_NAME_TEXT         = "text";
        public static final String COLUMN_NAME_DATA         = "data";
    }
}