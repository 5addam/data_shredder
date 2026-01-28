package com.justuju.shred.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.justuju.shred.Models.StorageItem;
import com.justuju.shred.R;

import java.io.File;
import java.util.List;

public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.StorageHolder> {

    private OnItemClickListener listener;
    Context context;
    List<StorageItem> items;

    public StorageAdapter(OnItemClickListener listener, Context context, List<StorageItem> items) {
        this.listener = listener;
        this.context = context;
        this.items = items;

        /*
        setting setHasStableIds true means adapter would publish a unique value as a key for item in data set.
        Adapter can use the key to indicate they are the same one or not after notifying data changed.
         */
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public StorageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.storage_item, parent, false);
        return new StorageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StorageHolder holder, int position) {
        StorageItem item = items.get(position);
        context = holder.icon.getContext();
        holder.name.setText(item.getName());
        holder.size.setText(item.getSize());
        holder.isChecked.setChecked(item.isSelected());
        if (item.getType().equals("Dir")) {
            holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_folder_filled));
        } else {
            if (item.getName().endsWith(".jpg") || item.getName().endsWith(".jpeg") ||
                    item.getName().endsWith(".png") ||
                    item.getName().endsWith(".mp4")) {

                Glide.with(context)
                        .load(new File(item.getPath()))
                        .apply(new RequestOptions()
                                .signature(new ObjectKey(
                                        new File(item.getPath())
                                                .lastModified())
                                )
                        )
                        .override(200, 200)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_empty_file))
                        .into(holder.icon);
            } else
                holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_file));

        }
    }

    @Override
    public int getItemCount() {
        if (items != null)
            return items.size();
        else
            return 0;
    }

    /*
    So we need to override the getItemId() to return the same id if the content is identical
     */
    @Override
    public long getItemId(int position) {
        StorageItem storageItem = items.get(position);
        return storageItem.getPath().hashCode(); // returning hashCode because it's unique and can be used as object's ID
    }


    public class StorageHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView size;
        private CheckBox isChecked;

        public StorageHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.storage_item_icon);
            name = itemView.findViewById(R.id.storage_item_name);
            size = itemView.findViewById(R.id.storage_item_size);
            isChecked = itemView.findViewById(R.id.isChecked);

            isChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemCheck(items.get(getAdapterPosition()));
                    }
                }
            });
        }
    }


    public void setData(List<StorageItem> newData) {
//        items.clear();
        items = newData;
//        items.addAll(newData);
        notifyItemInserted(items.size() - 1);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
//        void onItemClick(StorageItem storageItem);

        void onItemCheck(StorageItem storageItems);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
