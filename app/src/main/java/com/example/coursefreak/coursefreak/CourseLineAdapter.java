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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.coursefreak.coursefreak.fragment.catalog;
import com.example.coursefreak.coursefreak.fragment.interested;
import com.example.coursefreak.coursefreak.fragment.recommended;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CourseLineAdapter extends ArrayAdapter<Course> {
    private FirebaseAuth mAuth;
    private Context contex;
    private String choice;

    // Data for the current user.
    private User currentUser;

    private recommended recommendFragment;
    private catalog catalogFragment;
    private interested interestedFragment;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    public CourseLineAdapter(Context context, ArrayList<Course> courses, catalog catalogFragment,
                             recommended recommendFragment, interested interestedFragment) {
        super(context, 0, courses);
        this.contex = context;
        this.recommendFragment = recommendFragment;
        this.catalogFragment = catalogFragment;
        this.interestedFragment = interestedFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final Course course = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        View ret=null;
        if (convertView == null) {
            ret = LayoutInflater.from(getContext()).inflate(R.layout.single_course, null);
        }else{
            ret=convertView;
        }

        // Lookup view for data population
        final TextView courseName = (TextView) ret.findViewById(R.id.textViewCourseName);
        courseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(contex,CourseView.class);
                intent.putExtra("course",course);
                contex.startActivity(intent);

            }
        });

        final TextView courseID = (TextView) ret.findViewById(R.id.textViewCourseID);

        final ImageButton bookmarkButton = ret.findViewById(R.id.bookmarkBtn);
        bookmarkButton.setImageResource(R.drawable.ic_bookmark_border);
        bookmarkButton.setColorFilter(contex.getResources().getColor(R.color.colorFacebook));
        bookmarkButton.setTag(R.drawable.ic_bookmark_border);

        final ImageButton likeButton = ret.findViewById(R.id.likeCourseButton);
        likeButton.setImageResource(R.drawable.ic_love_empty);
        likeButton.setColorFilter(contex.getResources().getColor(R.color.colorError));
        likeButton.setTag(R.drawable.ic_love_empty);


        this.mAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        final String uid;

        if(currentUser != null){
            uid = currentUser.getUid();
        } else{
            uid="0";
        }

        if(this.currentUser == null) {
            myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    CourseLineAdapter.this.currentUser = u;
                    if(u == null) {
                        Log.d("user", "ERROR");
                    }

                    else {
                        if(u.getRelated_courses().containsKey(course.getCourseID())){
                            if(u.getRelated_courses().get(course.getCourseID()).getLiked()){
                                likeButton.setImageResource(R.drawable.ic_love);
                                likeButton.setTag(R.drawable.ic_love);
                                //Log.d("wtf", "wtf like");
                            } else {
                                likeButton.setBackgroundResource(R.drawable.ic_love_empty);
                                likeButton.setTag(R.drawable.ic_love_empty);
                            }

                            if(u.getRelated_courses().get(course.getCourseID()).getInterested()) {
                                bookmarkButton.setImageResource(R.drawable.ic_bookmark);
                                bookmarkButton.setTag(R.drawable.ic_bookmark);
                            } else {
                                bookmarkButton.setImageResource(R.drawable.ic_bookmark_border);
                                bookmarkButton.setTag(R.drawable.ic_bookmark_border);
                            }
                        } else{
                            bookmarkButton.setImageResource(R.drawable.ic_bookmark_border);
                            bookmarkButton.setTag(R.drawable.ic_bookmark_border);
                            likeButton.setImageResource(R.drawable.ic_love_empty);
                            likeButton.setTag(R.drawable.ic_love_empty);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("Courses", "Database Error");
                }
            });
        } else {
            User u = this.currentUser;
            if(u.getRelated_courses().containsKey(course.getCourseID())){
                if(u.getRelated_courses().get(course.getCourseID()).getLiked()){
                    likeButton.setImageResource(R.drawable.ic_love);
                    likeButton.setTag(R.drawable.ic_love);
                    //Log.d("wtf", "wtf like");
                } else {
                    likeButton.setBackgroundResource(R.drawable.ic_love_empty);
                    likeButton.setTag(R.drawable.ic_love_empty);
                }

                if(u.getRelated_courses().get(course.getCourseID()).getInterested()) {
                    bookmarkButton.setImageResource(R.drawable.ic_bookmark);
                    bookmarkButton.setTag(R.drawable.ic_bookmark);
                } else {
                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_border);
                    bookmarkButton.setTag(R.drawable.ic_bookmark_border);
                }
            } else{
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_border);
                bookmarkButton.setTag(R.drawable.ic_bookmark_border);
                likeButton.setImageResource(R.drawable.ic_love_empty);
                likeButton.setTag(R.drawable.ic_love_empty);
            }
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("wtf", "wtf1");
                Log.d("wtf", "wtf11");
                if (likeButton.getTag().equals(R.drawable.ic_love_empty)) {
                    Log.d("wtf", "wtf111");

                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(contex);
                    builderSingle.setTitle("Did you really complete the course?");
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(contex, android.R.layout.select_dialog_singlechoice);
                    arrayAdapter.add("Yes");
                    arrayAdapter.add("No");
                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            choice = arrayAdapter.getItem(which);
                            User u = CourseLineAdapter.this.currentUser;
                            if(u == null) {
                                Log.d("userRates", "ERROR");
                            }

                            else {
                                if(u.getRelated_courses().containsKey(course.getCourseID())) {
                                    Log.d("wtf", "1");
                                    u.getRelated_courses().get(course.getCourseID()).setCompleted(true);
                                    course.numCompleted++;
                                    FirebaseUtils.updateCourseNumCompleted(course.getCourseID(),course.getNumCompleted(), myRef);

                                    if(choice.equals("Yes")){
                                        Log.d("wtf", "11");
                                        course.numLikes++;
                                        FirebaseUtils.userAddPositiveRating(uid, course.getCourseID(), myRef);
                                        UserRelatedCourse data = new UserRelatedCourse(
                                                u.getRelated_courses().get(course.getCourseID()).getInterested(),
                                                true,
                                                true);
                                        FirebaseUtils.addUserRelatedCourse(uid ,course.getCourseID(), data ,myRef);
                                        recommendFragment.reloadRecommended();
                                        interestedFragment.updateLikedCourse(course.getCourseID(), R.drawable.ic_love);
                                        catalogFragment.updateLikedCourse(course.getCourseID(), R.drawable.ic_love);
                                        likeButton.setImageResource(R.drawable.ic_love);
                                        likeButton.setTag(R.drawable.ic_love);
                                    } else {
                                        UserRelatedCourse data = new UserRelatedCourse(
                                                u.getRelated_courses().get(course.getCourseID()).getInterested(),
                                                false,
                                                false);
                                        FirebaseUtils.addUserRelatedCourse(uid ,course.getCourseID(), data ,myRef);
                                    }
                                }
                                else{
                                    Log.d("wtf", "2");
                                    if(choice.equals("Yes")){
                                        Log.d("wtf", "22");
                                        course.numLikes++;
                                        FirebaseUtils.userAddPositiveRating(uid,course.getCourseID(),myRef);
                                        UserRelatedCourse data = new UserRelatedCourse(false, true, true);
                                        FirebaseUtils.addUserRelatedCourse(uid,course.getCourseID(),data,myRef);
                                        recommendFragment.reloadRecommended();
                                    } else {
                                        // If did not actually complete the course, just set hearts to "unclick"
                                        likeButton.setImageResource(R.drawable.ic_love_empty);
                                        likeButton.setTag(R.drawable.ic_love_empty);
                                    }

                                }
                            }
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(contex);
                            builderInner.setTitle("Thank you for your contribution!");
                            builderInner.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderInner.show();
                        }
                    });
                    builderSingle.show();

                } else { // Like button was pressed and HEART_FULL
                    Log.d("wtf", "wtf112");
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(contex);
                    builderSingle.setTitle("Did you not like the course?");
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(contex, android.R.layout.select_dialog_singlechoice);
                    arrayAdapter.add("I did not like it.");
                    arrayAdapter.add("I liked it by mistake.");
                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            choice = arrayAdapter.getItem(which);
                            User u = CourseLineAdapter.this.currentUser;
                            if (u == null) {
                                Log.d("userRates", "ERROR");
                            } else {
                                UserRelatedCourse urc = null;
                                if(choice.equals("I did not like it.")) {
                                    urc = new UserRelatedCourse(
                                            u.getRelated_courses().get(course.getCourseID()).getInterested(),
                                            true,
                                            false
                                    );
                                } else { //MISTAKE
                                    urc = new UserRelatedCourse(
                                            u.getRelated_courses().get(course.getCourseID()).getInterested(),
                                            false,
                                            false
                                    );
                                }
                                course.numLikes--;
                                FirebaseUtils.userRemoveExistingRating(u.getUid(), course.getCourseID(), myRef);
                                FirebaseUtils.updateCourseNumLikes(course.getCourseID(), course.getNumLikes(), myRef);
                                FirebaseUtils.addUserRelatedCourse(u.getUid(), course.getCourseID(), urc, myRef);
                                recommendFragment.reloadRecommended();
                            }

                            AlertDialog.Builder builderInner = new AlertDialog.Builder(contex);
                            builderInner.setTitle("Thank you for fixing your rating!");
                            builderInner.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderInner.show();
                        }
                    });
                    builderSingle.show();
                    Log.d("wtf", "wtf22222");
                    likeButton.setImageResource(R.drawable.ic_love_empty);
                    likeButton.setTag(R.drawable.ic_love_empty);
                    interestedFragment.updateLikedCourse(course.getCourseID(), R.drawable.ic_love_empty);
                    catalogFragment.updateLikedCourse(course.getCourseID(), R.drawable.ic_love_empty);
                }
            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("wtf", "wtf2");
                User u = CourseLineAdapter.this.currentUser;
                if (u == null) {
                    Log.d("user", "ERROR");
                } else {
                    if (bookmarkButton.getTag().equals(R.drawable.ic_bookmark_border)) {
                        if (u.getRelated_courses().containsKey(course.getCourseID())) {
                            u.getRelated_courses().get(course.getCourseID()).setInterested(true);
                            UserRelatedCourse uc = new UserRelatedCourse(true, u.getRelated_courses().get(course.getCourseID()).getCompleted(), u.getRelated_courses().get(course.getCourseID()).getLiked());
                            FirebaseUtils.addUserRelatedCourse(uid, course.getCourseID(), uc, myRef);

                        } else {
                            UserRelatedCourse data = new UserRelatedCourse(true, false, false);
                            u.relateNewCourse(course.getCourseID(), data);
                            FirebaseUtils.addUserRelatedCourse(uid,course.getCourseID(),data,myRef);
                        }
                        bookmarkButton.setImageResource(R.drawable.ic_bookmark);
                        bookmarkButton.setTag(R.drawable.ic_bookmark);
                        catalogFragment.updateBookmarkedCourse(course.getCourseID());
                        recommendFragment.updateBookmarkedCourse(course.getCourseID());
                        interestedFragment.updateBookmarkedCourse(course);
                    } else{
                        u.getRelated_courses().get(course.getCourseID()).setInterested(false);
                        if(u.getRelated_courses().get(course.getCourseID()).getCompleted()==false){
                            u.getRelated_courses().remove(course.getCourseID());
                            UserRelatedCourse uc = new UserRelatedCourse(false, false, false);
                            FirebaseUtils.userRemoveExistingRating(uid,course.getCourseID(),myRef);
                            FirebaseUtils.addUserRelatedCourse(uid, course.getCourseID(), uc, myRef);
                        }else{
                            UserRelatedCourse uc = new UserRelatedCourse(false, u.getRelated_courses().get(course.getCourseID()).getCompleted(), u.getRelated_courses().get(course.getCourseID()).getLiked());
                            FirebaseUtils.userRemoveExistingRating(uid,course.getCourseID(),myRef);
                            FirebaseUtils.addUserRelatedCourse(uid, course.getCourseID(), uc, myRef);
                        }
                        bookmarkButton.setImageResource(R.drawable.ic_bookmark_border);
                        bookmarkButton.setTag(R.drawable.ic_bookmark_border);
                        catalogFragment.removeBookmarkedCourse(course.getCourseID());
                        recommendFragment.removeBookmarkedCourse(course.getCourseID());
                        interestedFragment.removeBookmarkedCourse(course.getCourseID());
                    }
                }
            }
        });

        String name = course.getName();
        if(name.length() > 50) {
            name = name.substring(0, 50);
            name.concat("...");
        }

        courseName.setText(name);

        courseID.setText(course.getCourseID());


        return ret;
    }
}