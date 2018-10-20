package com.rovas.forgram.fogram.base;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
/**
 * Created by Mohamed El Sayed
 */

public abstract class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog;
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading....");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    // [END write_fan_out]
}
