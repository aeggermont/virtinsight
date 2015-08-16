package com.eggermont.virtinsight;

/**
 *
 */
public class Event {

    private String mediaPath;
    private long eventId;
    private int targetWidth;
    private int targetHeight;

    public Event(long eventId, String mediaPath){
        this.mediaPath = mediaPath;
        this.eventId = eventId;
    }

    public void setMediaPath(String path){
        this.mediaPath = path;
    }

    public void setEventId(long id){
        this.eventId = id;
    }

    public void setTargetDimenstions(int width, int height){
        this.targetWidth = width;
        this.targetHeight = height;
    }

    public long getEventId(){
        return this.eventId;
    }

    public String getMediaPath(){
        return this.mediaPath;
    }

    @Override
    public String toString(){
        return String.format("ID: " + this.eventId + " : " + this.mediaPath);
    }

}
