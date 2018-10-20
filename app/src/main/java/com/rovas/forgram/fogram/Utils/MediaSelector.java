package com.rovas.forgram.fogram.Utils;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.rovas.forgram.fogram.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;


import static android.app.Activity.RESULT_OK;


/**
 * Created by Mohamed El Sayed
 */
public class MediaSelector {

    private static final int TAKE_PHOTO = 100;
    private static final int CHOOSE_PHOTO = 101;
    private static final int TAKE_VIDEO = 102;
    private static final int CHOOSE_VIDEO = 103;

    protected Result resultHandler;
    protected CropType cropType = CropType.Rectangle;

    public enum CropType {
        Rectangle,
        Square,
        Circle,
    }

    public interface Result {
        void result (String result);
    }

    public void startTakePhotoActivity (Activity activity, Result resultHandler) throws Exception {
        this.resultHandler = resultHandler;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, TAKE_PHOTO);
        }
        else {
            resultHandler.result(null);
        }
    }

    public void startTakeVideoActivity (Activity activity, Result resultHandler) {
        this.resultHandler = resultHandler;
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takeVideoIntent, TAKE_VIDEO);
        }
    }

    public void startChooseVideoActivity (Activity activity, Result resultHandler) {
        this.resultHandler = resultHandler;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent , CHOOSE_VIDEO);
    }

    public void startChooseImageActivity(Activity activity, CropType cropType, Result resultHandler) {
        this.cropType = cropType;
        this.resultHandler = resultHandler;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)//Require Permisson if Version >= 6 Marsm
        {
            if(ContextCompat.checkSelfPermission(activity , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(intent, CHOOSE_PHOTO);
                ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE} , 1);

            }
            else
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(intent, CHOOSE_PHOTO);
            }

        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activity.startActivityForResult(intent, CHOOSE_PHOTO);
        }
    }

    protected void processPickedPhoto(Activity activity, int resultCode, Intent data) throws Exception {

        switch (resultCode)
        {
            case RESULT_OK:

                Uri uri = data.getData();

                    /*
                    // Let's read picked image path using content resolver
                    String[] filePath = { MediaStore.Images.Media.DATA };
                    Cursor cursor = activity.getContentResolver().query(uri, filePath, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                    handleImageFile(activity, imagePath);
                    */
                    if (cropType == CropType.Circle) {
                        Cropper.startCircleActivity(activity, uri);
                    }
                    else if (cropType == CropType.Square) {
                        Cropper.startSquareActivity(activity, uri);
                    }
                    else {
                        Cropper.startActivity(activity, uri);
                    }

                break;
            case AppCompatActivity.RESULT_CANCELED:
                throw new Exception();
        }
    }

    protected void processCroppedPhoto(Activity activity, int resultCode, Intent data) throws Exception {

        CropImage.ActivityResult result = CropImage.getActivityResult(data);

        if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE || resultCode == AppCompatActivity.RESULT_CANCELED) {
            throw new Exception(result.getError());
        }
        else if (resultCode == RESULT_OK) {
            try {
                handleImageFile(activity, result.getUri().getPath());
            }
            catch (NullPointerException e){
                throw new Exception(activity.getString(R.string.unable_to_fetch_image));
            }
        }

    }

    public void handleImageFile (Activity activity, String path) {

        // Scanning the messageImageView so it would be visible in the gallery images.
            ImageUtils.scanFilePathForGallery(activity, path);

        if(resultHandler != null) {
            resultHandler.result(path);
            clear();
        }
    }

    public void handleResult (Activity activity, int requestCode, int resultCode, Intent intent) throws Exception {

        if (requestCode == CHOOSE_PHOTO) {
            processPickedPhoto(activity, resultCode, intent);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            processCroppedPhoto(activity, resultCode, intent);
        }

        else if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            if(resultHandler != null) {
                Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                File file = ImageUtils.saveImageToCache(activity, bitmap);
                resultHandler.result(file.getPath());
                clear();
            }
        }
        else if (requestCode == TAKE_VIDEO && resultCode == RESULT_OK) {
            if(resultHandler != null) {
                Uri videoUri = intent.getData();
                resultHandler.result(videoUri.getPath());
                clear();
            }
        }
        else if (requestCode == CHOOSE_VIDEO && resultCode == RESULT_OK) {
            if(resultHandler != null) {
                Uri videoUri = intent.getData();

                // Let's read picked image path using content resolver
                String[] filePath = { MediaStore.Video.Media.DATA };
                Cursor cursor = activity.getContentResolver().query(videoUri, filePath, null, null, null);
                cursor.moveToFirst();
                String videoPath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                resultHandler.result(videoPath);
                clear();
            }
        }
        else {
        }
    }

    public void clear () {
        resultHandler = null;
    }



}
