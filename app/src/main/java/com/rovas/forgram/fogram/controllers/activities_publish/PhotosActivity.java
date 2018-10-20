package com.rovas.forgram.fogram.controllers.activities_publish;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.SectionPagerAdapter;
import com.rovas.forgram.fogram.controllers.fragments_publish.GalleryFragment;
/**
 * Created by Mohamed El Sayed
 */
public class PhotosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        try {
            setupViewPager();
        }
        catch(Exception e)
        {
            Toast.makeText(PhotosActivity.this, "Error , " + e , Toast.LENGTH_SHORT).show();
        }

    }
    private void setupViewPager(){
        SectionPagerAdapter adapter =  new SectionPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new GalleryFragment());//0

        klogi.com.RtlViewPager mViewPager = (klogi.com.RtlViewPager) findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.gallery));

    }
    public int getTask(){
        return getIntent().getFlags();
    }
}
