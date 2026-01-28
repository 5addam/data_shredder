package com.justuju.shred.Models;

import android.graphics.drawable.Drawable;

public class AboutUsItem {
    private String itemtitle;
    private String itemType;
    private Drawable itemIcon;

    public AboutUsItem(String itemtitle, String itemType, Drawable itemIcon) {
        this.itemtitle = itemtitle;
        this.itemType = itemType;
        this.itemIcon = itemIcon;
    }

    public String getItemtitle() {
        return itemtitle;
    }

    public String getItemType() {
        return itemType;
    }

    public Drawable getItemIcon() {
        return itemIcon;
    }
}
