package com.justuju.shred;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
//import android.hardware.biometrics.BiometricPrompt;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.justuju.shred.Adapters.StorageAdapter;
import com.justuju.shred.AsyncTasks.AppExecutors;
import com.justuju.shred.Helper.ItemClickSupport;
import com.justuju.shred.Helper.SharedPreferenceBooleanLiveData;
import com.justuju.shred.Models.StorageItem;
import com.justuju.shred.Utils.AppConstants;
import com.justuju.shred.Utils.AsyncResponse;
import com.justuju.shred.Utils.CustomProgressDialog;
import com.justuju.shred.Utils.PermissionCallback;
import com.justuju.shred.ViewModels.StorageViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import static android.os.Build.VERSION.SDK_INT;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class StorageActivity extends BaseActivity implements StorageAdapter.OnItemClickListener, View.OnClickListener, AsyncResponse {

    SharedPreferenceBooleanLiveData sharedPreferenceBooleanLiveData;

    public static String STORAGE_TYPE = "";
    RecyclerView recyclerView;
    ProgressBar progressBar;
    StorageAdapter adapter;
    List<StorageItem> storageItems; // contains all files of the current path
    List<StorageItem> selectedItems; // contains files selected to be shred
    List<Uri> selectedItemsUri; // contains the uri of selected file (only if the files are from external sd card)
    private String path;
    private String title;
    FloatingActionButton btn_shredd;
    AppConstants appSettings;   //Shared prefs settings
    AlertDialog customAlertDialog;
    Toast toast;
    private ShreddStorageData shreddStorageData;
    int itemPos = 0;
    boolean showHiddenFiles;
    TextView txtLoading;

    //ViewModel
    private StorageViewModel storageViewModel;

//    biometric implementation
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    Vibrator vibrator;


    String deletionAlgo;
    String deletionAlgoValue;
    String deletionType;
    String deletionTypeValue;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        vibrator = this.getSystemService(Vibrator.class);
        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(StorageActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication error:" + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                shredData();
                vibrator.vibrate(500);
                Toast.makeText(getApplicationContext(), "Authentication succeeded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Verification")
                .setSubtitle("Verify it's you")
                .setNegativeButtonText("Use pin")
                .build();


        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, 1001

                );
                break;
        }

        if (getIntent() != null) {
            path = getIntent().getStringArrayExtra("PATH")[0];  //contains path
            title = getIntent().getStringArrayExtra("PATH")[1];   // contains name of given path
        }

        //initialize UI components
        initView();

        //initialize recyclerview
        initRecyclerView();

        storageViewModel = new ViewModelProvider(this).get(StorageViewModel.class);

        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //loading data from given path
            storageViewModel.loadData(path, this);
        } else {
            requestStoragePermission("read");
        }

        //observe storage-data changes
        observeStorageData();

        showHiddenFiles = appSettings.isPrefHidden();

        observeSharedPrefs();
    }


    private void observeStorageData() {
        storageViewModel.getStorageData().observe(this, new Observer<List<StorageItem>>() {
            @Override
            public void onChanged(List<StorageItem> storageItemArrayList) {
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
                if (storageItemArrayList != null) {
                    storageItems.clear();
                    storageItems.addAll(storageItemArrayList);
                    //update recyclerview
                    updateAdapter();
                    setTitle(title);
                } else {
                    updateTitleAndPath();
                    Toast.makeText(StorageActivity.this, "Unable to load the files", Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void observeSharedPrefs() {
        sharedPreferenceBooleanLiveData.getBooleanLiveData("hidden", false).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (adapter != null && showHiddenFiles != aBoolean) {
                    storageViewModel.loadData(path, getApplicationContext());
                    showHiddenFiles = aBoolean;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setTitle(title); // set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //show back button on actionbar
        selectedItems.clear();
        selectedItemsUri.clear();

//        if (adapter != null) {
//            storageViewModel.loadData(path, this);
//        }

    }

    //    initialize activity view
    private void initView() {

        progressBar = findViewById(R.id.loader);
        txtLoading = findViewById(R.id.txt_loading);

        //shredding button
        btn_shredd = findViewById(R.id.btn_shredd);
        btn_shredd.setOnClickListener(this);

        selectedItems = new ArrayList<>();
        selectedItemsUri = new ArrayList<Uri>();

        storageItems = new ArrayList<>();

        //AppSetting
        appSettings = AppConstants.getInstance(this);

        customAlertDialog = CustomProgressDialog.getCustomDialog(this);

        storageItems = new ArrayList<>();
        selectedItems = new ArrayList<>();
        selectedItemsUri = new ArrayList<>();

        sharedPreferenceBooleanLiveData = appSettings.getSharedPrefs();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.base_recycler_view);
        recyclerView.setVisibility(View.GONE);
        adapter = new StorageAdapter(StorageActivity.this, StorageActivity.this, storageItems);
//        adapter.setOnItemClickListener(StorageActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if (new File(storageItems.get(position).getPath()).isDirectory()) {
                            recyclerView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            txtLoading.setVisibility(View.VISIBLE);
                            itemPos = position;
                            path = storageItems.get(position).getPath();
                            title = title + "/" + storageItems.get(position).getName();
                            storageViewModel.loadData(path, StorageActivity.this);

                        } else {
                            Toast.makeText(StorageActivity.this, "Double click to open the file", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onItemDoubleClicked(RecyclerView recyclerView, int position, View v) {
                        openFile(storageItems.get(position));
                    }
                });
    }

    private void shredData() {
        if (shreddStorageData != null)
            shreddStorageData = null;
        shreddStorageData = new ShreddStorageData(this, this, storageItems);
        final Future myHandler = AppExecutors.getInstance().getExecutorService().submit(shreddStorageData);
    }


    //    checks if at least one item's check box is not 'checked'
    private boolean ifAtleastOnUnSelected() {
        boolean unCheckedAvailable = false;
        for (StorageItem item : storageItems) {
            if (!item.isSelected()) {
                unCheckedAvailable = true;
//                break;
                break;
            }
        }
        return unCheckedAvailable;
    }

    //    checks if at least one storageItems item's check box is 'checked'
    private boolean ifAtleastOneSelected() {
        boolean checkedAvailable = false;
        for (StorageItem item : storageItems) {
            if (item.isSelected()) {
                checkedAvailable = true;
                break;
            }
        }
        return checkedAvailable;
    }


    @Override
    public void onBackPressed() {
        backPressed();
    }

    private void backPressed() {
        if (!title.contains("/")) { //if already in root dir, finish the activity
            super.onBackPressed();
//            finish();
        } else {
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            txtLoading.setVisibility(View.VISIBLE);
            updateTitleAndPath();
            storageViewModel.loadData(path, this);
            setTitle(title);
        }
    }

    private void updateTitleAndPath() {
        // remove the last item from path
        String[] pathArr = path.split("\\/");
        String[] newPathArr = {};
        newPathArr = Arrays.copyOf(pathArr, pathArr.length - 1);
        this.path = TextUtils.join("/", newPathArr);

        // remove the last item from title
        String[] titleArr = title.split("\\/");
        String[] newTitleArr = {};
        newTitleArr = Arrays.copyOf(titleArr, titleArr.length - 1);
        title = TextUtils.join("/", newTitleArr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        showSelectAll();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            backPressed();
        }
        selectAll.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_select_all));
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void selectAllItem(MenuItem selectAll) {

        if (ifAtleastOnUnSelected()) {
            //selecting all items of recyclerview
            selectAll();
            selectAll.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_done));
            setTitle(getResources().getString(R.string.selected_item) + "(" + storageItems.size() + ")"); //set title as the total no. of items selected
        } else {
            //un-selecting all items of recyclerview
            storageViewModel.unSelectAll();
            selectAll.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_select_all));
            setTitle(title); // set actionbar title
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //show back button on actionbar
        }

    }


    private void selectAll() {
        for (StorageItem item : storageItems) {
            item.setSelected(true);
        }
        updateAdapter();
    }

    public void unSelectAll() {
        for (StorageItem item : storageItems) {
            item.setSelected(false);
        }
        updateAdapter();

    }


    @Override
    public void onItemCheck(StorageItem storageItem) {
        if (storageItem.isSelected()) {
            storageItem.setSelected(false);
            selectedItems.remove(storageItem);
        } else {
            storageItem.setSelected(true);
            selectedItems.add(storageItem);
        }
//        int selectedItemsCount = getSelectedItemCount(); //get the count of no. of selected items
        if (selectedItems.size() > 0) {
            setTitle(getResources().getString(R.string.selected_item) + "(" + selectedItems.size() + ")");
        } else {
            setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_shredd) {
            if (ifAtleastOneSelected()) {  //if at least one item is selected, shred it
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (STORAGE_TYPE.toLowerCase().equals("external")) { // if the current dir is external sd card, then check if write permission is allowed or not
                                    if (appSettings.getSaveTreeUri().toString().equals("")) {  //returns the uri of the tree content external storage
                                        new AlertDialog.Builder(StorageActivity.this)
                                                .setTitle("No SD card permission")
                                                .setMessage("Please select the root directory SDCARD(/storage/sdcard1) of the external SD card to grant write permission for NCCS Shreddit")
                                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                                // The dialog is automatically dismissed when a dialog button is clicked.
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        openDirectory();
                                                    }
                                                })

                                                // A null listener allows the button to dismiss the dialog and take no further action.
                                                .setNegativeButton(R.string.no, null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    } else {
                                        customAlertDialog.show();
                                        biometricPrompt.authenticate(promptInfo);
//                                        shredData();
//
                                    }
                                } else {
//                                    customAlertDialog.show();
                                    biometricPrompt.authenticate(promptInfo);
//                                    shredData();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }

                };

                deletionType = appSettings.getDeletionType();
                deletionAlgo = appSettings.getDeletionAlgo();

                if (deletionType.equals("delete_files")){
                    deletionTypeValue = "Delete File";
                }
                else{
                    deletionTypeValue = "Scrap File Content";
                }


                switch (deletionAlgo) {
                    case "0":
                        deletionAlgoValue = this.getResources().getString(R.string.overwrite_zero);
                        break;
                    case "1":
                        deletionAlgoValue = this.getResources().getString(R.string.overwrite_one);
                        break;
                    case "random":
                        deletionAlgoValue = this.getResources().getString(R.string.overwrite_random);
                        break;
                    case "british_hmg":
                        deletionAlgoValue = this.getResources().getString(R.string.shredding_success_british_hmg);
                        break;
                    case "dod_3pass":
                        deletionAlgoValue = this.getResources().getString(R.string.dod_3pass);
                        break;
                    case "russian_gost":
                        deletionAlgoValue = this.getResources().getString(R.string.russian_gost);
                        break;
                    case "schneier_algo":
                        deletionAlgoValue = this.getResources().getString(R.string.schneier_algo);
                        break;
                    case "german_vsitr":
                        deletionAlgoValue = this.getResources().getString(R.string.german_vsitr);
                        break;
                }

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StorageActivity.this);
                builder.setMessage(getResources().getString(
                        R.string.shredding_message) + "\n\nShredding Algorithm: "+deletionAlgoValue+"\n\nDeletion Type: "+deletionTypeValue)
                        .setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();

            } else {
                vibrator.vibrate(200);
                Toast.makeText(StorageActivity.this, getResources().getString(R.string.select_item_message), Toast.LENGTH_LONG).show();
            }
        }

    }


    private void updateAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
//                    adapter.setData(storageItems);
                    recyclerView.getAdapter().notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    adapter = new StorageAdapter(StorageActivity.this, StorageActivity.this, storageItems);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(StorageActivity.this);
                }
                recyclerView.setVisibility(View.VISIBLE);

//                path = storageItems.get(itemPos).getPath();
//                title = title + "/" + storageItems.get(itemPos).getName();
//                setTitle(title);

            }
        });
    }

    private void requestStoragePermission(final String req) {
        requestPermission(StorageActivity.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, new PermissionCallback() {
                    @Override
                    public void onPermissionDenied(String[] permissions) {
                        finish(); // back to the previous activity
                    }

                    @Override
                    public void onPermissionGranted(String[] toArray) {
                        storageViewModel.loadData(path, StorageActivity.this);
                    }

                    @Override
                    public void onPermissionBlocked(String[] toArray) {
                    }
                });
    }

    public void openDirectory() {
        // Choose a directory using the system's file picker.
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
                intent.putExtra("android.content.extra.FANCY", true);
                intent.putExtra("android.content.extra.SHOW_FILESIZE", true);
                startActivityForResult(intent, 42);
            }

        } catch (
                Exception e) {
            System.out.println(e.getMessage());
        }
    }



}



