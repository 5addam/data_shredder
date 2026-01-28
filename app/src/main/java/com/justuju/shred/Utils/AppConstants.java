package com.justuju.shred.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.preference.PreferenceManager;

import com.justuju.shred.Helper.SharedPreferenceBooleanLiveData;

public class AppConstants {

    private static final Object MODE_PRIVATE = 1;
    private static SharedPreferences preferences;
    private static SharedPreferences mPrefs;


    private static final String internalUri = "content://com.android.externalstorage.documents/tree/primary%3A";

    private static AppConstants instance;
    private static SharedPreferenceBooleanLiveData sharedPreferenceBooleanLiveData;

    public static AppConstants getInstance(Context context) {
        if (instance == null)
            instance = new AppConstants();
//        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mPrefs = context.getSharedPreferences("", Context.MODE_PRIVATE);
        sharedPreferenceBooleanLiveData = new SharedPreferenceBooleanLiveData(preferences, "hidden", false);
        return instance;
    }

//    public AppConstants(Context context) {
//        this.context = context;
//        preferences = PreferenceManager.getDefaultSharedPreferences();
//        prefHiddenFiles = preferences.getBoolean("hidden", false);
//        deletionAlgo = preferences.getString("algorithm", "0");
//        deleteThumnail = preferences.getBoolean("delete_thumbnail", false);
//        deletionType = preferences.getString("type", "delete_files");
//        mPrefs = this.context.getSharedPreferences("", Context.MODE_PRIVATE);
//    }


    public boolean isPrefHidden() {
        return preferences.getBoolean("hidden", false);
    }


    public SharedPreferenceBooleanLiveData getSharedPrefs(){
        return sharedPreferenceBooleanLiveData;
    }

    public String getDeletionAlgo() {
        return preferences.getString("algorithm", "0");
    }

    public boolean isDeleteThumbnail() {
        return preferences.getBoolean("delete_thumbnail", false);
    }

    public String getDeletionType() {
        return preferences.getString("type", "delete_files");
    }


    public void setSaveTreeUri(Uri treeUri) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("treeUri", treeUri.toString()).apply();
    }

    public Uri getSaveTreeUri() {
//        SharedPreferences mPrefs = context.getSharedPreferences("", Context.MODE_PRIVATE);
        Uri uri = Uri.parse(mPrefs.getString("treeUri", ""));
        return uri;
    }

    public void setInternalTreeUri(Uri treeUri) {
//        SharedPreferences mPrefs = context.getSharedPreferences("", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("internalTreeUri", treeUri.toString()).apply();
    }

}
