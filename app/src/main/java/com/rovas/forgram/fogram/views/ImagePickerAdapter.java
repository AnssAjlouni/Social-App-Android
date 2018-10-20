package com.rovas.forgram.fogram.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fxn.utility.Utility;
import com.rovas.forgram.fogram.R;

import java.io.File;
import java.util.ArrayList;
/**
 * Created by Mohamed El Sayed
 */
public class ImagePickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Bitmap> list = new ArrayList<>();
    Context context;
    // --------------- Edit -----------------
    public ImagePickerAdapter(Context context) {
        this.context = context;
    }
    public void EditImage(int position , Bitmap bitmap) {
        this.list.set( position , bitmap);
        notifyDataSetChanged();

    }
    public void AddImage(ArrayList<Bitmap> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
        /*
        for(int i = 0 ; i < list.size() ; i++) {
            this.list.add( i , list.get(i));
            notifyDataSetChanged();
        }
        */


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.image_picker, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Bitmap d = list.get(position);
        //Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(512, com.fxn.utility.Utility.getExifCorrectedBitmap(f));
        Bitmap scaled = Utility.getScaledBitmap(512, d);
        ((Holder) holder).iv.setImageBitmap(scaled);

        // ((Holder) holder).iv.setImageURI(imageUri);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public ImageView iv;

        public Holder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
        }
    }
}