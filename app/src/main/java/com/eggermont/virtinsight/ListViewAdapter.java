package com.eggermont.virtinsight;

import android.app.usage.UsageEvents;
import android.content.Intent;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by aae on 8/13/15.
 */
public class ListViewAdapter extends BaseAdapter {

    Context context;
    // TODO: Rather than splitting into two ArrayLists to get albumNames and albumIds, we could instead use a TreeMap using the creation date as a key
    ArrayList<String> albumsNames = new ArrayList<String>();
    ArrayList<String> albumIds = new ArrayList<String>();
    LayoutInflater inflater;
    ArrayList<HashMap> test;


    public ListViewAdapter(Context context, ArrayList<String> albumIds, ArrayList<String> albumsNames){

        this.context = context;
        this.albumIds = albumIds;
        this.albumsNames = albumsNames;
    }


    @Override
    public int getCount() {
        return albumIds.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        TextView txAlbumId;
        TextView txAlbumName;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View itemView = inflater.inflate(R.layout.listview_item, parent, false);

        // Locate the TextViews
        // txAlbumId = (TextView) itemView.findViewById(R.id.albumId);
        txAlbumName = (TextView) itemView.findViewById(R.id.albumName);

        // Capture position and set to the TextViews
        // txAlbumId.setText(albumIds.get(position));
        txAlbumName.setText(albumsNames.get(position));

        // Create Event object to tag with specific album properties
        AlbumRecord albumRecord = new AlbumRecord(Long.parseLong(albumIds.get(position)), albumsNames.get(position));
        itemView.setTag(albumRecord);

        return itemView;
    }




}
