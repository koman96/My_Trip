package com.example.mytrip;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "My_Trip.db";

    private static final String TABLE1_NAME = "comingTrips";
    private static final String TABLE2_NAME = "pastTrips";
    private static final String TABLE3_NAME = "notifiedTrips";


    private static final String TRIP_ID = "trip_id";
    private static final String TRIP_NAME = "trip_name";

    private static final String START_LAT = "start_lat";
    private static final String START_LON = "start_lon";
    private static final String END_LAT = "end_lat";
    private static final String END_LON = "end_lon";

    private static final String TRIP_YEAR = "trip_year";
    private static final String TRIP_MONTH = "trip_month";
    private static final String TRIP_DAY = "trip_day";

    private static final String TRIP_HOUR = "trip_hour";
    private static final String TRIP_MIN = "trip_min";


    private static final String SQL_CREATE_TABLE1 =
            "CREATE TABLE "+ TABLE1_NAME + " (" +
                    TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TRIP_NAME + " TEXT," +
                    START_LAT + " DOUBLE," + START_LON + " DOUBLE," +
                    END_LAT + " DOUBLE," + END_LON + " DOUBLE," +
                    TRIP_YEAR +" INTEGER," + TRIP_MONTH + " INTEGER," + TRIP_DAY + " INTEGER," +
                    TRIP_HOUR + " INTEGER," + TRIP_MIN + " INTEGER);";

    private static final String SQL_CREATE_TABLE2 =
            "CREATE TABLE "+ TABLE2_NAME + " (" +
                    TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TRIP_NAME + " TEXT," +
                    START_LAT + " DOUBLE," + START_LON + " DOUBLE," +
                    END_LAT + " DOUBLE," + END_LON + " DOUBLE," +
                    TRIP_YEAR +" INTEGER," + TRIP_MONTH + " INTEGER," + TRIP_DAY + " INTEGER," +
                    TRIP_HOUR + " INTEGER," + TRIP_MIN + " INTEGER);";

    private static final String SQL_CREATE_TABLE3 =
            "CREATE TABLE "+ TABLE3_NAME + " (" +
                    TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TRIP_NAME + " TEXT," +
                    START_LAT + " DOUBLE," + START_LON + " DOUBLE," +
                    END_LAT + " DOUBLE," + END_LON + " DOUBLE," +
                    TRIP_YEAR +" INTEGER," + TRIP_MONTH + " INTEGER," + TRIP_DAY + " INTEGER," +
                    TRIP_HOUR + " INTEGER," + TRIP_MIN + " INTEGER);";


    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE1);
        db.execSQL(SQL_CREATE_TABLE2);
        db.execSQL(SQL_CREATE_TABLE3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE3_NAME);

        onCreate(db);
    }
}