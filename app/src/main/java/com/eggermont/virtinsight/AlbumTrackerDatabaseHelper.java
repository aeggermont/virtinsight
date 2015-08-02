package com.eggermont.virtinsight;

import com.eggermont.virtinsight.AlbumTrackerDatabase.PetType;
import com.eggermont.virtinsight.AlbumTrackerDatabase.Pets;

import com.eggermont.virtinsight.AlbumTrackerDatabase.VirtAlbums;
import com.eggermont.virtinsight.AlbumTrackerDatabase.AlbumEvents;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// This class handles the creation and versioning of the application database

class AlbumTrackerDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "pet_tracker.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DEBUG_TAG = AlbumTrackerDatabaseHelper.class.getCanonicalName();

	AlbumTrackerDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {


		Log.i(DEBUG_TAG, "CREATINg DATABASES ...  ");

		// Create the PetType table
		db.execSQL("CREATE TABLE " + PetType.PETTYPE_TABLE_NAME + " ("
				+ PetType._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ PetType.PET_TYPE_NAME + " TEXT" + ");");

		// Create the Pets table
		db.execSQL("CREATE TABLE " + Pets.PETS_TABLE_NAME + " (" + Pets._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT ," + Pets.PET_NAME
				+ " TEXT," + Pets.PET_TYPE_ID + " INTEGER" // this is a foreign
															// key to the pet
															// type table
				+ ");");

		// Create Album table
		db.execSQL("CREATE TABLE " + VirtAlbums.ALBUMS_TABLE_NAME + " ("
				+ VirtAlbums._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ VirtAlbums.ALBUM_TITLE_NAME  + " TEXT , "
				+ VirtAlbums.ALBUM_DESCRIPTION + " TEXT , "
				+ VirtAlbums.ALBUM_DATE_ADDED  + " DATE" + ")" );


		// Create Album Events table
		db.execSQL("CREATE TABLE " + AlbumEvents.ALBUMEVENTS_TABLE_NAME + " ("
				+ AlbumEvents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
				+ AlbumEvents.ALBUMEVENTS_EVENT_GPS  + " TEXT , "
				+ AlbumEvents.ALBUMEVENTS_AUDIO_SPEECH + " TEXT , "
				+ AlbumEvents.ALBUMEVENTS_PHOTO_LINK + " TEXT , "
				+ AlbumEvents.ALBUMEVENTS_AUDIO_LINK + " TEXT , "
				+ AlbumEvents.ALBUMEVENTS_DATE_ADDED + " DATE , "
				+ AlbumEvents.ALBUMEVENTS_ALBUM_ID + " INTEGER" + ")" );
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
