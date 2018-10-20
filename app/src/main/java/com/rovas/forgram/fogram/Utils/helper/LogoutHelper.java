package com.rovas.forgram.fogram.Utils.helper;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.rovas.forgram.fogram.managers.UserWiazrd;
/**
 * Created by Mohamed El Sayed
 */
public class LogoutHelper {

    private static final String TAG = LogoutHelper.class.getSimpleName();
    private static ClearImageCacheAsyncTask clearImageCacheAsyncTask;

    public static void signOut(FragmentActivity fragmentActivity) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String providerId = profile.getProviderId();
                logoutByProvider(providerId, fragmentActivity);
            }
            logoutFirebase(fragmentActivity.getApplicationContext());
        }

        if (clearImageCacheAsyncTask == null) {
            clearImageCacheAsyncTask = new ClearImageCacheAsyncTask(fragmentActivity.getApplicationContext());
            clearImageCacheAsyncTask.execute();
        }
    }

    private static void logoutByProvider(String providerId, FragmentActivity fragmentActivity) {
        switch (providerId) {

            case FacebookAuthProvider.PROVIDER_ID:
                //logoutFacebook(fragmentActivity.getApplicationContext());
                break;
        }
    }

    private static void logoutFirebase(Context context) {
        UserWiazrd.getInstance().dispose();
        try {
            FirebaseAuth.getInstance().signOut();
        }
        catch (Exception e)
        {
            Log.d(TAG, "logoutFirebase: " + e.getLocalizedMessage());
        }
    }


    private static class ClearImageCacheAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context context;

        public ClearImageCacheAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Glide.get(context.getApplicationContext()).clearDiskCache();
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            clearImageCacheAsyncTask = null;
            Glide.get(context.getApplicationContext()).clearMemory();
        }
    }
}
