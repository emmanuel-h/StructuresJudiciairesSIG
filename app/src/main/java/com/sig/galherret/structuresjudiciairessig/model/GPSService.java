package com.sig.galherret.structuresjudiciairessig.model;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class GPSService extends Service implements LocationListener {

    private LocationManager locationManager;
    private final IBinder mBinder = new LocalBinder();

    private boolean locationAvailable;

    private class LocalBinder extends Binder {
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            Toast.makeText(getBaseContext(), "No location permission", Toast.LENGTH_LONG).show();
            locationAvailable = false;

        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationAvailable = false;
            } else {
                locationAvailable = true;
                launchListening();
            }
        }
    }

    private void launchListening() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationAvailable=false;
            return;
        }
        // We store the location of the user at the start of the service..
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (null != location && locationAvailable) {
            saveCoordinates(location);
        }
        //.. and if he move we actualize it every second
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,   // 1 sec
                0.05f, this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(null != location && locationAvailable) {
            saveCoordinates(location);
            Intent intent = new Intent("updateLocation");
            intent.putExtra("longitude",location.getLongitude());
            intent.putExtra("latitude", location.getLatitude());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps turned off", Toast.LENGTH_LONG).show();
        locationAvailable = false;
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps turned on", Toast.LENGTH_LONG).show();
        locationAvailable = true;
        launchListening();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private void saveCoordinates(Location location){
        SharedPreferences coordinates = getSharedPreferences("coordinates",MODE_PRIVATE);
        SharedPreferences.Editor editor = coordinates.edit();
        editor.putFloat("lastKnownLatitude",(float) location.getLatitude());
        editor.putFloat("lastKnownLongitude",(float) location.getLongitude());
        editor.apply();
    }
}
