package com.rovas.forgram.fogram.controllers.activities_chatgroup;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.UI.ChatUI;
import com.rovas.forgram.fogram.controllers.activities_chat.ProfileImageDetailsActivity;
import com.rovas.forgram.fogram.controllers.activities_popup.vProfileActivity;

/**
 * Created by Mohamed El Sayed
 */
public class PublicGroupProfileActivity extends AppCompatActivity  {
    private static final String TAG = PublicGroupProfileActivity.class.getName();

    private TextView mToolbarSubTitle;
    private String user_name;
    private String profile_pic;
    private String status;
    private String name;
    private Button profile_visit_btn;
    private String user_id;
    //=========== FireBase =============
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_acticvity_public_profile);
        user_name = getIntent().getExtras().getString(ChatUI.BUNDLE_CHAT_USERNAME);
        user_id = getIntent().getExtras().getString(ChatUI.BUNDLE_CHAT_ID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // fullname as title
        TextView mToolbarTitle = findViewById(R.id.toolbar_title);

        // connection status (online/offline) as subtitle
        mToolbarSubTitle = findViewById(R.id.toolbar_subtitle);
        mToolbarSubTitle.setText("");

        // set user status
        TextView mStatus = findViewById(R.id.status);

        // setname
        TextView mName = findViewById(R.id.name);

        profile_visit_btn = findViewById(R.id.profile_visit_btn);
        profile_visit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(PublicGroupProfileActivity.this, vProfileActivity.class);
                profileIntent.putExtra("user_id", user_id);
                startActivity(profileIntent);
            }
        });
        // init user profile picture
        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        setName(task.getResult().getString("name"));
                        setUser_name(task.getResult().getString("username"));
                        setStatus(task.getResult().getString("status"));
                        setProfile_pic(task.getResult().getString("thumb_image"));
                        mToolbarTitle.setText(getUser_name());
                        mStatus.setText(getStatus());
                        mName.setText(getName());
                        initProfilePicture();
                    }
                }
            }
        });
    }


    private void initProfilePicture() {
        Log.d(TAG, "initProfilePicture");

        ImageView profilePictureToolbar = (ImageView) findViewById(R.id.image);
        profilePictureToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PublicGroupProfileActivity.this, ProfileImageDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ChatUI.BUNDLE_CHAT_PROFILE_PIC, getProfile_pic());
                intent.putExtra(ChatUI.BUNDLE_CHAT_USERNAME, getUser_name());
                startActivity(intent);
            }
        });
        Glide.with(getApplicationContext())
                .load(getProfile_pic())
                .into(profilePictureToolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}