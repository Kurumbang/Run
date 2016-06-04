package com.example.bishal.fitness;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class UserHistoryDetail extends AppCompatActivity {

    TextView title, distance, startTime, stopTime, averageSpeed, totalTime;
    TextView distanceLabel, startTimeLabel, stopTimeLabel, averageSpeedLabel, totalTimeLabel;
    TextView kmLabel, kmhLabel, minLabel;
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
        Typeface mytypeface = Typeface.createFromAsset(this.getAssets(),"Oswald-Stencil.ttf");
        title = (TextView)findViewById(R.id.title);
        title.setTypeface(mytypeface);
        distance = (TextView)findViewById(R.id.distance);
        distance.setTypeface(mytypeface);
        startTime = (TextView)findViewById(R.id.startTime);
        startTime.setTypeface(mytypeface);
        stopTime = (TextView)findViewById(R.id.stopTime);
        stopTime.setTypeface(mytypeface);
        averageSpeed = (TextView)findViewById(R.id.averageSpeed);
        averageSpeed.setTypeface(mytypeface);
        totalTime = (TextView)findViewById(R.id.totalTime);
        totalTime.setTypeface(mytypeface);

        distanceLabel = (TextView)findViewById(R.id.distanceLabel);
        distanceLabel.setTypeface(mytypeface);
        startTimeLabel = (TextView)findViewById(R.id.start_time_label);
        startTimeLabel.setTypeface(mytypeface);
        stopTimeLabel = (TextView)findViewById(R.id.stop_time_label);
        stopTimeLabel.setTypeface(mytypeface);
        averageSpeedLabel = (TextView)findViewById(R.id.average_speed_label);
        averageSpeedLabel.setTypeface(mytypeface);
        totalTimeLabel = (TextView)findViewById(R.id.total_time_label);
        totalTimeLabel.setTypeface(mytypeface);

        kmLabel = (TextView)findViewById(R.id.km_label);
        kmLabel.setTypeface(mytypeface);
        kmhLabel = (TextView)findViewById(R.id.kmh_label);
        kmhLabel.setTypeface(mytypeface);
        minLabel = (TextView)findViewById(R.id.min_label);
        minLabel.setTypeface(mytypeface);
    }
}
