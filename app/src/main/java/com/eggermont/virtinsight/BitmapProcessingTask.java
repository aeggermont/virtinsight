package com.eggermont.virtinsight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.io.File;
import java.lang.ref.WeakReference;


/**
 * Created by Antonio Eggermont  on 8/9/15.
 */
public class BitmapProcessingTask extends AsyncTask<String, Void, Bitmap> {

    private static final String DEBUG_TAG = BitmapProcessingTask.class.getCanonicalName();
    private final WeakReference<ImageView> imageViewReference;
    private int data = 0;
    private String mCurrentPhotoPath;
    int reqWidth = 0;
    int reqHeight = 0;

    public BitmapProcessingTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    /**
     * Decode photo image in the backgroud
     */
    @Override
    protected Bitmap doInBackground(String... params) {

        mCurrentPhotoPath = params[0];
        Log.i(DEBUG_TAG, "Processing image in the background: " + mCurrentPhotoPath);
        return processImage();
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.i(DEBUG_TAG, "DONE Processing image in the background");

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }


    /**
     * This method computes sampling factor to subsample an image to
     * load a smaller version into memory for display preview purposes
     *
     * @param options
     * @param reqWidth desired width for the subsampled image
     * @param reqHeight desired height for the subsampled image
     * @return samplicator to decode an image
     */
    public static int computeSubsamplingSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight){

        int inSampleSize = 1;
        final int height = options.outHeight;
        final int width = options.outWidth;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }

    /**
     * This method scales down a captured image photo into
     * a target photo image size to display in the UI in order
     * to save memory resources.
     */
    private Bitmap processImage() {

        final ImageView imageView = imageViewReference.get();

		/* Get the size of the ImageView */
        // TODO: These settings are not getting inherited from AlbumEvent so hardcoding for now
        int targetPhotoWidth = 548;
        int targetPhotoHeight = 548;

        int scaleFactor;

        Log.i(DEBUG_TAG, "Original IamegView width:  " +  targetPhotoWidth);
        Log.i(DEBUG_TAG, "Original IamegView height:  " + targetPhotoHeight);

        // Get the size of the row image
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        int rawPhotoWidth = bmOptions.outWidth;
        int rawPhotoHeight = bmOptions.outHeight;

        Log.i(DEBUG_TAG, "Original Photo width:  " +  rawPhotoWidth);
        Log.i(DEBUG_TAG, "Original Photo height:  " + rawPhotoHeight);

        scaleFactor = computeSubsamplingSize(bmOptions, targetPhotoWidth, targetPhotoHeight);
        Log.i(DEBUG_TAG, "Sacling by factor of: " + scaleFactor );

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        // Decode photo file into a subsampled image
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        return bitmap;

    }
}
