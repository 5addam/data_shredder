package com.justuju.shred;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.justuju.shred.Adapters.MediaFolderAdapter;
import com.justuju.shred.Models.StorageItem;
import com.justuju.shred.Utils.Folder;
import com.justuju.shred.Utils.ItemClickListener;
import com.justuju.shred.Utils.UtilConvertor;

import java.io.File;
import java.util.ArrayList;

public class LoadFoldersActivity extends BaseActivity implements ItemClickListener {
    String[] moduleTypes = {};
    RecyclerView folderRecycler;
    TextView empty, txt_totalMediaSize, txt_folderCount, txt_filesCount;
    private String selectedModule = "";
    private ArrayList<Folder> allFolders;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_folders);

        if (getIntent() != null) {
            selectedModule = getIntent().getStringExtra("module");
//            getSupportActionBar().setTitle(selectedModule);
            setTitle(selectedModule);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        empty = findViewById(R.id.empty);
        txt_totalMediaSize = findViewById(R.id.txt_totalMediaSize);
        txt_folderCount = findViewById(R.id.txt_noOfFolders);
        txt_filesCount = findViewById(R.id.txt_noOfFiles);

        folderRecycler = findViewById(R.id.base_recycler_view);
//        folderRecycler.hasFixedSize();
        folderRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        moduleTypes = getResources().getStringArray(R.array.media_titles);  //{'images','audios','videos','documents','contacts','messages'}

//        folderRecycler.addItemDecoration(new MarginDecoration(this));

//        new LoadDataFromStorage().execute();
        allFolders = GetAllDataFromStorage();
        if (allFolders != null) {
            long totalDataSize = 0;
            int totalFiles = 0;
            if (allFolders.isEmpty()) {
                empty.setVisibility(View.VISIBLE);
            } else {
                empty.setVisibility(View.GONE);
            }
            for (Folder folder : allFolders) {
                totalDataSize += folder.getFolderSize();
                totalFiles += folder.getNumberOfFiles();
            }
            RecyclerView.Adapter folderAdapter = new MediaFolderAdapter(allFolders, LoadFoldersActivity.this, LoadFoldersActivity.this);
            folderRecycler.setAdapter(folderAdapter);
            txt_totalMediaSize.setText(getResources().getString(R.string.total_size) + " " + UtilConvertor.getSize(totalDataSize)); // converting total size of folder into readable form
            txt_filesCount.setText(getResources().getString(R.string.number_of_files) + " " + totalFiles);
            txt_folderCount.setText(getResources().getString(R.string.number_of_folders) + " " + allFolders.size());
        }
    }


    private ArrayList<Folder> GetWhatsAppFolders() {
        ArrayList<String> picPaths = new ArrayList<>();
        ArrayList<Folder> picFolders = new ArrayList<>();
        String whatsAppDabasepath = "/storage/emulated/0/WhatsApp/Databases/";
        String whatsAppBackuppath = "/storage/emulated/0/WhatsApp/Backups/";

        String path = whatsAppDabasepath;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            Folder folds = new Folder();
            String folder = "Databases";
            String datapath = files[i].getAbsolutePath();
            String mediaSize = files[i].length() + "";

            if (!picPaths.contains(whatsAppDabasepath)) {
                picPaths.add(whatsAppDabasepath);
                folds.setPath(whatsAppDabasepath);
                folds.setFolderName(folder);
                folds.setFirstPic(R.drawable.ic_folder_filled);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                folds.addpics();
                folds.setFolderSize(Long.parseLong(mediaSize));
                picFolders.add(folds);
            } else {
                for (int k = 0; k < picFolders.size(); k++) {
                    if (picFolders.get(k).getPath().equals(whatsAppDabasepath)) {
                        picFolders.get(k).setFirstPic(R.drawable.ic_folder_filled);
                        picFolders.get(k).addpics();
                        picFolders.get(k).setFolderSize(Long.parseLong(mediaSize));
                    }
                }
            }
        }

        return picFolders;
    }

    /**
     * 1
     *
     * @return gets all folders with pictures on the device and loads each of them in a custom object imageFolder
     * the returns an ArrayList of these custom objects
     */
    private ArrayList<Folder> GetAllDataFromStorage() {
        ArrayList<Folder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri contentURI = null;
        String[] projection = null;
        String whereClause = "";
        String DisplayNameColumn = "";
        String DataColumn = "";
        String MimeTypeColumn = "";
        String DisplayBucketName = "";
        String FileSizeColumn = "";
        String orderBy = "";

        if (selectedModule.equals(moduleTypes[3])) { //moduleTypes[3] = "Documents"
            contentURI = MediaStore.Files.getContentUri("external");
            DisplayNameColumn = MediaStore.Files.FileColumns.DISPLAY_NAME;
            DataColumn = MediaStore.Files.FileColumns.DATA;
            MimeTypeColumn = MediaStore.Files.FileColumns.MIME_TYPE;
            DisplayBucketName = MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME;

            FileSizeColumn = MediaStore.Files.FileColumns.SIZE;

            whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN ('application/pdf' , 'text/plain','text/html' , " +
                    "'application/msword' , 'application/vnd.ms-excel' , 'application/mspowerpoint' , 'application/zip')";


            orderBy = MediaStore.Files.FileColumns.SIZE + " DESC";

        } else if (selectedModule.equals(moduleTypes[0])) {  //moduleTypes[0] = "Images"
            contentURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            DisplayNameColumn = MediaStore.Images.ImageColumns.DISPLAY_NAME;
            DataColumn = MediaStore.Images.ImageColumns.DATA;
            MimeTypeColumn = MediaStore.Images.ImageColumns.MIME_TYPE;
            DisplayBucketName = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;

            FileSizeColumn = MediaStore.Images.ImageColumns.SIZE;

            orderBy = MediaStore.Images.ImageColumns.SIZE + " DESC";
        } else if (selectedModule.equals(moduleTypes[1])) //moduleTypes[1] = "Audios"
        {
            contentURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            DisplayNameColumn = MediaStore.Audio.AudioColumns.DISPLAY_NAME;
            DataColumn = MediaStore.Audio.AudioColumns.DATA;
            MimeTypeColumn = MediaStore.Audio.AudioColumns.MIME_TYPE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) //BUCKET_DISPLAY_NAME is added in API 29 and won't work for APIs below 29
                DisplayBucketName = MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME;
            else
                DisplayBucketName = MediaStore.Audio.AudioColumns.DATA;
            FileSizeColumn = MediaStore.Audio.AudioColumns.SIZE;

            orderBy = MediaStore.Audio.AudioColumns.SIZE + " DESC";
        } else if (selectedModule.equals(moduleTypes[2])) //moduleTypes[2] = "Videos"
        {
            contentURI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            DisplayNameColumn = MediaStore.Video.VideoColumns.DISPLAY_NAME;
            DataColumn = MediaStore.Video.VideoColumns.DATA;
            MimeTypeColumn = MediaStore.Video.VideoColumns.MIME_TYPE;
            DisplayBucketName = MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME;
            FileSizeColumn = MediaStore.Video.VideoColumns.SIZE;

            orderBy = MediaStore.Video.VideoColumns.SIZE + " DESC";
        }

        projection = new String[]{DataColumn, DisplayNameColumn,
                DisplayBucketName, FileSizeColumn};


        Cursor cursor = this.getContentResolver().query(contentURI, projection, whereClause, null, orderBy);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                //cursor.moveToFirst();
                do {

                    Folder folds = new Folder();
                    String folder = "";
                    if (selectedModule.equals(moduleTypes[1]) && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                        //if media type is audios and sdk version <API 29 then get folder name from file path
                        folder = new File((cursor.getString(cursor.getColumnIndexOrThrow(DataColumn)))).getParentFile().getName();
                    } else {
                        folder = cursor.getString(cursor.getColumnIndexOrThrow(DisplayBucketName));
                    }
                    String datapath = cursor.getString(cursor.getColumnIndexOrThrow(DataColumn));
                    String mediaSize = cursor.getString(cursor.getColumnIndexOrThrow(FileSizeColumn));

                    String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder + "/"));
                    folderpaths = folderpaths + folder + "/";
                    if (!picPaths.contains(folderpaths)) {
                        picPaths.add(folderpaths);
                        folds.setPath(folderpaths);
                        folds.setFolderName(folder);
                        folds.setFirstPic(R.drawable.ic_folder_filled);
                        folds.addpics();
                        folds.setFolderSize(Long.parseLong(mediaSize));
                        picFolders.add(folds);
                    } else {
                        for (int i = 0; i < picFolders.size(); i++) {
                            if (picFolders.get(i).getPath().equals(folderpaths)) {
                                if (selectedModule.equals(moduleTypes[0])) //moduleTypes[0] = "Images"
                                {
                                    folds.setFirstPic(R.drawable.ic_folder_filled);
                                } else {
                                    folds.setFirstPic(R.drawable.ic_folder_filled);
                                }
                                picFolders.get(i).addpics();
                                picFolders.get(i).setFolderSize(Long.parseLong(mediaSize));
                            }
                        }
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
            e.printStackTrace();
        }
        for (int i = 0; i < picFolders.size(); i++) {
            Log.d("folders", picFolders.get(i).getFolderName() + " and path = " + picFolders.get(i).getPath() + " " + picFolders.get(i).getNumberOfFiles() + "And folder size = " + " " + UtilConvertor.getSize(picFolders.get(i).getFolderSize()));
        }

        return picFolders;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        new LoadDataFromStorage().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectAllItem(MenuItem menuItem) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

//
//    @Override
//    public void onPicClicked(PicHolder holder, int position, List<StorageItem> pics) {
//    }

    @Override
    public void onItemCheck(StorageItem storageItems) {

    }

    @Override
    public void onPicClicked(Folder folder) {
        Intent move = new Intent(LoadFoldersActivity.this, FolderDetailActivity.class);
        move.putExtra("folderPath", folder.getPath());
        move.putExtra("folderName", folder.getFolderName());
        move.putExtra(HomeActivity.EXTRA_MODULE, selectedModule);
        //move.putExtra("recyclerItemSize",getCardsOptimalWidth(4));

        startActivity(move);

    }
}