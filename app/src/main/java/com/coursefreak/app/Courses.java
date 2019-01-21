package com.coursefreak.app;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.coursefreak.app.fragment.catalog;
import com.coursefreak.app.fragment.interested;
import com.coursefreak.app.fragment.recommended;
import com.google.firebase.auth.FirebaseAuth;

public class Courses extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d("denis", FirebaseAuth.getInstance().getCurrentUser().getUid());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        this.mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        recommended recommendedFragment = mSectionsPagerAdapter.getRecommended();
        mSectionsPagerAdapter.getCatalog().setRecommendedFragment(recommendedFragment);
        mSectionsPagerAdapter.getInterested().setRecommendedFragment(recommendedFragment);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_courses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        else if(id == R.id.logoutAction) {
            this.mAuth.signOut();
            Intent logoutIntent = new Intent(getApplicationContext(), LoginPage.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private catalog c;
        private recommended r;
        private interested i;

        public catalog getCatalog() {
            if(this.c == null)
                this.c = new catalog();
            return this.c;
        }
        public recommended getRecommended() {
            if(this.r == null)
                this.r = new recommended();
            return this.r;
        }
        public interested getInterested() {
            if(this.i == null)
                this.i = new interested();
            return this.i;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return this.getCatalog();
                case 1:
                    return this.getRecommended();
                case 2:
                    return this.getInterested();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
       public CharSequence getPageTitle(int position){
            switch (position) {
                case 0:
                    return "Catalog";
                case 1:
                    return "Recommended";
                case 2:
                    return "Interested";
                default:
                    return null;
            }
        }
    }
}
