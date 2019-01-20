package com.example.coursefreak.coursefreak;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.coursefreak.coursefreak.fragment.FragmentTabsCatalog;
import com.example.coursefreak.coursefreak.fragment.catalog;
import com.example.coursefreak.coursefreak.fragment.interested;
import com.example.coursefreak.coursefreak.fragment.recommended;
import com.example.coursefreak.coursefreak.utils.Tools;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class TabsCoursesActivity extends AppCompatActivity {
    private ViewPager view_pager;
    private SectionsPagerAdapter viewPagerAdapter;
    private TabLayout tab_layout;

    private ActionBar actionBar;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs_courses_activity);
        this.mAuth = FirebaseAuth.getInstance();
        initComponent();
        initToolbar();
        initNavigationMenu();
    }
    @Override
    protected void onResume(){
        super.onResume();
        actionBar.setTitle("Courses");
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Courses");
        //Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        setupViewPager(view_pager);

        tab_layout.setupWithViewPager(view_pager);

        tab_layout.getTabAt(0).setIcon(R.drawable.ic_list);
        tab_layout.getTabAt(1).setIcon(R.drawable.ic_star);
        tab_layout.getTabAt(2).setIcon(R.drawable.ic_bookmark);

        // set icon color pre-selected
        tab_layout.getTabAt(0).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        viewPagerAdapter.addFragment(FragmentTabsCatalog.newInstance(), "Catalog");    // index 0
//        viewPagerAdapter.addFragment(FragmentTabsCatalog.newInstance(), "Recommended");   // index 1
//        viewPagerAdapter.addFragment(FragmentTabsCatalog.newInstance(), "Bookmarks");    // index 2
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private catalog fullCourses;
        private recommended recommendedCourses;
        private interested bookmarkedCourses;

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);

            this.fullCourses = new catalog();
            this.recommendedCourses = new recommended();
            this.bookmarkedCourses = new interested();

            this.fullCourses.setRecommendedFragment(this.recommendedCourses);
            this.fullCourses.setBookmarkFragment(this.bookmarkedCourses);

            this.recommendedCourses.setCatalogFragment(this.fullCourses);
            this.recommendedCourses.setInterestedFragment(this.bookmarkedCourses);

            this.bookmarkedCourses.setCatalogFragment(this.fullCourses);
            this.bookmarkedCourses.setRecommendedFragment(this.recommendedCourses);

            this.mFragmentList.add(this.fullCourses);
            this.mFragmentTitleList.add("Catalog");
            this.mFragmentList.add(this.recommendedCourses);
            this.mFragmentTitleList.add("Recommended");
            this.mFragmentList.add(this.bookmarkedCourses);
            this.mFragmentTitleList.add("Bookmarks");
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public catalog getFullCoursesFragment() { return this.fullCourses; }
        public recommended getRecommendedCoursesFragment() { return this.recommendedCourses; }
        public interested getBookmarkedCoursesFragment() { return this.bookmarkedCourses; }
    }

    private void initNavigationMenu() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                Intent nextActivity = null;
                switch (item.getTitle().toString()){
                    case "Log Out":
                        FirebaseAuth.getInstance().signOut();
                        nextActivity = new Intent(getApplicationContext(), LoginPage.class);
                        nextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        nextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        break;
                    case "About":
                        nextActivity = new Intent(getApplicationContext(), AboutActivity.class);
                        break;
                    case "Courses":
                        //refresh activity
                        nextActivity = new Intent(getApplicationContext(), TabsCoursesActivity.class);
                        nextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        nextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), item.getTitle() + " will be added soon", Toast.LENGTH_SHORT).show();
                }
                    //nextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //nextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                if(nextActivity != null){
                    startActivity(nextActivity);
                }
                    //finish();


                //actionBar.setTitle(item.getTitle());
                drawer.closeDrawers();
                return true;
            }
        });


    }
}