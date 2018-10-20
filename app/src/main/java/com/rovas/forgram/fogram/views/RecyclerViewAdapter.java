package com.rovas.forgram.fogram.views;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.Utils.Views.CircularImageView;
import com.rovas.forgram.fogram.Utils.Views.InsLoadingView;
import com.rovas.forgram.fogram.controllers.activities_chat.ChatSQLActivity;
import com.rovas.forgram.fogram.controllers.activities_home.StoriesActivity;
import com.rovas.forgram.fogram.models.StoriesPost;
import com.rovas.forgram.fogram.R;

import java.util.List;

import static com.rovas.forgram.fogram.Utils.Views.InsLoadingView.Status.CLICKED;
import static com.rovas.forgram.fogram.Utils.Views.InsLoadingView.Status.LOADING;

/**
 * Created by Mohamed El Sayed
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private static final int HEADER = 0;
    private static final int ITEM = 1;
    private List<StoriesPost> stories_list;
    //vars
    private Context mContext;
    public FirebaseFirestore fStore;
    public FirebaseAuth mAuth;
    public interface HeaderClickListener {
        void onHeaderClicked();
    }
    private final HeaderClickListener headerClickListener;
    public RecyclerViewAdapter(List<StoriesPost> stories_list  , HeaderClickListener headerClickListener) {
        this.stories_list = stories_list;
        this.headerClickListener = headerClickListener;
    }
    @Override
    public int getItemCount() {
        return stories_list.size() + 1;
    }
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER : ITEM;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        mContext = parent.getContext();
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case HEADER:
                v = layoutInflater.inflate(R.layout.layout_list_status_header, parent, false);
                return new HeaderViewHolder(v, headerClickListener);
            default:
                v = layoutInflater.inflate(R.layout.layout_list_status_item, parent, false);
                return new ItemViewHolder(v);
        }

    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM) {
            StoriesPost testTrends = stories_list.get(position - 1);
            ((ItemViewHolder)holder).bind(testTrends);

        }
    }
    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View itemView, final HeaderClickListener headerClickListener) {
            super(itemView);
            itemView.setOnClickListener(v -> headerClickListener.onHeaderClicked());
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircularImageView StoriesUserImage;//InsLoadingView
        private TextView StoriesUserName;
        ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            StoriesUserImage = mView.findViewById(R.id.status_img);
        }

        public void setUserData(String name , String image ){

            StoriesUserImage = mView.findViewById(R.id.status_img);
            StoriesUserName = mView.findViewById(R.id.status_name);

            StoriesUserName.setText(name);
            // Toast.makeText(context, "SetUserData", Toast.LENGTH_SHORT).show();

            Glide.with(mContext).asBitmap().load(image).into(StoriesUserImage);

        }
        void bind(StoriesPost storiesPost) {
            String current_user_id = mAuth.getCurrentUser().getUid();
            final String user_id =storiesPost.getUser_id();
            String userName =storiesPost.getUsername();
            String userImage =storiesPost.getThumb_image();
            setUserData(userName, userImage);
            StoriesUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent home_intent = new Intent(mContext, StoriesActivity.class);//ACTIVITY_NUM = 0
                    home_intent.putExtra("stories_post_id", user_id);
                    mContext.startActivity(home_intent);//content because the Class
                }
            });
            /*
            //InsLoadingView
            StoriesUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (StoriesUserImage.getStatus()) {
                        case UNCLICKED:
                            StoriesUserImage.setStatus(LOADING);
                            break;
                        case LOADING:
                            StoriesUserImage.setStatus(CLICKED);
                            break;
                        case CLICKED:
                            StoriesUserImage.setStatus(InsLoadingView.Status.UNCLICKED);
                    }
                    Intent home_intent = new Intent(mContext, StoriesActivity.class);//ACTIVITY_NUM = 0
                    home_intent.putExtra("stories_post_id", user_id);
                    mContext.startActivity(home_intent);//content because the Class
                }
            });*/
            StoriesUserImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(!user_id.equals(current_user_id)) {
                        CharSequence options[] = new CharSequence[]{"View Picture", "Send message"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, final int i) {
                                fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                String username = task.getResult().getString("username");
                                                if (i == 0) {

                                                    // Intent profileIntent = new Intent(vProfileActivity.this, ProfileActivity.class);
                                                    //profileIntent.putExtra("user_id", list_user_id);
                                                    //startActivity(profileIntent);

                                                }

                                                if (i == 1) {

                                                    Intent chatIntent = new Intent(mContext, ChatSQLActivity.class);
                                                    chatIntent.putExtra("user_id", user_id);
                                                    chatIntent.putExtra("user_name", username);
                                                    mContext.startActivity(chatIntent);

                                                }
                                            }
                                        }
                                    }
                                });
                                //Click Event for each item.


                            }
                        });

                        builder.show();
                    }
                    return true;
                }
            });
            /*
            StoriesUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent home_intent = new Intent(mContext, StoriesActivity.class);//ACTIVITY_NUM = 0
                    home_intent.putExtra("stories_post_id", user_id);
                    mContext.startActivity(home_intent);//content because the Class

                }
            });
            */
        }
    }
}