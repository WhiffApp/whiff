package com.app.whiff.whiff;

/**
 * Created by Jon on 18/1/2018.
 */

public class CapturePackets {

    private int _id;
    /*private String _source;
    private String _destination;*/
    private String _data;

    public CapturePackets(/*String _source, String _destination,*/ String _data) {
        this._id = _id;
        //this._source = _source;
        //this._destination = _destination;
        this._data = _data;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    /*public void set_source(String _source) {
        this._source = _source;
    }

    public void set_destination(String _destination) {
        this._destination = _destination;
    }*/

    public void set_data(String _data) {
        this._data = _data;
    }

    public int get_id() {
        return _id;
    }

    /*public String get_source() {
        return _source;
    }

    public String get_destination() {
        return _destination;
    }*/

    public String get_data() {
        return _data;
    }
}
