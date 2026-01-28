package com.justuju.shred.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.justuju.shred.Models.MediaItem;
import com.justuju.shred.R;

import java.util.ArrayList;

public class MediaItemAdapter extends RecyclerView.Adapter<MediaItemAdapter.MediaHolder> {
    private ArrayList<MediaItem> mediaItems;
    private OnItemClickListener listener;
    private Context context;

    public MediaItemAdapter(ArrayList<MediaItem> mediaItems, Context context) {
        this.mediaItems = mediaItems;
        this.context = context;
    }

    @NonNull
    @Override
    public MediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false);
        return new MediaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaHolder holder, int position) {
        MediaItem mediaItem = mediaItems.get(position);
        holder.mediaImage.setImageResource(mediaItem.getImageId());
        holder.imageTitle.setText(mediaItem.getImageTitle());
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    public class MediaHolder extends RecyclerView.ViewHolder {
        private ImageView mediaImage;
        private TextView imageTitle;

        public MediaHolder(@NonNull View itemView) {
            super(itemView);
            mediaImage = itemView.findViewById(R.id.media_img);
            imageTitle = itemView.findViewById(R.id.media_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null && pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(mediaItems.get(pos));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MediaItem mediaItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
