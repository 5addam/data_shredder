package com.justuju.shred.Repositories;

import android.content.Context;
import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.justuju.shred.AsyncTasks.AppExecutors;
import com.justuju.shred.Models.StorageItem;
import com.justuju.shred.Utils.AppConstants;
import com.justuju.shred.Utils.UtilConvertor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;

public class StorageRepository {

    private static StorageRepository instance;
    private AppConstants appConstants;
    private List<StorageItem> storageItems;


    private MutableLiveData<List<StorageItem>> mutableLiveData;
    private LoadDataFromStorage loadDataFromStorage;

    public static StorageRepository getInstance() {
        if (instance == null)
            instance = new StorageRepository();
        return instance;
    }

    private StorageRepository() {
        mutableLiveData = new MutableLiveData<>();
        storageItems = new ArrayList<>();
    }

    public LiveData<List<StorageItem>> getStorageData() {
        return mutableLiveData;
    }

    public void selectAll() {
        for (StorageItem item : storageItems) {
            item.setSelected(true);
            mutableLiveData.postValue(storageItems);
        }
    }

    public void unSelectAll() {
        for (StorageItem item : storageItems) {
            item.setSelected(false);
            mutableLiveData.postValue(storageItems);
        }
    }


    public void loadData(String path, Context context) {
        if (appConstants != null)
            appConstants = null;
        appConstants = AppConstants.getInstance(context);
        if (loadDataFromStorage != null)
            loadDataFromStorage = null;
        loadDataFromStorage = new LoadDataFromStorage(path);
        final Future myHandler = AppExecutors.getInstance().getExecutorService().submit(loadDataFromStorage);
    }

    //loading data from given path
    private class LoadDataFromStorage implements Runnable {
        private String path;
        File[] values;  //this will store all the files/folders of given path

        public LoadDataFromStorage(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            try {
                File f = new File(path);//converted string object to file
                values = f.listFiles();//getting the list of files in string array
                if (values == null) {
                    mutableLiveData.postValue(null);
                    return;
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Arrays.sort(values, Comparator.comparingLong(File::lastModified).reversed());
                } else {
                    Arrays.sort(values, new Comparator<File>() {
                        @Override
                        public int compare(File o1, File o2) {
                            return Long.compare(o2.lastModified(), o1.lastModified());
                        }
                    });
                }
                storageItems = initData(values);
                mutableLiveData.postValue(storageItems);

            } catch (Exception e) {
                System.out.println("Unable to load the files");
                mutableLiveData.postValue(null);

            }
        }

        //gets all the files and folder from given path and add them into arraylist of type StorageItem
        private List<StorageItem> initData(File[] files) {
            storageItems = new ArrayList<>();
            String type;
            String name;
            String file_size = "";
            double sizeMB = 0;
            String s = "";

            for (File file : files) {
                name = file.getName();

                if (file.isDirectory()) {
                    type = "Dir";
                    // no. of items in dir
                    File[] dirFiles = file.listFiles();
                    try {
                        file_size = dirFiles != null ? dirFiles.length + " items" : "0 items";
                    } catch (Exception e) {
//                        file_size = "0 items";
                        System.out.println(e.getMessage());
                    }
                } else {
                    type = "File";
                    //file size
                    file_size = UtilConvertor.readableFileSize(file.length());
                }

                //check whether to display hidden files/folders or not
                if (!appConstants.isPrefHidden()) {
                    if (!name.startsWith(".")) {   // ignore files which starts with (".")
                        storageItems.add(new StorageItem(file.getName(), file_size, type, file.getPath()));
                    }
                }
                if (appConstants.isPrefHidden()) {
                    storageItems.add(new StorageItem(file.getName(), file_size, type, file.getPath()));
                }
            }
            return storageItems;
        }
    }
}
