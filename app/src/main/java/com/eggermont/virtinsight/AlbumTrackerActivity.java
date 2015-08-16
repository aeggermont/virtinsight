package com.eggermont.virtinsight;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.text.SimpleDateFormat;

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
	 * This method inserts a new event to an existing album into the
	 * album events table
	 *
	 * @param albumId
	 * @param currentPhotoPath
	 * @param txtSpeechInput
	 *
	 * @return
	 */
	public long addNewEvent(long albumId, String currentPhotoPath, String txtSpeechInput ){

		Date today = new Date(System.currentTimeMillis());

		Log.i(DEBUG_TAG, "Saving Event: "
				+ " Album ID: " +  albumId
				+ " currentPhotoPath: " + currentPhotoPath
				+ " description: " + txtSpeechInput);

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

		return eventId;
	}


	/**
	 * This method fetches all media events stored in  the database for a
	 * specific album
	 *
	 * @param albumId unique identifier for a stored album
	 */
	public TreeMap<Integer, HashMap<String, String>> getEventsForAlbum(long albumId){

		TreeMap albumEvents = new TreeMap<Integer, HashMap<String, String>>();
		SimpleDateFormat dateformat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		long epoch = 0;

		Log.i(DEBUG_TAG, "About to fetch events for album ID: " + albumId );

		// SQL Query to fetch album events from the database
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME);

		// Get the Database and run the query
		SQLiteDatabase db = mDatabase.getReadableDatabase();

		String asColumnsToReturn[] = {
				AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME + "." + AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_ALBUM_ID,
				AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME + "." + AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_AUDIO_SPEECH,
				AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME + "." + AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_PHOTO_LINK,
				AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME + "." + AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_DATE_ADDED,
				AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME + "." + AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_EVENT_GPS,
				AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_TABLE_NAME + "." + AlbumTrackerDatabase.AlbumEvents._ID
		};

		queryBuilder.appendWhere(
				AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_ALBUM_ID + "=" +  albumId);

		Cursor eventRows = queryBuilder.query(db, asColumnsToReturn, null, null, null, null, null, null);


		if (eventRows.moveToFirst()) {
			do {
				Date date = null;
				HashMap<String, String> eventRecord = new HashMap<String, String>();

				try {
					date = dateformat.parse(eventRows.getString(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_DATE_ADDED)));
					epoch = date.getTime()/100;

				} catch (ParseException e) {
					e.printStackTrace();
				}

				Log.i(DEBUG_TAG, "Album ID " + eventRows.getInt(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_ALBUM_ID)));
				Log.i(DEBUG_TAG, "Evemt ID " + eventRows.getInt(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents._ID)));
				Log.i(DEBUG_TAG, eventRows.getString(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_AUDIO_SPEECH)));
				Log.i(DEBUG_TAG, eventRows.getString(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_PHOTO_LINK)));
				Log.i(DEBUG_TAG, eventRows.getString(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_DATE_ADDED)));
				Log.i(DEBUG_TAG, "Epoch Time: " + epoch);
				Log.i(DEBUG_TAG, eventRows.getString(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_EVENT_GPS)));

				eventRecord.put("album_id", eventRows.getString(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_ALBUM_ID)));
				eventRecord.put("date_added",eventRows.getString(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_DATE_ADDED)));
				eventRecord.put("photo_path", eventRows.getString(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_PHOTO_LINK)));
				eventRecord.put("gps_location", eventRows.getString(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_EVENT_GPS)));
				eventRecord.put("description", eventRows.getString(eventRows.getColumnIndex(AlbumTrackerDatabase.AlbumEvents.ALBUMEVENTS_AUDIO_SPEECH)));
				albumEvents.put(epoch, eventRecord);
			}while (eventRows.moveToNext());
		}

		Log.i(DEBUG_TAG, "Count of album evemt records: " + eventRows.getCount());
		Log.i(DEBUG_TAG, "asColumnsToReturn : " + asColumnsToReturn.toString());

		eventRows.close();
		db.close();

		return albumEvents;

	}


	/**
	 * This method gets a list of all albums available in the database
	 *
	 * @return a hashmap with all album records by album ids as keys
	 */
	public HashMap <String, HashMap<String,String>> getAlbumListRecords() {
		HashMap albumInventory = new HashMap<Integer, HashMap<String, String>>();

		// SQL Query to fetch albums from database
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME);

		// Get the Database and run the query
		SQLiteDatabase db = mDatabase.getReadableDatabase();

		String asColumnsToReturn[] = {
				AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME + "." + AlbumTrackerDatabase.VirtAlbums.ALBUM_TITLE_NAME,
				AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME + "." + AlbumTrackerDatabase.VirtAlbums.ALBUM_DESCRIPTION,
				AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME + "." + AlbumTrackerDatabase.VirtAlbums.ALBUM_DATE_ADDED,
				AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME + "." + AlbumTrackerDatabase.VirtAlbums._ID
		};


		Cursor allRows = queryBuilder.query(db, asColumnsToReturn, null, null, null, null, null, null);

		Log.i(DEBUG_TAG, "Count of album records: " + allRows.getCount());
		Log.i(DEBUG_TAG, "asColumnsToReturn : " + asColumnsToReturn.toString());

		if (allRows.moveToFirst()) {
			// Iterate over each cursor
			do {
				HashMap<String, String> record = new HashMap<String, String>();

				record.put("album_id", Integer.toString(allRows.getInt(allRows.getColumnIndex(AlbumTrackerDatabase.VirtAlbums._ID))));
				record.put("title_name", allRows.getString(allRows.getColumnIndex(AlbumTrackerDatabase.VirtAlbums.ALBUM_TITLE_NAME)));
				record.put("date_added", allRows.getString(allRows.getColumnIndex(AlbumTrackerDatabase.VirtAlbums.ALBUM_DATE_ADDED)));
				record.put("description", allRows.getString(allRows.getColumnIndex(AlbumTrackerDatabase.VirtAlbums.ALBUM_DESCRIPTION)));

				albumInventory.put(Integer.toString(allRows.getInt(
								allRows.getColumnIndex(AlbumTrackerDatabase.VirtAlbums._ID))),
						record);

				//Log.i(DEBUG_TAG, "ID " + allRows.getInt(allRows.getColumnIndex(AlbumTrackerDatabase.VirtAlbums._ID)));
				//Log.i(DEBUG_TAG, allRows.getString(allRows.getColumnIndex(AlbumTrackerDatabase.VirtAlbums.ALBUM_TITLE_NAME)));
				//Log.i(DEBUG_TAG, allRows.getString(allRows.getColumnIndex(AlbumTrackerDatabase.VirtAlbums.ALBUM_DATE_ADDED)));
				//Log.i(DEBUG_TAG, allRows.getString(allRows.getColumnIndex(AlbumTrackerDatabase.VirtAlbums.ALBUM_DESCRIPTION)));

			} while (allRows.moveToNext());
		}

		allRows.close();
		db.close();

		return albumInventory;
	}


	/**
	 *  This method creats a new album in the data base if thte album does
	 *  not exist
	 *
	 * @param albumName
	 * @param albumDesc
	 */
	public long saveAlbum(String albumName, String albumDesc){

		Date today = new Date(System.currentTimeMillis());
		long albumId = 0;
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

		return albumId;
	}


	/**
	 * TODO: This method is duplicated and should be removed
	 * @return
	 */
	public String [] getStoredAlbums(){

		// SQL Query to fetch albums from database
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME);

		// Get the Database and run the query
		SQLiteDatabase db = mDatabase.getReadableDatabase();

		String asColumnsToReturn[] = { AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME + "." + AlbumTrackerDatabase.VirtAlbums.ALBUM_TITLE_NAME,
				AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME + "." + AlbumTrackerDatabase.VirtAlbums.ALBUM_DESCRIPTION,
				AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME + "." + AlbumTrackerDatabase.VirtAlbums.ALBUM_DATE_ADDED,
				AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME + "." + AlbumTrackerDatabase.VirtAlbums._ID };

		Cursor c = queryBuilder.query(db, asColumnsToReturn, null, null, null, null, AlbumTrackerDatabase.VirtAlbums.DEFAULT_SORT_ORDER);

		return asColumnsToReturn;

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
