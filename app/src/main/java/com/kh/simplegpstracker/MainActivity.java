package com.kh.simplegpstracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationListener {

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    String userCountry, userAddress;
    LocationManager mLocationManager;
    Handler locationHandler;
    Location location;
    LocationListener listener;
    TextView txtGPS;
    String TAG = "SimpleGPSInformation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationHandler = new Handler();
        locationHandler.postDelayed(locationHandlerTask, 5000);
        location = null;
        listener = this;
        txtGPS = (TextView)findViewById(R.id.txtGPS);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    Runnable locationHandlerTask = new Runnable(){
        @Override
        public void run(){
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 87);
            }
            boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            locationHandler.postDelayed(locationHandlerTask, 5000);
            if(isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else if (isGPSEnabled){
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);;
            }
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            try {
                Geocoder geocoder = new Geocoder((Context)listener, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                String info = String.format("Address: %s\nCity: %s\nState: %s\nCountry: %s\npostalCode: %s\nKnownName%s\n", address, city, state, country, postalCode, knownName);
                info += String.format("Latitude: %s, Longitude: %s", latitude, longitude);
                Log.d(TAG, info);
                txtGPS.setText(info);
                if (addresses != null && addresses.size() > 0) {
                    userCountry = addresses.get(0).getCountryName();
                    userAddress = addresses.get(0).getAddressLine(0);
                }
                else {
                    userCountry = "Unknown";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    public void onLocationChanged(Location location) {
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