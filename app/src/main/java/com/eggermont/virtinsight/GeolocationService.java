package com.eggermont.virtinsight;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.Random;

/**
 * This class provides with a services that gets gelocation
 * information to be tagged to a media component.
 */

public class GeolocationService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    // Random number generator
    private final Random mGenerator = new Random();

    // Logger reference
    private static final String DEBUG_TAG = GeolocationService.class.getCanonicalName();

    /**
     * Class used for the client Binder since this service always
     * runs in the same process as the AlbumEvent instance
     */
    public class LocalBinder extends Binder {
        GeolocationService getService(){
            return GeolocationService.this;
        }
    }

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

    /** methods for clients */

    /**
     * This method is used only for testing service. It does not have any
     * logical usage in the app
     */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

}
