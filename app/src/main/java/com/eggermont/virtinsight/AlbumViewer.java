package com.eggermont.virtinsight;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import java.util.HashMap;
import java.util.TreeMap;


/**
 *  This class is used to view stored albums and their contents. This
 *  class extends the AlbumTrackerActivity to interact with the SQLite
 *  database keeping track of stored media for the different albums.
 *
 */


public class AlbumViewer extends AlbumTrackerActivity {


    private static final String DEBUG_TAG = AlbumViewer.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_viewer);

        Intent albumInt = getIntent();
        TreeMap<Integer, HashMap<String, String>> albumEvents;

        String albumName = albumInt.getExtras().getString("albumName");
        long albumId = albumInt.getExtras().getLong("albumId");
        Log.i(DEBUG_TAG, "Just got intent: " + albumId + " : " + albumName);

        albumEvents =  getEventsForAlbum(9);

        Log.i(DEBUG_TAG, "Events: " +  albumEvents.toString());

        //Log.i(DEBUG_TAG, "First Event: " + albumEvents.firstKey().toString());
        //Log.i(DEBUG_TAG, "Last Event: " +  albumEvents.lastEntry().toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_viewer, menu);
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
}
