package com.justuju.shred.Models;

import android.graphics.drawable.Drawable;

public class MainItemModel {
    private Drawable itemImage;
    private String itemTitle;

    public MainItemModel(Drawable itemImage, String itemTitle) {
        this.itemImage = itemImage;
        this.itemTitle = itemTitle;
    }

    public Drawable getItemImage() {
        return itemImage;
    }

    public String getItemTitle() {
        return itemTitle;
    }
}
