package com.justuju.shred.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.justuju.shred.Models.StorageItem;
import com.justuju.shred.Repositories.StorageRepository;

import java.util.List;

public class StorageViewModel extends ViewModel {

    //This class is used for ViewModel
    private StorageRepository storageRepository;

    //Constructor
    public StorageViewModel(){storageRepository = StorageRepository.getInstance();}

    public LiveData<List<StorageItem>> getStorageData(){return  storageRepository.getStorageData();}

    public void loadData(String path, Context context){
        storageRepository.loadData(path,context);
    }

    public void selectAll(){storageRepository.selectAll();}

    public void unSelectAll(){storageRepository.unSelectAll();
    }


}
