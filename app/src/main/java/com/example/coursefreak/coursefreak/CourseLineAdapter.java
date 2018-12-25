package com.example.coursefreak.coursefreak;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
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
import java.util.Map;
import java.util.Random;

public class CourseLineAdapter extends ArrayAdapter<Course> {
    private FirebaseAuth mAuth;
    private Context contex;
    private String choice;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    public CourseLineAdapter(Context context, ArrayList<Course> courses) {
        super(context, 0, courses);
        this.contex=context;
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
        courseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(contex,CourseView.class);
                intent.putExtra("course",course.getCourseID());
            }
        });
        final CheckBox cb = convertView.findViewById(R.id.checkBoxDone);
        this.mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uid;
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
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checked = ((CheckBox) buttonView).isChecked();
                if (checked == true) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(contex);
                    builderSingle.setTitle("how did you feel about the course?");

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(contex, android.R.layout.select_dialog_singlechoice);
                    arrayAdapter.add("Like");
                    arrayAdapter.add("Dislike");
                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            choice = arrayAdapter.getItem(which);
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(contex);
                            builderInner.setTitle("Thank You");
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderInner.show();
                        }
                    });
                    builderSingle.show();
                    myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User u = dataSnapshot.getValue(User.class);
                            if(u == null)
                                Log.d("user", "ERROR");
                            else {
                                if(u.related_courses.containsKey(course.getCourseID())) {
                                    u.related_courses.get(course.getCourseID()).completed = true;
                                    if(choice=="Like"){
                                        course.numLikes++;
                                    }
                                }else{
                                    boolean like=false;
                                    if(choice=="Like"){
                                        like=true;
                                        course.numLikes++;
                                    }
                                    UserRelatedCourse data = new UserRelatedCourse(false,true,like);
                                    u.relateNewCourse(course.getCourseID(),data);
                                }

                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("Courses", "Database Error");
                        }
                    });

            }else{
                    myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User u = dataSnapshot.getValue(User.class);
                            if (u == null)
                                Log.d("user", "ERROR");
                            else {
                                boolean liked=u.getRelated_courses().get(course.getCourseID()).getLiked();
                                u.related_courses.get(course.getCourseID()).completed = false;
                                if(liked){
                                    course.numLikes--;
                                }
                                if(u.getRelated_courses().get(course.getCourseID()).getInterested()==false){
                                    u.getRelated_courses().remove(course.getCourseID());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("Courses", "Database Error");
                        }
                    });
                }
        }});
        final Switch interested_switch=convertView.findViewById(R.id.switchInterested);
        interested_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User u = dataSnapshot.getValue(User.class);
                            if (u == null)
                                Log.d("user", "ERROR");

                            else {
                                if (isChecked) {
                                    if (u.getRelated_courses().containsKey(course.getCourseID())) {
                                        u.getRelated_courses().get(course.getCourseID()).interested = true;

                                    } else {
                                        UserRelatedCourse data = new UserRelatedCourse(true, false, false);
                                        u.relateNewCourse(course.getCourseID(), data);
                                    }

                                }else{
                                    u.getRelated_courses().get(course.getCourseID()).interested = false;
                                    if(u.getRelated_courses().get(course.getCourseID()).getCompleted()==false){
                                        u.getRelated_courses().remove(course.getCourseID());
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("Courses", "Database Error");
                        }
                });
            }
        });
        courseName.setText(course.getName()+"-"+course.getCourseID());

        return convertView;
    }
}