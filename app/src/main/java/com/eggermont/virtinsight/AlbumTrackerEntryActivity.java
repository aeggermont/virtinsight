package com.eggermont.virtinsight;

import java.sql.Date;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eggermont.virtinsight.AlbumTrackerDatabase.VirtAlbums;

import android.util.Log;

// Pet Entry Screen
public class AlbumTrackerEntryActivity extends AlbumTrackerActivity {
	/** Called when the activity is first created. */

	private static final String DEBUG_TAG = AlbumTrackerEntryActivity.class.getCanonicalName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.petentry);

		Log.i(DEBUG_TAG, "AlbumTrackerEntryActivity...  ");

		// Handle Go to List button
		final Button gotoList = (Button) findViewById(R.id.ButtonShowPets);
		gotoList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Go to other activity that displays pet list
				Intent intent = new Intent(AlbumTrackerEntryActivity.this, AlbumTrackerListActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Creates a new Album
	 */
	public void newAlbum(View view){
		Log.i(DEBUG_TAG, "Trting to start the Album Activity ...  ");
		Intent intent = new Intent(AlbumTrackerEntryActivity.this, Album.class);
		startActivity(intent);
	}
}