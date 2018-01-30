package com.app.whiff.whiff.UI.NonRootScanner;

/**
 * Created by danie on 30/1/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CaptureDAO implements Closeable {

    private WhiffDbHelper mDbHelper;
    private Long mCaptureID = -1L;

    public CaptureDAO(Context context) {
        mDbHelper = new WhiffDbHelper(context);
    }

    public boolean newCapture(Capture capture) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WhiffDbContract.CaptureTable.COLUMN_NAME_NAME,           capture.name);
        values.put(WhiffDbContract.CaptureTable.COLUMN_NAME_DESCRIPTION,    capture.desc);
        values.put(WhiffDbContract.CaptureTable.COLUMN_NAME_FILENAME,       capture.fileName);
        values.put(WhiffDbContract.CaptureTable.COLUMN_NAME_FILESIZE,       capture.fileSize);
        values.put(WhiffDbContract.CaptureTable.COLUMN_NAME_STARTTIME,      capture.startTime.getTime());
        //values.put(WhiffDbContract.CaptureTable.COLUMN_NAME_ENDTIME,        capture.endTime.getTime());

        mCaptureID = db.insert(WhiffDbContract.CaptureTable.TABLE_NAME, null, values);
        capture.ID = mCaptureID;
        return (mCaptureID > 0);
    }

    public boolean updateCaptureEndTime(Date d) {

        if ( mCaptureID <= 0 )
            throw new IllegalStateException("No capture record.");

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(WhiffDbContract.CaptureTable.COLUMN_NAME_ENDTIME, d.getTime());

        // Which row to update, based on the title
        String selection = WhiffDbContract.CaptureTable._ID + " = ?";
        String[] selectionArgs = { mCaptureID.toString() };

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = db.update(
                WhiffDbContract.CaptureTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        return count > 0;
    }

    public long addCaptureItem(CaptureItem item) {

        if ( mCaptureID <= 0 )
            throw new IllegalStateException("No capture record.");

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WhiffDbContract.CaptureItemTable.COLUMN_NAME_CAPTUREID,  mCaptureID);
        values.put(WhiffDbContract.CaptureItemTable.COLUMN_NAME_TIMESTAMP,  item.timestamp.getTime());
        values.put(WhiffDbContract.CaptureItemTable.COLUMN_NAME_SRCADDRESS, item.sourceAddress);
        values.put(WhiffDbContract.CaptureItemTable.COLUMN_NAME_SRCPORT,    item.sourcePort);
        values.put(WhiffDbContract.CaptureItemTable.COLUMN_NAME_DSTADDRESS, item.destinationAddress);
        values.put(WhiffDbContract.CaptureItemTable.COLUMN_NAME_DSTPORT,    item.destinationPort);
        values.put(WhiffDbContract.CaptureItemTable.COLUMN_NAME_PROTOCOL,   item.protocol);
        values.put(WhiffDbContract.CaptureItemTable.COLUMN_NAME_LENGTH,     item.length);

        long itemIndex = db.insert(WhiffDbContract.CaptureItemTable.TABLE_NAME, null, values);
        return itemIndex;
    }

    public List<Capture> getAllCapture() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                WhiffDbContract.CaptureTable._ID,
                WhiffDbContract.CaptureTable.COLUMN_NAME_NAME,
                WhiffDbContract.CaptureTable.COLUMN_NAME_STARTTIME,
                WhiffDbContract.CaptureTable.COLUMN_NAME_ENDTIME
        };

        // Returns all rows
        String selection = null;
        String[] selectionArgs = null;

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                WhiffDbContract.CaptureTable.COLUMN_NAME_STARTTIME + " DESC";

        List<Capture> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    WhiffDbContract.CaptureTable.TABLE_NAME,    // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    selectionArgs,                              // The values for the WHERE clause
                    null,                               // don't group the rows
                    null,                                // don't filter by row groups
                    sortOrder                                  // The sort order
            );

            while (cursor.moveToNext()) {

                Capture item = new Capture();
                item.ID = cursor.getLong(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureTable._ID));

                item.name = cursor.getString(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureTable.COLUMN_NAME_NAME));

                long start = cursor.getLong(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureTable.COLUMN_NAME_STARTTIME));
                item.startTime = new Date(start);

                long end = cursor.getLong(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureTable.COLUMN_NAME_ENDTIME));
                item.endTime = new Date(end);

                items.add(item);
            }
        }
        catch(Exception e) {

        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return items;
    }

    public List<CaptureItem> getCaptureItems(Long captureID) {

        if ( mCaptureID <= 0 )
            throw new IllegalArgumentException("Invalid capture id.");

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_TIMESTAMP,
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_SRCADDRESS,
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_SRCPORT,
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_DSTADDRESS,
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_DSTPORT,
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_PROTOCOL,
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_LENGTH
        };

        // Returns all rows
        String selection = WhiffDbContract.CaptureItemTable.COLUMN_NAME_CAPTUREID + " = ?";
        String[] selectionArgs = { captureID.toString() };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                WhiffDbContract.CaptureItemTable.COLUMN_NAME_TIMESTAMP + " ASC";

        List<CaptureItem> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    WhiffDbContract.CaptureItemTable.TABLE_NAME,// The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    selectionArgs,                              // The values for the WHERE clause
                    null,                               // don't group the rows
                    null,                                // don't filter by row groups
                    sortOrder                                  // The sort order
            );

            while (cursor.moveToNext()) {

                CaptureItem item = new CaptureItem();

                long date = cursor.getLong(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureItemTable.COLUMN_NAME_TIMESTAMP));
                item.timestamp = new Date(date);

                item.sourceAddress = cursor.getString(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureItemTable.COLUMN_NAME_SRCADDRESS));

                item.sourcePort = cursor.getInt(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureItemTable.COLUMN_NAME_SRCPORT));

                item.destinationAddress = cursor.getString(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureItemTable.COLUMN_NAME_DSTADDRESS));

                item.destinationPort = cursor.getInt(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureItemTable.COLUMN_NAME_DSTPORT));

                item.protocol = cursor.getString(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureItemTable.COLUMN_NAME_PROTOCOL));

                item.length = cursor.getInt(
                        cursor.getColumnIndexOrThrow(WhiffDbContract.CaptureItemTable.COLUMN_NAME_LENGTH));

                items.add(item);
            }
        }
        catch(Exception e) {

        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return items;
    }

    @Override
    public void close() throws IOException {
        mDbHelper.close();
    }
}
