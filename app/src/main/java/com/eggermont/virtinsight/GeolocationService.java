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
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        location.requestLocationUpdates(best, 1000, 0, GeolocationService.this);

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

        //lastLocation = location;

        /**

        if (lastLocation != null) {
            float distance = location.distanceTo(lastLocation);
            locInfo.append("Distance from last = ").append(distance)
                    .append(" meters\n");

        }
        lastLocation = location;

        if (Geocoder.isPresent()) {
            Geocoder coder = new Geocoder(this);
            try {
                List<Address> addresses = coder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 3);
                if (addresses != null) {
                    for (Address namedLoc : addresses) {
                        String placeName = namedLoc.getLocality();
                        String featureName = namedLoc.getFeatureName();
                        String country = namedLoc.getCountryName();
                        String road = namedLoc.getThoroughfare();
                        locInfo.append(String.format("[%s][%s][%s][%s]\n",
                                placeName, featureName, road, country));
                        int addIdx = namedLoc.getMaxAddressLineIndex();
                        for (int idx = 0; idx <= addIdx; idx++) {
                            String addLine = namedLoc.getAddressLine(idx);
                            locInfo.append(String.format("Line %d: %s\n", idx,
                                    addLine));
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("GPS", "Failed to get address", e);
            }
        } else {
            Toast.makeText(GeolocationService.this, "No geocoding available",
                    Toast.LENGTH_LONG).show();

        }

        Toast.makeText(GeolocationService.this, locInfo,Toast.LENGTH_LONG).show();
         */

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

        return geoInfo;

    }
}
