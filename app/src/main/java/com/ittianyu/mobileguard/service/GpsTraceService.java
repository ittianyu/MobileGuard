package com.ittianyu.mobileguard.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.utils.ConfigUtils;


/**
 * locatint and sending sms to safe phone
 */
public class GpsTraceService extends Service {
    private static final String TAG = "GpsTraceService";

    private MyLocationListener listener;
    private LocationManager locationManager;


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // locate
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // get best provider
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        System.out.println("best provider:" + bestProvider);
        // start listening
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(bestProvider, 0, 0, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // release
        locationManager.removeUpdates(listener);
        listener = null;
        locationManager = null;
    }

    /**
     * LocationListener
     */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // get location info
            double altitude = location.getAltitude();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            StringBuilder buffer = new StringBuilder();
            buffer.append("altitude:" + altitude + "\n");
            buffer.append("longitude:" + longitude + "\n");
            buffer.append("latitude:" + latitude + "\n");

            // get safe phone number
            String safePhone = ConfigUtils.getString(GpsTraceService.this, Constant.KEY_SAFE_PHONE, "");
            if(TextUtils.isEmpty(safePhone)) {
                Log.e(TAG, "safe phone is empty");
                return;
            }

            // send location info to safe phone number
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(safePhone, null, buffer.toString(), null, null);
            System.out.println("success send a sms to " + safePhone + ":\n" + buffer.toString());
            // stop service
            stopSelf();
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
    }
}
