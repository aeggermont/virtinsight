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
import java.util.List;
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
import android.widget.VideoView;
import android.view.MenuInflater;
import android.view.Menu;


public class AlbumEvent extends AlbumTrackerActivity {

    private static final String DEBUG_TAG = AlbumEvent.class.getCanonicalName();


    /**
     * Album Settings
     */
    private long albumId;
    private String albumName;
    private String albumDesc;


    // Call back ids
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int ACTION_TAKE_PHOTO_S = 2;
    private static final int ACTION_TAKE_VIDEO = 3;
    private static final int ACTION_CAPTURE_TEXT = 4;


    // Configuration settings
    private static final String BITMAP_STORAGE_KEY = "viewAlbumBitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imagealbumviewvisibility";

    private static final String VIDEO_STORAGE_KEY = "viewvideo";
    private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
    private VideoView mVideoView;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private TextView mEditText;
    private Bitmap mImageBitmap;
    private Button mRecordSpeech;
    private Button mSaveAlbumEvent;

    private TextView mTxtSpeechInput;
    private TextView mTextAlbumName;



    //Photo  carousel settings
    private LinearLayout mCarouselContainer;
    private ImageView mCurrentImageView;
    private String mCurrentPhotoPath;
    private long currentEventId;
    private int photoIndex;
    private int imageWidth;
    private static final float INITIAL_ITEMS_COUNT = 2.5F;


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




    // Setting Button listeners
    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
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

    /**
     *
     * @param actionCode
     */
    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        }

        Log.i(DEBUG_TAG, ">> Path for media file: " + mCurrentPhotoPath.toString());
        startActivityForResult(takePictureIntent, actionCode);
    }


    /**
     *
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
     *
     * Called when the activity is first created.
     *
     * */
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
                long eventId = addNewEvent(getAlbumId(),getCurrentPhotoPath(), getSpeehText());
                Log.i(DEBUG_TAG, "Event ID:" + eventId);
                resetUI();
            }
        });


        // TODO: Needs to be refactored
        mImageBitmap = null;

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
                dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                return true;
            case R.id.add_text:
                Log.i(DEBUG_TAG, "Recording Text");
                dispatchSpeechTextIntent();
                return true;
            case R.id.add_event:
                Log.i(DEBUG_TAG, "Adding a new event");
                long eventId = addNewEvent(getAlbumId(),getCurrentPhotoPath(), getSpeehText());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

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