package com.eggermont.virtinsight;

/**
 *  AlbumEvents
 *
 *  This class is used to add media events to a given album. Media events
 *  include photos, text, and GPS location coordinates of the event.
 */

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuInflater;
import android.view.Menu;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.content.ComponentName;
import android.os.Binder;

/**
 *
 *  TODO class description to be added here
 *
 */

public class AlbumEvent extends AlbumTrackerActivity {

    private static final String DEBUG_TAG = AlbumEvent.class.getCanonicalName();

    /**
     * Album Settings
     */
    private long albumId;
    private String albumName;
    private String albumDesc;


    // Call back ids
    private static final int ACTION_TAKE_PHOTO = 1;
    private static final int ACTION_CAPTURE_TEXT = 4;


    // Configuration settings for image content
    private static final String BITMAP_STORAGE_KEY = "viewAlbumBitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imagealbumviewvisibility";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;


    // Service references
    GeolocationService mGeoService;
    boolean mBound = false;

    // Geolocation references
    HashMap<String,Object> currentGeoInfo;

    // UI widget references
    private TextView mEditText;
    private Bitmap mImageBitmap;
    private Button mRecordSpeech;
    private Button mSaveAlbumEvent;
    private TextView mTxtSpeechInput;
    private TextView mTextAlbumName;
    private LinearLayout mCarouselContainer;
    private ImageView mCurrentImageView;


    // Key variables used in UI worflow
    private String mCurrentPhotoPath;
    private long currentEventId;
    private int photoIndex;
    private int imageWidth;
    private static final float INITIAL_ITEMS_COUNT = 2.5F;


    // Getters and setters
    private String getCurrentPhotoPath(){
        return mCurrentPhotoPath;
    }
    private long getAlbumId(){
        return albumId;
    }
    private String getAlbumName() {
        return this.albumName;
    }
    private String getAlbumDescription(){
        return this.albumDesc;
    }
    private String getSpeehText(){
        return mTxtSpeechInput.getText().toString();
    }
    private long getCurrentEventId(){
        return currentEventId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_events);

        // Set widget references
        mTxtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        mRecordSpeech   = (Button) findViewById(R.id.ButtonRecordSpeech);
        mSaveAlbumEvent = (Button) findViewById(R.id.ButtonSaveAlbumEvent);
        mTextAlbumName = (TextView) findViewById(R.id.TextAlbumName);
        mCurrentImageView = (ImageView) findViewById(R.id.imageContent);
        mImageBitmap = null;

        // Start new album or load album if already exists
        setAlbumInfo();

        mRecordSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchSpeechTextIntent();
            }
        });

        // Saving the album
        mSaveAlbumEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(DEBUG_TAG, "Album ID: " + getAlbumId());
                Log.i(DEBUG_TAG, "Image Path:" + getCurrentPhotoPath());

                // Attempt to get geolocation information
                currentGeoInfo = mGeoService.getCurrentLocation();
                Log.i(DEBUG_TAG, "Callback from Geolocation Service:" + currentGeoInfo.toString());

                long eventId = addNewEvent(getAlbumId(),getCurrentPhotoPath(), getSpeehText(), currentGeoInfo);
                Log.i(DEBUG_TAG, "Event ID:" + eventId);
                resetUI();
            }
        });


        // TODO: Needs to be refactored
        Button picBtn = (Button) findViewById(R.id.ButtonCapturePhoto);
        setBtnListenerOrDisable(
                picBtn,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.i(DEBUG_TAG , "About to bind geolocation service");
        // Bind to GeolocationService
        Intent intent = new Intent(this, GeolocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop(){
        super.onStop();;

        // Unbind the GeolocationService service
        if(mBound){
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_album_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_photo:
                Log.i(DEBUG_TAG, "Taking a photo");
                dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
                return true;
            case R.id.add_text:
                Log.i(DEBUG_TAG, "Recording Text");
                dispatchSpeechTextIntent();
                return true;
            case R.id.add_event:
                Log.i(DEBUG_TAG, "Adding a new event");
                long eventId = addNewEvent(getAlbumId(),getCurrentPhotoPath(), getSpeehText(), currentGeoInfo);
                Log.i(DEBUG_TAG, "Event ID:" + eventId);
                resetUI();
                return true;
            case  R.id.home:
                Log.i(DEBUG_TAG, "Returning to main menu");
                Intent home = new Intent(AlbumEvent.this, AlbumInventoryActivity.class);
                super.onDestroy();
                startActivity(home);
            case R.id.view_album:
                Log.i(DEBUG_TAG, "View current album");
                Intent intent = new Intent(AlbumEvent.this, AlbumViewer.class);
                intent.putExtra("albumName", getAlbumName());
                intent.putExtra("albumDesc", getAlbumDescription());
                intent.putExtra("albumId", getAlbumId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onDestroy(){
        mCarouselContainer = null;
        mCurrentImageView = null;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Compute the width of a carousel item based on the screen width and number of initial items.
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.imageWidth = (int) (displayMetrics.widthPixels / INITIAL_ITEMS_COUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    handleCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO

            case ACTION_CAPTURE_TEXT: {
                if ( resultCode == RESULT_OK ){
                    Log.i(DEBUG_TAG, "About to handle text");
                    handleCapturedText(data);
                }
                break;
            } // ACTION_CAPTURE_TEXT
        } // switch
    }


    /**
     * This method defines callbacks for service binding which
     * are passed to bindService()
     * */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GeolocationService.LocalBinder binder = (GeolocationService.LocalBinder) service;
            mGeoService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };




    // Setting Button listeners
    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
                }
            };


    /**
     * This method is called when a new album is created
     * and an album name and description have been set
     */
    private void setAlbumInfo(){

        //TODO: Refactor code to avoid extra variables

        Intent albumInt = getIntent();
        String albumName = albumInt.getExtras().getString("albumName");
        String albumDesc = albumInt.getExtras().getString("albumDesc");
        long albumId = albumInt.getExtras().getLong("albumId");

        Log.i(DEBUG_TAG, "Album ID: " + albumId);
        Log.i(DEBUG_TAG, "Album name: " + albumName);
        Log.i(DEBUG_TAG, "Album description: " + albumDesc);

        this.albumName = albumName;
        this.albumDesc = albumDesc;
        this.albumId = albumId;

        // Update UI
        mTextAlbumName.setText(this.albumName);
    }


    private File getAlbumDir() {

        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }


    /**
     * Creates a temporary image file to be used to store
     * a photo taken from the camera component
     *
     * @return image File
     * @throws IOException
     */
    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp;
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    /**
     * Sets the absolute path of for a captured image
     * @return reference to te file object
     * @throws IOException
     */
    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        this.mCurrentPhotoPath = f.getAbsolutePath();
        Log.i(DEBUG_TAG, "Current photo path: " + mCurrentPhotoPath);
        return f;
    }

    /**
     * Sends a broacast to publish recorded photo to
     * the photo gallery manager
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    // Intent dispatchers

    /**
     *  This method launches the SpeechTest activity to add edit
     *  text descriptions to the album
     */
    public void dispatchSpeechTextIntent(){
        Intent intent = new Intent(AlbumEvent.this, SpeechText.class);
        intent.putExtra("albumName", getAlbumName());
        intent.putExtra("albumDesc", getAlbumDescription());
        startActivityForResult(intent, ACTION_CAPTURE_TEXT);
    }

    /**
     * TODO Add comments
     */
    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileIn = null;

        try {
            fileIn = setUpPhotoFile();
            mCurrentPhotoPath = fileIn.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileIn));
        } catch (IOException e) {
            e.printStackTrace();
            fileIn = null;
            mCurrentPhotoPath = null;
        }

        // TODO: startActivityForResult should be started only if file was created. Needs to be moved to the try block above
        Log.i(DEBUG_TAG, ">> Path for media file: " + mCurrentPhotoPath.toString());
        startActivityForResult(takePictureIntent, actionCode);
    }


    // Handling intents dispatched

    /**
     * TODO Add comments
     */
    private void handleCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            this.mCurrentImageView.setVisibility(ImageView.VISIBLE);
            Log.i(DEBUG_TAG, "About to process photo bitmap, index: " + photoIndex);

            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(this.imageWidth,this.imageWidth);
            this.mCurrentImageView.setLayoutParams(parms);

            BitmapProcessingTask task = new BitmapProcessingTask(mCurrentImageView);
            task.execute(mCurrentPhotoPath);

            mCurrentImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Getting back tagging information
                    Log.i(DEBUG_TAG, "Clicking Image Path:" + getCurrentPhotoPath());
                    Log.i(DEBUG_TAG, "Clicking Image Event ID: " + getCurrentEventId());
                }
            });

            galleryAddPic();
        }
    }


    /**
     * Get the captured text from SpeechText activity and
     * update the activity UI.
     * @param data
     */
    private void handleCapturedText(Intent data){
        Log.i(DEBUG_TAG, "About to handleCapturedText()");
        mTxtSpeechInput.setText(data.getStringExtra("eventDescription"));
    }



    /**
     *  Rest all widgets in the UI to record a new album event
     */
    private void resetUI(){
        this.mTxtSpeechInput.setText("");
        this.mTxtSpeechInput.setVisibility(EditText.INVISIBLE);
        this.mCurrentPhotoPath = null;
        this.mCurrentImageView.setVisibility(ImageView.INVISIBLE);

        Toast.makeText(getApplicationContext(),
                getString(R.string.album_event_saved),
                Toast.LENGTH_SHORT).show();
    }



    // TODO The following metods need refactoring

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mCurrentImageView.setImageBitmap(mImageBitmap);
        mCurrentImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action The Intent action to check for availability.
     *
     * @return True if an Intent with the specified action can be sent and
     *         responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

}