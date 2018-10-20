package com.rovas.forgram.fogram.controllers.fragments_publish;


import android.accounts.Account;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.base.BaseFragment;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.StringManipulation;
import com.rovas.forgram.fogram.enums.AccountState;
import com.rovas.forgram.fogram.interfaces.OnSelectionListener;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.models.Img;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.views.InstantBackgroundAdapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by Mohamed El Sayed
 */
public class TweetFragment extends BaseFragment {
    private static final String TAG = "TweetFragment";
    //private static final String REQUIRED = ;

    // [START declare_database_ref]
    private FirebaseFirestore fStore;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String user_id;
    // [END declare_database_ref]

    private EditText mTitleField;
    private EditText mBodyField;
    private FloatingActionButton mSubmitButton;
    //
    private RecyclerView instantRecyclerView;
    private InstantBackgroundAdapter initaliseadapter;
    ArrayList<Img> INSTANTLIST = new ArrayList<>();
    //

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);

        // [START initialize_database_ref]
        fStore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        // [END initialize_database_ref]

        mBodyField = view.findViewById(R.id.field_body);
        mSubmitButton = view.findViewById(R.id.fab_submit_post);
        //
        instantRecyclerView = view.findViewById(R.id.instantRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        instantRecyclerView.setLayoutManager(linearLayoutManager);
        initaliseadapter = new InstantBackgroundAdapter(getContext());
        initaliseadapter.addOnSelectionListener(onSelectionListener);
        instantRecyclerView.setAdapter(initaliseadapter);
        AddBackgrounds();

        //
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        ImageView shareClose = (ImageView) view.findViewById(R.id.ivCloseTweet);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the Tweet fragment.");
                getActivity().finish();
            }
        });

        return view;
    }

    private void AddBackgrounds() {
        INSTANTLIST.add(new Img("http://cdn.playbuzz.com/cdn/62b7af36-65b7-41aa-8db2-e34fd8a76acf/62c5efd3-fa55-464b-8ee5-9a3e2543c830.jpg"));
        INSTANTLIST.add(new Img("http://cdn.playbuzz.com/cdn/62b7af36-65b7-41aa-8db2-e34fd8a76acf/62c5efd3-fa55-464b-8ee5-9a3e2543c830.jpg"));
        INSTANTLIST.add(new Img("http://cdn.playbuzz.com/cdn/fa415381-3e73-4678-915d-7abf8983ce09/813d91c3-f7c9-4a20-9e7b-7e7b6da78941.jpg"));
        INSTANTLIST.add(new Img("http://cdn.playbuzz.com/cdn/62b7af36-65b7-41aa-8db2-e34fd8a76acf/1e93e32c-7662-4de7-a441-59d4c29d6faf.jpg"));
        INSTANTLIST.add(new Img("http://cdn.playbuzz.com/cdn/5cb29908-40a5-42d4-831d-5bea595bcf05/3e9f0c63-60c6-4a0c-964c-1302d56295da.jpg"));
        INSTANTLIST.add(new Img("https://pmcfootwearnews.files.wordpress.com/2015/06/michael-jordan-chicago-bulls.jpg"));
        INSTANTLIST.add(new Img("http://healthyceleb.com/wp-content/uploads/2015/03/Michael-Jordan.jpg"));
        INSTANTLIST.add(new Img("http://thelegacyproject.co.za/wp-content/uploads/2015/04/Michael_Jordan_Net_Worth.jpg"));
        INSTANTLIST.add(new Img("http://www.guinnessworldrecords.com/Images/Michael-Jordan-main_tcm25-15662.jpg"));
        INSTANTLIST.add(new Img("http://sportsmockery.com/wp-content/uploads/2015/03/michael-jordan-1600x1200.jpg"));
        initaliseadapter.addImageList(INSTANTLIST);
        initaliseadapter.notifyDataSetChanged();
        setBottomSheetBehavior();
    }

    private void setBottomSheetBehavior() {

    };

    private OnSelectionListener onSelectionListener = new OnSelectionListener()
    {

        @Override
        public void OnClick(Img Img, View view, int position) {
            //ToDOO Here
        }

        @Override
        public void OnLongClick(Img img, View view, int position) {
            //ToDOO Here
        }
    };
    private void submitPost() {
        final String body = mBodyField.getText().toString();


        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(getString(R.string.required));
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText( getActivity(), getString(R.string.posting), Toast.LENGTH_SHORT).show();


        // [START single_value_read]

        String username = UserWiazrd.getInstance().getTempUser().getUsername();
        String thumb_image = UserWiazrd.getInstance().getTempUser().getThumb_image();
        long role = UserWiazrd.getInstance().getTempUser().getRole();
        // [START_EXCLUDE]
        writeNewPost(user_id, username, body , thumb_image , role);
        // Finish this Activity, back to the stream
        setEditingEnabled(true);
        getActivity().finish();
        // [END single_value_read]
        /*
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username, title, body);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
                */
        // [END single_value_read]
    }

    @SuppressLint("RestrictedApi")
    private void setEditingEnabled(boolean enabled) {
        //mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String body , String thumb_image , long role) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        //
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //String tags = StringManipulation.getTags(body);
        //String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        BlogPost blogPost = new BlogPost();
        //blogPost.setDesc("");
        blogPost.setTime_stamp(timestamp.getTime());
        //blogPost.setImage_url("");
        blogPost.setUsername(username);
        blogPost.setThumb_image(thumb_image);
        blogPost.setPost("tweet");
        blogPost.setText_post(body);
        //blogPost.setTags(tags);
        blogPost.setPost_type("1");
        blogPost.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //blogPost.setPhoto_id(newPhotoKey);
        final String randomname = UUID.randomUUID().toString();//generic randomname
        String post_id = username + "@" + timestamp.getTime() ;
        //insert into database
        fStore.collection(this.getString(R.string.dbname_photos)).document(post_id).set(blogPost);
        HashMap<String , Object> user_map = new HashMap<>();
        user_map.put("user_id" , user_id);
        fStore.collection(this.getString(R.string.dbname_user_photos)).document(user_id).collection(user_id).document(post_id).set(user_map);
        long last_count = UserWiazrd.getInstance().getTempUser().getPosts() + 1;
        UserWiazrd.getInstance().getTempUser().setPosts(last_count);//TODO
        if(role == 1 || role ==  2)
        {
            /*
            HashMap<String, String> userMap_ = new HashMap<>();
            userMap_.put("user_id", userId);
            userMap_.put("username", username);
            userMap_.put("type", "Tweet");
            userMap_.put("post_id", post_id);
            userMap_.put("image_url", thumb_image);
            userMap_.put("time_stamp", "" + timestamp.getTime());
            */
            fStore.collection("Admins").document(post_id).set(blogPost);
        }
        /*
        HashMap<String, String> userMap_ = new HashMap<>();
        userMap_.put("author", username);
        userMap_.put("body" ,body);
        userMap_.put("starCount", "0");
        userMap_.put("title" , title);
        userMap_.put("uid", userId);
        fStore.collection("posts").add(userMap_);
        */
        //fStore.collection("user-posts/" + userId +"/").add(userMap_);
        //
        /*
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
        */

    }
    // [END write_fan_out]

}
