package com.justuju.shred;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.justuju.shred.Adapters.MediaItemAdapter;
import com.justuju.shred.Models.MediaItem;
import com.justuju.shred.Utils.PermissionCallback;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


import static android.os.Build.VERSION.SDK_INT;

public class
HomeActivity extends BaseActivity implements MediaItemAdapter.OnItemClickListener {
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 100;
    public static final String EXTRA_MODULE = "module";
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private final int images_ids[] = {
            R.drawable.ic_image_filled,
            R.drawable.ic_audio_filled,
            R.drawable.ic_video_filled,
            R.drawable.ic_document_filled,
//            R.drawable.ic_contact_filled,
//            R.drawable.ic_message_filled
    };

    private Intent intent = null;

    private String[] image_titles = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        set activity title and back button
        setTitle(getResources().getString(R.string.media_files));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private ArrayList<MediaItem> prepareData() {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        for (int i = 0; i < images_ids.length; i++) {
            mediaItems.add(new MediaItem(image_titles[i], images_ids[i]));
        }
        return mediaItems;
    }

    //    initialize activity view
    private void init() {
        image_titles = getResources().getStringArray(R.array.media_titles);

        RecyclerView recyclerView = findViewById(R.id.base_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<MediaItem> mediaItems = prepareData();
        MediaItemAdapter mediaItemAdapter = new MediaItemAdapter(mediaItems, getApplicationContext());
        recyclerView.setAdapter(mediaItemAdapter);
        mediaItemAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(MediaItem mediaItem) {
        if (mediaItem.getImageTitle().equals(image_titles[0])) {
            intent = new Intent(HomeActivity.this, LoadFoldersActivity.class);
            intent.putExtra(EXTRA_MODULE, image_titles[0]); //image_titles[0] = "Images"
            requestStoragePermission(intent);
        } else if (mediaItem.getImageTitle().equals(image_titles[1])) {
            intent = new Intent(HomeActivity.this, LoadFoldersActivity.class);
            intent.putExtra(EXTRA_MODULE, image_titles[1]); //image_titles[1] = "Audios"
            requestStoragePermission(intent);
        } else if (mediaItem.getImageTitle().equals(image_titles[2])) {
            intent = new Intent(HomeActivity.this, LoadFoldersActivity.class);
            intent.putExtra(EXTRA_MODULE, image_titles[2]); //image_titles[2] = "Videos"
            requestStoragePermission(intent);
        } else if (mediaItem.getImageTitle().equals(image_titles[3])) {
            intent = new Intent(HomeActivity.this, LoadFoldersActivity.class);
            intent.putExtra(EXTRA_MODULE, image_titles[3]); //image_titles[0] = "Documents"
            requestStoragePermission(intent);
        } else if (mediaItem.getImageTitle().equals(image_titles[4])) {
            Toast.makeText(HomeActivity.this, R.string.feature_not_available, Toast.LENGTH_SHORT).show();
        } else if (mediaItem.getImageTitle().equals(image_titles[5])) {
            Toast.makeText(HomeActivity.this, R.string.feature_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectAllItem(MenuItem selectAll) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    //    delete phone contacts
//    private class DeleteContacts extends AsyncTask<Void, Void, Void> {
//        final Map<String, Uri> contacts = new HashMap<>();
//        final ContentResolver contentResolver = HomeActivity.this.getContentResolver();
//        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//
//        String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND "
//                + ContactsContract.Data.MIMETYPE + " = ?";
//
//        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
//
//        private ProgressDialog progressDialog;
//        private int MaxProgressSize = 100;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(HomeActivity.this);
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.setMessage("Please wait...");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            progressDialog.setMax(100);
//            progressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    cursor.moveToFirst();
//                    do {
//
//                        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
//                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
//                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID));
//                        contacts.put(id, uri);
//                        try {
//
////                            Updating Contacts name and number
//                            String[] nameParams = new String[]{id,
//                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
//                            String[] numberParams = new String[]{id,
//                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
//
////                            update name
//                            ops.add(android.content.ContentProviderOperation.newUpdate(
//                                    android.provider.ContactsContract.Data.CONTENT_URI)
//                                    .withSelection(where, nameParams)
//                                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
//                                            "f").build());
////                            update number
//                            ops.add(android.content.ContentProviderOperation.newUpdate(
//                                    android.provider.ContactsContract.Data.CONTENT_URI)
//                                    .withSelection(where, numberParams)
//                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "11111111")
//                                    .build());
//
//                            try {
//                                contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
//                            } catch (OperationApplicationException e) {
//                                Log.d("Contact Update", e.getMessage() + "");
//                                e.printStackTrace();
//                            } catch (RemoteException e) {
//                                Log.d("Contact Update", e.getMessage() + "");
//                                e.printStackTrace();
//                            }
//                        } catch (Exception e) {
//                            Log.d("Contact Update", e.getMessage());
//                            e.printStackTrace();
//                        }
////                        Deleting contact
//                        contentResolver.delete(uri, null, null);
//
//                    } while (cursor.moveToNext());
//
//                }
//            } catch (Exception e) {
//                Log.d("Exception", e.getMessage());
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            progressDialog.dismiss();
//            Toast.makeText(HomeActivity.this, "All Contacts Deleted!!", Toast.LENGTH_SHORT).show();
//
//        }
//    }


    private void requestStoragePermission(final Intent intent) {
        if (hasPermission(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE})) {
            startActivity(intent);
            return;
        }
        requestPermission(HomeActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                new PermissionCallback() {
                    @Override
                    public void onPermissionDenied(String[] permissions) {
                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.storage_read_write_permissions), Snackbar.LENGTH_LONG)
                                .setAction(getResources().getString(R.string.ok),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                requestStoragePermission(intent);
                                            }
                                        }).setActionTextColor(getResources().getColor(R.color.white)).show();
                    }

                    @Override
                    public void onPermissionGranted(String[] toArray) {
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionBlocked(String[] toArray) {

                    }
                });
    }

//    private void requestContactsPermission() {
//        if (hasPermission(Manifest.permission.READ_CONTACTS) &&
//                hasPermission(Manifest.permission.WRITE_CONTACTS)) {
//            new MaterialAlertDialogBuilder(this)
//                    .setTitle(getResources().getString(R.string.delete_contacts))
//                    .setMessage(getResources().getString(R.string.delete_contacts_message))
//                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            new DeleteContacts().execute();
//                        }
//                    })
//                    .setNegativeButton(getResources().getString(R.string.no), null)
//                    .show();
//            return;
//        }
//        requestPermission(HomeActivity.this, new String[]{Manifest.permission.READ_CONTACTS,
//                        Manifest.permission.WRITE_CONTACTS},
//                new PermissionCallback() {
//                    @Override
//                    public void onPermissionDenied(String[] permissions) {
//                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.contacts_read_write_permission),
//                                Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.ok),
//                                new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        requestContactsPermission();
//                                    }
//                                }).setActionTextColor(getResources().getColor(R.color.white)).show();
//                    }
//
//                    @Override
//                    public void onPermissionGranted(String[] toArray) {
//                        new MaterialAlertDialogBuilder(HomeActivity.this)
//                                .setTitle(getResources().getString(R.string.delete_contacts))
//                                .setMessage(getResources().getString(R.string.delete_contacts_message))
//                                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        new DeleteContacts().execute();
//                                    }
//                                })
//                                .setNegativeButton(getResources().getString(R.string.no), null)
//                                .show();
//                    }
//
//                    @Override
//                    public void onPermissionBlocked(String[] toArray) {
//                    }
//                });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    startActivity(intent);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.storage_read_write_permissions), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.ok),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            requestStoragePermission(intent);
                                        }
                                    }).setActionTextColor(getResources().getColor(R.color.white)).show();
                }
            }
        }
    }
}