package com.example.bishal.fitness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.w3c.dom.Text;

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
    private boolean startStopFlag = false;
    private static final String TAG = "com.example.bishal.fitness";
    private Intent intent;
    private TextView displaySpeed, displayDistance, status, timer;
    private Button startStopbtn;
    private OverlayItem olItem;
    private Drawable marker;
    Typeface mytypeface;
    double currentLatitude, currentLongitude, lastLatitude, lastLongitude, distanceTravelled;
    float currentSpeed;
    MyDataBaseHandler dbHandler;
    DecimalFormat form = new DecimalFormat("0.00");
    DecimalFormat form2 = new DecimalFormat("0.000000");
    String date;
    SimpleDateFormat simpleDateFormat;
    String start_Time;
    long startTime;
    Animation slideUp, slideDown;

    RelativeLayout controlBoard, upArrow, downArrow;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        displayLocationOnTheMap(lastLatitude, lastLongitude);

        startStopbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStartStopFlag()) {
                    start_Time = simpleDateFormat.format(new Date());
                    startTime = System.currentTimeMillis();
                    intent = new Intent(getApplicationContext(), MyLocationService.class);
                    startService(intent);
                    startStopbtn.setText(R.string.stop);
                    status.setText(R.string.running);
                    registerReceiver(broadcastReceiver, new IntentFilter(MyLocationService.BROADCAST_ACTION));
                } else {
                    stopService(intent);
                    startStopbtn.setText(R.string.start);
                    status.setText(R.string.start);
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
        });

    }

    public void emergencyCall(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:12345"));
        startActivity(callIntent);
    }

    public void userHistory(View view) {
        Intent userHisory = new Intent(getApplicationContext(), UserHistory.class);
        startActivity(userHisory);
    }

    public void init() {
        mytypeface = Typeface.createFromAsset(this.getAssets(), "Oswald-Stencil.ttf");
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
        displaySpeed.setTypeface(mytypeface);
        displayDistance = (TextView) findViewById(R.id.text_view_distance);
        displayDistance.setTypeface(mytypeface);
        startStopbtn = (Button) findViewById(R.id.startStopBtn);
        startStopbtn.setTypeface(mytypeface);
        status = (TextView) findViewById(R.id.status);
        status.setTypeface(mytypeface);
        timer = (TextView) findViewById(R.id.timer);
        timer.setTypeface(mytypeface);
        marker = this.getResources().getDrawable(R.drawable.location);

        controlBoard = (RelativeLayout) findViewById(R.id.controlBoard);
        upArrow = (RelativeLayout) findViewById(R.id.upArrow);
        downArrow = (RelativeLayout)findViewById(R.id.downArrow);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

        dbHandler = new MyDataBaseHandler(getApplicationContext());
        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        simpleDateFormat = new SimpleDateFormat("HH:mm");
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

        //set the text to current lat and lng speed and distance travelled...


        displaySpeed.setText(form.format(currentSpeed) + " km/h");
        displayDistance.setText(form.format(distanceTravelled) + " km");

        //function call to display map and the location
        displayLocationOnTheMap(currentLatitude, currentLongitude);


    }

    public void displayLocationOnTheMap(double latitude, double longitude) {

        GeoPoint currentLocation = new GeoPoint(latitude, longitude);
        mapController.setCenter(currentLocation);
        olItem = new OverlayItem("Here", "Current Position", currentLocation);
        olItem.setMarker(marker);
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.clear();
        items.add(olItem);
        this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
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
            downArrow.startAnimation(slideDown);
            downArrow.setVisibility(View.VISIBLE);
        }
    }

    public void downArrowTapped(View view) {
        controlBoard.startAnimation(slideDown);
        controlBoard.setVisibility(View.GONE);
        upArrow.startAnimation(slideUp);
        upArrow.setVisibility(View.VISIBLE);
        downArrow.setVisibility(View.INVISIBLE);
    }
}
