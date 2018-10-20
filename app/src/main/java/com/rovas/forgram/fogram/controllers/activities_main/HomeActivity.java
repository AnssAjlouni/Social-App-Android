package com.rovas.forgram.fogram.controllers.activities_main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Log;

import com.fxn.utility.PermUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.base.BaseActivity;
import com.rovas.forgram.fogram.controllers.fragments_home.ChatFragment;
import com.rovas.forgram.fogram.controllers.fragments_home.HomeFragment;
import com.rovas.forgram.fogram.controllers.fragments_home.NotificationFragment;
import com.rovas.forgram.fogram.controllers.fragments_home.ProfileFragment;
import com.rovas.forgram.fogram.Utils.SectionPagerAdapter;
import com.rovas.forgram.fogram.R;

import java.sql.Timestamp;
import java.util.HashMap;
/**
 * Created by Mohamed El Sayed
 */
public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";
    //FireBase
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private FirebaseUser current_user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {// Alt + Insert To Open Fast Insert anything
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            init();
        }
        else
        {
            init();
        }

    }

    private void init() {
        //makeFullScreen();
        //FireBase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        current_user = mAuth.getCurrentUser();
        //



        //
        Log.d(TAG, "onCreate: started.");
        //setupBottomNavigationView();
        setupViewPager();
        //getImages();
    }

    //Responsible For Adding the 3 tabs : Camera  , Home , Messages
    private void setupViewPager()
    {
        SectionPagerAdapter adapter = new  SectionPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new HomeFragment());//index 0
        adapter.AddFragment(new ChatFragment());//index 1
        adapter.AddFragment(new NotificationFragment()); //index 2
        adapter.AddFragment(new ProfileFragment()); //index 4
        //adapter.AddFragment(new GroupFragment()); //index 4
        // adapter.AddFragment(new FriendsFragment()); //index 4
        klogi.com.RtlViewPager viewPager = (klogi.com.RtlViewPager) findViewById(R.id.viewpager_container);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_notification);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_profile);
        //tabLayout.getTabAt(4).setIcon(R.drawable.ic_chat);
        //tabLayout.getTabAt(4).setIcon(R.drawable.ic_friends);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Reload current fragment
       /*
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentById(HomeFragment.class);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
        */
    }
    @Override
    public void onStart() {
        super.onStart();
        if(current_user != null) {
            HashMap<String, Object> online_map = new HashMap<>();
            online_map.put("online", (long)1);
            //online_map.put("online" , 1);
            fStore.collection("users").document(user_id).update(online_map);
        }
    }
    HashMap<String, Object> timestampCreated;
    @Override
    public void onStop() {
        super.onStop();
        if(current_user != null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            HashMap<String, Object> online_map = new HashMap<>();
            online_map.put("online" , timestamp.getTime());
            // this.timestampCreated = online_map;
            fStore.collection("users").document(user_id).update(online_map);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //Toast.makeText(MultiEditorActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    @Override
    public void onBackPressed() {
        attemptToExitIfRoot(null);
    }



}
