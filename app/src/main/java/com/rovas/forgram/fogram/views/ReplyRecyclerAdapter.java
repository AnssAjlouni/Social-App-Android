package com.rovas.forgram.fogram.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.Views.CircularImageView;
import com.rovas.forgram.fogram.Utils.Views.ExpandableTextView;
import com.rovas.forgram.fogram.Utils.FormatterUtil;
import com.rovas.forgram.fogram.controllers.activities_home.CommentsActivity;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.models.Reply;
import com.rovas.forgram.fogram.models.User;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Mohamed El Sayed
 */
public class ReplyRecyclerAdapter extends RecyclerView.Adapter<ReplyRecyclerAdapter.ViewHolder> {

    public List<Reply> reply_list;
    public List<User> user_list;
    public Context context;
    public List<BlogPost> blog_list;
    public FirebaseFirestore fStore;
    public FirebaseAuth mAuth;
    private CommentsActivity comment_;
    public ReplyRecyclerAdapter(List<Reply> reply_list , List<User> user_list , List<BlogPost> blog_list){

        this.reply_list = reply_list;
        this.user_list = user_list;
        this.blog_list = blog_list;

    }

    @Override
    public ReplyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_list_item, parent, false);
        context = parent.getContext();
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return new ReplyRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReplyRecyclerAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        SharedPreferences prefs_ = PreferenceManager.getDefaultSharedPreferences(context);
        String data = prefs_.getString("blog_post_id", ""); //no id: default value
        final String blogPostId = data;

        SharedPreferences prefs_c = PreferenceManager.getDefaultSharedPreferences(context);
        String data_c = prefs_c.getString("comment_post_id", ""); //no id: default value
        final String blog_commentid = data_c;

        String commentMessage = reply_list.get(position).getMessage();
        holder.setComment_message(commentMessage);
        final String reply_commentid = reply_list.get(position).ReplyCommentID;
        /*
        String userName = user_list.get(position).getName();
        String userImage = user_list.get(position).getImage();
        holder.setUserData(userName, userImage);
        */
        final String user_id = reply_list.get(position).getUser_id();

        String userName = reply_list.get(position).getUsername();
        String userImage = reply_list.get(position).getThumb_image();
        holder.setUserData(userImage);
        Reply c = reply_list.get(position);
        holder.fillComment(userName, c, holder.commentTextView, holder.comment_time_stamp);
        //blog_commentid
        holder.image_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fStore.collection("Posts/" + blogPostId + "/Comments/" + blog_commentid + "/Reply/"+reply_commentid +"/Likes").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){
                            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", timestamp.getTime());

                            fStore.collection("Posts/" + blogPostId + "/Comments/"+ blog_commentid +"/Reply/"+reply_commentid+"/Likes").document(user_id).set(likesMap);

                        } else {

                            fStore.collection("Posts/" + blogPostId + "/Comments/"+ blog_commentid+"/Reply/"+reply_commentid+"/Likes").document(user_id).delete();

                        }

                    }
                });
            }
        });
        //Get Likes

        fStore.collection("Posts/" + blogPostId + "/Comments/"+ blog_commentid + "/Reply/"+reply_commentid+"/Likes").document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){
                    holder.image_like.setPressed(true);

                } else {
                    holder.image_like.setPressed(false);

                }

            }
        });
        //Get Likes Count

        fStore.collection("Posts/" + blogPostId + "/Comments/"+ blog_commentid +"/Reply/"+reply_commentid+"/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updateLikesCount(count);

                } else {

                    holder.updateLikesCount(0);

                }

            }
        });

    }


    @Override
    public int getItemCount() {

        if(reply_list != null) {

            return reply_list.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private final ExpandableTextView commentTextView;
        private TextView comment_message;
        private TextView image_like;
        private TextView like_text;
        private TextView comment_time_stamp;
        private TextView blogUserName;
        private CircularImageView blogUserImage;
        private TextView blogcommentcount;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            commentTextView = (ExpandableTextView) itemView.findViewById(R.id.commentText);
            image_like = mView.findViewById(R.id.comment_like);
            blogUserImage = mView.findViewById(R.id.Comments_image);
            blogUserName = mView.findViewById(R.id.Comments_username);
            like_text = mView.findViewById(R.id.comment_like_count);
            comment_message = mView.findViewById(R.id.Comments_message);
            comment_time_stamp = mView.findViewById(R.id.comment_time_stamp);
        }
        private void fillComment(String userName, Reply comment, ExpandableTextView commentTextView, TextView dateTextView) {
            Spannable contentString = new SpannableStringBuilder(userName + "   " + comment.getMessage());
            contentString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highlight_text)),
                    0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            commentTextView.setText(contentString);

            CharSequence date = FormatterUtil.getRelativeTimeSpanString(context, comment.getTimestamp());
            dateTextView.setText(date);
        }
        public void setUserData(String name, String image){


            blogUserName.setText(name);

            if(image != null) {
                Glide.with(context).load(image).into(blogUserImage);
            }

        }
        public void setUserData(String image){
            if(image != null) {
                Glide.with(context).load(image).into(blogUserImage);
            }

        }
        public void setComment_message(String message){

            comment_message.setText(message);

        }
        public void updateLikesCount(int count){

            like_text.setText(count + " " + context.getString(R.string.likes));

        }

    }
}
