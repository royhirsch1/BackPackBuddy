package com.example.coursefreak.coursefreak;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class FirebaseUtils {
    /*
        This class is for different database utils for
        writing or reading from the database.
     */

    private FirebaseUtils() { }

    // Returns list of all courses in the system in Course objects
    // Given the reference to the root of the database.
    // Upon error, returns list with 1 course whose name is the error.
    public static void allCourses(DatabaseReference mDB, final List<Course> res) {
        Log.d("Courses", "In allCourses");
        mDB.child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                    Course c = courseSnap.getValue(Course.class);
                    Log.d("Courses", c.getName());
                    if (c != null)
                        res.add(c);
                }
                String name; Integer pos; Integer rev;
                if(res.size() > 0 && res.get(0).getPos() == -1) {
                    Log.d("Courses","ERROR");
                    Log.d("Courses", res.get(0).getName());
                    return;
                }
                if(res == null || res.size() == 0) {
                    Log.d("Courses", "Error");
                    return;
                }
                for(Course c : res) {
                    name = c.getName(); pos = c.getPos(); rev = c.getReviewNumber();
                    Log.d("Courses", name.concat(" ").concat(pos.toString()).concat(" ").concat(rev.toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                res.clear();
                res.add(new Course(databaseError.getMessage(), -1, -1));
            }
        });
    }

    // Returns a map of all of a single user's ratings, with key being course name.
    // Given a uid of the user and a database reference.
    // Upon error, returns map with one element whose key is the error and value is -1.
    public static void userRatings(String uid, DatabaseReference mDB) {
        final Map<String, Integer> res = new HashMap<>();
        Log.d("AllRate", "In userReatings");
        Log.d("Ratings", "Ratings for ".concat(uid));
        mDB.child("user_course_ratings").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ratingSnap : dataSnapshot.getChildren()) {
                    UserCourseRating ucr = ratingSnap.getValue(UserCourseRating.class);
                    if (ucr != null) {
                        res.put(ucr.getCourseName(), ucr.rating);
                        Log.d("AllRate",
                                "Course: ".concat(ucr.getCourseName())
                                .concat(" Rating: ").concat(ucr.getRating().toString()));
                    }
                    else {
                        Log.d("AllRAte", "ERROR: Null");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                res.clear();
                Log.d("AllRate", "ERROR Cancelled");
            }
        });
//        Log.d("Ratings", "Ratings for ".concat(uid));
//        for(Map.Entry<String, Integer> e : res.entrySet()) {
//            String str = "Rate for ";
//            str.concat(e.getKey()).concat(" is ");
//            str.concat(e.getValue().toString());
//            Log.d("Ratings", str);
//            str = "";
//        }
    }

    // Returns a Boolean that is true if succeeded, and false otherwise.
    // Given a uid and course name, adds a positive rating for the user in that course,
    // (and given reference to the root)
    // Upon error, returns a string that specifies the error. String is empty otherwise.
    // Assume: The course name is valid.
    public static void userAddPositiveRating(String uid, final String course_name, DatabaseReference mDB) {
        final String resStr = new String();
        UserCourseRating ucr = new UserCourseRating(course_name, 1);
        mDB.child("user_course_ratings")
                .child(uid)
                .child(course_name)
                .setValue(ucr)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    resStr.concat("Did not push!");
                }
            }
        });
        if(resStr.length() > 0) {
            Log.d("Rate", "Rating ERROR");
        }
        else {
            Log.d("Rate", "Rating Success");
        }
    }

    public static void userRemoveExistingRating(String uid, final String course_name, DatabaseReference mDB) {
        final String resStr = new String();
        Log.d("RemRate", "We meet again Mr. Anderson");
        mDB.child("user_course_ratings").child(uid).child(course_name).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Log.d("RemRate", "ERROR");
                }
            }
        });
    }

    // Returns matrix of all user ratings for the Matrix Recovery algorithm.
    public static double[][] getRatingsMatrix(DatabaseReference mDB) {
        final String resStr = new String();
        mDB.child("user_course_ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, List<String>> allRatings = new HashMap<>();
                Set<String> all_users = new TreeSet<>();
                Set<String> all_courses = new TreeSet<>();
                for(DataSnapshot singleUserData : dataSnapshot.getChildren()) {
                    String user_id = singleUserData.getKey();
                    all_users.add(user_id);
                    allRatings.put(user_id, new ArrayList<String>());
                    for(DataSnapshot singleCourseRatings : singleUserData.getChildren()) {
                        UserCourseRating ucr = singleCourseRatings.getValue(UserCourseRating.class);
                        all_courses.add(ucr.getCourseName());
                        allRatings.get(user_id).add(ucr.getCourseName());
                    }
                }
                Log.d("Matrix", "Participating courses found: ".concat(Integer.toString(all_courses.size())));
                Log.d("Matrix", "Users found: ".concat((Integer.toString(all_users.size()))));
                Map<String, Integer> coursesIndex = new HashMap<>();
                Map<Integer, String> coursesReverse = new HashMap<>();
                Map<String, Integer> usersIndex   = new HashMap<>();
                Map<Integer, String> usersReverse = new HashMap<>();
                int i = 0;
                for(String c : all_courses) {
                    coursesIndex.put(c, i);
                    coursesReverse.put(i, c);
                    i++;
                }
                int j = 0;
                for(String u : all_users) {
                    usersIndex.put(u, j);
                    usersReverse.put(j, u);
                    j++;
                }
                double[][] ratingsMatrix = new double[all_users.size()][all_courses.size()];
                for(String uid : all_users) {
                    for(String course : allRatings.get(uid)) {
                        ratingsMatrix[usersIndex.get(uid)][coursesIndex.get(course)] = 1.0;
                    }
                }
                for(int m = 0; m < all_users.size(); m++) {
                    Log.d("Matrix", usersReverse.get(m).concat(" 's course ratings"));
                    for(int n = 0; n < all_courses.size(); n++) {
                        Log.d("Matrix", "Rating for "
                            .concat(coursesReverse.get(n))
                            .concat(" is ")
                            .concat(Double.toString(ratingsMatrix[m][n])));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                resStr.concat("ERROR");
                Log.d("Matrix", "Cancellation error");
            }
        });
        return null;
    }
}
