package com.justuju.shred.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.justuju.shred.R;
import com.justuju.shred.Utils.Folder;
import com.justuju.shred.Utils.ItemClickListener;
import com.justuju.shred.Utils.UtilConvertor;

import java.util.ArrayList;

/**
 * An adapter for populating RecyclerView with items representing folders that contain images
 */
public class MediaFolderAdapter extends RecyclerView.Adapter<MediaFolderAdapter.FolderHolder> {

    private ArrayList<Folder> folders;
    private Context folderContx;
    private ItemClickListener listenToClick;

    /**
     * @param folders     An ArrayList of String that represents paths to folders on the external storage that contain pictures
     * @param folderContx The Activity or fragment Context
     * @param listen      interFace for communication between adapter and fragment or activity
     */
    public MediaFolderAdapter(ArrayList<Folder> folders, Context folderContx, ItemClickListener listen) {
        this.folders = folders;
        this.folderContx = folderContx;
        this.listenToClick = listen;
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.media_folder_item, parent, false);
        return new FolderHolder(cell);

    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, int position) {
        final Folder folder = folders.get(position);

        //setting the number of images
        String text = "" + folder.getFolderName();
        String folderSizeString = "" + folder.getNumberOfFiles() + " Media";
        holder.folderMediaCount.setText(folderSizeString);
        holder.folderSize.setText(UtilConvertor.getSize(folder.getFolderSize()));
        holder.folderName.setText(text);

//        holder.folderPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listenToClick.onPicClicked(folder.getPath(),folder.getFolderName());
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }


    public class FolderHolder extends RecyclerView.ViewHolder {
        ImageView folderPic;
        TextView folderName;
        //set textview for foldersize
        TextView folderMediaCount, folderSize;

        CardView folderCard;

        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            folderPic = itemView.findViewById(R.id.folderPic);
            folderName = itemView.findViewById(R.id.folderName);
            folderMediaCount = itemView.findViewById(R.id.folderMediaCount);
            folderSize = itemView.findViewById(R.id.folderSize);
            folderCard = itemView.findViewById(R.id.folderCard);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listenToClick != null && pos != RecyclerView.NO_POSITION) {
                        listenToClick.onPicClicked(folders.get(pos));
                    }
                }
            });

        }
    }
}
