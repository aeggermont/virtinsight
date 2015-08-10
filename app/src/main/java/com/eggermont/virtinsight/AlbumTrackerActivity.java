package com.eggermont.virtinsight;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;

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
	 * This method adds a new event to an existing album
	 *
	 * @param albumId
	 * @param currentPhotoPath
	 * @param txtSpeechInput
	 * @return
	 */
	public long addNewEvent(int albumId, String currentPhotoPath, String txtSpeechInput ){

		Log.i(DEBUG_TAG, ">>> Adding an new event");
		Log.i(DEBUG_TAG, "Adding album event ...  ");

		Date today = new Date(System.currentTimeMillis());

		SQLiteDatabase db = mDatabase.getWritableDatabase();
		long eventId = 0;
		db.beginTransaction();

		try{
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME);
			ContentValues albumEventToAdd = new ContentValues();

			albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_ALBUM_ID, albumId);
			albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_DATE_ADDED, today.toString());
			albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_AUDIO_LINK, "/audio_link");
			albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_PHOTO_LINK, "\"" + currentPhotoPath + "\"");
			albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_AUDIO_SPEECH, txtSpeechInput);
			albumEventToAdd.put(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_EVENT_GPS, "1.5555, 5.55555");

			eventId = db.insert(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME, AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_ALBUM_ID,
					albumEventToAdd);

			db.setTransactionSuccessful();

		} finally {
			db.endTransaction();
		}

		db.close();

		Log.i(DEBUG_TAG, "Album Event ID: " + eventId + " has been saved.");
		return eventId;
	}

	/**
	 *  This method creats a new album in the data base if thte album does
	 *  not exist
	 *
	 * @param albumId
	 * @param albumName
	 * @param albumDesc
	 */
	public void saveAlbum(long albumId, String albumName, String albumDesc){

		Date today = new Date(System.currentTimeMillis());

		SQLiteDatabase db = mDatabase.getWritableDatabase();

		db.beginTransaction();

		try{
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME);

			ContentValues albumRecordToAdd = new ContentValues();
			albumRecordToAdd.put(AlbumTrackerDatabase.VirtAlbums.ALBUM_TITLE_NAME, albumName);
			albumRecordToAdd.put(AlbumTrackerDatabase.VirtAlbums.ALBUM_DESCRIPTION, albumDesc);
			albumRecordToAdd.put(AlbumTrackerDatabase.VirtAlbums.ALBUM_DATE_ADDED, today.toString());

			albumId = db.insert(AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME, AlbumTrackerDatabase.VirtAlbums.ALBUM_TITLE_NAME,
					albumRecordToAdd);

			db.setTransactionSuccessful();

		} finally {
			db.endTransaction();
		}

		db.close();
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
