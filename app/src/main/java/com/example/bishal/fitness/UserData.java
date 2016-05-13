package com.example.bishal.fitness;

/**
 * Created by Bishal on 4/30/2016.
 */
public class UserData {
    private int _id;
    private String date;
    private String distance;
    private String averageSpeed;
    private String startTime;
    private String stopTime;
    private String totalTime;

    public UserData(){}

    public UserData(String date, String distance, String averageSpeed, String startTime, String stopTime, String totalTime){
        this.date = date;
        this.distance = distance;
        this.averageSpeed = averageSpeed;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.totalTime = totalTime;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
