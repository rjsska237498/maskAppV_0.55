package com.example.maskapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.print.PrinterId;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class GpsTracker extends Service implements LocationListener {

    private final Context mContext;
    Location location;
    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;


    public GpsTracker(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }


    /* getLoaction()을 호출하면 자신의 위치정보(위도, 경도)를 얻을 수 있다. */
    public Location getLocation(){
        try{
            /* locationManager를 통해 시스템이 관리하는 LOCATION_SERVICE정보에 접근할 수 있다. */
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //GPS정보 제공이 가능한지 여부
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); //NETWORK정보 제공이 가능한지 여부

            if(!isGPSEnabled && !isNetworkEnabled){
                /*  GPS와 NETWORK제공 여부가 모두 false일 경우 */
            }else {
                /* ACCESS_FINE_LOCATION 권한이 있는지에 대한 여부 */
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                /* ACCESS_COARSE_LOCATION 권한이 있는지에 대한 여부 */
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);


                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                    /* 두개의 권한이 모두 부여되어 있는 경우 */
                } else {
                    return null;
                }


                if (isNetworkEnabled) {
                    /*  */
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        /*  */
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }


                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            Log.d("@@@", "" + e.toString());
        }

        return location;
    }



    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }



    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
