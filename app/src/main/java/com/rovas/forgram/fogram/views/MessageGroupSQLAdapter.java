package com.rovas.forgram.fogram.views;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.GetTimeAgo;
import com.rovas.forgram.fogram.Utils.UI.ChatUI;
import com.rovas.forgram.fogram.controllers.activities_chatgroup.ImageGroupDetailsActivity;
import com.rovas.forgram.fogram.models.MessageGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mohamed El Sayed
 */

public class MessageGroupSQLAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<MessageGroup> mMessageList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private Context context;
    //
    private static final int sender_txt = 0;
    private static final int received_txt = 1;
    private static final int sender_img = 2;
    private static final int received_img = 3;
    private static final int sender_video = 4;
    private static final int received_video = 5;
    private static final int sender_voice = 6;
    private static final int received_voice = 7;
    private static final int sender_collection_image = 8;
    private static final int received_collection_image = 9;
    //
    public MessageGroupSQLAdapter(List<MessageGroup> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case sender_txt://0
                view = inflater.inflate(R.layout.message_single_layout_sender, parent, false);
                holder = new Sender_TXT_ViewHolder(view);
                break;
            case received_txt://1
                view = inflater.inflate(R.layout.message_single_layout, parent, false);
                holder = new Rec_TXT_ViewHolder(view);
                break;
            case sender_img://2
                view = inflater.inflate(R.layout.message_single_layout_sender_img, parent, false);
                holder = new Sender_IMG_ViewHolder(view);
                break;
            case received_img://3
                view = inflater.inflate(R.layout.message_single_layout_img, parent, false);
                holder = new Rec_IMG_ViewHolder(view);
                break;
                //senderVideo 4
                //RecVideo 5
            case sender_voice://6
                view = inflater.inflate(R.layout.message_single_layout_sender_record, parent, false);
                holder = new Sender_RECORD_ViewHolder(view);
                break;
            case received_voice://7
                view = inflater.inflate(R.layout.message_single_layout_record, parent, false);
                holder = new Rec_RECORD_ViewHolder(view);
                break;
            case sender_collection_image://8
                view = inflater.inflate(R.layout.message_single_layout_sender_img_coll, parent, false);
                holder = new Sender_IMG_COLL_ViewHolder(view);
                break;
            default://0
                view = inflater.inflate(R.layout.message_single_layout_sender, parent, false);
                holder = new Sender_TXT_ViewHolder(view);
                break;
        }


        return holder;

    }
    @Override
    public int getItemViewType(int position) {
        //0 for send_txt
        //1 for rec_txt
        //2 for sender_img
        //3 for rec_img
        //4 for send_vid
        //5 for rec_vid
        //6 for send_record
        //7 for rec_record


        String data_type = mMessageList.get(position).getPosition();

        //return -1;
        return Integer.parseInt(data_type);

    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == sender_txt)
        {
            onBindViewHolder_Sender_TXT((Sender_TXT_ViewHolder) holder, position);
        }
        else if(getItemViewType(position) == received_txt)
        {
            onBindViewHolder_Rec_TXT((Rec_TXT_ViewHolder) holder, position);
        }
        else if(getItemViewType(position) == sender_img)
        {
            onBindViewHolder_Sender_IMG((Sender_IMG_ViewHolder) holder, position);
        }
        else if(getItemViewType(position) == received_img)
        {
            onBindViewHolder_Rec_IMG((Rec_IMG_ViewHolder) holder, position);
        }
        else if(getItemViewType(position) == sender_voice)
        {
            onBindViewHolder_Sender_RECORD((Sender_RECORD_ViewHolder) holder, position);
        }
        else if(getItemViewType(position) == received_voice)
        {
            onBindViewHolder_Rec_RECORD((Rec_RECORD_ViewHolder) holder, position);
        }
        else if(getItemViewType(position) == sender_collection_image)
        {
            onBindViewHolder_Sender_IMG_COLL((Sender_IMG_COLL_ViewHolder) holder, position);
        }
    }
    public class Sender_TXT_ViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView time_text_layout;

        public Sender_TXT_ViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            time_text_layout = (TextView)view.findViewById(R.id.time_text_layout);

        }
        public void setTime(long time)
        {

            String lastSeenTime = GetTimeAgo.getTimeAgo(time, context);

            time_text_layout.setText(lastSeenTime);
        }
    }

    public void onBindViewHolder_Sender_TXT(final Sender_TXT_ViewHolder viewHolder, int i) {

        MessageGroup c = mMessageList.get(i);
        long time = c.getTime_stamp();
        viewHolder.setTime(time);
        viewHolder.messageText.setText(c.getMessage());


    }
    public class Rec_TXT_ViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView time_text_layout;
        public TextView username_text;
        public Rec_TXT_ViewHolder(View view) {
            super(view);
            username_text = (TextView)view.findViewById(R.id.message_user_name);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            time_text_layout = (TextView)view.findViewById(R.id.time_text_layout);

        }
        public void setUserName(String user_name)
        {
            username_text.setText(user_name);
        }
        public void setTime(long time)
        {


            String lastSeenTime = GetTimeAgo.getTimeAgo(time, context);

            time_text_layout.setText(lastSeenTime);
        }
    }

    public void onBindViewHolder_Rec_TXT(final Rec_TXT_ViewHolder viewHolder, int i) {

        MessageGroup c = mMessageList.get(i);
        long time = c.getTime_stamp();
        String user_name = c.getFrom_name();
        viewHolder.setUserName(user_name);
        viewHolder.setTime(time);
        viewHolder.messageText.setText(c.getMessage());

    }
    // ------------------------------------------------------
    public class Sender_IMG_ViewHolder extends RecyclerView.ViewHolder {

        public TextView time_text_layout;
        public ImageView Main_image;
        public Sender_IMG_ViewHolder(View view) {
            super(view);
            time_text_layout = (TextView)view.findViewById(R.id.time_text_layout);
            Main_image = (ImageView)view.findViewById(R.id.message_image);
        }
        public void setTime(long time)
        {

            String lastSeenTime = GetTimeAgo.getTimeAgo(time, context);

            time_text_layout.setText(lastSeenTime);
        }
        public void setImage(String image){

            Glide.with(context).load(image).into(Main_image);

        }
    }

    public void onBindViewHolder_Sender_IMG(final Sender_IMG_ViewHolder viewHolder, int i) {

        MessageGroup c = mMessageList.get(i);
        long time = c.getTime_stamp();
        String image = c.getMedia_path();
        viewHolder.setImage(image);
        viewHolder.setTime(time);
        viewHolder.Main_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageGroupDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ChatUI.BUNDLE_MESSAGE, c);
                context.startActivity(intent);
            }
        });


    }
    // ------------------------------------------------------
    public class Rec_IMG_ViewHolder extends RecyclerView.ViewHolder {

        public TextView username_text;
        public TextView time_text_layout;
        public ImageView Main_image;

        public Rec_IMG_ViewHolder(View view) {
            super(view);
            username_text = (TextView)view.findViewById(R.id.message_user_name);
            time_text_layout = (TextView)view.findViewById(R.id.time_text_layout);
            Main_image = (ImageView)view.findViewById(R.id.message_image);
        }
        public void setUserName(String user_name)
        {
            username_text.setText(user_name);
        }
        public void setTime(long time)
        {

            String lastSeenTime = GetTimeAgo.getTimeAgo(time, context);

            time_text_layout.setText(lastSeenTime);
        }
        public void setImage(String image){
            Glide.with(itemView.getContext())
                    .load(image)
                    .into(Main_image);
            //Glide.with(context).load(image).into(Main_image);

        }
    }

    public void onBindViewHolder_Rec_IMG(final Rec_IMG_ViewHolder viewHolder, int i) {

        MessageGroup c = mMessageList.get(i);
        long time = c.getTime_stamp();
        String image = c.getMedia_path();
        String user_name = c.getFrom_name();
        viewHolder.setUserName(user_name);
        viewHolder.setImage(image);
        viewHolder.setTime(time);
        viewHolder.Main_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageGroupDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ChatUI.BUNDLE_MESSAGE, c);
                context.startActivity(intent);
            }
        });



    }
    // ------------------------------------------------------
    public class Sender_RECORD_ViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView time_text_layout;
        public SeekBar current_sec_progress;
        private MediaPlayer mPlayer = null;
        private ImageView image_play_btn;
        private Handler handler;
        private Runnable runnable;
        private Boolean isPlaying = false;
        public Sender_RECORD_ViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            time_text_layout = (TextView)view.findViewById(R.id.time_text_layout);
            current_sec_progress = (SeekBar)view.findViewById(R.id.progress_sec);
            image_play_btn = (ImageView)view.findViewById(R.id.image_play_btn);



        }
        public void playCycle()
        {
            if(isPlaying) {
                current_sec_progress.setProgress(mPlayer.getCurrentPosition());
                if (mPlayer.isPlaying()) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            playCycle();
                        }
                    };
                    handler.postDelayed(runnable, 1000);

                }
            }
            else
            {
                image_play_btn.setImageDrawable(ContextCompat.getDrawable(context , R.drawable.ic_play));
            }
        }
        public void seek_bar()
        {
            //final SeekBar mSeelBar = new SeekBar(context);
            final int duration = mPlayer.getDuration();
            final int amoungToupdate = duration / 100;
            Timer mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if (!(amoungToupdate * current_sec_progress.getProgress() >= duration)) {
                                int p = current_sec_progress.getProgress();
                                p += 1;
                                current_sec_progress.setProgress(p);
                            }
                        }
                    });
                };
            }, amoungToupdate);
        }
        private void startPlaying(String mFileName) {

            mPlayer = new MediaPlayer();
            handler = new Handler();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            current_sec_progress.setMax(mPlayer.getDuration());
            current_sec_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                    image_play_btn.setImageDrawable(ContextCompat.getDrawable(context , R.drawable.ic_pause));
                    isPlaying = true;
                    if(input)
                    {
                        mPlayer.seekTo(progress);


                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            try {
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        current_sec_progress.setMax(mPlayer.getDuration());
                        playCycle();
                    }
                });
                mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

                //seek_bar();
        }

        private void stopPlaying() {
            mPlayer.release();
            mPlayer = null;
        }
        public void setTime(long time)
        {

            String lastSeenTime = GetTimeAgo.getTimeAgo(time, context);

            time_text_layout.setText(lastSeenTime);
        }
    }

    public void onBindViewHolder_Sender_RECORD(final Sender_RECORD_ViewHolder viewHolder, int i) {

        MessageGroup c = mMessageList.get(i);
        long time = c.getTime_stamp();
        viewHolder.setTime(time);
        final String message = c.getMedia_path();
        viewHolder.image_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!viewHolder.isPlaying) {
                    viewHolder.startPlaying(message);
                    viewHolder.isPlaying = true;
                    viewHolder.image_play_btn.setImageDrawable(ContextCompat.getDrawable(context , R.drawable.ic_play));
                }
                else
                {
                    viewHolder.stopPlaying();
                    viewHolder.isPlaying =false;
                }
            }
        });



    }
    // ------------------------------------------------------
    public class Rec_RECORD_ViewHolder extends RecyclerView.ViewHolder {

        public TextView username_text;
        public TextView messageText;
        public TextView time_text_layout;
        public SeekBar current_sec_progress;
        private MediaPlayer mPlayer = null;
        private ImageView image_play_btn;
        private Handler handler;
        private Runnable runnable;
        private Boolean isPlaying = false;
        public Rec_RECORD_ViewHolder(View view) {
            super(view);
            username_text = (TextView)view.findViewById(R.id.message_user_name);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            time_text_layout = (TextView)view.findViewById(R.id.time_text_layout);
            current_sec_progress = (SeekBar)view.findViewById(R.id.progress_sec);
            image_play_btn = (ImageView)view.findViewById(R.id.image_play_btn);



        }
        public void setUserName(String user_name)
        {
            username_text.setText(user_name);
        }
        public void playCycle()
        {
            if(isPlaying) {
                current_sec_progress.setProgress(mPlayer.getCurrentPosition());
                if (mPlayer.isPlaying()) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            playCycle();
                        }
                    };
                    handler.postDelayed(runnable, 1000);

                }
            }
            else
            {
                image_play_btn.setImageDrawable(ContextCompat.getDrawable(context , R.drawable.ic_play));
            }
        }
        public void seek_bar()
        {
            //final SeekBar mSeelBar = new SeekBar(context);
            final int duration = mPlayer.getDuration();
            final int amoungToupdate = duration / 100;
            Timer mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if (!(amoungToupdate * current_sec_progress.getProgress() >= duration)) {
                                int p = current_sec_progress.getProgress();
                                p += 1;
                                current_sec_progress.setProgress(p);
                            }
                        }
                    });
                };
            }, amoungToupdate);
        }
        private void startPlaying(String mFileName) {

            mPlayer = new MediaPlayer();
            handler = new Handler();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            current_sec_progress.setMax(mPlayer.getDuration());
            current_sec_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                    image_play_btn.setImageDrawable(ContextCompat.getDrawable(context , R.drawable.ic_pause));
                    isPlaying = true;
                    if(input)
                    {
                        mPlayer.seekTo(progress);


                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            try {
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        current_sec_progress.setMax(mPlayer.getDuration());
                        playCycle();
                    }
                });
                mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //seek_bar();
        }

        private void stopPlaying() {
            mPlayer.release();
            mPlayer = null;
        }
        public void setTime(long time)
        {

            String lastSeenTime = GetTimeAgo.getTimeAgo(time, context);

            time_text_layout.setText(lastSeenTime);
        }
    }

    public void onBindViewHolder_Rec_RECORD(final Rec_RECORD_ViewHolder viewHolder, int i) {

        MessageGroup c = mMessageList.get(i);
        long time = c.getTime_stamp();
        String user_name = c.getFrom_name();
        viewHolder.setUserName(user_name);
        viewHolder.setTime(time);
        final String message = c.getMedia_path();
        viewHolder.image_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!viewHolder.isPlaying) {
                    viewHolder.startPlaying(message);
                    viewHolder.isPlaying = true;
                    viewHolder.image_play_btn.setImageDrawable(ContextCompat.getDrawable(context , R.drawable.ic_play));
                }
                else
                {
                    viewHolder.stopPlaying();
                    viewHolder.isPlaying =false;
                }
            }
        });



    }
    // ------------------------------------------------------
    public class Sender_IMG_COLL_ViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView time_text_layout;
        private List<String> img_coll_list = new ArrayList<>();
        // ===============================
        private RecyclerView recyclerView;
        private ImageCollectionAdapter myAdapter;
        private LinearLayoutManager mManager;
        public Sender_IMG_COLL_ViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            time_text_layout = (TextView)view.findViewById(R.id.time_text_layout);
            recyclerView = view.findViewById(R.id.message_image_coll);


        }
        public void Recycle_Ad(List<String> returnValue)
        {


            // ================ Adapter ================
            mManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);;
            recyclerView.setLayoutManager(mManager);
            myAdapter = new ImageCollectionAdapter(context);
            recyclerView.setAdapter(myAdapter);
            // =========================================
            for(int x = 0 ; x < returnValue.size() ; x++) {
                myAdapter.AddImage(returnValue);
            }
        }
        public void setTime(long time)
        {
            String lastSeenTime = GetTimeAgo.getTimeAgo(time, context);

            time_text_layout.setText(lastSeenTime);
        }
    }

    public void onBindViewHolder_Sender_IMG_COLL(final Sender_IMG_COLL_ViewHolder viewHolder, int i) {

        MessageGroup c = mMessageList.get(i);
        long time = c.getTime_stamp();
        viewHolder.setTime(time);
        HashMap<String , String>  img_coll = c.getMedia_collection();//Max 1mb

        for(int x = 0 ; x < img_coll.size(); ++x)
        {
            viewHolder.img_coll_list.add( x , img_coll.get(""+ x +""));
            //Toast.makeText(context, "L :" + viewHolder.img_coll_list.get(x), Toast.LENGTH_SHORT).show();
        }
        viewHolder.Recycle_Ad(viewHolder.img_coll_list);

    }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }







}