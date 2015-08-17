package com.eggermont.virtinsight;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.Random;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * This class provides with a services that gets gelocation
 * information to be tagged to a media component.
 */

public class GeolocationService extends Service implements ConnectionCallbacks,
        OnConnectionFailedListener {

    /**  Google play geolocation services settings  */

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;



    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    // Random number generator
    private final Random mGenerator = new Random();

    // Logger reference
    private static final String DEBUG_TAG = GeolocationService.class.getCanonicalName();


    @Override
    public void onCreate() {
        Log.i(DEBUG_TAG, "creating service instance");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(DEBUG_TAG, "binding on GeolocationService ");
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
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
     * This method checks if google play services are available on
     * the device
     */
    private boolean checkGooglePlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                //        PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Toast.makeText(getApplicationContext(),
                        "This device can supported." + resultCode, Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }



    /** methods for clients */

    /**
     * This method is used only for testing service. It does not have any
     * logical usage in the app
     */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    public String getGeolocationData(){

        if (checkGooglePlayServices()) {
            return "OK";
        }else{
            return "NOT OK";
        }
    }


    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(DEBUG_TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        // displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
}
