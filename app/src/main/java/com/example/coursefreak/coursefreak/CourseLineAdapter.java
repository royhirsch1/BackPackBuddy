package com.example.coursefreak.coursefreak;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class CourseLineAdapter extends ArrayAdapter<Course> {
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    public CourseLineAdapter(Context context, ArrayList<Course> courses) {
        super(context, 0, courses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Course course = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_course, parent, false);
        }
        // Lookup view for data population
        final TextView courseName = (TextView) convertView.findViewById(R.id.textViewCourseName);
        final CheckBox cb = convertView.findViewById(R.id.checkBoxDone);
        this.mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid;
        if(currentUser != null){
            uid = currentUser.getUid();
        }else{
            uid="0";
        }
        myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if(u == null)
                    Log.d("user", "ERROR");
                else {
                    if(u.related_courses.containsKey(course.getCourseID())){
                        if(u.related_courses.get(course.getCourseID()).completed){
                            cb.setChecked(true);
                        }else {
                            cb.setChecked(false);
                        }
                    }else{
                        cb.setChecked(false);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Courses", "Database Error");
            }
        });
        courseName.setText(course.getName()+"-"+course.getCourseID());

        return convertView;
    }
}