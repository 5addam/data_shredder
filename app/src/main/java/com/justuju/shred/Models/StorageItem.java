package com.justuju.shred.Models;

import android.graphics.drawable.Drawable;

public class StorageItem {
    private Drawable Icon;
    private String name;
    private String size;
    private String type;
    private String path;
    private boolean isSelected;
    private Boolean type_img;

    public StorageItem(){
        type_img = false;
    }
    public StorageItem(String name, String size, String type, String path) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Drawable getIcon() {
        return Icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setIcon(Drawable icon) {
        Icon = icon;
    }

    public Boolean getType_img() {
        return type_img;
    }

    public void setType_img(Boolean type_img) {
        this.type_img = type_img;
    }
}
