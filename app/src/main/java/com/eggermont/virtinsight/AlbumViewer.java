package com.eggermont.virtinsight;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.GridView;
import java.util.HashMap;
import java.util.TreeMap;

import android.widget.AdapterView;
import android.view.View;
import android.app.Activity;
import android.widget.Toast;


/**
 *  This class is used to view stored albums and their contents. This
 *  class extends the AlbumTrackerActivity to interact with the SQLite
 *  database keeping track of stored media for the different albums.
 *
 */


public class AlbumViewer extends AlbumTrackerActivity {


    private static final String DEBUG_TAG = AlbumViewer.class.getCanonicalName();

    /**
     * Album Settings
     */
    private long albumId;
    private String albumName;
    private String albumDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_viewer);

        Intent albumInt = getIntent();
        TreeMap<Integer, HashMap<String, String>> albumEvents;

        this.albumName = albumInt.getExtras().getString("albumName");
        this.albumId = albumInt.getExtras().getLong("albumId");
        this.albumDesc = albumInt.getExtras().getString("albumDesc");

        Log.i(DEBUG_TAG, "Just got intent: " + albumId + " : " + albumName);

        // Get events from database
        albumEvents =  getEventsForAlbum(albumId);
        Log.i(DEBUG_TAG, "Events: " +  albumEvents.toString());


        GridView mGridView = (GridView) findViewById(R.id.gridViewPhotos);
        mGridView.setAdapter(new ImageAdapter(this));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AlbumViewer.this, "" + position,
                        Toast.LENGTH_SHORT).show();

            }
        });


        //Log.i(DEBUG_TAG, "First Event: " + albumEvents.firstKey().toString());
        //Log.i(DEBUG_TAG, "Last Event: " +  albumEvents.lastEntry().toString());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_album_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_event:
                Log.i(DEBUG_TAG, "Adding a new event");
                Intent events = new Intent(AlbumViewer.this, AlbumEvent.class);
                events.putExtra("albumId", this.albumId);
                events.putExtra("albumName", this.albumName);
                events.putExtra("albumDesc", this.albumDesc);
                super.onDestroy();
                return true;

            case  R.id.home:
                Log.i(DEBUG_TAG, "Returning to main menu");
                Intent home = new Intent(AlbumViewer.this, AlbumInventoryActivity.class);
                super.onDestroy();
                startActivity(home);

            case R.id.view_album:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
