package com.eggermont.virtinsight;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

//import com.advancedandroidbook.pettracker.AlbumTrackerDatabase.Pets;
import com.eggermont.virtinsight.AlbumTrackerDatabase.VirtAlbums;

// Pet Listing Screen
public class AlbumTrackerListActivity extends AlbumTrackerActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showpets);
		
		// Fill TableLayout with database results
		fillAlbumList();
		
		// Handle Go to List button
		final Button gotoEntry = (Button) findViewById(R.id.ButtonEnterMorePets);
		gotoEntry.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Go to other activity that displays pet list
				finish();
			}
		});
	}
	

	public void fillAlbumList()
	{
		// TableLayout where we want to Display list
		final TableLayout petTable = (TableLayout) findViewById(R.id.TableLayout_PetList);

		// SQL Query to fetch albums from database
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(AlbumTrackerDatabase.VirtAlbums.ALBUMS_TABLE_NAME);

		// Get the Database and run the query
		SQLiteDatabase db = mDatabase.getReadableDatabase();

		String asColumnsToReturn[] = { VirtAlbums.ALBUMS_TABLE_NAME + "." + VirtAlbums.ALBUM_TITLE_NAME,
			                       	   VirtAlbums.ALBUMS_TABLE_NAME + "." + VirtAlbums.ALBUM_DESCRIPTION,
				                       VirtAlbums.ALBUMS_TABLE_NAME + "." + VirtAlbums.ALBUM_DATE_ADDED,
				                       VirtAlbums.ALBUMS_TABLE_NAME + "." + VirtAlbums._ID };

		Cursor c = queryBuilder.query(db, asColumnsToReturn, null, null, null, null, VirtAlbums.DEFAULT_SORT_ORDER);


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
		c.close();
		db.close();
		
	}

	/**
	public void deletePet(Integer id)
	{
        SQLiteDatabase db = mDatabase.getWritableDatabase();
		String astrArgs[] = { id.toString() };
        db.delete(Pets.PETS_TABLE_NAME, Pets._ID + "=?",astrArgs );
        db.close();
		
		
	}*/

}
