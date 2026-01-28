package com.justuju.shred.Utils;

import android.graphics.drawable.Drawable;

/**
 * Custom class for holding data of images on the device external storage
 */
public class PictureFacer {
    private String Name;
    private Drawable Icon;
    private Boolean type_img;
    private String Path;
    private String Size;
    private String Uri;
    private Boolean selected = false;

    public PictureFacer(){
        type_img = false;
    }

    public PictureFacer(String picturName, String picturePath, String pictureSize, String imageUri) {
        this.Name = picturName;
        this.Path = picturePath;
        this.Size = pictureSize;
        this.Uri = imageUri;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setIcon(Drawable icon) {
        Icon = icon;
    }

    public void setType_img(Boolean type_img) {
        this.type_img = type_img;
    }

    public Boolean getType_img() {
        return type_img;
    }

    public Drawable getIcon() {
        return Icon;
    }

    public String getName() {
        return Name;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getPath() {
        return Path;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getSize() {
        return Size;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getUri() {
        return Uri;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
