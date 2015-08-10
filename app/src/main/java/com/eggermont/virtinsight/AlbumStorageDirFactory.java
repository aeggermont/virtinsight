package com.eggermont.virtinsight;

/**
 * Created by aae on 8/3/15.
 */

import java.io.File;

abstract class AlbumStorageDirFactory{
    public abstract File getAlbumStorageDir(String albumName);
}

