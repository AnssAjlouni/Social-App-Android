package com.rovas.forgram.fogram.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.Utils.Views.CircularImageView;
import com.rovas.forgram.fogram.Utils.Views.ExpandableTweetView;
import com.rovas.forgram.fogram.Utils.FormatterUtil;
import com.rovas.forgram.fogram.Utils.GetTimeAgo;
import com.rovas.forgram.fogram.Utils.UI.PostUI;
import com.rovas.forgram.fogram.Utils.tagGroup.TagGroup;
import com.rovas.forgram.fogram.controllers.activities_home.CommentsActivity;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.controllers.activities_home.CommentsPhotoActivity;
import com.rovas.forgram.fogram.controllers.activities_home.CommentsTxtPhotoActivity;
import com.rovas.forgram.fogram.controllers.activities_home.ImagePostDetailsActivity;
import com.rovas.forgram.fogram.controllers.activities_popup.vProfileActivity;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.managers.tags_DB.TagsManager;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.models.User;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by Mohamed El Sayed
 */
class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

    private static final String TAG = "SingleTapConfirm";
    private BlogPost c;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String blogPostId;
    private String currentUserId;
    SingleTapConfirm(String blogPostId ,BlogPost _c)
    {
        this.blogPostId = blogPostId;
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        this.c = _c;
    }
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.e(TAG, "onSingleTapConfirmed"); // never called..
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.e(TAG, "onDoubleTap"); // never called..
        if(c != null)
        {
            Log.d(TAG, "onDoubleTap: Not nUll");
            //holder.reactionView.show(holder.motionEvent);
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(!task.getResult().exists()){
                        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        Map<String, Object> likesMap = new HashMap<>();
                        likesMap.put("timestamp", timestamp.getTime());

                        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                        if(!currentUserId.equals(c.getUser_id())) {
                            firebaseFirestore.collection("users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {
                                        final String userName = task.getResult().getString("username");
                                        final String thumb_image = task.getResult().getString("thumb_image");
                                        firebaseFirestore.collection("users/" + c.getUser_id() + "/Notifications/").document(blogPostId + "%" + userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if(!task.getResult().exists())
                                                {
                                                    String message_noti = userName + " Liked your post";
                                                    Map<String, Object> notificatinMap = new HashMap<>();
                                                    notificatinMap.put("message", message_noti);
                                                    notificatinMap.put("from", currentUserId);
                                                    notificatinMap.put("username", userName);
                                                    notificatinMap.put("thumb_image", thumb_image);
                                                    notificatinMap.put("post", "photo");
                                                    notificatinMap.put("type", "like");
                                                    notificatinMap.put("forward", blogPostId);
                                                    notificatinMap.put("timestamp", timestamp.getTime());
                                                    firebaseFirestore.collection("users/" + c.getUser_id() + "/Notifications/").document(blogPostId + "%" + userName).set(notificatinMap);
                                                }
                                                else
                                                {
                                                    //firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "@" + userName).delete();
                                                }
                                            }
                                        });



                                    } else {

                                        //Firebase Exception

                                    }

                                }
                            });
                        }

                    } else {
                        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                        firebaseFirestore.collection("users").document(c.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if(task.isSuccessful()){

                                    String userName = task.getResult().getString("username");
                                    firebaseFirestore.collection("users/" + c.getUser_id() + "/Notifications/").document(blogPostId + "%" + userName).delete();
                                }
                            }
                        });

                    }

                }
            });
        }
       // View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
       // int position = recyclerView.getChildPosition(view);
        return super.onDoubleTap(e);
    }
}
public class BlogRecyclyerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<BlogPost> blog_list;
    public List<User> user_list;
    public Context context;
    //
    private static final int Ordinary_Tweets = 1;
    private static final int Ordinary_Photo = 2;
    private static final int Video_Post = 3;
    private static final int Image_Text = 4;
    private static final int Image_Collections = 5;
    private static final int ADS = 6;
    //
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private final PublishClickListener publishClickListener;
    private GestureDetectorCompat gestureDetector;
    public interface PublishClickListener {
        void onPublishClicked();
    }
    public BlogRecyclyerAdapter(List<BlogPost> blog_list , PublishClickListener publishClickListener){
        // in your adapter constructor
        this.blog_list = blog_list;
        this.publishClickListener = publishClickListener;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case Ordinary_Tweets:
                view = inflater.inflate(R.layout.single_list_item_tweets_t, parent, false);//ForTEST
                holder = new TweetsViewHolder(view);
                break;
            case Image_Text:
                view = inflater.inflate(R.layout.single_list_item_image_text, parent, false);
                holder = new ImagePhotoViewHolder(view);
                break;
            case Ordinary_Photo:
                view = inflater.inflate(R.layout.single_list_item_odrinary_photo_t, parent, false);
                holder = new PhotosViewHolder(view);
                break;

            default:
                view = inflater.inflate(R.layout.single_list_item_tweets_t, parent, false);
                holder = new TweetsViewHolder(view);
                break;
        }


        return holder;

    }
    @Override
    public int getItemViewType(int position) {
        //0 for ordinary_posts
        //1 for ordinary_photos
        //2 for image_text
        //3 for video
        //4 for image_collections
        //5 for ads

        //return -1;
        String data_type = blog_list.get(position).getPost_type();

        //return -1;
        return Integer.parseInt(data_type);

    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == Ordinary_Tweets)
        {
            onBindViewHolder_Tweets((TweetsViewHolder) holder, position);
        }
        else if(getItemViewType(position) == Image_Text)
        {
            onBindViewHolder_ImagePhoto((ImagePhotoViewHolder) holder, position);
        }
        else if(getItemViewType(position) == Ordinary_Photo)
        {
            onBindViewHolder_Photos((PhotosViewHolder) holder, position);
        }
    }
    //Different_methods
    public void onBindViewHolder_Tweets(final TweetsViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
        //User_id
        final String Ownder_id = blog_list.get(position).getUser_id();
        final String blogPostId = blog_list.get(position).BlogPostID;
        final String currentUserId = mAuth.getCurrentUser().getUid();

        //SharedPreferences
        SharedPreferences prefs_c_p = PreferenceManager.getDefaultSharedPreferences(context);
        String data_c_p = prefs_c_p.getString("blog_user_icon", ""); //no id: default value
        final String blog_user_icon = data_c_p;
        /*
        //Tweets_Text
        final String desc_post = blog_list.get(position).getText_post();
        holder.setDescText_Post(desc_post);
        */
        //UserInfo
        String userName = blog_list.get(position).getUsername();
        final String userImage = blog_list.get(position).getThumb_image();
        holder.setUserData(userName , userImage);
        //Time_Stamp[Date]
        final long time = blog_list.get(position).getTime_stamp();
        holder.setTime(time);

        BlogPost c = blog_list.get(position);
        holder.fillTweet("",c, holder.expandableTweetView);
        ///////////////////////////////////////////////////////////////////////
        //Likes Feature
        holder.blogLikeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //holder.reactionView.show(holder.motionEvent);
                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){
                            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", timestamp.getTime());
                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                            if(!currentUserId.equals(Ownder_id)) {
                                firebaseFirestore.collection("users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            final String userName = task.getResult().getString("username");
                                            final String thumb_image = task.getResult().getString("thumb_image");
                                            firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "%" + userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    if(!task.getResult().exists())
                                                    {
                                                        String message_noti = userName + " Liked your post";
                                                        Map<String, Object> notificatinMap = new HashMap<>();
                                                        notificatinMap.put("message", message_noti);
                                                        notificatinMap.put("from", currentUserId);
                                                        notificatinMap.put("username", userName);
                                                        notificatinMap.put("thumb_image", thumb_image);
                                                        notificatinMap.put("post", "tweet");
                                                        notificatinMap.put("type", "like");
                                                        notificatinMap.put("forward", blogPostId);
                                                        notificatinMap.put("timestamp", timestamp.getTime());
                                                        firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "%" + userName).set(notificatinMap);
                                                    }
                                                    else
                                                    {
                                                        //firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "@" + userName).delete();
                                                    }
                                                }
                                            });



                                        } else {

                                            //Firebase Exception

                                        }

                                    }
                                });
                            }

                        } else {
                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                            firebaseFirestore.collection("users").document(Ownder_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){

                                        String userName = task.getResult().getString("username");
                                        firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "%" + userName).delete();
                                    }
                                }
                            });

                        }

                    }
                });
            }
        });
        //Get Likes icon
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener((Activity) context,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.blogLikeBtn.setImageDrawable(ContextCompat.getDrawable(context , R.mipmap.action_like_accent));

                } else {

                    holder.blogLikeBtn.setImageDrawable(ContextCompat.getDrawable(context ,R.drawable.ic_like));


                }

            }
        });
        //Comment Feature
        holder.blogCommentBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra(PostUI.POST_COMMENT_BUNDLE, (Serializable) c);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);

            }
        });
        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener((Activity)context , new EventListener<QuerySnapshot>() {
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
        //Comments_Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener( (Activity) context , new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.UpdateCommentCount(count , blog_user_icon);

                } else {

                    holder.UpdateCommentCount(0 , blog_user_icon);

                }

            }
        });

        //Comment_Intent
        holder.blogUserImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Ownder_id.equals(currentUserId)) {
                    Intent commentIntent = new Intent(context, vProfileActivity.class);
                    commentIntent.putExtra("user_id", Ownder_id);
                    context.startActivity(commentIntent);
                }

            }
        });
        ///////////////////////////////////////////////////////////////////////

        holder.more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display option menu

                android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(context, holder.more_btn);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.mnu_item_save:
                                Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.mnu_item_delete: {
                                //Delete item
                                if (Ownder_id.equals(currentUserId))
                                {
                                    firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            notifyDataSetChanged();
                                            firebaseFirestore.collection(context.getString(R.string.dbname_user_photos)).document(currentUserId).collection(currentUserId).document(blogPostId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                                                    long last_count = UserWiazrd.getInstance().getTempUser().getPosts() - 1;
                                                    UserWiazrd.getInstance().getTempUser().setPosts(last_count);//TODO
                                                }
                                            });

                                        }
                                    });
                                    break;
                                }
                            }
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        //////////////////////////////////////////////////////////////////////
        holder.Tags();
    }
    public void onBindViewHolder_ImagePhoto(final ImagePhotoViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
        //User_id
        final String Ownder_id = blog_list.get(position).getUser_id();
        final String blogPostId = blog_list.get(position).BlogPostID;
        final String currentUserId = mAuth.getCurrentUser().getUid();

        //SharedPreferences
        SharedPreferences prefs_c_p = PreferenceManager.getDefaultSharedPreferences(context);
        String data_c_p = prefs_c_p.getString("blog_user_icon", ""); //no id: default value
        final String blog_user_icon = data_c_p;

        //Tweets_Text
        final String desc_post = blog_list.get(position).getText_post();
        holder.setDescText_Post(desc_post);

        //UserInfo
        String userName = blog_list.get(position).getUsername();
        final String userImage = blog_list.get(position).getThumb_image();
        holder.setUserData(userName, userImage);

        //Time_Stamp[Date]
        final long time = blog_list.get(position).getTime_stamp();
        holder.setTime(time);
        ///////////////////////////////////////////////////////////////////////
        //Likes Feature
        holder.blogLikeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //holder.reactionView.show(holder.motionEvent);
                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){
                            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", timestamp.getTime());

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                            if(!currentUserId.equals(Ownder_id)) {
                                firebaseFirestore.collection("users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            final String userName = task.getResult().getString("username");
                                            final String thumb_image = task.getResult().getString("thumb_image");
                                            firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "%" + userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    if(!task.getResult().exists())
                                                    {
                                                        String message_noti = userName + " Liked your post";
                                                        Map<String, Object> notificatinMap = new HashMap<>();
                                                        notificatinMap.put("message", message_noti);
                                                        notificatinMap.put("username", userName);
                                                        notificatinMap.put("from", currentUserId);
                                                        notificatinMap.put("thumb_image", thumb_image);
                                                        notificatinMap.put("post", "photo_tweet");
                                                        notificatinMap.put("type", "like");
                                                        notificatinMap.put("forward", blogPostId);
                                                        notificatinMap.put("timestamp", timestamp.getTime());
                                                        firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "%" + userName).set(notificatinMap);
                                                    }
                                                    else
                                                    {
                                                        //firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "@" + userName).delete();
                                                    }
                                                }
                                            });



                                        } else {

                                            //Firebase Exception

                                        }

                                    }
                                });
                            }

                        } else {
                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                            firebaseFirestore.collection("users").document(Ownder_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){

                                        String userName = task.getResult().getString("username");
                                        firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "%" + userName).delete();
                                    }
                                }
                            });

                        }

                    }
                });
            }
        });
        //Get Likes icon
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener((Activity) context,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.blogLikeBtn.setImageDrawable(ContextCompat.getDrawable(context , R.mipmap.action_like_accent));

                } else {

                    holder.blogLikeBtn.setImageDrawable(ContextCompat.getDrawable(context ,R.drawable.ic_like));


                }

            }
        });
        //Comment Feature
        BlogPost c = blog_list.get(position);
        holder.blogCommentBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsTxtPhotoActivity.class);
                commentIntent.putExtra(PostUI.POST_COMMENT_BUNDLE, (Serializable) c);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);

            }
        });
        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener( (Activity) context , new EventListener<QuerySnapshot>() {
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
        //Comment_Intent
        holder.blogUserImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Ownder_id.equals(currentUserId)) {
                    Intent commentIntent = new Intent(context, vProfileActivity.class);
                    commentIntent.putExtra("user_id", Ownder_id);
                    context.startActivity(commentIntent);
                }

            }
        });
        //Comments_Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener( (Activity) context ,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.UpdateCommentCount(count , blog_user_icon);

                } else {

                    holder.UpdateCommentCount(0 , blog_user_icon);

                }

            }
        });
        ///////////////////////////////////////////////////////////////////////
        holder.more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display option menu

                android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(context, holder.more_btn);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.mnu_item_save:
                                Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.mnu_item_delete: {
                                //Delete item
                                if (Ownder_id.equals(currentUserId))
                                {
                                    firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            notifyDataSetChanged();
                                            firebaseFirestore.collection(context.getString(R.string.dbname_user_photos)).document(currentUserId).collection(currentUserId).document(blogPostId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                                                    long last_count = UserWiazrd.getInstance().getTempUser().getPosts() - 1;
                                                    UserWiazrd.getInstance().getTempUser().setPosts(last_count);//TODO
                                                }
                                            });

                                        }
                                    });
                                    break;
                                }
                            }
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        //////////////////////////////////////////////////////////////////////
        holder.Tags();
    }
    public void onBindViewHolder_Photos(final PhotosViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
        //User_id
        final String Ownder_id = blog_list.get(position).getUser_id();
        final String blogPostId = blog_list.get(position).BlogPostID;
        final String currentUserId = mAuth.getCurrentUser().getUid();

        //SharedPreferences
        SharedPreferences prefs_c_p = PreferenceManager.getDefaultSharedPreferences(context);
        String data_c_p = prefs_c_p.getString("blog_user_icon", ""); //no id: default value
        final String blog_user_icon = data_c_p;
        /*
        //Tweets_Text
        final String desc_post = blog_list.get(position).getDesc();
        holder.setDescText_Post(desc_post);
        */
        //BlogPost
        String image_url = blog_list.get(position).getImage_url();
        holder.setBlogImage(image_url);

        //UserInfo
        String userName = blog_list.get(position).getUsername();
        final String userImage = blog_list.get(position).getThumb_image();
        holder.setUserData(userName, userImage);

        //Time_Stamp[Date]
        final long time = blog_list.get(position).getTime_stamp();
        holder.setTime(time);
        BlogPost c = blog_list.get(position);
        gestureDetector = new GestureDetectorCompat((Activity) context, new SingleTapConfirm( blogPostId, c));
        holder.fillTweet("",c, holder.expandableTweetView , image_url);
        ///////////////////////////////////////////////////////////////////////
        //Likes Feature
        holder.blogPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        holder.blogLikeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //holder.reactionView.show(holder.motionEvent);
                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){
                            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", timestamp.getTime());

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                            if(!currentUserId.equals(Ownder_id)) {
                                firebaseFirestore.collection("users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            final String userName = task.getResult().getString("username");
                                            final String thumb_image = task.getResult().getString("thumb_image");
                                            firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "%" + userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    if(!task.getResult().exists())
                                                    {
                                                        String message_noti = userName + " Liked your post";
                                                        Map<String, Object> notificatinMap = new HashMap<>();
                                                        notificatinMap.put("message", message_noti);
                                                        notificatinMap.put("from", currentUserId);
                                                        notificatinMap.put("username", userName);
                                                        notificatinMap.put("thumb_image", thumb_image);
                                                        notificatinMap.put("post", "photo");
                                                        notificatinMap.put("type", "like");
                                                        notificatinMap.put("forward", blogPostId);
                                                        notificatinMap.put("timestamp", timestamp.getTime());
                                                        firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "%" + userName).set(notificatinMap);
                                                    }
                                                    else
                                                    {
                                                        //firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "@" + userName).delete();
                                                    }
                                                }
                                            });



                                        } else {

                                            //Firebase Exception

                                        }

                                    }
                                });
                            }

                        } else {
                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                            firebaseFirestore.collection("users").document(Ownder_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){

                                        String userName = task.getResult().getString("username");
                                        firebaseFirestore.collection("users/" + Ownder_id + "/Notifications/").document(blogPostId + "%" + userName).delete();
                                    }
                                }
                            });

                        }

                    }
                });
            }
        });
        //Get Likes icon
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener((Activity) context,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.blogLikeBtn.setImageDrawable(ContextCompat.getDrawable(context , R.mipmap.action_like_accent));

                } else {

                    holder.blogLikeBtn.setImageDrawable(ContextCompat.getDrawable(context ,R.drawable.ic_like));

                }

            }
        });
        //Comment Feature
        holder.blogCommentBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsPhotoActivity.class);
                commentIntent.putExtra(PostUI.POST_COMMENT_BUNDLE, (Serializable) c);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);

            }
        });
        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener((Activity) context , new EventListener<QuerySnapshot>() {
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
        //Comment_Intent
        holder.blogUserImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Ownder_id.equals(currentUserId)) {
                    Intent commentIntent = new Intent(context, vProfileActivity.class);
                    commentIntent.putExtra("user_id", Ownder_id);
                    context.startActivity(commentIntent);
                }

            }
        });
        //Comments_Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener((Activity) context , new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.UpdateCommentCount(count , blog_user_icon);

                } else {

                    holder.UpdateCommentCount(0 , blog_user_icon);

                }

            }
        });
        ///////////////////////////////////////////////////////////////////////
        holder.more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display option menu

                android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(context, holder.more_btn);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.mnu_item_save:
                                Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.mnu_item_delete: {
                                //Delete item
                                if (Ownder_id.equals(currentUserId))
                                {
                                    firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            notifyDataSetChanged();
                                            firebaseFirestore.collection(context.getString(R.string.dbname_user_photos)).document(currentUserId).collection(currentUserId).document(blogPostId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                                                    long last_count = UserWiazrd.getInstance().getTempUser().getPosts() - 1;
                                                    UserWiazrd.getInstance().getTempUser().setPosts(last_count);//TODO
                                                }
                                            });

                                        }
                                    });
                                    break;
                                }
                            }
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        BlogPost blogPost = blog_list.get(position);
        holder.blogPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImagePostDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(PostUI.POST_IMAGE_BUNDLE, (Serializable) blogPost);
                context.startActivity(intent);
            }
        });
        //////////////////////////////////////////////////////////////////////
        holder.Tags();
    }



    @Override
    public int getItemCount() {

        return blog_list.size();
    }
    public class TweetsViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView blogUserName;
        private CircularImageView blogUserImage;
        private TextView blogDate;
        private ImageButton more_btn;

        private ImageView blogLikeBtn;
        private ImageView blogCommentBtn;
        private TextView blogLikeCount;
        private TextView blogCommentCount;


        private TextView blogTweet;
        //Tags
        private TagGroup mSmallTagGroup;
        private TagsManager mTagsManager;

        private ExpandableTweetView expandableTweetView;
        private TagGroup.OnTagClickListener mTagClickListener = new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                Toast.makeText(context, tag, Toast.LENGTH_SHORT).show();
            }
        };
        public TweetsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //Upper_Layout
            blogUserName = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogDate = mView.findViewById(R.id.blog_date);
            more_btn = mView.findViewById(R.id.imageButton_more);
            //Middle_Layout
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);
            blogCommentCount = mView.findViewById(R.id.blog_comment_count);

            //Content
            blogTweet = mView.findViewById(R.id.blog_Tweet);
            //Tags
            mSmallTagGroup = (TagGroup) mView.findViewById(R.id.tag_group_small);
            expandableTweetView = (ExpandableTweetView) itemView.findViewById(R.id.tweetText);

        }
        private void fillTweet( String userName ,BlogPost blogPost, ExpandableTweetView tweetTextView) {
            Spannable contentString = new SpannableStringBuilder(userName + "   " + blogPost.getText_post());
            contentString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highlight_text)),
                    0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tweetTextView.setText(contentString);
            CharSequence date = FormatterUtil.getRelativeTimeSpanString(context, blogPost.getTime_stamp());
            blogDate.setText(date);
        }
        public void Tags()
        {
            mTagsManager = TagsManager.getInstance(context);
            String[] tags = mTagsManager.getTags();
            if (tags != null && tags.length > 0) {
                mSmallTagGroup.setTags(tags);
            }
            mSmallTagGroup.setOnTagClickListener(mTagClickListener);
        }
        public void setDescText_Post(String descText){

            blogTweet.setText(descText);

        }
        public void setTime(long time) {

            GetTimeAgo getTimeAgo = new GetTimeAgo();

            long lastTime = time;

            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, context);

            blogDate.setText(lastSeenTime);

        }

        public void setUserData(String name, String image){

            blogUserName.setText(name);
            Glide.with(context).load(image).into(blogUserImage);

        }
        public void setUserData(String image){
            Glide.with(context).load(image).into(blogUserImage);

        }

        public void updateLikesCount(int count){

            blogLikeCount.setText(count + " " +context.getString(R.string.likes));

        }
        public void UpdateCommentCount(int count , String image){
            blogCommentCount.setText(count + " " +context.getString(R.string.comments));
        }



    }
    public class ImagePhotoViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView blogUserName;
        private CircleImageView blogUserImage;
        private TextView blogDate;
        private ImageButton more_btn;

        private ImageView blogLikeBtn;
        private ImageView blogCommentBtn;
        private TextView blogLikeCount;
        private TextView blogCommentCount;

        private TextView blogTweet;
        //Tags
        private TagGroup mSmallTagGroup;
        private TagsManager mTagsManager;

        private TagGroup.OnTagClickListener mTagClickListener = new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                Toast.makeText(context, tag, Toast.LENGTH_SHORT).show();
            }
        };
        public ImagePhotoViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //Upper_Layout
            blogUserName = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogDate = mView.findViewById(R.id.blog_date);
            more_btn = mView.findViewById(R.id.imageButton_more);
            //Middle_Layout
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);
            blogCommentCount = mView.findViewById(R.id.blog_comment_count);
            //Content
            blogTweet = mView.findViewById(R.id.blog_Tweet);
            //Tags
            mSmallTagGroup = (TagGroup) mView.findViewById(R.id.tag_group_small);

        }

        public void Tags()
        {
            mTagsManager = TagsManager.getInstance(context);
            String[] tags = mTagsManager.getTags();
            if (tags != null && tags.length > 0) {
                mSmallTagGroup.setTags(tags);
            }
            mSmallTagGroup.setOnTagClickListener(mTagClickListener);
        }
        public void setDescText_Post(String descText){

            blogTweet.setText(descText);

        }
        public void setTime(long time) {

            GetTimeAgo getTimeAgo = new GetTimeAgo();

            long lastTime = time;

            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, context);

            blogDate.setText(lastSeenTime);

        }

        public void setUserData(String name, String image){

            blogUserName.setText(name);
            Glide.with(context).load(image).into(blogUserImage);

        }

        public void updateLikesCount(int count){

            blogLikeCount.setText(count +" " + context.getString(R.string.likes));

        }
        public void UpdateCommentCount(int count , String image){
            blogCommentCount.setText(count + " " +context.getString(R.string.comments));
        }



    }
    public class PhotosViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView blogUserName;
        private CircularImageView blogUserImage;
        private TextView blogDate;
        private ImageButton more_btn;

        private ImageView blogLikeBtn;
        private ImageView blogCommentBtn;
        private TextView blogLikeCount;
        private TextView blogCommentCount;

        private TextView blogTweet;
        private ImageView blogPhoto;

        View parentLayout;

        //Tags
        private TagGroup mSmallTagGroup;
        private TagsManager mTagsManager;
        private ExpandableTweetView expandableTweetView;

        private TagGroup.OnTagClickListener mTagClickListener = new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                Toast.makeText(context, tag, Toast.LENGTH_SHORT).show();
            }
        };

        public PhotosViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //Upper_Layout
            blogUserName = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogDate = mView.findViewById(R.id.blog_date);
            more_btn = mView.findViewById(R.id.imageButton_more);
            //Middle_Layout
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);
            blogCommentCount = mView.findViewById(R.id.blog_comment_count);
            //Content
            blogTweet = mView.findViewById(R.id.blog_Tweet);
            blogPhoto = mView.findViewById(R.id.blog_image);
            //
            parentLayout = mView.findViewById(R.id.snackbar_action);
            //Tags
            mSmallTagGroup = (TagGroup) mView.findViewById(R.id.tag_group_small);
            expandableTweetView = (ExpandableTweetView) itemView.findViewById(R.id.tweetText);

        }
        private void fillTweet( String userName ,BlogPost blogPost, ExpandableTweetView tweetTextView , String ImageURL) {
            Spannable contentString = new SpannableStringBuilder(userName + "   " + blogPost.getText_post());
            contentString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highlight_text)),
                    0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tweetTextView.setText(contentString);
            CharSequence date = FormatterUtil.getRelativeTimeSpanString(context, blogPost.getTime_stamp());
            blogDate.setText(date);
            //Glide.with(context).load(blogPost.getImage_url()).into(blogPhoto);
        }

        public void Tags()
        {
            mTagsManager = TagsManager.getInstance(context);
            String[] tags = mTagsManager.getTags();
            if (tags != null && tags.length > 0) {
                mSmallTagGroup.setTags(tags);
            }
            mSmallTagGroup.setOnTagClickListener(mTagClickListener);
        }
        public void setBlogImage(String downloadUri){

            Glide.with(context).load(downloadUri).into(blogPhoto);

        }
        public void setDescText_Post(String descText){

            blogTweet.setText(descText);

        }
        public void setTime(long time) {

            GetTimeAgo getTimeAgo = new GetTimeAgo();

            long lastTime = time;

            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, context);

            blogDate.setText(lastSeenTime);

        }

        public void setUserData(String name, String image){

            blogUserName.setText(name);
            Glide.with(context).load(image).into(blogUserImage);

        }

        public void updateLikesCount(int count){

            blogLikeCount.setText(count +" " + context.getString(R.string.likes));

        }
        public void UpdateCommentCount(int count , String image){
            blogCommentCount.setText(count +" " + context.getString(R.string.comments));
        }



    }

}
