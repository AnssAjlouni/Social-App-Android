package com.rovas.forgram.fogram.controllers.activities_profile;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rovas.forgram.fogram.MainActivity;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.Availability;
import com.rovas.forgram.fogram.Utils.File.FilePaths;
import com.rovas.forgram.fogram.Utils.MediaSelector;
import com.rovas.forgram.fogram.Utils.helper.LogoutHelper;
import com.rovas.forgram.fogram.base.BaseActivity;
import com.rovas.forgram.fogram.controllers.activities_main.HomeActivity;
import com.rovas.forgram.fogram.controllers.activities_main.SignInActivity;
import com.rovas.forgram.fogram.managers.UserWiazrd;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

/**
 * Created by Mohamed El Sayed
 */
public class EditProfileActivity extends BaseActivity {

    private static final String TAG = "EditProfileActivity" ;
    protected CircleImageView avatarImageView;
    protected EditText statusEditText;
    protected Spinner availabilitySpinner;
    protected EditText nameEditText;
    protected EditText locationEditText;
    protected EditText phoneNumberEditText;
    protected EditText emailEditText;
    protected Button saveButton;
    protected Button logoutButton;
    protected HashMap<String, Object> userMeta;
    protected MediaSelector mediaSelector = new MediaSelector();
    protected FirebaseAuth mAuth;
    protected FirebaseFirestore fStore;
    protected String current_user_id;
    private File new_image_file;
    private ProgressDialog mLoginProgress;
    private Bitmap compressedImageFile;
    private StorageReference storageReference;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Progress Dialog
        mLoginProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        //setEmail(mAuth.getCurrentUser().getEmail());
        initViews();
    }

    protected void initViews() {

        avatarImageView = findViewById(R.id.ivAvatar);
        statusEditText = findViewById(R.id.etStatus);
        availabilitySpinner = findViewById(R.id.spAvailability);
        nameEditText = findViewById(R.id.etName);

        saveButton = findViewById(R.id.btnSave);
        logoutButton = findViewById(R.id.btnLogout);
        if(UserWiazrd.getInstance().getTempUser().getThumb_image() != null) {
            Glide.with(EditProfileActivity.this)
                    .load(UserWiazrd.getInstance().getTempUser().getThumb_image())
                    .into(avatarImageView);
        }
        avatarImageView.setOnClickListener(view -> mediaSelector.startChooseImageActivity(EditProfileActivity.this, MediaSelector.CropType.Circle,result -> {
            Uri file = Uri.fromFile(new File(result));
            setNew_image_file(new File(file.getPath()));
            try{
                avatarImageView.setImageURI(Uri.fromFile(getNew_image_file()));
            }
            catch (Exception e) {
                Toast.makeText(EditProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

        saveButton.setOnClickListener(view -> save());
        logoutButton.setOnClickListener(view -> logout());

        statusEditText.setText(UserWiazrd.getInstance().getTempUser().getStatus());

        nameEditText.setText(UserWiazrd.getInstance().getTempUser().getName());
        //locationEditText.setText(location);
        //phoneNumberEditText.setText(phoneNumber);
        //emailEditText.setText(getEmail());

    }

    private void save() {
        final String name = nameEditText.getText().toString();//name
        final String status = statusEditText.getText().toString();
        if (!TextUtils.isEmpty(name) && getNew_image_file() != null) {
            mLoginProgress.setTitle("Uploading Image");
            mLoginProgress.setMessage("Please wait while we check your credentials.");
            mLoginProgress.setCanceledOnTouchOutside(false);
            mLoginProgress.show();

            uploadFile(getNew_image_file() , name ,status);
        }
        else if (!TextUtils.isEmpty(name) && (!TextUtils.isEmpty(status)))
        {
            mLoginProgress.setTitle("Uploading Image");
            mLoginProgress.setMessage("Please wait while we check your credentials.");
            mLoginProgress.setCanceledOnTouchOutside(false);
            mLoginProgress.show();

            uploadFile(name ,status);
        }
    }
    public void uploadFile(File file , final String name  , final String status) {
        Log.d(TAG, "uploadFile");

        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);

        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(EditProfileActivity.this)//Compressor Library
                    .setMaxWidth(100)
                    .setMaxHeight(100)
                    .setQuality(2)

                    .compressToBitmap(new_image_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FilePaths filePaths = new FilePaths();
        final String randomname = UUID.randomUUID().toString();//generic randomname
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumb_data = baos.toByteArray();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        UploadTask uploadTask = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + current_user_id + "/profile_photo/").child(randomname + ".jpg")
                .putBytes(thumb_data);//upload image after Compressed
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                String download_thumb_uri = task.getResult().getDownloadUrl().toString();
                HashMap<String, Object> userMap_ = new HashMap<>();
                userMap_.put("name", name);
                userMap_.put("status", status);
                userMap_.put("thumb_image", download_thumb_uri);

                fStore.collection("users").document(current_user_id).update(userMap_).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mLoginProgress.dismiss();
                            UserWiazrd.getInstance().getTempUser().setName(name);
                            UserWiazrd.getInstance().getTempUser().setStatus(status);
                            UserWiazrd.getInstance().getTempUser().setThumb_image(download_thumb_uri);
                            //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                            Intent mIntent = new Intent(EditProfileActivity.this, HomeActivity.class);
                            startActivity(mIntent);
                            finish();
                        } else {
                            mLoginProgress.hide();
                            String e = task.getException().getMessage();
                            Toast.makeText(EditProfileActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
    public void uploadFile(final String name  , final String status) {
        Log.d(TAG, "uploadFile");
        HashMap<String, Object> userMap_ = new HashMap<>();
        userMap_.put("name", name);
        userMap_.put("status", status);

        fStore.collection("users").document(current_user_id).update(userMap_).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mLoginProgress.dismiss();
                    UserWiazrd.getInstance().getTempUser().setName(name);
                    UserWiazrd.getInstance().getTempUser().setStatus(status);
                    //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                    Intent mIntent = new Intent(EditProfileActivity.this, HomeActivity.class);
                    startActivity(mIntent);
                    finish();
                } else {
                    mLoginProgress.hide();
                    String e = task.getException().getMessage();
                    Toast.makeText(EditProfileActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void logout()
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Map<String , Object > tokenMap = new HashMap<>();
        tokenMap.put("token_id" , FieldValue.delete());
        tokenMap.put("online" , timestamp.getTime());
        fStore.collection("users").document(current_user_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LogoutHelper.signOut( EditProfileActivity.this);
                startMainActivity();
            }
        });
    }
    private void startMainActivity() {
        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item =
                menu.add(Menu.NONE, R.id.action_chat_sdk_save, 12, getString(R.string.action_save));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setIcon(R.drawable.ic_save);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /* Cant use switch in the library*/
        int id = item.getItemId();

        if (id == R.id.action_chat_sdk_save)
        {
            saveAndExit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            mediaSelector.handleResult(this, requestCode, resultCode, data);
        }
        catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void saveAndExit () {

        String status = statusEditText.getText().toString();
        String availability = getAvailability();
        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String email = emailEditText.getText().toString();


        // TODO: Add this in for Firebase maybe move this to push user...
//        if(imageChanged && avatarURL != null) {
//            UserAvatarHelper.saveProfilePicToServer(avatarURL, this).subscribe();
//        }
//        else if (changed) {



    }

    protected boolean valueChanged (Map<String, Object> h1, Map<String, Object> h2, String key) {
        Object o1 = h1.get(key);
        Object o2 = h2.get(key);
        if (o1 == null) {
            return o2 != null;
        } else {
            return !o1.equals(o2);
        }
    }

    protected int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    protected String getAvailability () {
        String a = availabilitySpinner.getSelectedItem().toString().toLowerCase();
        if(a.equals("away")) {
            return Availability.Away;
        }
        else if(a.equals("extended away")) {
            return Availability.XA;
        }
        else if(a.equals("busy")) {
            return Availability.Busy;
        }
        else {
            return Availability.Available;
        }
    }

    protected void setAvailability (String a) {
        String availability = "available";
        if(a.equals(Availability.Away)) {
            availability = "away";
        }
        else if(a.equals(Availability.XA)) {
            availability = "extended away";
        }
        else if(a.equals(Availability.Busy)) {
            availability = "busy";
        }
        availabilitySpinner.setSelection(getIndex(availabilitySpinner, availability));

    }
    public File getNew_image_file() {
        return new_image_file;
    }

    public void setNew_image_file(File new_image_file) {
        this.new_image_file = new_image_file;
    }

}
