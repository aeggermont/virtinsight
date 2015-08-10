package com.eggermont.virtinsight;

/**
 * Created by aae on 8/3/15.
 */

import java.io.File;
import android.os.Environment;


public class FroyoAlbumDirFactory extends AlbumStorageDirFactory{

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }
}


