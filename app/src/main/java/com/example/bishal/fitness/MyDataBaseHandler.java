package com.example.bishal.fitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bishal on 4/30/2016.
 */
public class MyDataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userdata.db";
    public static final String TABLE_USERDATA = "userdata";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "_date";
    public static final String COLUMN_DISTANCE = "_distance";
    public static final String COLUMN_START_TIME = "_startTime";
    public static final String COLUMN_STOP_TIME = "_stopTime";
    public static final String COLUMN_TOTAL_TIME = "_totalTime";
    public static final String COLUMN_AVERAGE_SPEED = "_averageSpeed";


    public MyDataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_USERDATA + "(" +
                COLUMN_ID  + " INTEGER PRIMARY KEY," +
                COLUMN_DATE  + " TEXT," +
                COLUMN_START_TIME  + " TEXT," +
                COLUMN_STOP_TIME  + " TEXT," +
                COLUMN_TOTAL_TIME  + " TEXT," +
                COLUMN_AVERAGE_SPEED  + " TEXT," +
                COLUMN_DISTANCE + " TEXT" + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERDATA + ";");
        onCreate(db);
    }

    public void addUserData(UserData userData){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, userData.getDate());
        values.put(COLUMN_DISTANCE, userData.getDistance());
        values.put(COLUMN_START_TIME, userData.getStartTime());
        values.put(COLUMN_STOP_TIME, userData.getStopTime());
        values.put(COLUMN_TOTAL_TIME, userData.getTotalTime());
        values.put(COLUMN_AVERAGE_SPEED, userData.getAverageSpeed());
        db.insert(TABLE_USERDATA, null, values);
        db.close();

    }

    public List<UserData>getUserData(){
        List<UserData>userDataList = new ArrayList<UserData>();
        String query = "SELECT * FROM " + TABLE_USERDATA ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                UserData userData = new UserData();
                //userData.set_id(Integer.parseInt(cursor.getString(0)));
                userData.setDate(cursor.getString(cursor.getColumnIndex("_date")));
                userData.setDistance(cursor.getString(cursor.getColumnIndex("_distance")));
                userData.setStartTime(cursor.getString(cursor.getColumnIndex("_startTime")));
                userData.setStopTime(cursor.getString(cursor.getColumnIndex("_stopTime")));
                userData.setTotalTime(cursor.getString(cursor.getColumnIndex("_totalTime")));
                userData.setAverageSpeed(cursor.getString(cursor.getColumnIndex("_averageSpeed")));

                userDataList.add(userData);
            }while (cursor.moveToNext());
        }
        //Log.v("Kurumbang", userDataList.toString());
        return userDataList;
    }

}
