package com.eggermont.virtinsight;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 *  Manages the instantiation and closing of a local
 *  data base ( SQLLite ) to track different media
 *  events captures in the media album.
 */

public class AlbumTrackerActivity extends Activity {

	private static final String DEBUG_TAG = AlbumTrackerActivity.class.getCanonicalName();

	// Our application database
	protected AlbumTrackerDatabaseHelper mDatabase = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDatabase = new AlbumTrackerDatabaseHelper(this.getApplicationContext());
		Log.i(DEBUG_TAG, "Instantiated AlbumTrackerActivity() ...  ");
	}


	/**
	 * Closes the SQLite database upon desctroying this activity
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mDatabase != null)
		{
			mDatabase.close();
		}
	}
}
