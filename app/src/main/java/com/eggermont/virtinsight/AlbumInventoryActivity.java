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
	}


	/**
	 * Get a list of all albums availbale in the database
	 */
	public void getAlbumList() {

		HashMap albumInventory = new HashMap<String, HashMap<String, String>>();
		albumInventory = getAlbumListRecords();

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
