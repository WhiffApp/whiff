package com.app.whiff.whiff;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

/**
 * Created by Jon on 18/1/2018.
 */

public class DBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "packets.db";
    public static final String TABLE_PACKETS = "packets";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SOURCE = "_source";
    public static final String COLUMN_DESTINATION = "_destination";
    public static final String COLUMN_DATA = "_data";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Create DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PACKETS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                //COLUMN_SOURCE + " TEXT " +
                //COLUMN_DESTINATION + " TEXT " +
                COLUMN_DATA + " TEXT " +
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
       // values.put(COLUMN_SOURCE, capturePackets.get_source());
        //values.put(COLUMN_DESTINATION, capturePackets.get_destination());
        values.put(COLUMN_DATA, capturePackets.get_data());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PACKETS, null, values);
        db.close();
    }

    //Print DB as string
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PACKETS + " WHERE 1";

        Cursor c = db.rawQuery(query, null);

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("data"))!= null){
                //dbString += c.getString(c.getColumnIndex("source"));
                //dbString += c.getString(c.getColumnIndex("destination"));
                dbString += c.getString(c.getColumnIndex("data"));
                dbString += "\n";
            }
        }

        db.close();
        return dbString;
    }

}
