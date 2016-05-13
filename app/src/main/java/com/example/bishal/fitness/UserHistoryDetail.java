package com.example.bishal.fitness;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class UserHistoryDetail extends AppCompatActivity {

    TextView title, distance, startTime, stopTime, averageSpeed, totalTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        init();

        Bundle bundle = getIntent().getExtras();
        title.setText(bundle.getString("date"));
        distance.setText(bundle.getString("distance"));
        startTime.setText(bundle.getString("startTime"));
        stopTime.setText(bundle.getString("stopTime"));
        averageSpeed.setText(bundle.getString("averageSpeed"));
        totalTime.setText(bundle.getString("totalTime"));

    }
    public void init(){
        title = (TextView)findViewById(R.id.title);
        distance = (TextView)findViewById(R.id.distance);
        startTime = (TextView)findViewById(R.id.startTime);
        stopTime = (TextView)findViewById(R.id.stopTime);
        averageSpeed = (TextView)findViewById(R.id.averageSpeed);
        totalTime = (TextView)findViewById(R.id.totalTime);
    }
}
