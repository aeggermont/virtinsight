package com.eggermont.virtinsight;


import android.content.Intent;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.sql.Date;

public class Album extends AlbumTrackerActivity {

    private static final String DEBUG_TAG = Album.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(DEBUG_TAG, "Starting Album Activity  ...  ");
        setContentView(R.layout.activity_album);

        /**
        // Handle Save New Album Button
        final Button savePet = (Button) findViewById(R.id.ButtonNew);
        savePet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final EditText albumName = (EditText) findViewById(R.id.EditTextName);
                final EditText albumDesc = (EditText) findViewById(R.id.EditAlbumDescription);

                // Save new records

                SQLiteDatabase db = mDatabase.getWritableDatabase();
                db.beginTransaction();
                try {

                    Date today = new Date(System.currentTimeMillis());

                    // Start Query
                    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                    queryBuilder.setTables(AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME);

                    ContentValues albumRecordToAdd = new ContentValues();
                    albumRecordToAdd.put(AlbumTrackerDatabase.VirtAlbums.ALBUM_TITLE_NAME, albumName.getText().toString());
                    albumRecordToAdd.put(AlbumTrackerDatabase.VirtAlbums.ALBUM_DESCRIPTION, albumDesc.getText().toString());
                    albumRecordToAdd.put(AlbumTrackerDatabase.VirtAlbums.ALBUM_DATE_ADDED, today.toString());

                    db.insert(AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME, AlbumTrackerDatabase.VirtAlbums.ALBUM_TITLE_NAME,
                            albumRecordToAdd);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                // reset form
                albumName.setText(null);
                //petType.setText(null);
                db.close();

            }
        });
         */

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

    public void startNewAlbum(View view){
        Log.i(DEBUG_TAG, "Trting to start AlbumEvents Activity ...  ");

        final EditText albumName = (EditText) findViewById(R.id.EditTextName);
        final EditText albumDesc = (EditText) findViewById(R.id.EditAlbumDescription);

        Log.i(DEBUG_TAG, "Starting AlbumEvents Activity ...  ");
        Intent intent = new Intent(Album.this, AlbumEvents.class);
        intent.putExtra("albumName", albumName.getText().toString());
        intent.putExtra("albumDesc", albumDesc.getText().toString());

        startActivity(intent);
    }
}
