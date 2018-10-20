package com.rovas.forgram.fogram.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.controllers.activities_notification.PhotoForward;
import com.rovas.forgram.fogram.controllers.activities_notification.PhotoTextForward;
import com.rovas.forgram.fogram.controllers.activities_notification.TCommentsActivity;
import com.rovas.forgram.fogram.controllers.activities_notification.TCommentsPhotoActivity;
import com.rovas.forgram.fogram.controllers.activities_notification.TCommentsTxtPhotoActivity;
import com.rovas.forgram.fogram.controllers.activities_notification.TweetsForward;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.GetTimeAgo;
import com.rovas.forgram.fogram.models.Notification;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by Mohamed El Sayed
 */
public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder>{
    private List<Notification> notificationList;
    private Context context;
    public FirebaseAuth mAuth;
    public FirebaseFirestore fStore;
    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

    public NotificationRecyclerViewAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_notification_item,parent,false);
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationRecyclerViewAdapter.ViewHolder holder,final int position) {

        String current_user_id = mAuth.getCurrentUser().getUid();
        final String from = notificationList.get(position).getFrom();
        final String post = notificationList.get(position).getPost();
        final String forward = notificationList.get(position).getForward();
        final String username = notificationList.get(position).getUsername();
        long time = notificationList.get(position).getTimestamp();
        holder.setTime_stamp_message(time);
        final String thumb_image = notificationList.get(position).getThumb_image();
        holder.setUserData(username, thumb_image);
        final String message = notificationList.get(position).getMessage();
        holder.setNotification_message(message);
        if(post.equals("tweet")) {
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent Intent = new Intent(context, TCommentsActivity.class);
                    Intent.putExtra("from", from);
                    Intent.putExtra("thumb_image", thumb_image);
                    Intent.putExtra("time_stamp", time);
                    Intent.putExtra("username", username);
                    Intent.putExtra("forward", forward);
                    Intent.putExtra("message", message);
                    Intent.putExtra("post", post);
                    context.startActivity(Intent);
                }
            });
        }
        else if(post.equals("photo_tweet")) {
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent Intent = new Intent(context, TCommentsTxtPhotoActivity.class);//UnderMain
                    Intent.putExtra("from", from);
                    Intent.putExtra("thumb_image", thumb_image);
                    Intent.putExtra("time_stamp", time);
                    Intent.putExtra("username", username);
                    Intent.putExtra("forward", forward);
                    Intent.putExtra("message", message);
                    Intent.putExtra("post", post);
                    context.startActivity(Intent);
                }
            });
        }
        else if(post.equals("photo")) {
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent Intent = new Intent(context, TCommentsPhotoActivity.class);
                    Intent.putExtra("from", from);
                    Intent.putExtra("thumb_image", thumb_image);
                    Intent.putExtra("time_stamp", time);
                    Intent.putExtra("username", username);
                    Intent.putExtra("forward", forward);
                    Intent.putExtra("message", message);
                    Intent.putExtra("post", post);
                    context.startActivity(Intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private CircleImageView circleImageView;
        private TextView name_sender;
        private TextView message_noti;
        //private ProgressBar progressBar;
        private TextView time_stamp;
        private LinearLayout linearLayout;
        public ViewHolder(View itemView){
            super(itemView);
            view=itemView;
            linearLayout = (LinearLayout)view.findViewById(R.id.box_notification);
            circleImageView = (CircleImageView)view.findViewById(R.id.listview_image);
            name_sender = (TextView)view.findViewById(R.id.listview_name);
            message_noti = (TextView)view.findViewById(R.id.listview_message);
           // progressBar = (ProgressBar)view.findViewById(R.id.progress);
            time_stamp = (TextView)view.findViewById(R.id.listview_time_stamp);
        }

        public void setUserData(String name, String image){

            circleImageView = (CircleImageView)view.findViewById(R.id.listview_image);
            name_sender = (TextView)view.findViewById(R.id.listview_name);

            name_sender.setText(name);


            Glide.with(context).load(image).into(circleImageView);

        }
        public void setNotification_message(String message){

            message_noti = view.findViewById(R.id.listview_message);
            message_noti.setText(message);

        }
        public void setTime_stamp_message(long time){

            GetTimeAgo getTimeAgo = new GetTimeAgo();

            long lastTime = time;

            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, context);

            time_stamp.setText(lastSeenTime);

        }
    }
}