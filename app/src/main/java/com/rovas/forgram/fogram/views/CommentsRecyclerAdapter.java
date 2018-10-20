package com.rovas.forgram.fogram.views;

import android.content.Context;
import android.content.Intent;
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
import com.rovas.forgram.fogram.Utils.GetTimeAgo;
import com.rovas.forgram.fogram.controllers.activities_home.CommentsActivity;
import com.rovas.forgram.fogram.controllers.activities_home.ReplyActivity;
import com.rovas.forgram.fogram.controllers.activities_popup.vProfileActivity;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.models.Comments;
import com.rovas.forgram.fogram.models.Reply;
import com.rovas.forgram.fogram.models.User;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Mohamed El Sayed
 */
public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public List<User> user_list;
    public List<Reply> reply_list;
    public Context context;
    public List<BlogPost> blog_list;
    public FirebaseFirestore fStore;
    public FirebaseAuth mAuth;
    private CommentsActivity comment_;
    public CommentsRecyclerAdapter(List<Comments> commentsList){

        this.commentsList = commentsList;
    }

    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String data = prefs.getString("blog_post_id", ""); //no id: default value
        final String user_id = commentsList.get(position).getUser_id();
        String userName = commentsList.get(position).getUsername();
        String userImage = commentsList.get(position).getThumb_image();
        holder.setUserData(userImage);
        long time = commentsList.get(position).getTimestamp();
        holder.setTime(time);

        //final String blogPostId = comment_.getBlog_post_id();
        final String blogPostId = data;
        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);
        final String blog_commentid = commentsList.get(position).BlogCommentID;
        Comments c = commentsList.get(position);
        holder.fillComment(userName, c, holder.commentTextView, holder.comment_time_stamp);
        holder.blogUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(context, vProfileActivity.class);
                commentIntent.putExtra("user_id",  user_id );
                context.startActivity(commentIntent);
            }
        });

        holder.show_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(context, ReplyActivity.class);
                commentIntent.putExtra("blog_post_id",  blogPostId );
                commentIntent.putExtra("comment_id" ,blog_commentid);
                context.startActivity(commentIntent);
            }
        });
        //like comment
        holder.image_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fStore.collection("Posts/" + blogPostId + "/Comments/" + blog_commentid + "/Likes").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){
                            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp",timestamp.getTime());

                            fStore.collection("Posts/" + blogPostId + "/Comments/"+ blog_commentid +"/Likes").document(user_id).set(likesMap);

                        } else {

                            fStore.collection("Posts/" + blogPostId + "/Comments/"+ blog_commentid+"/Likes").document(user_id).delete();

                        }

                    }
                });
            }
        });

        //comment_count
        fStore.collection("Posts/" + blogPostId + "/Comments/"+ blog_commentid +"/Reply").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.UpdateCommentCount(count );

                } else {

                    holder.UpdateCommentCount(0 );

                }

            }
        });
        //Get Likes Count
        fStore.collection("Posts/" + blogPostId + "/Comments/"+ blog_commentid +"/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
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

        //Get Likes
        fStore.collection("Posts/" + blogPostId + "/Comments/"+ blog_commentid + "/Likes").document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    //holder.image_like.setImageDrawable(ContextCompat.getDrawable(context , R.mipmap.action_like_accent));
                    holder.image_like.setPressed(true);

                } else {
                    holder.image_like.setPressed(false);
                    //holder.image_like.setImageDrawable(ContextCompat.getDrawable(context ,R.drawable.ic_like));

                }

            }
        });

        //
        //




    }


    @Override
    public int getItemCount() {

        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView comment_message;
        private TextView image_like;
        private TextView like_text;
        private TextView blogUserName;
        private CircularImageView blogUserImage;
        private TextView comment_time_stamp;
        private final ExpandableTextView commentTextView;
        private TextView show_reply;
        private TextView reply_count;
        // reply
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            commentTextView = (ExpandableTextView) itemView.findViewById(R.id.commentText);
            image_like = mView.findViewById(R.id.comment_like);
            like_text = mView.findViewById(R.id.comment_like_count);
            blogUserImage = mView.findViewById(R.id.Comments_image);
            blogUserName = mView.findViewById(R.id.Comments_username);
            comment_message = mView.findViewById(R.id.Comments_message);
            comment_time_stamp = mView.findViewById(R.id.comment_time_stamp);
            show_reply = mView.findViewById(R.id.comment_reply);
            reply_count = mView.findViewById(R.id.comment_reply_count);
            //

        }
        private void fillComment(String userName, Comments comment, ExpandableTextView commentTextView, TextView dateTextView) {
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

        public void UpdateCommentCount(int count ){
            reply_count.setText(count + " " + context.getString(R.string.comments));
        }

        public void setTime(long time) {

            GetTimeAgo getTimeAgo = new GetTimeAgo();

            long lastTime = time;

            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, context);

            comment_time_stamp.setText(lastSeenTime);

        }
    }
}
