package com.rovas.forgram.fogram.Utils.download;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mohamed El Sayed
 */
public class BasicSoundDownloader {

    private OnSoundLoaderListener mSoundLoaderListener;
    private Set<String> mUrlsInProgress = new HashSet<>();
    private final String TAG = this.getClass().getSimpleName();

    public BasicSoundDownloader(@NonNull OnSoundLoaderListener listener) {
        this.mSoundLoaderListener = listener;
    }

    /**
     * Interface definition for callbacks to be invoked
     * when the sound download status changes.
     */
    public interface OnSoundLoaderListener {
        /**
         * Invoked if an error has occurred and thus
         * the download did not complete
         *
         * @param error the occurred error
         */
        void onError(SoundError error);

        /**
         * Invoked every time the progress of the download changes
         *
         * @param percent new status in %
         */
        void onProgressChange(int percent);

        /**
         * Invoked after the sound has been successfully downloaded
         *
         * @param result the downloaded sound
         */
        void onComplete(String result);
    }

    /**
     * Downloads the sound from the given URL using an {@link AsyncTask}. If a download
     * for the given URL is already in progress this method returns immediately.
     *
     * @param soundUrl        the URL to get the sound from
     * @param displayProgress if <b>true</b>, the {@link OnSoundLoaderListener#onProgressChange(int)}
     *                        callback will be triggered to notify the caller of the download progress
     */
    @SuppressLint("StaticFieldLeak")
    public void download(@NonNull final String soundUrl , String mFileName, final boolean displayProgress) {
        if (mUrlsInProgress.contains(soundUrl)) {
            Log.w(TAG, "a download for this url is already running, " +
                    "no further download will be started");
            return;
        }

        new AsyncTask<Void, Integer, String>() {

            private SoundError error;

            @Override
            protected void onPreExecute() {
                mUrlsInProgress.add(soundUrl);
                Log.d(TAG, "starting download");
            }

            @Override
            protected void onCancelled() {
                mUrlsInProgress.remove(soundUrl);
                mSoundLoaderListener.onError(error);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                mSoundLoaderListener.onProgressChange(values[0]);
            }

            @Override
            protected String doInBackground(Void... params) {
                String status = null;
                OutputStream outStream = null;
                HttpURLConnection connection = null;
                InputStream is = null;
                ByteArrayOutputStream out = null;
                try {
                    connection = (HttpURLConnection) new URL(soundUrl).openConnection();
                    if (displayProgress) {
                        connection.connect();
                        final int length = connection.getContentLength();
                        if (length <= 0) {
                            error = new SoundError("Invalid content length. The URL is probably not pointing to a file")
                                    .setErrorCode(SoundError.ERROR_INVALID_FILE);
                            this.cancel(true);
                        }
                        is = new BufferedInputStream(connection.getInputStream(), 8192);
                        out = new ByteArrayOutputStream();
                        byte bytes[] = new byte[8192];
                        int count;
                        long read = 0;
                        while ((count = is.read(bytes)) != -1) {
                            read += count;
                            out.write(bytes, 0, count);
                            publishProgress((int) ((read * 100) / length));
                        }
                        //below is the different part
                        try {
                            outStream = new FileOutputStream(mFileName);
                            out = new ByteArrayOutputStream();
                            // writing bytes in to byte output stream
                            out.write(bytes); //data
                            out.writeTo(outStream);
                            status = "Done";
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            outStream.close();
                        }
                        //bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
                    } else {
                        is = connection.getInputStream();
                        try {
                            OutputStream output = new FileOutputStream(mFileName);
                            try {
                                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                int read;

                                while ((read = is.read(buffer)) != -1) {
                                    output.write(buffer, 0, read);
                                }

                                output.flush();
                            } finally {
                                output.close();
                            }
                        } finally {
                            is.close();
                        }
                        status = "Done-2";
                        //bitmap = BitmapFactory.decodeStream(is);
                    }
                } catch (Throwable e) {
                    if (!this.isCancelled()) {
                        error = new SoundError(e).setErrorCode(SoundError.ERROR_GENERAL_EXCEPTION);
                        this.cancel(true);
                    }
                } finally {
                    try {
                        if (connection != null)
                            connection.disconnect();
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                        if (is != null)
                            is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return status;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    Log.e(TAG, "factory returned a null result");
                    mSoundLoaderListener.onError(new SoundError("downloaded file could not be decoded as String")
                            .setErrorCode(SoundError.ERROR_DECODE_FAILED));
                } else {
                    //Log.d(TAG, "download complete, " + result.getByteCount() + " bytes transferred");
                    mSoundLoaderListener.onComplete(result);
                }
                mUrlsInProgress.remove(soundUrl);
                System.gc();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Interface definition for callbacks to be invoked when
     * the sound save procedure status changes
     */
    public interface OnBitmapSaveListener {
        /**
         * Invoked to notify that the sound has been
         * successfully saved
         */
        void onBitmapSaved();

        /**
         * Invoked if an error occurs while saving the sound
         *
         * @param error the occurred error
         */
        void onBitmapSaveError(SoundError error);
    }

    /**
     * Tries to write the given Bitmap to device's storage using an {@link AsyncTask}.
     * This method handles common errors and will provide an error message via the
     * {@link OnBitmapSaveListener#onBitmapSaveError(SoundError)} callback in case anything
     * goes wrong.
     *
     * @param soundFile       a File representing the sound to be saved
     * @param sound           the actual Bitmap to save
     * @param listener        an OnBitmapSaveListener instance
     * @param format          sound format. Can be one of the following:<br>
     *                        <ul>
     *                        <li>{@link Bitmap.CompressFormat#PNG}</li>
     *                        <li>{@link Bitmap.CompressFormat#JPEG}</li>
     *                        <li>{@link Bitmap.CompressFormat#WEBP}</li>
     *                        </ul>
     * @param shouldOverwrite whether to overwrite an existing file
     */


    /**
     * Represents an error that has occurred while
     * downloading sound or writing it to disk. Since
     * this class extends {@code Throwable}, you may get the
     * stack trace from an {@code SoundError} object
     */
    public static final class SoundError extends Throwable {

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
         * The downloaded file could not be decoded as String
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


        public SoundError(@NonNull String message) {
            super(message);
        }

        public SoundError(@NonNull Throwable error) {
            super(error.getMessage(), error.getCause());
            this.setStackTrace(error.getStackTrace());
        }

        /**
         * @param code the code for the occurred error
         * @return the same SoundError object
         */
        public SoundError setErrorCode(int code) {
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