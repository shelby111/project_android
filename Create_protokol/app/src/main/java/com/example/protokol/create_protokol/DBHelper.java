package com.example.protokol.create_protokol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 7;
    public static final String DATABLE_NAME = "contactDB";
    //table1
    public static final String TABLE_ROOMS = "rooms";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "room";
    //table2
    public static final String TABLE_ELEMENTS = "elements";

    public static final String EL_ID = "_id";
    public static final String EL_NAME = "element";
    public static final String EL_NUMBER = "number";
    public static final String ROOM_ID = "room_id";
    public static final String EL_SOPR = "sopr";

    public DBHelper(Context context) {
        super(context, DATABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ROOMS + "(" + KEY_ID + " integer primary key," + KEY_NAME +
            " text" + ");");
        db.execSQL("create table " + TABLE_ELEMENTS + "(" + EL_ID + " integer primary key AUTOINCREMENT," + EL_NAME +
                " text," + EL_NUMBER + " text,"  + ROOM_ID + " integer," + EL_SOPR + " text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_ROOMS);
        db.execSQL("drop table if exists " + TABLE_ELEMENTS);
        onCreate(db);
    }
}
