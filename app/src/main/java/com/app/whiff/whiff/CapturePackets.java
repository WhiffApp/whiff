package com.app.whiff.whiff;

/**
 * Created by Jon on 18/1/2018.
 */
/*   public static class CaptureTable implements BaseColumns {
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
        */
public class CapturePackets {

    private int capture_id;
    private String date;
    private String timestamp;
    private String src_addr;
    private String dst_addr;
    private String _data; //column to be removed in future
    private String dataAscii; //data in ascii
    private String dataHex; //data in hexadecimal
    private String port; //contains both source and destination port
    private String protocolInfo; // contains information regarding the packet's protocols

    public CapturePackets(String date, String timestamp, String src_addr, String dst_addr, String port, String protocolInfo, String dataHex, String dataAscii, String _data) {
        this.capture_id = capture_id;
        this.date = date;
        this.timestamp = timestamp;
        this.src_addr = src_addr;
        this.dst_addr = dst_addr;
        this._data = _data;
        this.dataAscii = dataAscii;
        this.dataHex = dataHex;
        this.port = port;
        this.protocolInfo = protocolInfo;
    }

    public void set_id(int capture_id) {
        this.capture_id = capture_id;
    }

    public void set_date(String date) {
        this.date = date;
    }

    public void set_time(String timestamp) {
        this.timestamp = timestamp;
    }

    public void set_source(String src_addr) {
        this.src_addr = src_addr;
    }

    public void set_destination(String dst_addr) {
        this.dst_addr = dst_addr;
    }

    public void set_protocol(String port) {
        this.port = port;
    }

    public void set_protocolInfo(String protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    public void set_data(String _data) {
        this._data = _data;
    }

    public void set_dataAscii(String dataAscii) {
        this.dataAscii = dataAscii;
    }

    public void set_dataHex(String dataHex) {
        this.dataHex = dataHex;
    }

    public int get_id() {
        return capture_id;
    }

    public String get_date() {
        return date;
    }

    public String get_time() {
        return timestamp;
    }

    public String get_source() {
        return src_addr;
    }

    public String get_destination() {
        return dst_addr;
    }

    public String get_protocol() {
        return port;
    }

    public String get_protocolInfo() {
        return protocolInfo;
    }

    public String get_data() {
        return _data;
    }

    public String get_dataAscii() {
        return dataAscii;
    }

    public String get_dataHex() {
        return dataHex;
    }
}
