package com.example.coursefreak.coursefreak;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    public catalog() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.catalog, container, false);
        final ListView lv = (ListView) rootView.findViewById(R.id.course_list);
        final ArrayList<Course> res = new ArrayList<>();
        myRef.child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                    Log.d("Courses", courseSnap.getKey());
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

        return lv;
    }

}

