package com.rovas.forgram.fogram.managers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.File.FilePaths;
import com.rovas.forgram.fogram.Utils.helper.Helper;
import com.rovas.forgram.fogram.Utils.ImageManager;
import com.rovas.forgram.fogram.Utils.StringManipulation;
import com.rovas.forgram.fogram.controllers.activities_main.HomeActivity;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.models.StoriesPost;
import com.rovas.forgram.fogram.models.Video;
import com.rovas.forgram.fogram.interfaces.OnUploadedCallback;
import com.rovas.forgram.fogram.storage.StorageHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Mohamed El Sayed
 */
//DELETED
public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore fStore;
    //private FirebaseDatabase mFirebaseDatabase;
    //private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;
    private String user_id;
    private Uri uri_video;
    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        //mFirebaseDatabase = FirebaseDatabase.getInstance();
        //myRef = mFirebaseDatabase.getReference();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;
        uri_video = null;
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void uploadNewPhoto(String photoType, final String caption, final int count, final String imgUrl,
                               Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        //case1) new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Uri file = Uri.fromFile(new File(imgUrl));
            File new_image_file = new File(file.getPath());
            // bugfix Issue #45
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getString(R.string.activity_message_list_progress_dialog_upload));
            progressDialog.setCancelable(false);
            progressDialog.show();
            StorageHandler.uploadFile(mContext, new_image_file, new OnUploadedCallback() {
                @Override
                public void onUploadSuccess(final String uid, final Uri downloadUrl, final String type) {
                    Log.d(TAG, "uploadFile.onUploadSuccess - downloadUrl: " + downloadUrl);

                    progressDialog.dismiss(); // bugfix Issue #45
                    // ================== ==================
                    addPhotoToDatabase(caption, downloadUrl.toString());

                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                }

                @Override
                public void onProgress(double progress) {
                    Log.d(TAG, "uploadFile.onProgress - progress: " + progress);

                    // bugfix Issue #45
                    progressDialog.setProgress((int) progress);

                    // TODO: 06/09/17 progress within viewholder
                }

                @Override
                public void onUploadFailed(Exception e) {
                    Log.e(TAG, "uploadFile.onUploadFailed: " + e.getMessage());

                    progressDialog.dismiss(); // bugfix Issue #45

                    Toast.makeText(mContext,
                            mContext.getString(R.string.activity_message_list_progress_dialog_upload_failed),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    public void uploadNewStory(String photoType, final String caption, final int count, final String imgUrl,
                               Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new Story.");

        //case1) new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");
            Uri file = Uri.fromFile(new File(imgUrl));
            File new_image_file = new File(file.getPath());
            // bugfix Issue #45
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getString(R.string.activity_message_list_progress_dialog_upload));
            progressDialog.setCancelable(false);
            progressDialog.show();
            StorageHandler.uploadFile(mContext, new_image_file, new OnUploadedCallback() {
                @Override
                public void onUploadSuccess(final String uid, final Uri downloadUrl, final String type) {
                    Log.d(TAG, "uploadFile.onUploadSuccess - downloadUrl: " + downloadUrl);

                    progressDialog.dismiss(); // bugfix Issue #45
                    // ================== ==================
                    addStoryToDatabase(caption, downloadUrl.toString());

                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                }

                @Override
                public void onProgress(double progress) {
                    Log.d(TAG, "uploadFile.onProgress - progress: " + progress);

                    // bugfix Issue #45
                    progressDialog.setProgress((int) progress);

                    // TODO: 06/09/17 progress within viewholder
                }

                @Override
                public void onUploadFailed(Exception e) {
                    Log.e(TAG, "uploadFile.onUploadFailed: " + e.getMessage());

                    progressDialog.dismiss(); // bugfix Issue #45

                    Toast.makeText(mContext,
                            mContext.getString(R.string.activity_message_list_progress_dialog_upload_failed),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    public void uploadNewMulti(String photoType, final String caption, final int count, final String imgUrl,
                               Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        FilePaths filePaths = new FilePaths();
        //case1) new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");
            final String randomname = UUID.randomUUID().toString();
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + randomname);

            //convert image url to bitmap
            if(bm == null){
                bm = ImageManager.getBitmap(imgUrl);
            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext,  mContext.getString(R.string.upload_sucess), Toast.LENGTH_SHORT).show();

                    //add the new photo to 'photos' node and 'user_photos' node
                    addPhotoToDatabase(caption, firebaseUrl.toString());
                    addStoryToDatabase(caption, firebaseUrl.toString());
                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: BlogPost upload failed.");
                    Toast.makeText(mContext,  mContext.getString(R.string.failed_upload), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext,  mContext.getString(R.string.uploading_progress)+" " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }
        //case new profile photo
        else if(photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");


            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final String randomname = UUID.randomUUID().toString();//generic randomname
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo / " + randomname + ".jpg");

            //convert image url to bitmap
            if(bm == null){
                bm = ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext,  mContext.getString(R.string.upload_sucess), Toast.LENGTH_SHORT).show();

                    //insert into 'user_account_settings' node
                    setProfilePhoto(firebaseUrl.toString());

                    /*
                    ((AccountSettingsActivity)mContext).setViewPager(
                            ((AccountSettingsActivity)mContext).pagerAdapter
                                    .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );
                    */

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: BlogPost upload failed.");
                    Toast.makeText(mContext, "BlogPost upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext,  mContext.getString(R.string.uploading_progress)+" " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }

    }
    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void uploadNewVideo(String photoType, final String caption, final int count, final String VidUrl,
                               Bitmap bm) {
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new video.");

        FilePaths filePaths = new FilePaths();
        //case1) new photo
        if (photoType.equals(mContext.getString(R.string.new_video))) {
            Log.d(TAG, "uploadNewPhoto: uploading NEW Video.");
            final String randomname = UUID.randomUUID().toString();
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/video" + randomname +".mp4");

            //convert image url to bitmap

            //byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            uri_video = Uri.parse(VidUrl);
            String filePath = Helper.getPath(mContext, uri_video);
            Uri file = Uri.fromFile(new File(filePath));
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("video/mp4")
                    .build();
            UploadTask uploadTask = storageReference.putFile(file , metadata);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, mContext.getString(R.string.upload_sucess_vid), Toast.LENGTH_SHORT).show();

                    //add the new photo to 'photos' node and 'user_photos' node
                    addVideoToDatabase(caption, String.valueOf(firebaseUrl));

                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Video upload failed.");
                    Toast.makeText(mContext,  mContext.getString(R.string.upload_failed_vid), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext,  mContext.getString(R.string.uploading_progress_vid)+" " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }


    }

    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);
        /*
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
                */
        fStore.collection(mContext.getString(R.string.dbname_user_account_settings)).document(user_id)
                .collection(mContext.getString(R.string.profile_photo)).add(url);
    }

    private void addVideoToDatabase(final String caption, final String url){
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");
        fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        final String randomname = UUID.randomUUID().toString();//generic randomname
                        String username = task.getResult().getString("username");
                        String thumb_image = task.getResult().getString("thumb_image");
                        String tags = StringManipulation.getTags(caption);
                        //String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
                        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        Video video = new Video();
                        video.setDesc(caption);
                        video.setTime_stamp(timestamp.getTime());
                        video.setVideo_url(url);
                        video.setText_post("");
                        video.setTags(tags);
                        video.setUsername(username);
                        video.setThumb_image(thumb_image);
                        video.setPost_type("3");
                        video.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        //photo.setPhoto_id(newPhotoKey);
                        String post_id = username + "@" + timestamp.getTime() ;
                        //insert into database
                        fStore.collection(mContext.getString(R.string.dbname_photos)).document(post_id).set(video);
                        HashMap<String , Object> user_map = new HashMap<>();
                        user_map.put("user_id" , user_id);
                        fStore.collection(mContext.getString(R.string.dbname_user_photos)).document(user_id).collection(user_id).document(post_id).set(user_map);
                    }
                }
            }
        });

        /*
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);
        */

    }
    private void addPhotoToDatabase(final String caption, final String url){
        Log.d(TAG, "addPhotoToDatabase: adding blogPost to database.");
        final String randomname = UUID.randomUUID().toString();//generic randomname
        String username = UserWiazrd.getInstance().getTempUser().getUsername();
        String thumb_image = UserWiazrd.getInstance().getTempUser().getThumb_image();
        //String tags = StringManipulation.getTags(caption);

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BlogPost blogPost = new BlogPost();
        blogPost.setText_post(caption);
        blogPost.setTime_stamp(timestamp.getTime());
        blogPost.setImage_url(url);
        blogPost.setUsername(username);
        blogPost.setThumb_image(thumb_image);
        //blogPost.setTags(tags);
        blogPost.setPost_type("2");
        blogPost.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //blogPost.setPhoto_id(newPhotoKey);
        String post_id = username + "@" + timestamp.getTime() ;
        //insert into database
        fStore.collection(mContext.getString(R.string.dbname_photos)).document(post_id).set(blogPost);
        HashMap<String , Object> user_map = new HashMap<>();
        user_map.put("user_id" , user_id);
        fStore.collection(mContext.getString(R.string.dbname_user_photos)).document(user_id).collection(user_id).document(post_id).set(user_map);
        long last_count = UserWiazrd.getInstance().getTempUser().getPosts() + 1;
        UserWiazrd.getInstance().getTempUser().setPosts(last_count);//TODO
        /*
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).setValue(blogPost);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(blogPost);
        */

    }
    private void addStoryToDatabase(final String caption, final String url){
        Log.d(TAG, "addStoryToDatabase: adding Stroy to database.");
        fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        String username = task.getResult().getString("username");
                        String thumb_image = task.getResult().getString("thumb_image");
                        String tags = StringManipulation.getTags(caption);
                        //String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
                        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        StoriesPost storiesPost = new StoriesPost();
                        storiesPost.setTime_stamp(timestamp.getTime());
                        storiesPost.setImage_url(url);
                        storiesPost.setText_post("");
                        storiesPost.setUsername(username);
                        storiesPost.setThumb_image(thumb_image);//Profile_pic
                        storiesPost.setPost_type("2");
                        storiesPost.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        //photo.setPhoto_id(newPhotoKey);

                        //insert into database
                        Map<String, Object> Map = new HashMap<>();
                        Map.put("user_id", user_id);
                        Map.put("username", username);
                        Map.put("thumb_image", thumb_image);
                        Map.put("time_stamp", timestamp.getTime());
                        fStore.collection("Stories").document(user_id).collection("Photos").add(storiesPost);
                        fStore.collection("Stories").document(user_id).set(Map);
                    }
                }
            }
        });

        /*
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);
        */

    }

}