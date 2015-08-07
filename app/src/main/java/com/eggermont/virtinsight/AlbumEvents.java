package com.eggermont.virtinsight;

/**
 * AlbumEvents
 *
 * TO BE REMOVED
 */


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;


// Google Play Services
import android.location.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


// Android speech recoginition
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

public class AlbumEvents extends AlbumTrackerActivity implements ConnectionCallbacks, OnConnectionFailedListener{

    private static final String DEBUG_TAG = AlbumEvents.class.getCanonicalName();
    private long albumId;

    /**
     * Variables related to Google Location Services
     */

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    /**
     * Variables releated to Speech recognition
     */

    private TextView txtSpeechInput;
    private final int REQ_CODE_SPEECH_INPUT = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_events);

        final Button takePhoto = (Button) findViewById(R.id.ButtonTakePhoto);
        final Button recSpeech = (Button) findViewById(R.id.ButtonRecordSpeech);
        final Button addEvent = (Button) findViewById(R.id.ButtonSaveAlbumEvent);
        final TextView textViewAlbumName = (TextView) findViewById(R.id.TextAlbumName);

        txtSpeechInput = (EditText) findViewById(R.id.txtSpeechInput);

        // Getting album info from previous activity
        Intent albumInt = getIntent();
        String albumName = albumInt.getExtras().getString("albumName");
        String albumDesc = albumInt.getExtras().getString("albumDesc");

        Log.i(DEBUG_TAG, "Album name: " + albumName);
        Log.i(DEBUG_TAG, "Album description: " + albumDesc);

        textViewAlbumName.setText(albumName);

        // Creating album in database
        saveAlbum(albumName, albumDesc);

        // Handling speech recording
        recSpeech.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                rec_speech();
            }
        });

        // Handling photo taking
        takePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                take_photo();
            }
        });

        // Handling events recording
        addEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                add_event();
            }
        });

    }

    /**
     * Creating new album in database
     */
    public void saveAlbum(String albumName, String albumDesc){

        Date today = new Date(System.currentTimeMillis());

        SQLiteDatabase db = mDatabase.getWritableDatabase();
        long albumId = 0;

        db.beginTransaction();

        try{
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME);

            ContentValues albumRecordToAdd = new ContentValues();
            albumRecordToAdd.put(AlbumTrackerDatabase.VirtAlbums.ALBUM_TITLE_NAME, albumName);
            albumRecordToAdd.put(AlbumTrackerDatabase.VirtAlbums.ALBUM_DESCRIPTION, albumDesc);
            albumRecordToAdd.put(AlbumTrackerDatabase.VirtAlbums.ALBUM_DATE_ADDED, today.toString());

            this.albumId = db.insert(AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME, AlbumTrackerDatabase.VirtAlbums.ALBUM_TITLE_NAME,
                    albumRecordToAdd);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_events, menu);
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

    public void take_photo() {
        Log.i(DEBUG_TAG, "Taking photo ...  ");
        Log.i(DEBUG_TAG, "Album ID: " + this.albumId);
        Log.i(DEBUG_TAG, "Starting AlbumEvents Activity ...  ");
        Intent intent = new Intent(AlbumEvents.this, StillImage.class);
        startActivity(intent);
    }


    public void rec_speech() {
        Log.i(DEBUG_TAG, "Recording speech ...  ");
        Log.i(DEBUG_TAG, "Album ID: " + this.albumId);
        promptSpeechInput();
    }


    public void add_event() {
        // First we need to check availability of play services, but it does not
        // seem to be working for now

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        getCurrentLocation();


        Log.i(DEBUG_TAG, "Adding album event ...  ");
        Log.i(DEBUG_TAG, "Album ID: " + this.albumId);

        Date today = new Date(System.currentTimeMillis());

        SQLiteDatabase db = mDatabase.getWritableDatabase();
        long eventId = 0;
        db.beginTransaction();

        try{
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME);
            ContentValues albumEventToAdd = new ContentValues();

            albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_ALBUM_ID, this.albumId);
            albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_DATE_ADDED, today.toString());
            albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_AUDIO_LINK, "/audio_link");
            albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_PHOTO_LINK, "/photo_link");
            albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_AUDIO_SPEECH, txtSpeechInput.getText().toString());
            albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_EVENT_GPS, "1.5555, 5.55555");

            eventId = db.insert(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME, AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_ALBUM_ID,
                    albumEventToAdd);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        db.close();
        Log.i(DEBUG_TAG, "Album Event ID: " + eventId);
    }


    // Methods related to speech services

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

        }
    }

    // Methods releated to Location Services

    @Override
    protected void onStart() {

        Log.i(DEBUG_TAG, "Starting Googler API Client ...  ");

        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Method to display the location on UI
     * */
    private void getCurrentLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            Log.i(DEBUG_TAG, "GPS Coordinates: " + latitude + ", " + longitude);

        } else {
            Log.i(DEBUG_TAG, "Couldn't get the location. Make sure location is enabled on the device");
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
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
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

}
