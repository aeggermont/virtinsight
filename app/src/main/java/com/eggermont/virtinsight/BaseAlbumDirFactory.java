package com.eggermont.virtinsight;

/**
 * Created by aae on 8/3/15.
 */

import java.io.File;
import android.os.Environment;


public class BaseAlbumDirFactory extends AlbumStorageDirFactory {

    // Setting standard storage location for digital camera files
    private static final String CAMERA_DIR = "/dcim/";

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File (
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }

}
