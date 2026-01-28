package com.justuju.shred.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.BidiFormatter;
import androidx.recyclerview.widget.RecyclerView;

import com.justuju.shred.Models.AboutUsItem;
import com.justuju.shred.R;

import java.util.ArrayList;

public class AboutUsItemAdapter extends RecyclerView.Adapter<AboutUsItemAdapter.AboutUsHolder> {
    private ArrayList<AboutUsItem> items;

    public AboutUsItemAdapter(ArrayList<AboutUsItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AboutUsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new AboutUsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutUsHolder holder, int position) {
        AboutUsItem item = items.get(position);

        //The unicodeWrap() method detects the direction of a string and wraps it in Unicode formatting characters that declare that direction.
        BidiFormatter myBidiFormatter = BidiFormatter.getInstance(); // BidiFormatter is a method of unicodeWrap()

        // The "did_you_mean" localized string resource includes
        // a "%s" placeholder for the suggestion.
//        String.format(myBidiFormatter.unicodeWrap(item.getItemtitle()));

        holder.title.setText(String.format(myBidiFormatter.unicodeWrap(item.getItemtitle()))); //number and such text will still apper in ltr direction even in rtl view
        holder.type.setText(item.getItemType());
        holder.icon.setImageDrawable(item.getItemIcon());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class AboutUsHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView type;
        private ImageView icon;

        public AboutUsHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.aboutus_item_title);
            type = itemView.findViewById(R.id.aboutus_item_type);
            icon = itemView.findViewById(R.id.aboutus_item_icon);
        }
    }
}
