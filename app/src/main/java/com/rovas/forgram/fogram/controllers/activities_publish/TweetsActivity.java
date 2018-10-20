package com.rovas.forgram.fogram.controllers.activities_publish;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.SectionPagerAdapter;
import com.rovas.forgram.fogram.controllers.fragments_publish.TweetFragment;
/**
 * Created by Mohamed El Sayed
 */
public class TweetsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweets);

        try {
            setupViewPager();
        }
        catch(Exception e)
        {
            Toast.makeText(TweetsActivity.this, "Error , " + e , Toast.LENGTH_SHORT).show();
        }

    }
    private void setupViewPager(){
        SectionPagerAdapter adapter =  new SectionPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new TweetFragment());//0

        klogi.com.RtlViewPager mViewPager = (klogi.com.RtlViewPager) findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.tweet));

    }
}
