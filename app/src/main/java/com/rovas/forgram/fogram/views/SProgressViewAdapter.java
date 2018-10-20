package com.rovas.forgram.fogram.views;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.Utils.GetTimeAgo;
import com.rovas.forgram.fogram.Utils.Stories.StoriesProgressView;
import com.rovas.forgram.fogram.controllers.activities_main.HomeActivity;
import com.rovas.forgram.fogram.models.StoriesPost;
import com.rovas.forgram.fogram.R;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed El Sayed
 */

public class SProgressViewAdapter extends RecyclerView.Adapter<SProgressViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private List<StoriesPost> stories_list;
    //vars
    //


    //
    private Context mContext;
    public FirebaseFirestore fStore;
    public FirebaseAuth mAuth;
    public SProgressViewAdapter(List<StoriesPost> stories_list ) {
        this.stories_list = stories_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_stories_change_items, parent, false);
        mContext = parent.getContext();
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        //User_Details
        String profile_img = stories_list.get(position).getThumb_image();
        String profile_name = stories_list.get(position).getUsername();
        holder.setUserData(profile_name, profile_img);

        List<String> myList = new ArrayList<String>();
        List<Long> TimeList = new ArrayList<Long>();
        for (position  = 0 ; stories_list.size() > position ;)
        {
            //Time_Stamp[Date]
            long time_stamp = stories_list.get(position).getTime_stamp();
            TimeList.add(position , time_stamp);
            //Stories_photo
            String image_url = stories_list.get(position).getImage_url();

            //String image_url = stories_list.get(position).getImage_url();
            myList.add( position , image_url);
            ++position;
            //Toast.makeText(mContext, "DocumentSize = " +stories_list.size(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(mContext, "X = " + x +"  "+ myList.size(), Toast.LENGTH_SHORT).show();
        }

        //String userName = stories_list.get(position).get();


       // holder.resources[holder.stories_count++]  = image_url;
        holder.setIMG(myList , myList.size() , TimeList , TimeList.size());
        holder.image_stories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.storiesProgressView.skip();
            }
        });
        holder.forward_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ------------------ Share Via ------------------
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, "Forward");
                share.putExtra(Intent.EXTRA_TEXT , "");//Forward Text
                mContext.startActivity(Intent.createChooser(share  , "Share Via"));
                holder.storiesProgressView.pause();


            }
        });

    }

    @Override
    public int getItemCount() {
        return stories_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private ImageView StoriesUserImage;
        private TextView StoriesUserName;
        private TextView StoriesUserTime;
        private StoriesProgressView storiesProgressView;
        private ImageView image_stories;
        private RelativeLayout forward_dialog;
        private RelativeLayout Content_Layout;
        long pressTime = 0L;
        long limit = 500L;
        int counter = 0;
        //int stories_count;
        private ImageView image_forward;

        //private String[] resources;
        /*
        private int[] resources = new int[]
                {
                        R.drawable.b1,
                        R.drawable.profile,
                        R.drawable.b1
                };
                */
        //

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            image_stories = mView.findViewById(R.id.image_stories);
            StoriesUserName = mView.findViewById(R.id.stories_profile_name);
            StoriesUserImage  = mView.findViewById(R.id.stories_profile_img);
            StoriesUserTime = mView.findViewById(R.id.stories_profile_time);
            storiesProgressView = (StoriesProgressView) mView.findViewById(R.id.stories_progress);
            forward_dialog = mView.findViewById(R.id.forward_dialog);
            Content_Layout = mView.findViewById(R.id.relativeLayout3);
        }
        public void setTime(long time) {

            GetTimeAgo getTimeAgo = new GetTimeAgo();

            long lastTime = time;

            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, mContext);

            StoriesUserTime.setText(lastSeenTime);

        }

        public void setUserData(String name, String image){

            StoriesUserName.setText(name);
            if(image != null) {
                Glide.with(mContext).load(image).into(StoriesUserImage);
            }

        }
        @SuppressLint("CutPasteId")
        public void setIMG(final List<String> Story_list , int stories_count  ,  List<Long> Time_list , int time_count  ){

            storiesProgressView = (StoriesProgressView) mView.findViewById(R.id.stories_progress);
            storiesProgressView.setStoriesCount(stories_count);//stories_count
            storiesProgressView.setStoryDuration(5000L);
            storiesProgressView.startStories();
            image_stories = (ImageView) mView.findViewById(R.id.image_stories);
            //storiesProgressView.pause();
            Glide.with(mContext)
                    .load(Story_list.get(counter))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //storiesProgressView.resume();
                            return false;
                        }
                    })
                    .into(image_stories);//
            //String lastSeenTime = GetTimeAgo.getTimeAgo(Time_list.get(counter), mContext);
            //StoriesUserTime.setText(lastSeenTime);
            setTime(Time_list.get(counter));
            storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
                @Override
                public void onNext() {
                    //image_stories.setImageResource(Integer.parseInt(image_url.get(++counter)));
                    Glide.with(mContext)
                            .load(Story_list.get(++counter))
                            .into(image_stories);//
                    //String lastSeenTime = GetTimeAgo.getTimeAgo(Time_list.get(++counter), mContext);
                    //StoriesUserTime.setText(lastSeenTime);
                    setTime(Time_list.get(counter));
                }

                @Override
                public void onPrev() {
                    if ((counter - 1) < 0) return;
                    //image_stories.setImageResource(Integer.parseInt(image_url.get(--counter)));
                    Glide.with(mContext)
                            .load(Story_list.get(--counter))
                            .into(image_stories);
                    // storiesProgressView.pause();
                    //String lastSeenTime = GetTimeAgo.getTimeAgo(Time_list.get(--counter), mContext);
                    //StoriesUserTime.setText(lastSeenTime);
                    setTime(Time_list.get(counter));
                }

                @Override
                public void onComplete() {
                    //Toast.makeText(mContext, "Done", Toast.LENGTH_SHORT).show();
                    Intent home_intent = new Intent(mContext, HomeActivity.class);//ACTIVITY_NUM = 0
                    mContext.startActivity(home_intent);//content because the Class
                }
            });
            image_stories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storiesProgressView.skip();
                }
            });
            image_stories.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            pressTime = System.currentTimeMillis();
                            storiesProgressView.pause();
                            Content_Layout.setVisibility(View.INVISIBLE);

                            return false;
                        case MotionEvent.ACTION_UP:
                            long now = System.currentTimeMillis();
                            storiesProgressView.resume();
                            Content_Layout.setVisibility(View.VISIBLE);
                            return limit < now - pressTime;
                    }
                    return false;
                }
            });

            //image_stories.setImageResource(Integer.parseInt(image_url.get(counter)));

        }

    }
}