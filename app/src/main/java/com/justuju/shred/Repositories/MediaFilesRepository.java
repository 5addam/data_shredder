package com.justuju.shred.Repositories;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.justuju.shred.AsyncTasks.AppExecutors;
import com.justuju.shred.Models.StorageItem;
import com.justuju.shred.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;

public class MediaFilesRepository {

    private MutableLiveData<List<StorageItem>> mutableLiveData;
    private List<StorageItem> listAllFiles;
    Context context;

    private static MediaFilesRepository instance;

    private String[] moduleTypes = {};

    private LoadMediaFiles loadMediaFiles;

    public static MediaFilesRepository getInstance() {
        if (instance == null) {
            instance = new MediaFilesRepository();
        }
        return instance;
    }

    private MediaFilesRepository() {
        mutableLiveData = new MutableLiveData<>();
        listAllFiles = new ArrayList<>();
    }

    public LiveData<List<StorageItem>> getMediaFiles() {
        return mutableLiveData;
    }

    public void loadMediaFiles(String folderPath, String selectedModule, Context context) {
        if (loadMediaFiles != null)
            loadMediaFiles = null;
        if (mutableLiveData != null)
            mutableLiveData = new MutableLiveData<>();
        if (listAllFiles != null)
            listAllFiles = new ArrayList<>();

        loadMediaFiles = new LoadMediaFiles(folderPath, selectedModule, context);
        final Future myHandler = AppExecutors.getInstance().getExecutorService().submit(loadMediaFiles);
    }

    public void selectAll() {
        for (StorageItem item : listAllFiles) {
            item.setSelected(true);
            mutableLiveData.postValue(listAllFiles);
        }
    }

    public void unSelectAll() {
        for (StorageItem item : listAllFiles) {
            item.setSelected(false);
            mutableLiveData.postValue(listAllFiles);
        }
    }


    private class LoadMediaFiles implements Runnable {

        private String folderPath;
        private String selectedModule;
        Context context;

        public LoadMediaFiles(String folderPath, String selectedModule, Context context) {
            this.folderPath = folderPath;
            this.selectedModule = selectedModule;
            this.context = context;
            if (moduleTypes != null)
                moduleTypes = context.getResources().getStringArray(R.array.media_titles);  //{'images','audios','videos','documents','contacts','messages'}
        }

        @Override
        public void run() {
            try {
                listAllFiles = getFolderSubData();
                mutableLiveData.postValue(listAllFiles);
            } catch (Exception e) {
                System.out.println("Unable to load the files");
                mutableLiveData.postValue(null);
            }

        }

        public ArrayList<StorageItem> getFolderSubData() {
            ArrayList<StorageItem> images = new ArrayList<>();

            Uri contentURI = null;
            String[] projection = null;
            String whereClause = "";
            String DisplayNameColumn = "";
            String DataColumn = "";
            String MimeTypeColumn = "";
            String DisplayBucketName = "";
            String FileSizeColumn = "";

            if (selectedModule.equals(moduleTypes[3])) { //moduleTypes[3] = "Documents"
                contentURI = MediaStore.Files.getContentUri("external");
                DisplayNameColumn = MediaStore.Files.FileColumns.DISPLAY_NAME;
                DataColumn = MediaStore.Files.FileColumns.DATA;
                MimeTypeColumn = MediaStore.Files.FileColumns.MIME_TYPE;
                DisplayBucketName = MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME;
                FileSizeColumn = MediaStore.Files.FileColumns.SIZE;

                whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN (" +
                        "'application/pdf' , " +
                        "'text/plain'," +
                        "'text/html' , " +
                        "'application/msword' , " +
                        "'application/vnd.ms-excel' , " +
                        "'application/mspowerpoint' ," +
                        "'application/zip') AND " + MediaStore.Files.FileColumns.DATA + " like ? ";

            } else if (selectedModule.equals(moduleTypes[0])) {  //moduleTypes[0] = "Images"
                contentURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                DisplayNameColumn = MediaStore.Images.ImageColumns.DISPLAY_NAME;
                DataColumn = MediaStore.Images.ImageColumns.DATA;
                MimeTypeColumn = MediaStore.Images.ImageColumns.MIME_TYPE;
                DisplayBucketName = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
                FileSizeColumn = MediaStore.Images.ImageColumns.SIZE;
                whereClause = MediaStore.Images.ImageColumns.DATA + " like ? ";
            } else if (selectedModule.equals(moduleTypes[1])) {  //moduleTypes[1] = "Audios"
                contentURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                DisplayNameColumn = MediaStore.Audio.AudioColumns.DISPLAY_NAME;
                DataColumn = MediaStore.Audio.AudioColumns.DATA;
                MimeTypeColumn = MediaStore.Audio.AudioColumns.MIME_TYPE;
                DisplayBucketName = MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME;
                FileSizeColumn = MediaStore.Audio.AudioColumns.SIZE;
                whereClause = MediaStore.Audio.AudioColumns.DATA + " like ? ";
            } else if (selectedModule.equals(moduleTypes[2])) //moduleTypes[2] = "Videos"
            {
                contentURI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                DisplayNameColumn = MediaStore.Video.VideoColumns.DISPLAY_NAME;
                DataColumn = MediaStore.Video.VideoColumns.DATA;
                MimeTypeColumn = MediaStore.Video.VideoColumns.MIME_TYPE;
                DisplayBucketName = MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME;
                FileSizeColumn = MediaStore.Video.VideoColumns.SIZE;
                whereClause = MediaStore.Video.VideoColumns.DATA + " like ? ";
            }

            projection = new String[]{DataColumn, DisplayNameColumn, FileSizeColumn};

            String orderBy = MediaStore.Files.FileColumns.SIZE + " DESC";

            Cursor cursor = context.getContentResolver()
                    .query(
                            contentURI,
                            projection,
                            whereClause,
                            new String[]{"%" + folderPath + "%"},
                            orderBy
                    );
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    cursor.moveToFirst();
                    do {
                        StorageItem pic = new StorageItem();

                        if (selectedModule.equals(moduleTypes[3])) { //using this method because MediaStore.Files.FileColumns.DISPLAY_NAME was returning null for Files
                            String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                            String[] pathArr = filePath.split("/");
                            pic.setName(pathArr[pathArr.length - 1]);  //name of the file
                        } else {
                            pic.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)));  //name of the file
                        }
                        pic.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))); //file path
                        pic.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))); //file size

                        if (selectedModule.equals(moduleTypes[0])) {
                            pic.setType_img(true);
                        }
                        pic.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_file));
                        images.add(pic);
                    } while (cursor.moveToNext());
                }

                cursor.close();
                ArrayList<StorageItem> reSelection = new ArrayList<>();
                for (int i = images.size() - 1; i > -1; i--) {
                    reSelection.add(images.get(i));
                }
                images = reSelection;
            } catch (Exception e) {
                Log.d("Exception", e.getMessage());
                e.printStackTrace();
            }

            // sorting arraylist in descending order by 'last modified'
            Collections.sort(images, new Comparator<StorageItem>() {
                @Override
                public int compare(StorageItem lhs, StorageItem rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return Long.compare(new File(rhs.getPath()).lastModified(), new File(lhs.getPath()).lastModified());
                }
            });
            return images;
        }

    }

}
