package com.rovas.forgram.fogram.Utils.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mohamed El Sayed
 */
public class BasicImageUploader {

    private OnImageLoaderListener mImageLoaderListener;
    private Set<byte[]> mUrlsInProgress = new HashSet<>();
    private final String TAG = this.getClass().getSimpleName();

    public BasicImageUploader(@NonNull OnImageLoaderListener listener) {
        this.mImageLoaderListener = listener;
    }

    /**
     * Interface definition for callbacks to be invoked
     * when the image upload status changes.
     */
    public interface OnImageLoaderListener {
        /**
         * Invoked if an error has occurred and thus
         * the upload did not complete
         *
         * @param error the occurred error
         */
        void onError(ImageError error);

        /**
         * Invoked every time the progress of the upload changes
         *
         * @param percent new status in %
         */
        void onProgressChange(int percent);

        /**
         * Invoked after the image has been successfully uploaded
         *
         * @param result the uploaded image
         */
        void onComplete(String result);
    }

    /**
     * Downloads the image from the given URL using an {@link AsyncTask}. If a upload
     * for the given URL is already in progress this method returns immediately.
     *
     * @param thumb_data        the URL to get the image from
     * @param displayProgress if <b>true</b>, the {@link OnImageLoaderListener#onProgressChange(int)}
     *                        callback will be triggered to notify the caller of the upload progress
     */
    @SuppressLint("StaticFieldLeak")
    public void upload(Context context, @NonNull final byte[] thumb_data , final StorageReference imageRef , final String user_id , final String to_user_id, final boolean displayProgress) {
        if (mUrlsInProgress.contains(thumb_data)) {
            Log.w(TAG, "a upload for this url is already running, " +
                    "no further upload will be started");
            return;
        }

        new AsyncTask<Void, Integer, String[]>() {

            private ImageError error;
            private String single_img_url;
            private FirebaseFirestore fStore;
            @Override
            protected void onPreExecute() {
                mUrlsInProgress.add(thumb_data);
                Log.d(TAG, "starting upload");
            }

            @Override
            protected void onCancelled() {
                mUrlsInProgress.remove(thumb_data);
                mImageLoaderListener.onError(error);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                mImageLoaderListener.onProgressChange(values[0]);
            }

            @Override
            protected String[] doInBackground(Void... params) {
                final String[] status = {null};
                fStore = FirebaseFirestore.getInstance();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                UploadTask uploadTask = imageRef.putBytes(thumb_data);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        single_img_url = task.getResult().getDownloadUrl().toString();

                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // ================== ==================
                                Map<String, Object> message_map = new HashMap<>();
                                message_map.put("media_path", single_img_url);
                                message_map.put("message", "image");
                                message_map.put("seen", false);
                                message_map.put("type", "image");
                                message_map.put("position", "2");
                                message_map.put("from_name", "test");
                                message_map.put("time_stamp", timestamp.getTime());
                                message_map.put("from", user_id);
                                fStore.collection("messages").document(user_id).collection(to_user_id).add(message_map);
                                // ========================================================================================
                                HashMap<String, Object> message_map_rec = new HashMap<>();
                                message_map_rec.put("message", "image");
                                message_map_rec.put("media_path", single_img_url);
                                message_map_rec.put("seen", false);
                                message_map_rec.put("type", "image");
                                message_map_rec.put("position", "3");
                                message_map_rec.put("from_name", "test");
                                message_map_rec.put("time_stamp", timestamp.getTime());
                                message_map_rec.put("from", user_id);
                                fStore.collection("messages").document(to_user_id).collection(user_id).add(message_map_rec);
                                status[0] = "done";
                            }
                        });
                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    }
                });
                return status;
            }

            @Override
            protected void onPostExecute(String[] result) {
                if (result == null) {
                    Log.e(TAG, "factory returned a null result");
                    mImageLoaderListener.onError(new ImageError("uploaded file could not be decoded as bitmap")
                            .setErrorCode(ImageError.ERROR_DECODE_FAILED));
                } else {

                    mImageLoaderListener.onComplete(result[0]);
                }
                mUrlsInProgress.remove(thumb_data);
                System.gc();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Interface definition for callbacks to be invoked when
     * the image save procedure status changes
     */




    /**
     * Represents an error that has occurred while
     * uploading image or writing it to disk. Since
     * this class extends {@code Throwable}, you may get the
     * stack trace from an {@code ImageError} object
     */
    public static final class ImageError extends Throwable {

        private int errorCode;
        /**
         * An exception was thrown during an operation.
         * Check the error message for details.
         */
        public static final int ERROR_GENERAL_EXCEPTION = -1;
        /**
         * The URL does not point to a valid file
         */
        public static final int ERROR_INVALID_FILE = 0;
        /**
         * The uploaded file could not be decoded as bitmap
         */
        public static final int ERROR_DECODE_FAILED = 1;
        /**
         * File already exists on disk and shouldOverwrite == false
         */
        public static final int ERROR_FILE_EXISTS = 2;
        /**
         * Could not complete a file operation, most likely due to permission denial
         */
        public static final int ERROR_PERMISSION_DENIED = 3;
        /**
         * The target file is a directory
         */
        public static final int ERROR_IS_DIRECTORY = 4;


        public ImageError(@NonNull String message) {
            super(message);
        }

        public ImageError(@NonNull Throwable error) {
            super(error.getMessage(), error.getCause());
            this.setStackTrace(error.getStackTrace());
        }

        /**
         * @param code the code for the occurred error
         * @return the same ImageError object
         */
        public ImageError setErrorCode(int code) {
            this.errorCode = code;
            return this;
        }

        /**
         * @return the error code that was previously set
         * by {@link #setErrorCode(int)}
         */
        public int getErrorCode() {
            return errorCode;
        }
    }
}