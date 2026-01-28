package com.justuju.shred.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.justuju.shred.Models.MainItemModel;
import com.justuju.shred.R;

import java.util.ArrayList;

public class MainItemAdapter extends RecyclerView.Adapter<MainItemAdapter.ItemViewHolder> {

    private ArrayList<MainItemModel> mainItems;
    private OnItemClickListener listener;

    public MainItemAdapter(ArrayList<MainItemModel> mainItems) {
        this.mainItems = mainItems;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        MainItemModel itemModel = mainItems.get(position);

//        holder.mainItemImage.setImageResource(itemModel.getItemImage());
        holder.mainItemImage.setImageDrawable(itemModel.getItemImage());
        holder.mainItemTitle.setText(itemModel.getItemTitle());

    }

    @Override
    public int getItemCount() {
        return mainItems.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private ImageView mainItemImage;
        private TextView mainItemTitle;

        public ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            mainItemImage = itemView.findViewById(R.id.home_item_img);
            mainItemTitle = itemView.findViewById(R.id.home_item_title);

           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   int pos = getAdapterPosition();

                   if(listener != null && pos != RecyclerView.NO_POSITION){
                       listener.onItemClick(mainItems.get(pos));
                   }
               }
           });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MainItemModel mainItemModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }
}
