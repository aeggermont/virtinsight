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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class AlbumEvent extends AlbumTrackerActivity {

    private static final String DEBUG_TAG = AlbumEvent.class.getCanonicalName();


    // Call back ids
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int ACTION_TAKE_PHOTO_S = 2;
    private static final int ACTION_TAKE_VIDEO = 3;
    private static final int ACTION_RECORD_SPEECH = 100;


    // Configuration settings
    private static final String BITMAP_STORAGE_KEY = "viewAlbumBitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imagealbumviewvisibility";

    private static final String VIDEO_STORAGE_KEY = "viewvideo";
    private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
    private VideoView mVideoView;

    private Uri mVideoUri;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    // UI Widgets
    private ImageView mImageView1;
    private TextView mEditText;
    private Bitmap mImageBitmap;
    private Button mRecordSpeech;
    private Button mSaveAlbumEvent;
    private TextView mTxtSpeechInput;
    private TextView mTextViewAlbumName;

    /* Photo album for this application */
    private String getAlbumName() {

        return getString(R.string.album_name);
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
     * Sets the absolute path of for an image
     * @return reference to te file object
     * @throws IOException
     */
    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        Log.i(DEBUG_TAG, "Current photo path: " + mCurrentPhotoPath);

        return f;
    }

    /**
     * There isn't enough memory to open up more than a couple camera photos
     * so pre-scale the target bitmap into which the file is decoded
     */
    private void setPic() {

		/* Get the size of the ImageView */
        int targetW = mImageView1.getWidth();
        int targetH = mImageView1.getHeight();

        // Lets try to view the file in its original size
        Log.i(DEBUG_TAG, "Original IamegView width:  " +  targetW);
        Log.i(DEBUG_TAG, "Original IamegView height:  " + targetH);

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.i(DEBUG_TAG, "Original Photo width:  " +  photoW);
        Log.i(DEBUG_TAG, "Original Photo height:  " + photoH);

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        Log.i(DEBUG_TAG, "Sacling by factor of: " + scaleFactor );

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView1.setImageBitmap(bitmap);
        mVideoUri = null;
        mImageView1.setVisibility(View.VISIBLE);
        //mImageViewTest.setVisibility(View.INVISIBLE);
    }

    /**
     *
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

        Log.i(DEBUG_TAG, ">> Path for media file: " +  mCurrentPhotoPath.toString());

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
     * @param intent
     */
    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        mImageView1.setImageBitmap(mImageBitmap);
        mVideoUri = null;
        mImageView1.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.INVISIBLE);
    }

    /**
     *
     */
    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {

            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    /**
     * Disabling camera reocrdings for now
     * @param intent
     */
    private void handleCameraVideo(Intent intent) {
        //mVideoUri = intent.getData();
        //mVideoView.setVideoURI(mVideoUri);
        //mImageBitmap = null;
        //mVideoView.setVisibility(View.VISIBLE);
        mImageView1.setVisibility(View.INVISIBLE);
    }



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


    public void rec_speech() {
        Log.i(DEBUG_TAG, "Recording speech ...  ");
        //Log.i(DEBUG_TAG, "Album ID: " + this.albumId);
        promptSpeechInput();
    }

    /**
     * Showing google speech input dialog
     * TODO: Need to check if the intent is available first
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, ACTION_RECORD_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_events);

        mImageView1 = (ImageView) findViewById(R.id.imageView1);
        mTextViewAlbumName = (TextView) findViewById(R.id.TextAlbumName);

        // Getting album info from previous activity
        // Intent albumInt = getIntent();
        // String albumName = albumInt.getExtras().getString("albumName");
        // String albumDesc = albumInt.getExtras().getString("albumDesc");

        // Log.i(DEBUG_TAG, "Album name: " + albumName);
        // Log.i(DEBUG_TAG, "Album description: " + albumDesc);

        /**
         * Handling speech recording
         */
        mTxtSpeechInput = (EditText) findViewById(R.id.txtSpeechInput);
        mTxtSpeechInput.setVisibility(EditText.INVISIBLE);


        mRecordSpeech = (Button) findViewById(R.id.ButtonRecordSpeech);
        //inal Button addEvent = (Button) findViewById(R.id.ButtonSaveAlbumEvent);

        mRecordSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rec_speech();
            }
        });

        /**
         * Handling photo recordings
         */
        //mVideoView = (VideoView) findViewById(R.id.videoView1);
        mImageBitmap = null;
        //mVideoUri = null;


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

            case ACTION_TAKE_PHOTO_S: {
                if (resultCode == RESULT_OK) {
                    handleSmallCameraPhoto(data);
                }
                break;
            } // ACTION_TAKE_PHOTO_S

            case ACTION_TAKE_VIDEO: {
                if (resultCode == RESULT_OK) {
                    handleCameraVideo(data);
                }
                break;
            } // ACTION_TAKE_VIDEO
            case ACTION_RECORD_SPEECH:{
                if (resultCode == RESULT_OK && null != data ){
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mTxtSpeechInput.setText(result.get(0));
                    mTxtSpeechInput.setVisibility(EditText.VISIBLE);
                }
                break;
            }// ACTION_RECORD_SPEECH
        } // switch
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
        mImageView1.setImageBitmap(mImageBitmap);
        mImageView1.setVisibility(
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