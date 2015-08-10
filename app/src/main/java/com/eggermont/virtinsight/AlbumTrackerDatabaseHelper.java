package com.eggermont.virtinsight;

import com.eggermont.virtinsight.AlbumTrackerDatabase.VirtAlbums;
import com.eggermont.virtinsight.AlbumTrackerDatabase.AlbumEvents;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 *  This class handles the creation of tables in the database
 *
 */

class AlbumTrackerDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "pet_tracker.db";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase db;

	private static final String DEBUG_TAG = AlbumTrackerDatabaseHelper.class.getCanonicalName();

	AlbumTrackerDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db = db;

		Log.i(DEBUG_TAG, "creating database tables ...  ");

		// Create Album table
		db.execSQL("CREATE TABLE " + VirtAlbums.ALBUMS_TABLE_NAME + " ("
				+ VirtAlbums._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ VirtAlbums.ALBUM_TITLE_NAME  + " TEXT , "
				+ VirtAlbums.ALBUM_DESCRIPTION + " TEXT , "
				+ VirtAlbums.ALBUM_DATE_ADDED  + " DATE" + ")" );


		// Create Album Events table
		db.execSQL("CREATE TABLE " + AlbumEvents.ALBUMEVENTS_TABLE_NAME + " ("
				+ AlbumEvents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
				+ AlbumEvents.ALBUMEVENTS_EVENT_GPS + " TEXT , "
				+ AlbumEvents.ALBUMEVENTS_AUDIO_SPEECH + " TEXT , "
				+ AlbumEvents.ALBUMEVENTS_PHOTO_LINK + " TEXT , "
				+ AlbumEvents.ALBUMEVENTS_AUDIO_LINK + " TEXT , "
				+ AlbumEvents.ALBUMEVENTS_DATE_ADDED + " DATE , "
				+ AlbumEvents.ALBUMEVENTS_ALBUM_ID + " INTEGER" + ")");
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Housekeeping here.
		// Implement how "move" your application data during an upgrade of
		// schema versions
		// There is no ALTER TABLE command in SQLite, so this generally involves
		// CREATING a new table, moving data if possible, or deleting the old
		// data and starting fresh
		// Your call.
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
}
