package com.justuju.shred.Utils;

import com.justuju.shred.Models.StorageItem;

import java.util.List;

public interface AsyncResponse {
    void processFinish(String output, List<StorageItem> storageItemList);
}
