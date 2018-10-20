package com.rovas.forgram.fogram.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.interfaces.OnSelectionListener;
import com.rovas.forgram.fogram.models.Img;

import java.util.ArrayList;
/**
 * Created by Mohamed El Sayed
 */
public class InstantBackgroundAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Img> list;
    private OnSelectionListener onSelectionListener;
    private RequestManager glide;
    private RequestOptions options;

    public InstantBackgroundAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();

        glide = Glide.with(context);
        options = new RequestOptions().override(256).transform(new CenterCrop()).transform(new FitCenter());
    }

    public void addOnSelectionListener(OnSelectionListener onSelectionListener) {
        this.onSelectionListener = onSelectionListener;
    }

    public InstantBackgroundAdapter addImage(Img image) {
        list.add(image);
        notifyDataSetChanged();
        return this;
    }

    public ArrayList<Img> getItemList() {
        return list;
    }

    public void addImageList(ArrayList<Img> images) {
        list.addAll(images);
        notifyDataSetChanged();
    }

    public void clearList() {
        list.clear();
    }

    public void select(boolean selection, int pos) {
        if (pos < 100) {
            list.get(pos).setSelected(selection);
            notifyItemChanged(pos);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.inital_image, parent, false);
            return new Holder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String image = list.get(position).getContentUrl();
        if (holder instanceof Holder) {
            ((Holder) holder).setImage(image);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView preview;
        ImageView selection;

        Holder(View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.preview);
            selection = itemView.findViewById(R.id.selection);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListener.OnClick(list.get(id), view, id);
        }

        @Override
        public boolean onLongClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListener.OnLongClick(list.get(id), view, id);
            return true;
        }
        public void setImage(String image)
        {
            glide.load(image).apply(options).into(preview);
        }
    }

    public class HolderNone extends RecyclerView.ViewHolder {
        HolderNone(View itemView) {
            super(itemView);
        }
    }
}