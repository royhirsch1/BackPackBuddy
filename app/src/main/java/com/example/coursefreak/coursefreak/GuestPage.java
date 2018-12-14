package com.example.coursefreak.coursefreak;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class GuestPage extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private List<Course> all_courses;
    private GuestCoursesRecyclerAdapter mCoursesAdapter;
    private RecyclerView mCoursesRecycler;
    private Context ctx;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_course_view);
        this.all_courses = new ArrayList<>();
        mCoursesRecycler = (RecyclerView) findViewById(R.id.recyclerCourseList);
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.ctx = this;
        mDatabase.child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    Course c = courseSnapshot.getValue(Course.class);
                    all_courses.add(c);
                    if(c != null)
                        Log.d("Course", c.getName());
                    else
                        Log.d("Course", "NULL");
                }
                mCoursesAdapter = new GuestCoursesRecyclerAdapter(all_courses, ctx);
                LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
                mCoursesRecycler.setLayoutManager(layoutManager);
                mCoursesRecycler.addItemDecoration(new DividerItemDecoration
                        (mCoursesRecycler.getContext(), layoutManager.getOrientation()));
                mCoursesRecycler.setAdapter(mCoursesAdapter);
                List<GuestCoursesRecyclerAdapter.ViewHolder> views = mCoursesAdapter.getViews();
                for(GuestCoursesRecyclerAdapter.ViewHolder view : views) {
                    setViewClickEvents(view);
                    Double percentPositive = view.percents;
                    if(percentPositive < 0.45) {
                        view.coursePercentageTitle.setTextColor(getResources().getColor(R.color.colorReviewNegative));
                    } else if (percentPositive > 0.55) {
                        view.coursePercentageTitle.setTextColor(getResources().getColor(R.color.colorReviewPositive));
                    } else {
                        view.coursePercentageTitle.setTextColor(getResources().getColor(R.color.colorReviewNeutral));
                    }
                }
            }

            private void setViewClickEvents(GuestCoursesRecyclerAdapter.ViewHolder view) {
                view.courseNameTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), SignupPage.class);
                        startActivity(intent);
                    }
                });
                view.coursePercentageTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), SignupPage.class);
                        startActivity(intent);
                    }
                });
                view.courseReviewsTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), SignupPage.class);
                        startActivity(intent);
                    }
                });
                view.moreInfoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), SignupPage.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database Connection Error", Toast.LENGTH_LONG)
                    .show();
            }
        });
        if(mDatabase == null) {
            Toast.makeText(getApplicationContext(), "Database Connection Error", Toast.LENGTH_LONG)
                .show();
        }
    }
}
