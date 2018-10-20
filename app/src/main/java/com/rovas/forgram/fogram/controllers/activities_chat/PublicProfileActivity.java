package com.rovas.forgram.fogram.controllers.activities_chat;


import android.content.Intent;
import android.os.Bundle;
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
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.UI.ChatUI;
import com.rovas.forgram.fogram.controllers.activities_popup.vProfileActivity;


import static com.rovas.forgram.fogram.Utils.DebugConstants.DEBUG_USER_PRESENCE;

/**
 * Created by Mohamed El Sayed
 */
public class PublicProfileActivity extends AppCompatActivity  {
    private static final String TAG = PublicProfileActivity.class.getName();

    private TextView mToolbarSubTitle;
    private String user_name;
    private String profile_pic;
    private String status;
    private String name;
    private Button profile_visit_btn;
    private String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_acticvity_public_profile);
        user_name = getIntent().getExtras().getString(ChatUI.BUNDLE_CHAT_USERNAME);
        profile_pic = getIntent().getExtras().getString(ChatUI.BUNDLE_CHAT_PROFILE_PIC);
        status = getIntent().getExtras().getString(ChatUI.BUNDLE_CHAT_STATUS);
        name = getIntent().getExtras().getString(ChatUI.BUNDLE_CHAT_NAME);
        user_id = getIntent().getExtras().getString(ChatUI.BUNDLE_CHAT_ID);
        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // fullname as title
        TextView mToolbarTitle = findViewById(R.id.toolbar_title);
        mToolbarTitle.setText(user_name);

        // connection status (online/offline) as subtitle
        mToolbarSubTitle = findViewById(R.id.toolbar_subtitle);
        mToolbarSubTitle.setText("");

        // set user status
        TextView mStatus = findViewById(R.id.status);
        mStatus.setText(status);

        // setname
        TextView mName = findViewById(R.id.name);
        mName.setText(name);

        profile_visit_btn = findViewById(R.id.profile_visit_btn);
        profile_visit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(PublicProfileActivity.this, vProfileActivity.class);
                profileIntent.putExtra("user_id", user_id);
                startActivity(profileIntent);
            }
        });
        // init user profile picture
        initProfilePicture();
    }


    private void initProfilePicture() {
        Log.d(TAG, "initProfilePicture");

        ImageView profilePictureToolbar = (ImageView) findViewById(R.id.image);
        profilePictureToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PublicProfileActivity.this, ProfileImageDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ChatUI.BUNDLE_CHAT_PROFILE_PIC, profile_pic);
                intent.putExtra(ChatUI.BUNDLE_CHAT_USERNAME, user_name);
                startActivity(intent);
            }
        });
        Glide.with(getApplicationContext())
                .load(profile_pic)
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



}