package com.rovas.forgram.fogram;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.Utils.SpinKit.SpinKitView;
import com.rovas.forgram.fogram.Utils.SpinKit.style.CubeGrid;
import com.rovas.forgram.fogram.Utils.SpinKit.style.ThreeBounce;
import com.rovas.forgram.fogram.Utils.helper.LoadHelper;
import com.rovas.forgram.fogram.controllers.activities_main.SignInActivity;
import com.rovas.forgram.fogram.controllers.activities_main.SetupActivity;
import com.rovas.forgram.fogram.controllers.activities_main.HomeActivity;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.models.Notification;
import com.rovas.forgram.fogram.models.User;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity"; //declration using logt
    private static int TIME_OUT = 3000; //Time to launch the another activity
    //Firebase Objects
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private List<Notification> notificationList;
    //
    private String current_user_id;//Current_user_id>> Using Firebase Auth
    private SpinKitView spinKitView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start);
        Log.d(TAG, "onCreate: strarting..");//logd >> Save Logs
        //Data Base Objects Decleration
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        notificationList = new ArrayList<>();
        spinKitView = findViewById(R.id.spin_kit);
        ThreeBounce threeBounce = new ThreeBounce();
        spinKitView.setIndeterminateDrawable(threeBounce);

    }
    public void sendtoLogin()
    {
        //Intent
        Intent loginIntent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(loginIntent);
        finish();//Don't Return AnyMore TO the last page

    }
    public void sendtoHome()
    {
        if(UserWiazrd.getInstance().getTempUser().getUsername() != null && UserWiazrd.getInstance().getTempUser().getThumb_image() != null ) {
            Intent loginIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(loginIntent);
            finish();//Don't Return AnyMore TO the last page
        }
        else
        {
            LoadHelper.Load();
            //Start
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    sendtoHome();
                }
            }, TIME_OUT);
        }

    }
    public void sendtoSetup()
    {
        Intent loginIntent = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(loginIntent);
        finish();//Don't Return AnyMore TO the last page

    }
    private void Load_ChatGroup() {
        fStore.collection("users").whereEqualTo("user_id" , current_user_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String groupID = doc.getDocument().getId();
                        User sGroup = doc.getDocument().toObject(User.class).withid(groupID);

                    }
                }
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        /*
        final ComponentName onBootReceiver = new ComponentName(getApplication().getPackageName(), MyBroadcastReceiver.class.getName());
        if(getPackageManager().getComponentEnabledSetting(onBootReceiver) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            getPackageManager().setComponentEnabledSetting(onBootReceiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
        Log.e(TAG, "Notification :  getPackageManager().setComponentEnabledSetting(onBootReceiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED");
        */
        if (CurrentUser == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendtoLogin();
                }
            }, TIME_OUT);// Time Out >> Declare A Function After countdown end

        } else {

            LoadHelper.Load();
            //Start
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    sendtoHome();
                }
            }, TIME_OUT);
            //End


        }

    }


}