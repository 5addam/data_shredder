package com.justuju.shred.Utils;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.justuju.shred.R;

public class PicHolder extends RecyclerView.ViewHolder {
    public ImageView picture;
    public TextView name;
    public CheckBox checkBox;
    public View translucentOverlay;

    public PicHolder(@NonNull View itemView) {
        super(itemView);
        picture = itemView.findViewById(R.id.image);
        checkBox = itemView.findViewById(R.id.checkbox);
        name = itemView.findViewById(R.id.image_name);
        translucentOverlay = itemView.findViewById(R.id.translucent_overlay);
    }
}
