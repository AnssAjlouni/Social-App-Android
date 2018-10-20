package com.rovas.forgram.fogram.controllers.activities_chat;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.UI.ChatUI;
import com.rovas.forgram.fogram.Utils.StringUtils;
import com.rovas.forgram.fogram.Utils.TimeUtils;
import com.rovas.forgram.fogram.Utils.TouchImageView;
import com.rovas.forgram.fogram.models.Message;

/**
 * Created by Mohamed El Sayed
 */
public class ImageDetailsActivity extends AppCompatActivity {
    private static final String TAG = ImageDetailsActivity.class.getName();

    private Message message;

//    private FloatingActionButton mBtnShare;
//    private FloatingActionButton mBtnDownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_activity_image_details);

        message = (Message) getIntent().getExtras().getSerializable(ChatUI.BUNDLE_MESSAGE);

        // ### begin toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(message.getFrom_name());
        // ### end toolbar


        registerViews();

        // ### begin image
        String imgUrl = message.getMedia_path();
        setImage(imgUrl);
        // ### end image

        // ### begin title
        String title = message.getFrom();
        if (StringUtils.isValid(title)) {
            TextView mTitle = findViewById(R.id.image_title);
            mTitle.setText(title);
        }
        // ### end title

        // ### begin sender
        String sender = message.getFrom_name();
        if (StringUtils.isValid(sender)) {
            TextView mSender = findViewById(R.id.sender);
            mSender.setText(sender);
        }
        // ### end sender

        // ### begin timestamp
        TextView mTimestamp = findViewById(R.id.timestamp);
        try {
            long timestamp = message.getTime_stamp();
            String formattedTimestamp = TimeUtils.getFormattedTimestamp(this, timestamp);
            mTimestamp.setText(formattedTimestamp);
        } catch (Exception e) {
            Log.e(TAG, "cannot retrieve the timestamp. " + e.getMessage());
        }
        // ### end timestamp


//        // change the statusbar color
//        ThemeUtils.changeStatusBarColor(this, getResources().getColor(R.color.black));

//        initListeners();
    }


    private void registerViews() {
        Log.i(TAG, "registerViews");


//        mBtnShare = (FloatingActionButton) findViewById(R.id.share);
//        mBtnDownload = (FloatingActionButton) findViewById(R.id.download);
    }


//    private void initListeners() {
//        mBtnShare.setOnClickListener(onShareClickListener);
//        mBtnDownload.setOnClickListener(onDownloadClickListener);
//    }
//
//    private View.OnClickListener onShareClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Snackbar.make(findViewById(R.id.coordinator), "share pressed", Snackbar.LENGTH_LONG).show();
//        }
//    };
//
//    private View.OnClickListener onDownloadClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Snackbar.make(findViewById(R.id.coordinator), "download pressed", Snackbar.LENGTH_LONG).show();
//        }
//    };


    private String getImageUrl(Message message) {
        String imgUrl = "";
        //TODO
        /*
        Map<String, Object> metadata = message.getMetadata();
        if (metadata != null) {
            imgUrl = (String) metadata.get("src");
        }
        */
        return imgUrl;
    }

    private void setImage(String imgUrl) {
        Log.i(TAG, "setImage");

        final TouchImageView mImage = findViewById(R.id.image);

        mImage.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
            @Override
            public void onMove() {
//                RectF rect = mImage.getZoomedRect();
//                float currentZoom = mImage.getCurrentZoom();
//                boolean isZoomed = mImage.isZoomed();
            }
        });


        // https://github.com/MikeOrtiz/TouchImageView/issues/135
        Glide.with(this)
                .asBitmap()
                .load(imgUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mImage.setImageBitmap(resource);
                    }

                });

//                // make the imageview zoomable
//                // source : https://github.com/chrisbanes/PhotoView
//                PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImage);
//                mAttacher.update();
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
