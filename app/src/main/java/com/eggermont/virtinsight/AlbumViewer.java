package com.eggermont.virtinsight;

import android.os.Bundle;
import android.util.DisplayMetrics;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

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
    private int imageWidth;
    private static final float INITIAL_ITEMS_COUNT = 4.5F;
    private TreeMap<Integer, HashMap<String, String>> albumEvents;
    private TextView mDescription;
    private GridView mGridView;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_viewer);

        Intent albumInt = getIntent();
        this.albumName = albumInt.getExtras().getString("albumName");
        this.albumId = albumInt.getExtras().getLong("albumId");
        this.albumDesc = albumInt.getExtras().getString("albumDesc");

        // Compute the width of a carousel item based on the screen width and number of initial items.
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.imageWidth = (int) (displayMetrics.widthPixels / INITIAL_ITEMS_COUNT);


        // TODO: Retrieving media from the activity should be done in the ImageAdapter
        albumEvents = getEventsForAlbum(albumId);

        Log.i(DEBUG_TAG, "Just got intent: " + albumId + " : " + albumName);

        mGridView = (GridView) findViewById(R.id.gridViewPhotos);
        this.mDescription = (TextView) findViewById(R.id.eventDesc);

        mGridView.setAdapter(new ImageAdapter(this, albumEvents, this.imageWidth, this.albumName ));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.getTag();
                Toast.makeText(AlbumViewer.this, "" + position,
                        Toast.LENGTH_SHORT).show();

                EventTag eventTag = (EventTag)view.getTag();
                mDescription.setText(eventTag.getDescription());
                Log.i(DEBUG_TAG, "Clicking " + eventTag.getDescription());
                Log.i(DEBUG_TAG, "Clicking " + eventTag.getDateAdded());
                Log.i(DEBUG_TAG, "Clicking " + eventTag.getGelocation_info());
            }
        });
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
                startActivity(events);
                super.onDestroy();
                return true;

            case  R.id.home:
                Log.i(DEBUG_TAG, "Returning to main menu");
                Intent home = new Intent(AlbumViewer.this, AlbumInventoryActivity.class);
                startActivity(home);

            case R.id.view_album:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
