package com.example.bishal.fitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bishal on 4/30/2016.
 */
public class MyDataBaseHandler extends SQLiteOpenHelper {

    public MyDataBaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + Constants.TABLE_USERDATA + "(" +
                Constants.COLUMN_ID  + " INTEGER PRIMARY KEY," +
                Constants.COLUMN_DATE  + " TEXT," +
                Constants.COLUMN_START_TIME  + " TEXT," +
                Constants.COLUMN_STOP_TIME  + " TEXT," +
                Constants.COLUMN_TOTAL_TIME  + " TEXT," +
                Constants.COLUMN_AVERAGE_SPEED  + " TEXT," +
                Constants.COLUMN_DISTANCE + " TEXT" + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_USERDATA + ";");
        onCreate(db);
    }

    public void addUserData(UserData userData){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_DATE, userData.getDate());
        values.put(Constants.COLUMN_DISTANCE, userData.getDistance());
        values.put(Constants.COLUMN_START_TIME, userData.getStartTime());
        values.put(Constants.COLUMN_STOP_TIME, userData.getStopTime());
        values.put(Constants.COLUMN_TOTAL_TIME, userData.getTotalTime());
        values.put(Constants.COLUMN_AVERAGE_SPEED, userData.getAverageSpeed());
        db.insert(Constants.TABLE_USERDATA, null, values);
        db.close();

    }

    public List<UserData>getUserData(){
        List<UserData>userDataList = new ArrayList<UserData>();
        String query = "SELECT * FROM " + Constants.TABLE_USERDATA ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                UserData userData = new UserData();
                userData.setDate(cursor.getString(cursor.getColumnIndex("_date")));
                userData.setDistance(cursor.getString(cursor.getColumnIndex("_distance")));
                userData.setStartTime(cursor.getString(cursor.getColumnIndex("_startTime")));
                userData.setStopTime(cursor.getString(cursor.getColumnIndex("_stopTime")));
                userData.setTotalTime(cursor.getString(cursor.getColumnIndex("_totalTime")));
                userData.setAverageSpeed(cursor.getString(cursor.getColumnIndex("_averageSpeed")));

                userDataList.add(userData);
            }while (cursor.moveToNext());
        }
        return userDataList;
    }

}
