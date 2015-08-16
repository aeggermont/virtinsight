package com.eggermont.virtinsight;

/**
 *  This is the entry class to create a bew Album
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Album extends AlbumTrackerActivity {

    private static final String DEBUG_TAG = Album.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(DEBUG_TAG, "Starting Album Activity  ...  ");
        setContentView(R.layout.activity_album);

        final Button newAlbum = (Button) findViewById(R.id.ButtonStartAlbum);
        newAlbum.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startNewAlbum();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_album, menu);
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

    /**
     * Creates a new album by starting the activity AlbumEvents to
     * beging recording media events.
     */
    public void startNewAlbum(){
        Log.i(DEBUG_TAG, "Trting to start AlbumEvents Activity ...  ");

        final EditText albumName = (EditText) findViewById(R.id.EditTextAlbumName);
        final EditText albumDesc = (EditText) findViewById(R.id.EditAlbumDescription);

        long albumId = saveAlbum(albumName.getText().toString(), albumDesc.getText().toString());

        Log.i(DEBUG_TAG, "Starting AlbumEvents Activity ...  ");
        Intent intent = new Intent(Album.this, AlbumEvent.class);
        intent.putExtra("albumName", albumName.getText().toString());
        intent.putExtra("albumDesc", albumDesc.getText().toString());
        intent.putExtra("albumId", albumId);

        startActivity(intent);

    }
}
