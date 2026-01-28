package com.justuju.shred.Utils;

public interface PermissionCallback {
    void onPermissionDenied(String[] permissions);

    void onPermissionGranted(String[] toArray);

    void onPermissionBlocked(String[] toArray);
}
