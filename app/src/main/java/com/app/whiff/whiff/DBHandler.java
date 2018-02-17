package com.app.whiff.whiff;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jon on 18/1/2018.
 */

public class DBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "packets.db";
    public static final String TABLE_PACKETS = "packets";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "_date";
    public static final String COLUMN_TIME = "_time";
    public static final String COLUMN_SOURCE = "_source";
    public static final String COLUMN_DESTINATION = "_destination";
    public static final String COLUMN_PROTOCOL = "_protocol";
    public static final String COLUMN_PROTOCOLINFO = "_protocolInfo";
    public static final String COLUMN_DATA = "_data";
    public static final String COLUMN_DATAHEX = "_dataHex";
    public static final String COLUMN_DATAASCII = "_dataAscii";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Create DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PACKETS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TIME + " TEXT NOT NULL, " +
                COLUMN_SOURCE + " TEXT NOT NULL, " +
                COLUMN_DESTINATION + " TEXT NOT NULL, " +
                COLUMN_PROTOCOL + " TEXT NOT NULL, " +
                COLUMN_PROTOCOLINFO + " TEXT, " +
                COLUMN_DATAHEX + " TEXT NOT NULL, " +
                COLUMN_DATAASCII + " TEXT NOT NULL, " +
                COLUMN_DATA + " TEXT NOT NULL" +
                ");";
        db.execSQL(query);
    }

    //Update DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACKETS);
        onCreate(db);
    }

    //Create new row in DB
    public void addPacket(CapturePackets capturePackets){
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, capturePackets.get_date());
        values.put(COLUMN_TIME, capturePackets.get_time());
        values.put(COLUMN_SOURCE, capturePackets.get_source());
        values.put(COLUMN_DESTINATION, capturePackets.get_destination());
        values.put(COLUMN_PROTOCOL, capturePackets.get_protocol());
        values.put(COLUMN_PROTOCOLINFO, capturePackets.get_protocolInfo());
        values.put(COLUMN_DATAHEX, capturePackets.get_dataHex());
        values.put(COLUMN_DATAASCII, capturePackets.get_dataAscii());
        values.put(COLUMN_DATA, capturePackets.get_data());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PACKETS, null, values);
        db.close();
    }

    //Delete DB
    public void dropDB(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACKETS);
        String query = "CREATE TABLE " + TABLE_PACKETS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TIME + " TEXT NOT NULL, " +
                COLUMN_SOURCE + " TEXT NOT NULL, " +
                COLUMN_DESTINATION + " TEXT NOT NULL, " +
                COLUMN_PROTOCOL + " TEXT NOT NULL, " +
                COLUMN_PROTOCOLINFO + " TEXT, " +
                COLUMN_DATAHEX + " TEXT NOT NULL, " +
                COLUMN_DATAASCII + " TEXT NOT NULL, " +
                COLUMN_DATA + " TEXT NOT NULL" +
                ");";
        db.execSQL(query);
    }

    //Print DB as string
    public String databaseToString(){

        Log.d("DBHandler", "getTableAsString called");

        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PACKETS ;
        String[] columns = {COLUMN_ID, COLUMN_DATE, COLUMN_TIME, COLUMN_SOURCE, COLUMN_DESTINATION, COLUMN_PROTOCOL, COLUMN_PROTOCOLINFO, COLUMN_DATAHEX, COLUMN_DATAASCII, COLUMN_DATA};

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PACKETS, null);

        if (c.moveToFirst() ){
            String[] columnNames = c.getColumnNames();
            do {
                for (String name: columnNames) {
                    dbString += String.format("%s \n",
                            c.getString(c.getColumnIndex(name)));
                }
                dbString += "\n\n";

            } while (c.moveToNext());
        }

        db.close();
        return dbString;
    }

}
