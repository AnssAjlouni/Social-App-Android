package com.rovas.forgram.fogram.controllers.activities_publish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.ImageManager;
import com.rovas.forgram.fogram.Utils.tagGroup.TagGroup;
import com.rovas.forgram.fogram.base.BaseActivity;
import com.rovas.forgram.fogram.Utils.StringManipulation;
import com.rovas.forgram.fogram.controllers.activities_main.HomeActivity;
import com.rovas.forgram.fogram.Utils.UniversalImageLoader;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.managers.tags_DB.TagsManager;
import com.rovas.forgram.fogram.models.BlogPost;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;



/**
 * Created by Mohamed El Sayed
 */

public class NextActivity extends BaseActivity {

    private static final String TAG = "NextActivity";

    //widgets
    private EditText mCaption;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;

    //Tags
    private TagGroup mTagGroup;
    private TagsManager mTagsManager;

    // ======================= FireBase =======================
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore fStore;
    private StorageReference mStorageReference;
    private StorageReference storageRef, imageRef;
    private String user_id;
    private String single_img_url;
    private Bitmap compressedImageFile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mCaption = (EditText) findViewById(R.id.caption) ;
        Tags();
        setupFirebaseAuth();

        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });


        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //upload the image to firebase
                Toast.makeText(NextActivity.this, getString(R.string.attepmt_to_upload), Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();
                //Tags
                mTagsManager.updateTags(mTagGroup.getTags());
                //
                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    //
                    /*
                    Intent intent = new Intent(NextActivity.this, OptionActivity.class);
                    intent.putExtra(getString(R.string.selected_image), imgUrl);
                    intent.putExtra("caption", caption);
                    startActivity(intent);
                    */
                    //
                    uploadnewImage(getString(R.string.new_photo), caption, imageCount, imgUrl, null);
                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    //
                    /*
                    Intent intent = new Intent(NextActivity.this, OptionActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                    intent.putExtra("caption", caption);
                    startActivity(intent);
                    */
                    //
                    uploadnewImage(getString(R.string.new_photo), caption, imageCount, null, bitmap);
                }




            }
        });

        setImage();
    }


    private void ShareBtn()
    {
                ShowOptionsDialog(R.style.DialogSlide_up_down , "Test");
    }
    private void ShowOptionsDialog(int type , String message)
    {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(getString(R.string.share_to));
        dialog.setMessage(message);
        dialog.getWindow().getAttributes().windowAnimations = type;
        dialog.show();
    }
    private void someMethod(){
        /*
            Step 1)
            Create a data model for Photos

            Step 2)
            Add properties to the BlogPost Objects (caption, date, imageUrl, photo_id, tags, user_id)

            Step 3)
            Count the number of photos that the user already has.

            Step 4)
            a) Upload the photo to Firebase Storage
            b) insert into 'photos' node
            c) insert into 'user_photos' node

         */

    }


    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void uploadnewImage(String photoType, final String caption, final int count, String img_url,
                                Bitmap bm)
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        showLoading(getString(R.string.saving));

        //convert image url to bitmap
        if(bm == null){
            bm = ImageManager.getBitmap(imgUrl);
        }
        final String randomname = UUID.randomUUID().toString();
        byte[] bytes = ImageManager.getBytesFromBitmap(bm, 10);
        imageRef = storageRef.child("Posts").child(String.valueOf(timestamp.getTime())).child(randomname);
        // Create Upload Task
        UploadTask uploadTask = imageRef.putBytes(bytes);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                single_img_url = task.getResult().getDownloadUrl().toString();
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        final String randomname = UUID.randomUUID().toString();//generic randomname
                        String username = UserWiazrd.getInstance().getTempUser().getUsername();
                        String thumb_image = UserWiazrd.getInstance().getTempUser().getThumb_image();
                        long role = UserWiazrd.getInstance().getTempUser().getRole();
                        //String tags = StringManipulation.getTags(caption);
                        //String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
                        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        BlogPost blogPost = new BlogPost();
                        blogPost.setText_post(caption);
                        blogPost.setTime_stamp(timestamp.getTime());
                        blogPost.setImage_url(single_img_url);
                        blogPost.setUsername(username);
                        blogPost.setThumb_image(thumb_image);
                        blogPost.setPost("photo");
                        //blogPost.setTags(tags);
                        blogPost.setPost_type("2");
                        blogPost.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        //blogPost.setPhoto_id(newPhotoKey);
                        String post_id = username + "@" + timestamp.getTime() ;
                        //insert into database
                        fStore.collection(getString(R.string.dbname_photos)).document(post_id).set(blogPost);
                        HashMap<String , Object> user_map = new HashMap<>();
                        user_map.put("user_id" , user_id);
                        fStore.collection(getString(R.string.dbname_user_photos)).document(user_id).collection(user_id).document(post_id).set(user_map);
                        if(role == 1 || role ==  2)
                        {
                            /*
                            HashMap<String, String> userMap_ = new HashMap<>();
                            userMap_.put("user_id", user_id);
                            userMap_.put("username", username);
                            userMap_.put("type", "Photo");
                            userMap_.put("post_id", post_id);
                            userMap_.put("image_url", thumb_image);
                            userMap_.put("time_stamp", "" + timestamp.getTime());
                            */
                            fStore.collection("Admins").document(post_id).set(blogPost);
                        }
                        hideLoading();
                        Intent intent = new Intent(NextActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideLoading();
                Toast.makeText(NextActivity.this, "Failed To Upload The Image Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
    private void setImage() {
        intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);

        if (intent.hasExtra(getString(R.string.selected_image))) {
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image url: " + imgUrl);
            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
        } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            image.setImageBitmap(bitmap);
        } else if (intent.hasExtra(getString(R.string.selected_video))) {
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_video));
            Log.d(TAG, "setImage: got new Video");
            image.setImageBitmap(bitmap);
        }
    }

     /*
     ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        // ================ Firebase ===============
        mAuth = FirebaseAuth.getInstance();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        // Get instance and specify regional
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Get reference
        storageRef = storage.getReference();
        // ==========================================

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tag_editor_activity, menu);
        return true;
    }
    private void Tags() {
        mTagsManager = TagsManager.getInstance(getApplicationContext());
        String[] tags = mTagsManager.getTags();

        mTagGroup = (TagGroup) findViewById(R.id.tag_group);
        mTagGroup.setTags(tags);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mTagsManager.updateTags(mTagGroup.getTags());
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_submit) {
            mTagGroup.submitTag();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        mTagsManager.updateTags(mTagGroup.getTags());
        super.onBackPressed();
    }
}
