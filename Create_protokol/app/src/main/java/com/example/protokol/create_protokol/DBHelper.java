package com.example.protokol.create_protokol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 10;
    public static final String DATABLE_NAME = "contactDB";

    //ITEM1

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

    //ITEM2

    //table3
    public static final String TABLE_LINE_ROOMS = "lnrooms";
    public static final String LNR_ID = "_id";
    public static final String LNR_NAME = "room";

    //table4
    public static final String TABLE_LINES = "lines";
    public static final String LN_ID = "_id";
    public static final String LN_NAME = "line";
    public static final String LN_ID_ROOM = "lnr_id";

    //table5
    public static final String TABLE_GROUPS = "groups";
    public static final String GR_LINE_ID = "grline_id";
    public static final String GR_LNR_ID = "grlnr_id";
    public static final String GR_ID = "_id";
    public static final String GR_NAME = "name_group";
    public static final String GR_U1 = "u1";
    public static final String GR_MARK = "mark";
    public static final String GR_VEIN = "vein";
    public static final String GR_SECTION = "section";
    public static final String GR_U2 = "u2";
    public static final String GR_R = "r";
    public static final String GR_A_B = "a_b";
    public static final String GR_B_C = "b_c";
    public static final String GR_C_A = "c_a";
    public static final String GR_A_N = "a_n";
    public static final String GR_B_N = "b_n";
    public static final String GR_C_N = "c_n";
    public static final String GR_A_PE = "a_pe";
    public static final String GR_B_PE = "b_pe";
    public static final String GR_C_PE = "c_pe";
    public static final String GR_N_PE = "n_pe";

    public DBHelper(Context context) {
        super(context, DATABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ROOMS + "(" + KEY_ID + " integer primary key," + KEY_NAME +
            " text" + ");");

        db.execSQL("create table " + TABLE_ELEMENTS + "(" + EL_ID + " integer primary key AUTOINCREMENT," + EL_NAME +
                " text," + EL_NUMBER + " text,"  + ROOM_ID + " integer," + EL_SOPR + " text" + ");");

        db.execSQL("create table " + TABLE_LINE_ROOMS + "(" + LNR_ID + " integer primary key," + LNR_NAME +
                " text" + ");");

        db.execSQL("create table " + TABLE_LINES + "(" + LN_ID + " integer primary key," + LN_NAME +
                " text, " + LN_ID_ROOM + " integer" + ");");

        db.execSQL("create table " + TABLE_GROUPS + "(" + GR_ID + " integer primary key AUTOINCREMENT," + GR_LINE_ID + " integer," + GR_LNR_ID + " integer," + GR_NAME +
                " text," + GR_U1 + " text,"  + GR_MARK + " text," + GR_VEIN + " text," + GR_SECTION + " text," + GR_U2 + " text," + GR_R + " text," + GR_A_B + " text," + GR_B_C + " text," +
                GR_C_A + " text," + GR_A_N + " text," + GR_B_N + " text," + GR_C_N + " text," + GR_A_PE + " text," + GR_B_PE + " text," + GR_C_PE + " text," +
                GR_N_PE + " text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_ROOMS);
        db.execSQL("drop table if exists " + TABLE_ELEMENTS);
        db.execSQL("drop table if exists " + TABLE_LINE_ROOMS);
        db.execSQL("drop table if exists " + TABLE_LINES);
        db.execSQL("drop table if exists " + TABLE_GROUPS);
        onCreate(db);
    }
}
