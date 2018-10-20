package com.rovas.forgram.fogram.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fxn.utility.Utility;
import com.rovas.forgram.fogram.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class ImageCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> list = new ArrayList<>();
    Context context;
    // --------------- Edit -----------------
    public ImageCollectionAdapter(Context context) {
        this.context = context;
    }
    public void EditImage(int position , String stri) {
        this.list.set( position , stri);
        notifyDataSetChanged();

    }
    public void AddImage(List<String> list) {
        this.list.clear();
        //this.list.addAll(list);
        //notifyDataSetChanged();
        for(int i = 0 ; i < list.size() ; i++) {
            this.list.add(i, list.get(i));
            notifyDataSetChanged();
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.single_list_item_img_coll, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        String d = list.get(position);
        Glide.with(context).load(d).into((ImageView) holder.itemView.findViewById(R.id.iv_coll));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public ImageView iv;

        public Holder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_coll);
        }
    }
}