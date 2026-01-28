package com.justuju.shred.ViewModels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.justuju.shred.Models.StorageItem;
import com.justuju.shred.Repositories.MediaFilesRepository;

import java.util.List;

public class MediaFilesViewModel extends AndroidViewModel {

    private MediaFilesRepository mediaFilesRepository;
    private Context mContext;

    public MediaFilesViewModel(@NonNull Application application) {
        super(application);
        mediaFilesRepository = MediaFilesRepository.getInstance();
        mContext = application.getApplicationContext();
    }


    public LiveData<List<StorageItem>> getMediaFiles() {
        return mediaFilesRepository.getMediaFiles();
    }

    public void loadMediaFiles(String folderPath, String selectedModule) {
        mediaFilesRepository.loadMediaFiles(folderPath, selectedModule, mContext);
    }

    public void selectAll() {
        mediaFilesRepository.selectAll();
    }

    public void unSelectAll() {
        mediaFilesRepository.unSelectAll();
    }

}
