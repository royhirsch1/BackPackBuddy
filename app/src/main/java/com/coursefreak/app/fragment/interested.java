package com.coursefreak.app.fragment;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coursefreak.app.Course;
import com.coursefreak.app.CourseLineAdapter;
import com.coursefreak.app.R;
import com.coursefreak.app.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class interested extends Fragment {
    private static View rootView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private recommended recommendedFragment;
    private catalog catalogFragment;

    private ListView bookmarkList;
    final ArrayList<Course> bookmarkedCourses = new ArrayList<>();
    private FirebaseAuth mAuth;
    public interested() {}

    public void setRecommendedFragment(recommended recommendedFragment) {
        this.recommendedFragment = recommendedFragment;
    }

    public void setCatalogFragment(catalog catalogFragment) {
        this.catalogFragment = catalogFragment;
    }

    public void updateBookmarkedCourse(Course c) {
        // Course does not exist in this tab, and so we add it.
        if(this.bookmarkList == null)
            return;
        this.bookmarkedCourses.add(c);
        ((CourseLineAdapter)this.bookmarkList.getAdapter()).notifyDataSetChanged();
    }

    public void updateLikedCourse(String courseID, int updatedIcon) {
        if(this.bookmarkList == null)
            return;
        for(int i = 0; i < this.bookmarkList.getAdapter().getCount(); i++) {
            View v = this.bookmarkList.getChildAt(i);
            if(v == null)
                continue;
            String text = ((TextView)v.findViewById(R.id.textViewCourseID)).getText().toString();
            if(text.contains(courseID)) {
                ImageView bookmarkView = v.findViewById(R.id.likeCourseButton);
                if(updatedIcon == R.drawable.ic_love)
                    bookmarkView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_love));
                else
                    bookmarkView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_love_empty));
                bookmarkView.setTag(updatedIcon);
                break;
            }
        }
    }

    public void removeBookmarkedCourse(String courseID) {
        if(this.bookmarkList == null)
            return;
        for(int i = 0; i < this.bookmarkList.getAdapter().getCount(); i++) {
            View v = this.bookmarkList.getChildAt(i);
            if(v == null)
                continue;
            String text = ((TextView)v.findViewById(R.id.textViewCourseID)).getText().toString();
            if(text.contains(courseID)) {
                this.bookmarkedCourses.remove(i);
                ((CourseLineAdapter)this.bookmarkList.getAdapter()).notifyDataSetChanged();
                break;
            }
        }
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
//        Log.d("denis", "UID is ".concat(uid));
        myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User u = dataSnapshot.getValue(User.class);
                myRef.child("courses").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                        res.clear();
                        for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
//                            Log.d("Courses", courseSnap.getKey());
                            Course c = courseSnap.getValue(Course.class);
                            c.parseCatsReqs();
                            if(u.getRelated_courses().containsKey(c.getCourseID()))
                                if(u.getRelated_courses().get(c.getCourseID()).getInterested() == true)
                                    res.add(c);
                        }
                        interested.this.bookmarkedCourses.clear();
                        interested.this.bookmarkedCourses.addAll(res);
                        CourseLineAdapter cla = new CourseLineAdapter(getContext(), interested.this.bookmarkedCourses,
                                interested.this.catalogFragment,
                                interested.this.recommendedFragment,
                                interested.this);
                        lv.setAdapter(cla);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        res.clear();
//                        Log.d("Courses", "Database Error");
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
