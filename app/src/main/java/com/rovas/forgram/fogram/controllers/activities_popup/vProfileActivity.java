package com.rovas.forgram.fogram.controllers.activities_popup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.UI.PostUI;
import com.rovas.forgram.fogram.base.BaseActivity;
import com.rovas.forgram.fogram.controllers.activities_profile.FollowersActivity;
import com.rovas.forgram.fogram.controllers.activities_profile.FollowingActivity;
import com.rovas.forgram.fogram.controllers.activities_chat.ChatSQLActivity;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.views.BlogRecyclyerAdapter;
import com.rovas.forgram.fogram.models.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class vProfileActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener , BlogRecyclyerAdapter.PublishClickListener {
    private static final String TAG = "vProfileActivity";
    private ImageView mProfileImage;
    private ImageView online_status;
    private TextView mProfileName, mProfileUserName;
    private Button mProfileFollowBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser mCurrent_user;
    private Uri mainImageUri = null;
    private String mCurrent_state;
    private ProgressDialog mProgressDialog;
    //
    private List<BlogPost> blog_list;
    private List<User> user_list;
    //RecycleAdapters
    private BlogRecyclyerAdapter blogRecyclyerAdapter;
    //RecycleViews
    private RecyclerView blog_list_view;
    //
    private LinearLayoutManager mManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String user_id;
    //
    //
    private LinearLayout lFollowing;
    private LinearLayout lFollowers;
    private TextView mPosts, mFollowers, mFollowing;
    //
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_visit_profile_t);
        this.setFinishOnTouchOutside(false);


        user_id = getIntent().getStringExtra("user_id");

        mAuth =FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        online_status = (ImageView) findViewById(R.id.online_status);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileUserName = (TextView) findViewById(R.id.profile_UserName);
        mProfileFollowBtn = (Button) findViewById(R.id.follow_btn);
        //
        if(!mCurrent_user.getUid().equals(user_id))
        {
            mProfileFollowBtn.setVisibility(View.VISIBLE);
        }
        //
        mPosts = (TextView) findViewById(R.id.posts_count);
        mFollowers = (TextView) findViewById(R.id.followers_count);
        mFollowing = (TextView) findViewById(R.id.following_count);
        getFollowersCount();
        getFollowingCount();
        getPostsCount();
        //
        mCurrent_state = "not_followers";
        //

        //
        lFollowers = (LinearLayout) findViewById(R.id.followers_con);
        lFollowing = (LinearLayout) findViewById(R.id.following_con);
        lFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FIntent = new Intent(vProfileActivity.this, FollowersActivity.class);
                FIntent.putExtra("user_id", user_id);
                startActivity(FIntent);
            }
        });
        lFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FIntent = new Intent(vProfileActivity.this, FollowingActivity.class);
                FIntent.putExtra("user_id", user_id);
                startActivity(FIntent);
            }
        });
        //
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        String name = task.getResult().getString("name");
                        String username = task.getResult().getString("username");
                        final String thumb_image = task.getResult().getString("thumb_image");
                        long online = task.getResult().getLong(("online"));

                        mProfileName.setText(name);
                        mProfileUserName.setText("@" + username);
                        mainImageUri = Uri.parse(thumb_image);
                        if(online == 1)
                        {
                            online_status.setVisibility(View.VISIBLE);

                        }
                        else
                        {
                            online_status.setVisibility(View.INVISIBLE);
                        }

                        if(mainImageUri != null) {
                            Glide.with(vProfileActivity.this).load(mainImageUri).into(mProfileImage);
                        }
                    }
                }
            }
        });
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]{"View Picture", "Send message"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(vProfileActivity.this);

                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {
                        fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    if(task.getResult().exists())
                                    {
                                        String username = task.getResult().getString("username");
                                        if(i == 0){
                                            if(mainImageUri != null) {
                                                Intent chatIntent = new Intent(vProfileActivity.this, ImagePopupDetailsActivity.class);
                                                chatIntent.putExtra(PostUI.PROFILE_IMAGE_BUNDLE, mainImageUri.toString());
                                                chatIntent.putExtra(PostUI.PROFILE_USERNAME_BUNDLE, username);
                                                startActivity(chatIntent);
                                            }
                                            else
                                            {
                                                Toast.makeText(vProfileActivity.this, "Empty Profile Image", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        if(i == 1){
                                            Intent chatIntent = new Intent(vProfileActivity.this, ChatSQLActivity.class);
                                            chatIntent.putExtra("user_id", user_id);
                                            chatIntent.putExtra("user_name", username);
                                            startActivity(chatIntent);

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
        });
        fStore.collection("users/" + mCurrent_user.getUid() + "/Followers/").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {

                        fStore.collection("users/" + mCurrent_user.getUid() + "/Following/").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                 if(task.isSuccessful())
                                 {
                                     if(task.getResult().exists())
                                     {
                                         mProfileFollowBtn.setText(getString(R.string.unfollow));
                                         mCurrent_state = "mutual_followers";
                                     }
                                     else
                                     {
                                         mProfileFollowBtn.setText(getString(R.string.unfollow));
                                         mCurrent_state = "single_followers";
                                     }
                                 }
                            }
                        });

                    }
                    else
                        fStore.collection("users/" + mCurrent_user.getUid() + "/Following/").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                     if(task.getResult().exists())
                                     {
                                         mProfileFollowBtn.setText(getString(R.string.unfollow));
                                         mCurrent_state = "single_following";
                                     }
                                     else
                                     {
                                         mProfileFollowBtn.setText(getString(R.string.follow));
                                         mCurrent_state = "not_followers";
                                     }
                                }
                            }
                        });

                }
                else
                {
                    String e = task.getException().getMessage();
                    Toast.makeText(vProfileActivity.this, "FireStore Retrieve Error" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mProgressDialog.dismiss();
        mProfileFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // --------------- NOT FOLLOWERS STATE ------------
                if(mCurrent_state.equals("not_followers"))
                {
                    final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                String thumb_image = task.getResult().getString("thumb_image");
                                String username = task.getResult().getString("username");

                                //
                                HashMap<String, Object> followingMap_ = new HashMap<>();
                                followingMap_.put("time_stamp", timestamp.getTime());
                                followingMap_.put("user_id", user_id);
                                followingMap_.put("thumb_image", thumb_image);
                                followingMap_.put("username", username);
                                fStore.collection("users/" + mCurrent_user.getUid() + "/Following/").document(user_id).set(followingMap_);

                            }
                        }
                    });
                    fStore.collection("users").document(mCurrent_user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                String thumb_image = task.getResult().getString("thumb_image");
                                String username = task.getResult().getString("username");

                                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                HashMap<String, Object> followersMap_ = new HashMap<>();
                                followersMap_.put("time_stamp", timestamp.getTime());
                                followersMap_.put("user_id", mCurrent_user.getUid());
                                followersMap_.put("thumb_image", thumb_image);
                                followersMap_.put("username", username);
                                //

                                fStore.collection("users/" + user_id + "/Followers/").document(mCurrent_user.getUid()).set(followersMap_);
                            }
                        }
                    });
                    mCurrent_state = "single_following";
                    mProfileFollowBtn.setText(getString(R.string.unfollow));
                }
                // --------------- SINGLE FOLLOWING STATE ------------
                else if(mCurrent_state.equals("single_following"))
                {

                    fStore.collection("users/" + mCurrent_user.getUid() + "/Following/").document(user_id).delete();
                    fStore.collection("users/" + user_id + "/Followers/").document(mCurrent_user.getUid()).delete();
                    mCurrent_state = "not_followers";
                    mProfileFollowBtn.setText(getString(R.string.follow));
                }
                // --------------- SINGLE FOLLOWERS STATE ------------
               else if(mCurrent_state.equals("single_followers"))
                {
                    final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    fStore.collection("users").document(mCurrent_user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                String thumb_image = task.getResult().getString("thumb_image");
                                String username = task.getResult().getString("username");

                                //
                                HashMap<String, Object> followersMap_ = new HashMap<>();
                                followersMap_.put("time_stamp", timestamp.getTime());
                                followersMap_.put("user_id", mCurrent_user.getUid());
                                followersMap_.put("thumb_image", thumb_image);
                                followersMap_.put("username", username);
                                fStore.collection("users/" + user_id + "/Followers/").document(mCurrent_user.getUid()).set(followersMap_);

                            }
                        }
                    });

                    fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                String thumb_image = task.getResult().getString("thumb_image");
                                String username = task.getResult().getString("username");

                                //
                                HashMap<String, Object> followingMap_ = new HashMap<>();
                                followingMap_.put("time_stamp", timestamp.getTime());
                                followingMap_.put("user_id", user_id);
                                followingMap_.put("thumb_image", thumb_image);
                                followingMap_.put("username", username);
                                fStore.collection("users/" + mCurrent_user.getUid() + "/Following/").document(user_id).set(followingMap_);
                            }
                        }
                    });

                    mCurrent_state = "mutual_followers";
                    mProfileFollowBtn.setText(getString(R.string.unfollow));
                }
                // --------------- MUTUAL FOLLOWERS STATE ------------
               else if(mCurrent_state.equals("mutual_followers"))
                {
                    fStore.collection("users/" + mCurrent_user.getUid() + "/Following/").document(user_id).delete();
                    fStore.collection("users/" + user_id + "/Followers/").document(mCurrent_user.getUid()).delete();
                    mCurrent_state = "single_followers";
                    mProfileFollowBtn.setText(getString(R.string.follow_back));
                }
            }
        });
        //
        //Recent_posts_Lists
        blog_list = new ArrayList<>();
        //
        blog_list_view = findViewById(R.id.ViewPager_profile);
        blog_list_view.setHasFixedSize(true);
        //
        blogRecyclyerAdapter = new BlogRecyclyerAdapter(blog_list , this);
        //blog_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        //

        mManager = new LinearLayoutManager(vProfileActivity.this);
        //mManager.setReverseLayout(true);
        //mManager.setStackFromEnd(true);
        blog_list_view.setLayoutManager(mManager);
        //
        //
        blog_list_view.setAdapter(blogRecyclyerAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        getData();
        //
    }
    @Override
    public void onRefresh() {
        getData();

    }
    public void getData(){
        try {
            swipeRefreshLayout.setRefreshing(true);
            getAllPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getAllPosts()
    {
        blog_list.clear();
        if(mAuth.getCurrentUser() != null) {

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });
            //CollectionReference collectionReference = fStore.collection("Posts" );
            //collectionReference.whereEqualTo("user_id" , user_id);
            Query f_query =  fStore.collection("Posts").whereEqualTo("user_id" , user_id);
            f_query.addSnapshotListener(vProfileActivity.this ,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            final String blogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withid(blogPostId);
                            blog_list.add(blogPost);

                            String blogUserID = doc.getDocument().getString("user_id");
                            fStore.collection("users").document(blogUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        if(task.getResult().exists()) {
                                            //String username = task.getResult().getString("username");
                                            String thumb_image = task.getResult().getString("thumb_image");
                                            HashMap<String, Object> userMap_ = new HashMap<>();
                                            //userMap_.put("name", username);
                                            userMap_.put("thumb_image", thumb_image);
                                            fStore.collection("Posts").document(blogPostId).update(userMap_);
                                        }


                                    }
                                }
                            });

                            blogRecyclyerAdapter.notifyDataSetChanged();

                        }
                    }


                }
            });


        }
        swipeRefreshLayout.setRefreshing(false);

    }
    private void getFollowersCount(){

        fStore.collection("users").document(user_id).collection("Followers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mFollowers.setText(String.valueOf(task.getResult().size()));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getFollowingCount(){
        fStore.collection("users").document(user_id).collection("Following")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mFollowing.setText(String.valueOf(task.getResult().size()));
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getPostsCount(){
        fStore.collection("user_Posts").document(user_id).collection(user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mPosts.setText(String.valueOf(task.getResult().size()));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onPublishClicked() {
        //startActivity(new Intent(vProfileActivity.this, ShareActivity.class));
    }
}