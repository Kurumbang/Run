package com.example.bishal.fitness;

/**
 * Created by Bishal on 4/13/2016.
 */

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;


/**
 * Created by Bishal on 4/6/2016.
 */
public class MyLocationService extends Service implements LocationListener {

    private static final String TAG = "com.example.bishal.fitness";
    public static final String BROADCAST_ACTION = "com.example.bishal.fitness";
    private final Handler handler = new Handler();
    Intent intent;
    LocationManager locationManager;

    private static long MINIMUM_TIME_BETWEEN_UPDATES = 3000;
    private static long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1;
    public static Double EARTH_RADIUS = 6371.00;

    double lat_old = 0.0;
    double lon_old = 0.0;
    double lat_new = 0.0;
    double lon_new = 0.0;
    double distance;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        distance = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
        }
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            handler.postDelayed(this, 3000); // 3 seconds
            sendBroadcast(intent);
        }
    };

    @Override
    public void onLocationChanged(Location location) {

        lat_new = location.getLatitude();
        lon_new = location.getLongitude();

        if(lat_old == 0 && lon_old == 0){
            distance = 0;
        }else{
            distance = distance + calculateDistance(lat_new, lon_new,lat_old, lon_old);
        }
        lat_old = lat_new;
        lon_old = lon_new;

        intent.putExtra("latitude", lat_new);
        intent.putExtra("longitude", lon_new);
        intent.putExtra("speed", location.getSpeed());
        intent.putExtra("distance", distance);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2){
        double radius = EARTH_RADIUS;
        double dLat = Math.toRadians(lat2-lat1);
        double dlon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2* Math.asin(Math.sqrt(a));
        return radius*c;
    }

}

