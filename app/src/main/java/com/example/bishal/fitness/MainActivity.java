package com.example.bishal.fitness;

/**
 * Created by Bishal on 4/6/2016.
 */

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private MapController mapController;
    private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
    private DefaultResourceProxyImpl mResourceProxy;
    private boolean startStopFlag;
    private static final String TAG = "com.example.bishal.fitness";
    private Intent intent;
    private TextView displaySpeed, displayDistance, status, timer;
    private Button startStopbtn;
    private OverlayItem olItem;
    private Drawable marker;
    private Typeface oswaldStencil, oswaldLight;
    private Animation slideUp, slideDown;
    double currentLatitude, currentLongitude, lastLatitude, lastLongitude, distanceTravelled;
    public static final double DEFAULT_LATITUDE = 53.556556;
    public static final double DEFAULT_LONGITUDE = 10.022079;
    float currentSpeed;
    MyDataBaseHandler dbHandler;
    DecimalFormat form = new DecimalFormat("0.00");
    String date;
    SimpleDateFormat simpleDateFormat;
    String start_Time;
    long startTime;
    RelativeLayout controlBoard, upArrow;
    ImageView downArrowIcon;
    public static final String DEFAULT = "N/A";
    String phoneNumber = DEFAULT;
    SharedPreferences sharedPreferences;
    SharedPreferences locationSharedPreferences;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };


    //----------For Timer-----------------
    long timeInMilliseconds,timeSwapBuff,updatedtime;
    int hours, mins, secs, milliseconds;
    Handler handler = new Handler();
    //-------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        displayLocationOnTheMap(lastLatitude, lastLongitude);
    }

    public void startStopBtn(View view){
        if (checkStartStopFlag()) {
            start_Time = simpleDateFormat.format(new Date());
            resetTimer();
            startTime = System.currentTimeMillis();
            handler.postDelayed(updateTimer, 0);
            startStopbtn.setText(R.string.stop);
            status.setText(R.string.running);
            intent = new Intent(getApplicationContext(), MyLocationService.class);
            startService(intent);
            registerReceiver(broadcastReceiver, new IntentFilter(MyLocationService.BROADCAST_ACTION));

        } else {
            stopService(intent);
            handler.removeCallbacks(updateTimer);
            startStopbtn.setText(R.string.start);
            status.setText(R.string.justRun);
            displaySpeed.setText("0.00 km/h");
            unregisterReceiver(broadcastReceiver);
            String stop_Time = simpleDateFormat.format(new Date());
            long total = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime);
            float timeTakenInHours = (float) total / 60;
            float averageSpeed = (float) distanceTravelled / timeTakenInHours;
            UserData userData = new UserData(
                    date, String.valueOf(form.format(distanceTravelled)),
                    String.valueOf(form.format(averageSpeed)), start_Time,
                    stop_Time, String.valueOf(total));
            dbHandler.addUserData(userData);
            dbHandler.close();

        }
    }

    public void resetTimer(){
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedtime = 0L;
        secs = 0;
        mins = 0;
        milliseconds = 0;
        handler.removeCallbacks(updateTimer);
        timer.setText(R.string.timer);
    }
    public Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = System.currentTimeMillis() - startTime;
            updatedtime = timeSwapBuff + timeInMilliseconds;
            secs = (int) (updatedtime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            hours = mins / 60;
            timer.setText("" + String.format("%02d", hours) + ":" + "" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            handler.postDelayed(this, 0);
        }
    };

    public void emergencyCall(View view) {
        if(phoneNumber.equals(DEFAULT)){
            Toast.makeText(getApplicationContext(),"Invalid phone number please change it via settings ", Toast.LENGTH_LONG).show();
        }else{
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }

    }

    public void userHistory(View view) {
        Intent userHisory = new Intent(getApplicationContext(), UserHistory.class);
        startActivity(userHisory);
    }

    public void init() {
        oswaldStencil = Typeface.createFromAsset(this.getAssets(), "Oswald-Stencil.ttf");
        oswaldLight = Typeface.createFromAsset(this.getAssets(), "Oswald-Light.ttf");
        startStopFlag = false;
        mMapView = (MapView) findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.setTileSource(TileSourceFactory.MAPNIK);
            mMapView.setBuiltInZoomControls(true);
            mMapView.setMultiTouchControls(true);
            mapController = (MapController) mMapView.getController();
        }
        mapController.setZoom(15);
        mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        displaySpeed = (TextView) findViewById(R.id.text_view_speed);
        displaySpeed.setTypeface(oswaldStencil);
        displayDistance = (TextView) findViewById(R.id.text_view_distance);
        displayDistance.setTypeface(oswaldStencil);
        startStopbtn = (Button) findViewById(R.id.startStopBtn);
        startStopbtn.setTypeface(oswaldStencil);
        status = (TextView) findViewById(R.id.status);
        status.setTypeface(oswaldStencil);
        timer = (TextView) findViewById(R.id.timer);
        timer.setTypeface(oswaldStencil);
        marker = this.getResources().getDrawable(R.drawable.location);
        controlBoard = (RelativeLayout) findViewById(R.id.controlBoard);
        upArrow = (RelativeLayout) findViewById(R.id.upArrow);
        downArrowIcon = (ImageView)findViewById(R.id.downArrowIcon);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        dbHandler = new MyDataBaseHandler(getApplicationContext());
        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        simpleDateFormat = new SimpleDateFormat("HH:mm");
        //phone number.........
        SharedPreferences mysharedPreferences = getSharedPreferences("UserPhoneNumber", Context.MODE_PRIVATE);
        phoneNumber = mysharedPreferences.getString("phoneNumber", DEFAULT);
        //saving the last location......
        SharedPreferences locationSharedPreferences = getSharedPreferences("LastLocation", Context.MODE_PRIVATE);
        String latitude = locationSharedPreferences.getString("latitude", String.valueOf(DEFAULT_LATITUDE));
        String longitude = locationSharedPreferences.getString("longitude",String.valueOf(DEFAULT_LONGITUDE));
        lastLatitude = Double.valueOf(latitude);
        lastLongitude = Double.valueOf(longitude);

    }

    public boolean checkStartStopFlag() {
        if (!startStopFlag) {
            startStopFlag = true;

        } else
            startStopFlag = false;
        return startStopFlag;
    }

    private void updateUI(Intent intent) {

        //receives all the broadcast data here....
        currentLatitude = intent.getDoubleExtra("latitude", 0);
        currentLongitude = intent.getDoubleExtra("longitude", 0);
        currentSpeed = intent.getFloatExtra("speed", 0);
        distanceTravelled = intent.getDoubleExtra("distance", 0);
        //display speed and distance in UI...
        displaySpeed.setText(form.format(currentSpeed) + " km/h");
        displayDistance.setText(form.format(distanceTravelled) + " km");

        //function call to display map and the location
        displayLocationOnTheMap(currentLatitude, currentLongitude);

        //storing the last location in the form of string double is not used in sharedPreferences so using in string form and while using it converting it to double
        locationSharedPreferences = getSharedPreferences("LastLocation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = locationSharedPreferences.edit();
        editor.putString("latitude", String.valueOf(currentLatitude));
        editor.putString("longitude", String.valueOf(currentLongitude));
        editor.apply();
    }

    public void displayLocationOnTheMap(double latitude, double longitude) {

        GeoPoint currentLocation = new GeoPoint(latitude, longitude);
        mapController.setCenter(currentLocation);
        mapController.setZoom(15);
        olItem = new OverlayItem("Here", "Current Position", currentLocation);
        olItem.setMarker(marker);
        ArrayList<OverlayItem> items = new ArrayList<>();
        items.clear();
        items.add(olItem);
        this.mMyLocationOverlay = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index,
                                                     final OverlayItem item) {
                        // Toast.makeText(DemoMap.this, "Item '" + item.mTitle, Toast.LENGTH_LONG).show();
                        return true; // We 'handled' this event.
                    }

                    @Override
                    public boolean onItemLongPress(final int index,
                                                   final OverlayItem item) {
                        //Toast.makeText(DemoMap.this, "Item '" + item.mTitle ,Toast.LENGTH_LONG).show();
                        return false;
                    }
                }, mResourceProxy);
        this.mMapView.getOverlays().add(this.mMyLocationOverlay);
        mMapView.invalidate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            View view = LayoutInflater.from(this).inflate(R.layout.settings_layout,null);
            TextView settingsTitle = (TextView)view.findViewById(R.id.settingsTitle);
            settingsTitle.setTypeface(oswaldStencil);
            TextView currentPhoneNumber = (TextView)view.findViewById(R.id.currentPhoneNumber);
            currentPhoneNumber.setTypeface(oswaldLight);
            currentPhoneNumber.setText("Current: " + phoneNumber);
            final EditText inputNewPhoneNumber = (EditText)view.findViewById(R.id.inputNewPhoneNumber);
            inputNewPhoneNumber.setTypeface(oswaldLight);

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);


            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences = getSharedPreferences("UserPhoneNumber", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phoneNumber", inputNewPhoneNumber.getText().toString());
                        editor.apply();
                        phoneNumber = sharedPreferences.getString("phoneNumber", DEFAULT);
                        Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.setCancelable(false);

            final AlertDialog alert = builder.create();
            alert.show();

            Button cancel = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            cancel.setTypeface(oswaldStencil);
            cancel.setTextColor(getResources().getColor(R.color.textColor));

            Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            ok.setTypeface(oswaldStencil);
            ok.setTextColor(getResources().getColor(R.color.textColor));
            inputNewPhoneNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().trim().length()==0){
                        alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            if(inputNewPhoneNumber.getText().toString().isEmpty() ){
                alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(broadcastReceiver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(broadcastReceiver, new IntentFilter(MyLocationService.BROADCAST_ACTION));
    }

    public void upArrowTapped(View view) {
        upArrow.startAnimation(slideDown);
        upArrow.setVisibility(View.GONE);
        controlBoard.startAnimation(slideUp);
        controlBoard.setVisibility(View.VISIBLE);
        if(controlBoard.getVisibility()==View.VISIBLE){
            downArrowIcon.startAnimation(slideDown);
            downArrowIcon.setVisibility(View.VISIBLE);
        }
    }

    public void downArrowTapped(View view) {
        controlBoard.startAnimation(slideDown);
        controlBoard.setVisibility(View.GONE);
        upArrow.startAnimation(slideUp);
        upArrow.setVisibility(View.VISIBLE);
        downArrowIcon.setVisibility(View.INVISIBLE);
    }
}
