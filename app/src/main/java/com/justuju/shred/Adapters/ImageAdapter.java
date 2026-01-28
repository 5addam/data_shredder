package com.justuju.shred.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.justuju.shred.Models.StorageItem;
import com.justuju.shred.R;
import com.justuju.shred.Utils.ItemClickListener;
import com.justuju.shred.Utils.PicHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.view.ViewCompat.setTransitionName;

public class ImageAdapter extends RecyclerView.Adapter<PicHolder> {
    private List<StorageItem> pictureList;
    private Context pictureContx;
    private final ItemClickListener picListerner;

    /**
     * @param pictureList  ArrayList of pictureFacer objects
     * @param pictureContx The Activities Context
     * @param picListerner An interface for listening to clicks on the RecyclerView's items
     */
    public ImageAdapter(List<StorageItem> pictureList, Context pictureContx, ItemClickListener picListerner) {
        this.pictureList = pictureList;
        this.pictureContx = pictureContx;
        this.picListerner = picListerner;
        setHasStableIds(true);
    }

    public void setData(List<StorageItem> newData) {
        this.pictureList = newData;
        notifyDataSetChanged();
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    @NonNull
    @Override
    public PicHolder onCreateViewHolder(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(pictureContx);
        View cell = inflater.inflate(R.layout.media_file_item, container, false);
        return new PicHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull final PicHolder holder, final int position) {
        StorageItem image = pictureList.get(position);

        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(pictureList.get(position).isSelected());
        //holder.checkBox.setSelected(pictureList.get(position).getSelected());
        holder.translucentOverlay.setVisibility(pictureList.get(position).isSelected() ? View.VISIBLE : View.GONE);
        holder.translucentOverlay.setAlpha(.6f);

        holder.name.setText(image.getName());
//        if item type is image then we set its thumbnail as icon
        if (image.getName().endsWith(".jpg") || image.getName().endsWith(".jpeg") ||
                image.getName().endsWith(".png") ||
                image.getName().endsWith(".mp4")) {
            Glide.with(pictureContx)
                    .load(image.getPath())
                    .apply(new RequestOptions().fitCenter())
                    .apply(new RequestOptions()
                            .signature(new ObjectKey(
                                    new File(image.getPath()).lastModified()))
                    )
                    .override(200, 200)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                    .placeholder(ContextCompat.getDrawable(pictureContx, R.drawable.ic_empty_file))
                    .into(holder.picture);
        }
//        else we set our default image as its icon
        else {
            Glide.with(pictureContx)
                    .load(ContextCompat.getDrawable(pictureContx, R.drawable.ic_file))
                    .apply(new RequestOptions().fitCenter())
                    .override(200, 200)
                    .into(holder.picture);
        }

        setTransitionName(holder.picture, String.valueOf(position) + "_image");

//        holder.picture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                picListerner.onPicClicked(holder, position, pictureList);
//            }
//        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) holder.checkBox.getTag();
                picListerner.onItemCheck(pictureList.get(pos));
                holder.translucentOverlay.setVisibility(pictureList.get(position).isSelected() ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void updateList(ArrayList<StorageItem> storageItemList) {
        this.pictureList = storageItemList;
        notifyDataSetChanged();
    }

    public void removeItem(int pos) {
        pictureList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, pictureList.size());
    }

    @Override
    public long getItemId(int position) {
        StorageItem storageItem = pictureList.get(position);
        return storageItem.getPath().hashCode();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (pictureList != null)
            return pictureList.size();
        else
            return 0;

    }
}
