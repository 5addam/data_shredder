package com.justuju.shred;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;

public class WhatsAppFolderActivity extends AppCompatActivity {

    RecyclerView folderRecycler;
    TextView empty,tv_total_data;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 100;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private String selectedModule = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_folder);
    }
}