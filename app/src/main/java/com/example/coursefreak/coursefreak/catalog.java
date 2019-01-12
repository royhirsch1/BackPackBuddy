package com.example.coursefreak.coursefreak;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class catalog extends Fragment {
    private static View rootView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    final ArrayList<Course> res = new ArrayList<>();
    public catalog() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.catalog, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        final ListView lv = (ListView) rootView.findViewById(R.id.course_list);
        myRef.child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                res.clear();
                for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                    Course c = courseSnap.getValue(Course.class);
                    c.parseCatsReqs();
                    res.add(c);
                }

                CourseLineAdapter cla= new CourseLineAdapter(getContext(),res);
                lv.setAdapter(cla);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                res.clear();
                Log.d("Courses", "Database Error");
            }
        });

        return rootView;
    }

}

