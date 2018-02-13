package com.app.whiff.whiff;

/**
 * Created by Jon on 18/1/2018.
 */

public class CapturePackets {

    private int _id;
    private String _date;
    private String _time;
    private String _source;
    private String _destination;
    private String _data;
    private String _protocol;
    private String _protocolInfo;

    public CapturePackets(String _date, String _time, String _source, String _destination, String _protocol, String _protocolInfo, String _data) {
        this._id = _id;
        this._date = _date;
        this._time = _time;
        this._source = _source;
        this._destination = _destination;
        this._data = _data;
        this._protocol = _protocol;
        this._protocolInfo = _protocolInfo;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public void set_source(String _source) {
        this._source = _source;
    }

    public void set_destination(String _destination) {
        this._destination = _destination;
    }

    public void set_protocol(String _protocol) {
        this._protocol = _protocol;
    }

    public void set_protocolInfo(String _protocolInfo) {
        this._protocolInfo = _protocolInfo;
    }

    public void set_data(String _data) {
        this._data = _data;
    }

    public int get_id() {
        return _id;
    }

    public String get_date() {
        return _date;
    }

    public String get_time() {
        return _time;
    }

    public String get_source() {
        return _source;
    }

    public String get_destination() {
        return _destination;
    }

    public String get_protocol() {
        return _protocol;
    }

    public String get_protocolInfo() {
        return _protocolInfo;
    }

    public String get_data() {
        return _data;
    }
}
