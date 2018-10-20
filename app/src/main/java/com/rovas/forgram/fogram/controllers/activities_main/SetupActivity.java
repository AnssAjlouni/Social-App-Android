package com.rovas.forgram.fogram.controllers.activities_main;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rovas.forgram.fogram.R;

import com.rovas.forgram.fogram.Utils.File.FilePaths;
import com.rovas.forgram.fogram.Utils.MediaSelector;
import com.rovas.forgram.fogram.managers.UserWiazrd;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
/**
 * Created by Mohamed El Sayed
 */
public class SetupActivity extends AppCompatActivity {

    private String user_id;
    //Views
    protected MediaSelector mediaSelector = new MediaSelector();
    private CircleImageView setup_img;
    private EditText setup_name;
    private Button setup_btn;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    //Firebase
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore fStore;
    //Picture
    private Bitmap compressedImageFile;
    private File new_image_file;
    //mainImageUri
    private Boolean isChanged =false;
    //ProgressDialog
    private ProgressDialog mLoginProgress;

    private  static final String TAG = "SetupActivity";//logt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //Progress Dialog
        mLoginProgress = new ProgressDialog(this);
        //Toolbar
        Toolbar sToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(sToolbar);
        getSupportActionBar().setTitle(getString(R.string.account_settings));
        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();//Get_user_id
        storageReference = FirebaseStorage.getInstance().getReference();
        //Views
        setup_name = (EditText) findViewById(R.id.setup_name);
        setup_btn = (Button) findViewById(R.id.setup_btn);
        setup_img = findViewById(R.id.setup_image);
        mDisplayDate = (TextView) findViewById(R.id.display_date);
        //Click_Listeners
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(SetupActivity.this,
                        R.style.Theme_AppCompat_Dialog ,//Themes
                        mDateSetListener ,//set Listener_method
                        year,month,day);//set date_arrangment
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//Color
                dialog.show();//Show Date Dialog

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1 ;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + year + "/" +  + month + "/"+  + day + "/");//Date_style
                String date = month + "/" + day + "/" +year;
                mDisplayDate.setText(date);//Show Date_in_TextView

            }
        };
        setup_btn.setOnClickListener(new View.OnClickListener() {//Finish_BTN
            @Override
            public void onClick(View view) {
                final String name = setup_name.getText().toString();//name
                final String date_ = mDisplayDate.getText().toString();
                if (!TextUtils.isEmpty(name) && getNew_image_file() != null) {
                    mLoginProgress.setTitle("Uploading Image");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    uploadFile(getNew_image_file() , name ,date_);
                }

            }
        });

        setup_img.setOnClickListener(view -> mediaSelector.startChooseImageActivity(SetupActivity.this, MediaSelector.CropType.Circle, result -> {
            Uri file = Uri.fromFile(new File(result));
            setNew_image_file(new File(file.getPath()));
            try{
                setup_img.setImageURI(Uri.fromFile(getNew_image_file()));
            }
            catch (Exception e) {
                Toast.makeText(SetupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

    }

    public void uploadFile(File file , final String user_name  , final String date_) {
        Log.d(TAG, "uploadFile");

        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);

        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(SetupActivity.this)//Compressor Library
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
        UploadTask uploadTask = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo/").child(randomname + ".jpg")
                .putBytes(thumb_data);//upload image after Compressed
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                String download_thumb_uri = task.getResult().getDownloadUrl().toString();
                HashMap<String, Object> userMap_ = new HashMap<>();
                userMap_.put("name", user_name);
                userMap_.put("brith_date", date_);
                userMap_.put("thumb_image", download_thumb_uri);

                fStore.collection("users").document(user_id).update(userMap_).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            UserWiazrd.getInstance().getTempUser().setName(user_name);
                            UserWiazrd.getInstance().getTempUser().setThumb_image(download_thumb_uri);
                            UserWiazrd.getInstance().getTempUser().setPosts(0);
                            UserWiazrd.getInstance().getTempUser().setFollowing(0);
                            UserWiazrd.getInstance().getTempUser().setFollowers(0);
                            mLoginProgress.dismiss();
                            //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                            Intent mIntent = new Intent(SetupActivity.this, HomeActivity.class);
                            startActivity(mIntent);
                            finish();
                        } else {
                            mLoginProgress.hide();
                            String e = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
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
    public File getNew_image_file() {
        return new_image_file;
    }

    public void setNew_image_file(File new_image_file) {
        this.new_image_file = new_image_file;
    }
}
