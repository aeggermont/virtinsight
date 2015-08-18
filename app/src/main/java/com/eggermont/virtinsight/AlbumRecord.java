package com.eggermont.virtinsight;

/**
 * Created by aae on 8/15/15.
 *
 * This class is used to tag album entries with specific
 * information about the alnum
 *
 */
public class AlbumRecord {

    private String albumName;
    private long albumId;

    public AlbumRecord(long albumId, String alnumName ){
        this.albumId = albumId;
        this.albumName = alnumName;
    }

    public String getAlbumName(){
        return this.albumName;
    }

    public long getAlbumId(){
        return this.albumId;
    }

    @Override
    public String toString(){
        return String.format("ID: " + this.albumId + " : " + this.albumName);
    }

}
