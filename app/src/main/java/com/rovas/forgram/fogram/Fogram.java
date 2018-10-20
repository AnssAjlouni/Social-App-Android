package com.rovas.forgram.fogram;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.controllers.activities_main.SignInActivity;
import com.rovas.forgram.fogram.managers.UserWiazrd;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by Mohamed El Sayed
 */
public class Fogram extends Application {
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser current_user;
    private String current_user_id;
    private Messenger mService;
    private boolean mBound;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);}
    @Override
    public void onCreate() {
        super.onCreate();


        //FireBase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        current_user = mAuth.getCurrentUser();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException (Thread thread, Throwable e) {
                        handleUncaughtException (thread, e);
                    }
                });


        // Force Login
        /*
        if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getEmail().isEmpty()) {
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        /*
        Intent startServiceIntent = new Intent(this, MessagingService.class);
        this.startService(startServiceIntent);

        this.bindService(new Intent(this, MessagingService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        Log.e(TAG, " Context.BIND_AUTO_CREATE");

        if (this.mBound) {
            Message msg = Message.obtain(null, MessagingService.MSG_SEND_NOTIFICATION,
                    1, 1);
            try {
                this.mService.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "Error sending a message", e);
                MessageLogger.logMessage(this, "Error occurred while sending a message.");
            }
        }
        //
        */
        /*
        FileUtils.createApplicationFolder();
        //Need to recode this thread
        Thread.setDefaultUncaughtExceptionHandler(new LocalFileUncaughtExceptionHandler(this,
                Thread.getDefaultUncaughtExceptionHandler()));
        */
    }
    private void handleUncaughtException (Thread thread, Throwable e) {

        // The following shows what I'd like, though it won't work like this.
        Log.d(TAG, "handleUncaughtException: " + e);
        Intent intent = new Intent (getApplicationContext() , MainActivity.class);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);

        // Add some code logic if needed based on your requirement
    }
    @Override
    public void onTerminate() {
        /*
        if (this.mBound) {
            this.unbindService(mConnection);
            this.mBound = false;
        }
        */
        super.onTerminate();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;

        }
    };
}
