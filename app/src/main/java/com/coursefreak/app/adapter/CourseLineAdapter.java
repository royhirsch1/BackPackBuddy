package com.coursefreak.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.coursefreak.app.fragment.catalog;
import com.coursefreak.app.fragment.interested;
import com.coursefreak.app.fragment.recommended;
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

    private Drawable heart_empty;
    private Drawable heart_filled;
    private Drawable bookm_empty;
    private Drawable bookm_filled;

    private recommended recommendFragment;
    private catalog catalogFragment;
    private interested interestedFragment;

    private ArrayList<Course> tabCourses;

    private static Boolean messageShown = false;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    public CourseLineAdapter(Context context, ArrayList<Course> courses, catalog catalogFragment,
                             recommended recommendFragment, interested interestedFragment) {
        super(context, 0, courses);
//        Log.d("wtf", "NULL? ".concat(Boolean.toString(courses == null)));
//        Log.d("wtf", "NULL? ".concat(Boolean.toString(context == null)));
//        Log.d("wtf", "NULL? ".concat(Boolean.toString(catalogFragment == null)));
//        Log.d("wtf", "NULL? ".concat(Boolean.toString(recommendFragment == null)));
//        Log.d("wtf", "NULL? ".concat(Boolean.toString(interestedFragment == null)));
        this.contex = context;
        this.recommendFragment = recommendFragment;
        this.catalogFragment = catalogFragment;
        this.interestedFragment = interestedFragment;

        this.heart_empty = this.contex.getResources().getDrawable(R.drawable.ic_love_empty);
        this.heart_filled = this.contex.getResources().getDrawable(R.drawable.ic_love);
        this.bookm_empty = this.contex.getResources().getDrawable(R.drawable.ic_bookmark_border);
        this.bookm_filled = this.contex.getResources().getDrawable(R.drawable.ic_bookmark);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final Course course = getItem(position);
        if(course == null)
            return null;

        // Check if an existing view is being reused, otherwise inflate the view
        View ret = null;
        if (convertView == null) {
            ret = LayoutInflater.from(getContext()).inflate(R.layout.single_course, null);
        } else {
            ret = convertView;
        }

        // Lookup view for data population
        final TextView courseName = (TextView) ret.findViewById(R.id.textViewCourseName);
        courseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(contex,CourseDataActivity.class);
                intent.putExtra("course",course);
                contex.startActivity(intent);

            }
        });

        final TextView courseID = (TextView) ret.findViewById(R.id.textViewCourseID);

        final ImageButton bookmarkButton = ret.findViewById(R.id.bookmarkBtn);
        bookmarkButton.setImageDrawable(this.bookm_empty);
        bookmarkButton.setColorFilter(contex.getResources().getColor(R.color.colorFacebook));
        bookmarkButton.setTag(R.drawable.ic_bookmark_border);

        final ImageButton likeButton = ret.findViewById(R.id.likeCourseButton);
        likeButton.setImageDrawable(this.heart_empty);
        likeButton.setColorFilter(contex.getResources().getColor(R.color.colorError));
        likeButton.setTag(R.drawable.ic_love_empty);


        this.mAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        final String uid;

        if (currentUser != null) {
            uid = currentUser.getUid();
        } else {
            uid = "0";
        }

        if (this.currentUser == null) {
            myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    CourseLineAdapter.this.currentUser = u;
                    if (u == null) {
//                        Log.d("user", "ERROR");
                    } else {
                        if (u.getRelated_courses().containsKey(course.getCourseID())) {
                            if (u.getRelated_courses().get(course.getCourseID()).getLiked()) {
                                likeButton.setImageDrawable(CourseLineAdapter.this.heart_filled);
                                likeButton.setTag(R.drawable.ic_love);
                                //Log.d("wtf", "wtf like");
                            } else {
                                likeButton.setImageDrawable(CourseLineAdapter.this.heart_empty);
                                likeButton.setTag(R.drawable.ic_love_empty);
                            }

                            if (u.getRelated_courses().get(course.getCourseID()).getInterested()) {
                                bookmarkButton.setImageDrawable(CourseLineAdapter.this.bookm_filled);
                                bookmarkButton.setTag(R.drawable.ic_bookmark);
                            } else {
                                bookmarkButton.setImageDrawable(CourseLineAdapter.this.bookm_empty);
                                bookmarkButton.setTag(R.drawable.ic_bookmark_border);
                            }
                        } else {
                            bookmarkButton.setImageDrawable(CourseLineAdapter.this.bookm_empty);
                            bookmarkButton.setTag(R.drawable.ic_bookmark_border);
                            likeButton.setImageDrawable(CourseLineAdapter.this.heart_empty);
                            likeButton.setTag(R.drawable.ic_love_empty);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.d("Courses", "Database Error");
                    Toast.makeText(getContext(), "User data could not be loaded.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            User u = this.currentUser;
            if (u.getRelated_courses().containsKey(course.getCourseID())) {
                if (u.getRelated_courses().get(course.getCourseID()).getLiked()) {
                    likeButton.setImageDrawable(CourseLineAdapter.this.heart_filled);
                    likeButton.setTag(R.drawable.ic_love);
                    //Log.d("wtf", "wtf like");
                } else {
                    likeButton.setImageDrawable(CourseLineAdapter.this.heart_empty);
                    likeButton.setTag(R.drawable.ic_love_empty);
                }

                if (u.getRelated_courses().get(course.getCourseID()).getInterested()) {
                    bookmarkButton.setImageDrawable(CourseLineAdapter.this.bookm_filled);
                    bookmarkButton.setTag(R.drawable.ic_bookmark);
                } else {
                    bookmarkButton.setImageDrawable(CourseLineAdapter.this.bookm_empty);
                    bookmarkButton.setTag(R.drawable.ic_bookmark_border);
                }
            } else {
                bookmarkButton.setImageDrawable(CourseLineAdapter.this.bookm_empty);
                bookmarkButton.setTag(R.drawable.ic_bookmark_border);
                likeButton.setImageDrawable(CourseLineAdapter.this.heart_empty);
                likeButton.setTag(R.drawable.ic_love_empty);
            }
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!likeButton.isEnabled()) {
                    Toast.makeText(getContext(), "Processing, please wait.", Toast.LENGTH_LONG).show();
                    return;
                }
                likeButton.setEnabled(false);
                if(CourseLineAdapter.this.currentUser == null) {
                    Toast.makeText(getContext(), "Loading data, please wait a moment...", Toast.LENGTH_LONG).show();
                    likeButton.setEnabled(true);
                    return;
                }
                if (likeButton.getTag().equals(R.drawable.ic_love_empty)) {
                    User u = CourseLineAdapter.this.currentUser;

                    likeButton.setImageDrawable(CourseLineAdapter.this.heart_filled);
                    likeButton.setTag(R.drawable.ic_love);

                    UserRelatedCourse data = null;
                    if (u.getRelated_courses().containsKey(course.getCourseID())) {
                        u.getRelated_courses().get(course.getCourseID()).setCompleted(true);
                        u.getRelated_courses().get(course.getCourseID()).setLiked(true);
                        data = new UserRelatedCourse(
                                u.getRelated_courses().get(course.getCourseID()).getInterested(),
                                true,
                                true);

                    } else {
                        data = new UserRelatedCourse(false, true, true);
                        u.relateNewCourse(course.courseID, data);
                    }

                    course.numCompleted++;
                    course.numLikes++;
                    recommendFragment.reloadRecommended();
                    interestedFragment.updateLikedCourse(course.getCourseID(), R.drawable.ic_love);
                    catalogFragment.updateLikedCourse(course.getCourseID(), R.drawable.ic_love);
                    likeButton.setEnabled(true);

                    FirebaseUtils.updateCourseNumCompleted(course.getCourseID(), course.getNumCompleted(), myRef);
                    FirebaseUtils.userAddPositiveRating(uid, course.getCourseID(), myRef);
                    FirebaseUtils.addUserRelatedCourse(uid, course.getCourseID(), data, myRef);

                } else { // Like button was pressed and HEART_FULL

                    likeButton.setTag(R.drawable.ic_love_empty);
                    likeButton.setImageDrawable(CourseLineAdapter.this.heart_empty);
                    interestedFragment.updateLikedCourse(course.getCourseID(), R.drawable.ic_love_empty);
                    catalogFragment.updateLikedCourse(course.getCourseID(), R.drawable.ic_love_empty);

                    User u = CourseLineAdapter.this.currentUser;
                    boolean interest = u.getRelated_courses().get(course.getCourseID()).getInterested();
                    UserRelatedCourse data = null;

                    if(interest)
                        data = new UserRelatedCourse(true, false, false);
                    else
                        data = new UserRelatedCourse(false, false, false);

                    course.numLikes--;
                    FirebaseUtils.userRemoveExistingRating(u.getUid(), course.getCourseID(), myRef);
                    FirebaseUtils.updateCourseNumLikes(course.getCourseID(), course.getNumLikes(), myRef);
                    FirebaseUtils.addUserRelatedCourse(u.getUid(), course.getCourseID(), data, myRef);
                    recommendFragment.reloadRecommended();
                }
                likeButton.setEnabled(true);
            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmarkButton.isEnabled() == false) {
                    Toast.makeText(getContext(), "Processing, please wait.", Toast.LENGTH_LONG).show();
                    return;
                }
                bookmarkButton.setEnabled(false);
                if(CourseLineAdapter.this.currentUser == null) {
                    Toast.makeText(getContext(), "Loading data, please wait a moment...", Toast.LENGTH_LONG).show();
                    return;
                }
                User u = CourseLineAdapter.this.currentUser;
                if (bookmarkButton.getTag().equals(R.drawable.ic_bookmark_border)) {
                    bookmarkButton.setImageDrawable(CourseLineAdapter.this.bookm_filled);
                    bookmarkButton.setTag(R.drawable.ic_bookmark);
                    if (u.getRelated_courses().containsKey(course.getCourseID())) {
                        u.getRelated_courses().get(course.getCourseID()).setInterested(true);
                        UserRelatedCourse uc = new UserRelatedCourse(true, u.getRelated_courses().get(course.getCourseID()).getCompleted(), u.getRelated_courses().get(course.getCourseID()).getLiked());
                        FirebaseUtils.addUserRelatedCourse(uid, course.getCourseID(), uc, myRef);

                    } else {
                        UserRelatedCourse data = new UserRelatedCourse(true, false, false);
                        u.relateNewCourse(course.getCourseID(), data);
                        FirebaseUtils.addUserRelatedCourse(uid, course.getCourseID(), data, myRef);
                    }
                    catalogFragment.updateBookmarkedCourse(course.getCourseID());
                    recommendFragment.updateBookmarkedCourse(course.getCourseID());
                    interestedFragment.updateBookmarkedCourse(course);
                } else {
                    bookmarkButton.setImageDrawable(CourseLineAdapter.this.bookm_empty);
                    bookmarkButton.setTag(R.drawable.ic_bookmark_border);
                    UserRelatedCourse uc = null;
                    if(u.getRelated_courses().keySet().contains(course.getCourseID())) {
                        boolean liked = u.getRelated_courses().get(course.getCourseID()).getLiked();
                        boolean completed = u.getRelated_courses().get(course.getCourseID()).getCompleted();
                        u.getRelated_courses().get(course.getCourseID()).setInterested(false);
                        uc = new UserRelatedCourse(false, completed, liked);
                        u.getRelated_courses().remove(course.getCourseID());
                    } else {
                        uc = new UserRelatedCourse(false, false, false);
                    }

                    FirebaseUtils.userRemoveExistingRating(uid, course.getCourseID(), myRef);
                    FirebaseUtils.addUserRelatedCourse(uid, course.getCourseID(), uc, myRef);
                    catalogFragment.removeBookmarkedCourse(course.getCourseID());
                    recommendFragment.removeBookmarkedCourse(course.getCourseID());
                    interestedFragment.removeBookmarkedCourse(course.getCourseID());
                }
                bookmarkButton.setEnabled(true);
            }
        });

        String name = course.getName();
        if (name != null && name.length() > 60) {
            name = name.substring(0, 57);
            name = name.concat("...");
        }

        courseName.setText(name);

        courseID.setText(course.getCourseID());

        return ret;
    }
}