package com.eggermont.virtinsight;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import android.os.Looper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;


/**
 * This class provides with a services that gets gelocation
 * information to be tagged to a media component.
 */

public class GeolocationService extends Service implements LocationListener {
    LocationManager location = null;


    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    // Random number generator
    private final Random mGenerator = new Random();

    // Logger reference
    private static final String DEBUG_TAG = GeolocationService.class.getCanonicalName();

    Location lastLocation = null;

    private double currentLogitud;
    private double currentLatitud;
    private double currentAltitud;
    private List<Address> addresses;

    // Status of current geo updates, true if geo info is available,
    // false otherwise
    private boolean status;

    @Override
    public void onCreate() {
        Log.i(DEBUG_TAG, "creating service instance");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(DEBUG_TAG, "binding on GeolocationService ");

        location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Iterator<String> providers = location.getAllProviders()
                .iterator();

        while (providers.hasNext()) {
            Log.v("Location", providers.next());
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

        String best = location.getBestProvider(criteria, true);
        //location.requestLocationUpdates(best, 1000, 0, GeolocationService.this);
        location.requestLocationUpdates(best, 120000, 0, GeolocationService.this);
        //location.requestSingleUpdate(best, GeolocationService.this,  Looper.myLooper());

        if(best != null) {
            this.status = true;
            Log.i(DEBUG_TAG, "Best Provider: " + best);
        }
        return mBinder;
    }


    /**
     * Gets updates from Location Manager on current
     * positional geolocation parameters.
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {

       this.currentLogitud = location.getLongitude();
       this.currentLatitud = location.getLatitude();
       this.currentAltitud = location.getAltitude();

        StringBuilder locationInfo = new StringBuilder("Current loc = (")
                .append(location.getLatitude()).append(", ")
                .append(location.getLongitude()).append(") @ (")
                .append(location.getAltitude()).append(" meters up)\n");

        Log.i(DEBUG_TAG, "Current Location:" + locationInfo);
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        int satellites = extras.getInt("satellites", -1);
        String statusInfo = String.format(Locale.getDefault(),
                "Provider: %s, status: %s, satellites: %d", provider,
                providerStatusMap.get(status), satellites);
        Log.i(DEBUG_TAG, statusInfo);
    }

    private static final SparseArray<String> providerStatusMap = new SparseArray<String>() {
        {
            put(LocationProvider.AVAILABLE, "Available");
            put(LocationProvider.OUT_OF_SERVICE, "Out of Service");
            put(LocationProvider.TEMPORARILY_UNAVAILABLE,
                    "Temporarily Unavailable");
            put(-1, "Not Reported");
        }
    };

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(DEBUG_TAG, "Provider enabled " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(DEBUG_TAG, "Provider disabled " + provider);
    }

    /**
     * Class used for the client Binder since this service always
     * runs in the same process as the AlbumEvent instance
     */
    public class LocalBinder extends Binder {
        GeolocationService getService(){
            return GeolocationService.this;
        }
    }


    /**
     * This method gets the geolocation information
     */
    public HashMap<String,Object> getCurrentLocation() {

        HashMap geoInfo = new HashMap<String,Object>();

        geoInfo.put("status", status);
        geoInfo.put("currentLogitud", currentLogitud);
        geoInfo.put("currentLatitud", currentLatitud);
        geoInfo.put("currentAltitud", currentAltitud);
        geoInfo.put("addresses" , addresses);

        return geoInfo;

    }
}
