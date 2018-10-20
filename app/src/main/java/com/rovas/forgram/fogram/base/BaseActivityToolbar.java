package com.rovas.forgram.fogram.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.Constants;

/**
 * Created by Mohamed El Sayed
 */

public class BaseActivityToolbar extends AppCompatActivity {

    public static final int READ_WRITE_STORAGE = 52;
    public ProgressDialog mProgressDialog;
    private long backPressedTime;
    public ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
    }

    public boolean requestPermission(String permission) {
        boolean isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    READ_WRITE_STORAGE);
        }
        return isGranted;
    }

    public void isPermissionGranted(boolean isGranted, String permission) {

    }

    public void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_WRITE_STORAGE:
                isPermissionGranted(grantResults[0] == PackageManager.PERMISSION_GRANTED, permissions[0]);
                break;
        }
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

    protected void showSnackbar(@NonNull String message) {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    public void attemptToExitIfRoot() {
        attemptToExitIfRoot(null);
    }
    public void attemptToExitIfRoot(@Nullable View anchorView) {
        if (isTaskRoot()) {
            if (backPressedTime + Constants.General.DOUBLE_CLICK_TO_EXIT_INTERVAL> System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                if (anchorView != null) {
                    showSnackBar(anchorView, R.string.press_once_again_to_exit);
                } else {
                    showSnackBar(R.string.press_once_again_to_exit);
                }

                backPressedTime = System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
        }
    }
    public void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    public void showSnackBar(@StringRes int messageId) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                messageId, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showSnackBar(View view, @StringRes int messageId) {
        Snackbar snackbar = Snackbar.make(view, messageId, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showToast(@StringRes int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_LONG).show();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showWarningDialog(int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageId);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }
    public void showNotCancelableWarningDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, null);
        builder.setCancelable(false);
        builder.show();
    }

    public void showWarningDialog(int message, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, listener);
        builder.show();
    }

    public void showWarningDialog(String message, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, listener);
        builder.show();
    }
}
