package com.example.coursefreak.coursefreak;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class interested extends Fragment {
    private static View rootView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private recommended recommendedFragment;
    private catalog catalogFragment;
    private interested interestedFragment;
    private ListView bookmarkList;
    final ArrayList<Course> bookmarkedCourses = new ArrayList<>();
    private FirebaseAuth mAuth;
    private ListView bookmarkedList;
    public interested() { this.interestedFragment = this; }

    public void setRecommendedFragment(recommended recommendedFragment) {
        this.recommendedFragment = recommendedFragment;
    }

    public void setCatalogFragment(catalog catalogFragment) { this.catalogFragment = catalogFragment; }

    public void updateInterested() {
        String uid = mAuth.getUid();
        myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User u = dataSnapshot.getValue(User.class);
                myRef.child("courses").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookmarkedCourses.clear();
                        for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                            Log.d("Courses", courseSnap.getKey());
                            Course c = courseSnap.getValue(Course.class);
                            c.parseCatsReqs();
                            if (u.getRelated_courses().containsKey(c.getCourseID()))
                                if (u.getRelated_courses().get(c.getCourseID()).getInterested() == true)
                                    bookmarkedCourses.add(c);
                        }
                        CourseLineAdapter cla = new CourseLineAdapter(getContext(), bookmarkedCourses, recommendedFragment, catalogFragment, interestedFragment);
                        bookmarkedList.setAdapter(cla);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        bookmarkedCourses.clear();
                        Log.d("Courses", "Database Error");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                bookmarkedCourses.clear();
                Toast.makeText(rootView.getContext(), "Error getting bookmarks", Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.interested, null);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        if(rootView.getParent()!=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            parent.removeAllViews();
        }
        final ListView lv = rootView.findViewById(R.id.course_list2);
        final ArrayList<Course> res = new ArrayList<>();
        this.mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uid;
        if(currentUser != null){
            uid = currentUser.getUid();
        }else{
            uid="0";
        }
        Log.d("denis", "UID is ".concat(uid));
        myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User u = dataSnapshot.getValue(User.class);
                myRef.child("courses").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                        res.clear();
                        for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                            Log.d("Courses", courseSnap.getKey());
                            Course c = courseSnap.getValue(Course.class);
                            c.parseCatsReqs();
                            if(u.getRelated_courses().containsKey(c.getCourseID()))
                                if(u.getRelated_courses().get(c.getCourseID()).getInterested() == true)
                                    res.add(c);
                        }
                        CourseLineAdapter cla = new CourseLineAdapter(getContext(), res, recommendedFragment, catalogFragment, interestedFragment);
                        lv.setAdapter(cla);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        res.clear();
                        Log.d("Courses", "Database Error");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return rootView;
    }
}
