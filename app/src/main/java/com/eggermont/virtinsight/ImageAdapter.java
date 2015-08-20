package com.eggermont.virtinsight;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.GridView;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


/**
 * Created by aae on 8/16/15.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private long albumId;
    private String albumName;
    private int imageWidth;
    private int targetWidth;
    private String currentPhotoPath;

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private AlbumTrackerActivity albumTrackerDatabase;
    private static final String DEBUG_TAG = ImageAdapter.class.getCanonicalName();
    private TreeMap<Integer, HashMap<String, String>> albumEvents;

    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private ArrayList<HashMap<String, String>> imageEvents = new ArrayList<>();


    public ImageAdapter(Context c, TreeMap<Integer, HashMap<String, String>> albumEvents , int targetWidth, String albumName){
        this.mContext = c;
        this.albumEvents = albumEvents;
        this.targetWidth = targetWidth;
        this.albumName = albumName;

        populateThumbIdArray();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }


    private void populateThumbIdArray(){

        // TreeMap albumEvents = new TreeMap<Integer, HashMap<String, String>>();
        Log.i(DEBUG_TAG, "About to get image paths ");
        // Log.i(DEBUG_TAG, albumEvents.toString());

        for(Object id : albumEvents.keySet()) {
            HashMap<String, String> map = (HashMap)albumEvents.get((Long)id);
            Log.i(DEBUG_TAG, map.get("album_id"));
            Log.i(DEBUG_TAG, map.get("photo_path"));
            Log.i(DEBUG_TAG, map.get("date_added"));
            Log.i(DEBUG_TAG, map.get("description"));
            Log.i(DEBUG_TAG, map.get("gps_location"));
            imageEvents.add(map);

        }

    }

    public int getCount(){
        return imageEvents.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            GridView.LayoutParams params = new GridView.LayoutParams(this.targetWidth, this.targetWidth);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);


            String imageName = imageEvents.get(position).get("photo_path").replace("\"", "");

            // Check if image is available in cache
            String imageKey = String.valueOf(imageName.hashCode());

            Log.i(DEBUG_TAG, "Hash Code: " + imageKey);

            File file = new File(imageName);
            imageView.setLayoutParams(params);

            //TODO Bitmaps should be cached using the ImageCache class and read from it if available.
            BitmapProcessingTask task = new BitmapProcessingTask(imageView);
            task.execute(imageName);

            EventTag eventTag = new EventTag(imageEvents.get(position));
            imageView.setTag(new EventTag(imageEvents.get(position)));


            Log.i(DEBUG_TAG, eventTag.toString());

        } else {
            imageView = (ImageView) convertView;
        }

        return imageView;
    }
}
