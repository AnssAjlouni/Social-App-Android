package com.rovas.forgram.fogram.Utils.File;

import android.os.Environment;

/**
 * Created by Mohamed El Sayed
 */

public class FilePaths {

    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String Messenger = ROOT_DIR + "/Pictures/Messenger";
    public String DCIM_PICTURES = ROOT_DIR + "/DCIM";
    public String DOWNLOAD = ROOT_DIR + "/Download";
    public String HOME = ROOT_DIR + "/";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
    public String FIREBASE_VIDEO_STORAGE = "videos/users/";

}
