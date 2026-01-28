package com.justuju.shred.Utils;

import com.justuju.shred.Models.StorageItem;

public interface ItemClickListener {
    void onItemCheck(StorageItem storageItems);
    void onPicClicked(Folder folder);
}
