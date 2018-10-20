package com.rovas.forgram.fogram.controllers.activities_publish;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.Permissions;
import com.rovas.forgram.fogram.Utils.SectionPagerAdapter;
import com.rovas.forgram.fogram.controllers.fragments_publish.GalleryFragment;
import com.rovas.forgram.fogram.controllers.fragments_publish.StudioFragment;
import com.rovas.forgram.fogram.controllers.fragments_publish.TweetFragment;


/**
 * Created by Mohamed El Sayed
 */

public class ShareActivity extends AppCompatActivity{
    private static final String TAG = "ShareActivity";

    //constants
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private klogi.com.RtlViewPager mViewPager;


    private Context mContext = ShareActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Log.d(TAG, "onCreate: started.");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)//Require Permisson if Version >= 6 Marsm
        {
            if (checkPermissionsArray(Permissions.PERMISSIONS)) {
                setupViewPager();
            } else {
                verifyPermissions(Permissions.PERMISSIONS);
            }
        }
        else
        {
            try {
                setupViewPager();
            }
            catch(Exception e)
            {
                Toast.makeText(ShareActivity.this, "Error , " + e , Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * return the current tab number
     * 0 = GalleryFragment
     * 1 = PhotoFragment
     * @return
     */
    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    /**
     * setup viewpager for manager the tabs
     */
    private void setupViewPager(){
        SectionPagerAdapter adapter =  new SectionPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new TweetFragment());//0
        adapter.AddFragment(new GalleryFragment());//1
        adapter.AddFragment(new StudioFragment());//2

        mViewPager = (klogi.com.RtlViewPager) findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.tweet));
        tabLayout.getTabAt(1).setText(getString(R.string.gallery));
        tabLayout.getTabAt(2).setText(getString(R.string.video));

    }

    public int getTask(){
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }

    /**
     * verifiy all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    /**
     * BottomNavigationView setup
     */

}