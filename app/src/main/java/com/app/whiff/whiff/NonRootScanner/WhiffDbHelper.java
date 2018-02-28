package com.app.whiff.whiff.NonRootScanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 *  This class creates or upgrades the database used in the application
 *
 * @author Yeo Pei Xuan
 */

public class WhiffDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "edu.sim.whiff.db";

    private static final String SQL_CREATE_TABLE_CAPTURE =
            "CREATE TABLE " + WhiffDbContract.CaptureTable.TABLE_NAME + " (" +
                    WhiffDbContract.CaptureTable._ID                        + " INTEGER PRIMARY KEY," +
                    WhiffDbContract.CaptureTable.COLUMN_NAME_NAME           + " TEXT," +
                    WhiffDbContract.CaptureTable.COLUMN_NAME_DESCRIPTION    + " TEXT," +
                    WhiffDbContract.CaptureTable.COLUMN_NAME_FILENAME       + " TEXT," +
                    WhiffDbContract.CaptureTable.COLUMN_NAME_FILESIZE       + " INTEGER," +
                    WhiffDbContract.CaptureTable.COLUMN_NAME_STARTTIME      + " DATETIME," +
                    WhiffDbContract.CaptureTable.COLUMN_NAME_ENDTIME        + " DATETIME)";

    private static final String SQL_CREATE_TABLE_CAPTUREDATA =
            "CREATE TABLE " + WhiffDbContract.CaptureItemTable.TABLE_NAME + " (" +
                    WhiffDbContract.CaptureItemTable._ID                    + " INTEGER PRIMARY KEY," +
                    WhiffDbContract.CaptureItemTable.COLUMN_NAME_CAPTUREID  + " INTEGER," +
                    WhiffDbContract.CaptureItemTable.COLUMN_NAME_TIMESTAMP  + " DATETIME," +
                    WhiffDbContract.CaptureItemTable.COLUMN_NAME_SRCADDRESS + " TEXT," +
                    WhiffDbContract.CaptureItemTable.COLUMN_NAME_SRCPORT    + " INTEGER," +
                    WhiffDbContract.CaptureItemTable.COLUMN_NAME_DSTADDRESS + " TEXT," +
                    WhiffDbContract.CaptureItemTable.COLUMN_NAME_DSTPORT    + " INTEGER," +
                    WhiffDbContract.CaptureItemTable.COLUMN_NAME_PROTOCOL   + " TEXT," +
                    WhiffDbContract.CaptureItemTable.COLUMN_NAME_LENGTH     + " INTEGER," +
                    WhiffDbContract.CaptureItemTable.COLUMN_NAME_TEXT       + " TEXT," +
                    WhiffDbContract.CaptureItemTable.COLUMN_NAME_DATA       + " TEXT)";

    private static final String SQL_DELETE_TABLE_CAPTURE =
            "DROP TABLE IF EXISTS " + WhiffDbContract.CaptureTable.TABLE_NAME;

    private static final String SQL_DELETE_TABLE_CAPTUREDATA =
            "DROP TABLE IF EXISTS " + WhiffDbContract.CaptureItemTable.TABLE_NAME;

    public WhiffDbHelper(Context context) {
        super(context, FileManager.getAppDatabaseFilePath(DATABASE_NAME), null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_CAPTURE);
        db.execSQL(SQL_CREATE_TABLE_CAPTUREDATA);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TABLE_CAPTUREDATA);
        db.execSQL(SQL_DELETE_TABLE_CAPTURE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void clearDatabase(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + WhiffDbContract.CaptureTable.TABLE_NAME);
        String query1 = "CREATE TABLE " + WhiffDbContract.CaptureTable.TABLE_NAME + " (" +
                WhiffDbContract.CaptureTable._ID                        + " INTEGER PRIMARY KEY," +
                WhiffDbContract.CaptureTable.COLUMN_NAME_NAME           + " TEXT," +
                WhiffDbContract.CaptureTable.COLUMN_NAME_DESCRIPTION    + " TEXT," +
                WhiffDbContract.CaptureTable.COLUMN_NAME_FILENAME       + " TEXT," +
                WhiffDbContract.CaptureTable.COLUMN_NAME_FILESIZE       + " INTEGER," +
                WhiffDbContract.CaptureTable.COLUMN_NAME_STARTTIME      + " DATETIME," +
                WhiffDbContract.CaptureTable.COLUMN_NAME_ENDTIME        + " DATETIME)";
        db.execSQL(query1);

        db.execSQL("DROP TABLE IF EXISTS " + WhiffDbContract.CaptureItemTable.TABLE_NAME);
        String query2 = "CREATE TABLE " + WhiffDbContract.CaptureItemTable.TABLE_NAME + " (" +
                WhiffDbContract.CaptureItemTable._ID                    + " INTEGER PRIMARY KEY," +
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_CAPTUREID  + " INTEGER," +
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_TIMESTAMP  + " DATETIME," +
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_SRCADDRESS + " TEXT," +
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_SRCPORT    + " INTEGER," +
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_DSTADDRESS + " TEXT," +
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_DSTPORT    + " INTEGER," +
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_PROTOCOL   + " TEXT," +
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_LENGTH     + " INTEGER," +
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_TEXT       + " TEXT," +
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_DATA       + " TEXT)";
        db.execSQL(query2);
    }
}
