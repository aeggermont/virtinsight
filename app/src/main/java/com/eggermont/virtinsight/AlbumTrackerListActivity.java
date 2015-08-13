package com.eggermont.virtinsight;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONTokener;

//import com.advancedandroidbook.pettracker.AlbumTrackerDatabase.Pets;
import com.eggermont.virtinsight.AlbumTrackerDatabase.VirtAlbums;

import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Pet Listing Screen
public class AlbumTrackerListActivity extends AlbumTrackerActivity {

	private static final String DEBUG_TAG = AlbumTrackerListActivity.class.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.showpets);

		// Fill TableLayout with database results
		getAlbumList();

		// Handle Go to List button
		/**
		 final Button gotoEntry = (Button) findViewById(R.id.ButtonEnterMorePets);
		 gotoEntry.setOnClickListener(new View.OnClickListener() {
		 public void onClick(View v) {
		 // Go to other activity that displays pet list
		 finish();
		 }
		 });*/
	}

	/**
	 * Gets all the events for a specific album id
	 */
	public void getEvventsForAlbum(long albumId) {
		// TODO needs to be implemented
	}

	/**
	 * Get a list of all albums availbale in the database
	 */
	public void getAlbumList() {

		HashMap albumInventory = new HashMap<String, HashMap<String, String>>();

		albumInventory = getAlbumListRecords();

		Log.i(DEBUG_TAG, "About to render all recorded albums");
		Log.i(DEBUG_TAG, albumInventory.entrySet().toString());

		//Map<String, String> row = new HashMap<String, String>();
		// HashMap <String, HashMap<String,String>>


		for(Object id : albumInventory.keySet()) {
			HashMap<String, String> map = (HashMap)albumInventory.get((String)id);
			Log.i(DEBUG_TAG, map.get("album_id"));
			Log.i(DEBUG_TAG, map.get("title_name"));
			Log.i(DEBUG_TAG, map.get("date_added"));
			Log.i(DEBUG_TAG, map.get("description"));
		}
	}
}

		/**
        // Display the results by adding some TableRows to the existing table layout
		if(c.moveToFirst())
		{
			for(int i = 0; i< c.getCount(); i++)
			{
				TableRow newRow = new TableRow(this);
				TextView nameCol = new TextView(this);
				TextView typeCol = new TextView(this);
				TextView dateAdded = new TextView(this);
				//Button deleteButton = new Button(this);
				Button viewAlbumButton = new Button(this);
						
				newRow.setTag(c.getInt(c.getColumnIndex(VirtAlbums._ID)));		// set the tag field on the TableRow view so we know which row to delete
				nameCol.setText(c.getString(c.getColumnIndex(VirtAlbums.ALBUM_TITLE_NAME)));
				typeCol.setText(c.getString(c.getColumnIndex(VirtAlbums.ALBUM_DESCRIPTION)));
				//dateAdded.setText(c.getString(c.getColumnIndex(VirtAlbums.ALBUM_DATE_ADDED)));
				viewAlbumButton.setText("View");
				viewAlbumButton.setTag(c.getColumnIndex(VirtAlbums._ID));
				//deleteButton.setText("Delete ALbum");
				//deleteButton.setTag(c.getInt(c.getColumnIndex(VirtAlbums._ID)));		// set the tag field on the button so we know which ID to delete


				// Handling

				newRow.addView(nameCol);
				newRow.addView(typeCol);
				newRow.addView(dateAdded);
				//newRow.addView(deleteButton);
				petTable.addView(newRow);
				c.moveToNext();
			}
		}
		else
		{
			TableRow newRow = new TableRow(this);
			TextView noResults = new TextView(this);
			noResults.setText("No results to show.");
			newRow.addView(noResults);
			petTable.addView(newRow);
		}

		 */


	/**
	public void deletePet(Integer id)
	{
        SQLiteDatabase db = mDatabase.getWritableDatabase();
		String astrArgs[] = { id.toString() };
        db.delete(Pets.PETS_TABLE_NAME, Pets._ID + "=?",astrArgs );
        db.close();
		
		
	}*/

