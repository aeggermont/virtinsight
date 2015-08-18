package com.eggermont.virtinsight;

import java.util.HashMap;

/**
 * This class is used to keep state of properties of
 * a media event additions for tagging in UI elements.
 * Data stored includes description, geolocation
 * information, insertion/update dates information. etc.
 */
public class EventTag {


    private long albumId;
    private String description;
    private String photo_path;
    private String gelocation_info;
    private String dateAdded;

    public EventTag(HashMap<String, String> eventAttributes){
        this.description = eventAttributes.get("description");
        this.albumId = Long.valueOf(eventAttributes.get("album_id"));
        this.photo_path = eventAttributes.get("photo_path");
        this.gelocation_info = eventAttributes.get("gelocation_info");
        this.dateAdded = eventAttributes.get("date_added");
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto_path() {
        return photo_path;
    }

    public void setPhoto_path(String photo_path) {
        this.photo_path = photo_path;
    }

    public String getGelocation_info() {
        return gelocation_info;
    }

    public void setGelocation_info(String gelocation_info) {
        this.gelocation_info = gelocation_info;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return "EventTag{" +
                "albumId=" + albumId +
                ", description='" + description + '\'' +
                ", photo_path='" + photo_path + '\'' +
                ", gelocation_info='" + gelocation_info + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                '}';
    }
}
