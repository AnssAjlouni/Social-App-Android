package com.rovas.forgram.fogram.interfaces;

import android.net.Uri;

/**
 * Created by Mohamed El Sayed
 */
public interface OnUploadedCallback {
    void onUploadSuccess(String uid, Uri downloadUrl, String type);

    void onProgress(double progress);

    void onUploadFailed(Exception e);
}