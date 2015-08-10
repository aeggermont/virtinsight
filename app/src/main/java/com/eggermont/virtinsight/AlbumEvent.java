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

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class AlbumEvent extends AlbumTrackerActivity {

    private static final String DEBUG_TAG = AlbumEvent.class.getCanonicalName();


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

    private Uri mVideoUri;

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
    private int photoIndex;
    private int imageWidth;
    private static final float INITIAL_ITEMS_COUNT = 2.5F;

    // Album settings
    private long albumId;
    private String albumName;
    private String albumDesc;


    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                }
            };

    Button.OnClickListener mTakePicSOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_S);
                }
            };

    Button.OnClickListener mTakeVidOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakeVideoIntent();
                }
            };

    private String getCurrentPhotoPath(){
        return mCurrentPhotoPath;
    }

    private long getAlbumId(){
        return albumId;
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return this.albumName;
    }

    private String getAlbumDescription(){
        return this.albumDesc;
    }


    /**
     * This method is called when a new album is created
     * and an album name and description have been set
     */
    private void setAlbumInfo(){

        // Getting album info from previous activity
        Intent albumInt = getIntent();
        String albumName = albumInt.getExtras().getString("albumName");
        String albumDesc = albumInt.getExtras().getString("albumDesc");

        Log.i(DEBUG_TAG, "Album name: " + albumName);
        Log.i(DEBUG_TAG, "Album description: " + albumDesc);

        this.albumName = albumName;
        this.albumDesc = albumDesc;

        // Update UI
        mTextAlbumName.setText(this.albumName);
        // Creating album in database
        saveAlbum(albumName, albumDesc);
    }


    /**
     * This method creates a directory in the appropriate directory location
     * on the device.
     */
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
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }


    /**
     *
     */
    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {

            this.photoIndex +=1;

            Log.i(DEBUG_TAG, "About to process photo bitmap, index: " + photoIndex);

            this.mCurrentImageView = new ImageView(this);
            this.mCurrentImageView.setId(photoIndex);

            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(this.imageWidth,this.imageWidth);
            this.mCurrentImageView.setLayoutParams(parms);
            this.mCurrentImageView.setId(photoIndex);
            //this.mCurrentImageView.setTag(100);

            Log.i(DEBUG_TAG, "Photo Index:  " + photoIndex);
            BitmapProcessingTask task = new BitmapProcessingTask(mCurrentImageView);
            task.execute(mCurrentPhotoPath);

            // Set the size of the image view to the previously computed value
            //imageItem.setLayoutParams(new LinearLayout.LayoutParams(this.imageWidth, this.imageWidth));
            this.mCurrentImageView.setFocusable(true);
            this.mCurrentImageView.setFocusableInTouchMode(true);
            this.mCarouselContainer.addView(mCurrentImageView);

            int count = mCarouselContainer.getChildCount();
            Log.i(DEBUG_TAG, "Current photo count: " + count);

            mCurrentImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(DEBUG_TAG, ">> Clicking : " + view.getId());
                    Log.i(DEBUG_TAG, ">> Resources : " + view.getResources());
                    Log.i(DEBUG_TAG, ">> Path : " + view.getTag());
                }
            });


            //mCarouselContainer.getChildAt(count);
            //mCarouselContainer.setNextFocusDownId(photoIndex);
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



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_events);


        mTextAlbumName = (TextView) findViewById(R.id.TextAlbumName);

        Log.i(DEBUG_TAG, "Album ID: " + getAlbumId());

        // Set widget references
        mTxtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        mRecordSpeech   = (Button) findViewById(R.id.ButtonRecordSpeech);
        mSaveAlbumEvent = (Button) findViewById(R.id.ButtonSaveAlbumEvent);
        photoIndex = 0;

        // Get reference to the carousel container
        mCarouselContainer = (LinearLayout) findViewById(R.id.photoCarousel);

        // Start new album or load album if already exists
        setAlbumInfo();

        mRecordSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchSpeechTextIntent();
            }
        });
        mSaveAlbumEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(DEBUG_TAG, "Album ID: " + getAlbumId());
                Log.i(DEBUG_TAG, "Image Path:" + getCurrentPhotoPath());
                addNewEvent();
            }
        });


        // TODO: Needs to be refactored
        mImageBitmap = null;

        Button picBtn = (Button) findViewById(R.id.btnIntend);
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
                    handleBigCameraPhoto();
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
        //this.mCurrentImageView.setImageBitmap(null);

        Toast.makeText(getApplicationContext(),
                getString(R.string.album_event_saved),
                Toast.LENGTH_SHORT).show();
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
        mCurrentImageView.setImageBitmap(mImageBitmap);
        mCurrentImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );

        //mVideoView.setVideoURI(mVideoUri);
        //mVideoView.setVisibility(
        //        savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ?
        //                ImageView.VISIBLE : ImageView.INVISIBLE
        //);
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