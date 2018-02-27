package com.app.whiff.whiff.DB;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
/**
 * Created by gyych on 20/1/2018.
 */

public class LocalDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WhiffDB";
    private static LocalDBHelper dbHelper;
    public static SQLiteDatabase read;
    public static SQLiteDatabase write;


    private LocalDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion)
    {

    }

    public static void init(Context context)
    {
        if(dbHelper == null)
        {
            dbHelper = new LocalDBHelper(context);
        }
    }
}
