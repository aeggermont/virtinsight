package com.eggermont.virtinsight;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import java.util.ArrayList;

import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.content.Intent;

import java.util.HashMap;

// Pet Listing Screen
public class AlbumInventoryActivity extends AlbumTrackerActivity {

	private static final String DEBUG_TAG = AlbumInventoryActivity.class.getCanonicalName();

	ArrayList<String> albumsNames = new ArrayList<String>();
	ArrayList<String> albumIds = new ArrayList<String>();
	ListViewAdapter adapter;
	ListView list;
	ListView albumsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_inventory);

		// Locate the ListView in listview_main.xml
		handleAlbumsMenu();


	}

	public void handleAlbumsMenu(){

		list = (ListView) findViewById(R.id.listview);
		getAlbumList();


		// Pass results to ListViewAdapter Class
		adapter = new ListViewAdapter(this,
				albumIds,
				albumsNames);

		// Binds the Adapter to the ListView
		list.setAdapter(adapter);

		// Capture ListView item click
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				view.getId();
				AlbumRecord albumRecord = (AlbumRecord)view.getTag();
				Log.i(DEBUG_TAG, "Clicking " + albumRecord);

				Intent albIntent = new Intent(AlbumInventoryActivity.this, AlbumViewer.class);
				albIntent.putExtra("albumId", albumRecord.getAlbumId());
				albIntent.putExtra("albumName", albumRecord.getAlbumName());
				startActivity(albIntent);

			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.add_album:
				Intent newAlbum = new Intent(AlbumInventoryActivity.this, Album.class);
				startActivity(newAlbum);
				Log.i(DEBUG_TAG, "Adding New Album");
				return true;
			case R.id.settings:
				Log.i(DEBUG_TAG, "Configuring App");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//handleAlbumsMenu();
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
			albumsNames.add(map.get("title_name"));
			albumIds.add(map.get("album_id"));
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

