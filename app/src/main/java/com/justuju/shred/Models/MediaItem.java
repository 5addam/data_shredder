package com.justuju.shred.Models;

public class MediaItem {

    private String imageTitle;
    private int imageId;

    public MediaItem(String imageTitle, int imageId) {
        this.imageTitle = imageTitle;
        this.imageId = imageId;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public int getImageId() {
        return imageId;
    }
}
