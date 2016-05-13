package com.example.bishal.fitness;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class UserHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView title;
    MyAdapter myAdapter;
    MyDataBaseHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        title = (TextView) toolbar.findViewById(R.id.title);
        title.setText(R.string.myTrips);

        dbHandler = new MyDataBaseHandler(this);

        List<UserData> userDatas = dbHandler.getUserData();

        myAdapter = new MyAdapter(this,userDatas);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

}