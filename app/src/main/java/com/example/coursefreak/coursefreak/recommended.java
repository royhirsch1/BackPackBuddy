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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class recommended extends Fragment {
    private static View rootView;
    private static ListView recomList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private FirebaseAuth mAuth;

    public recommended() {
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
            rootView = inflater.inflate(R.layout.recommended, null);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        final Set<String> nontrivialPredictions = new TreeSet<>();
        if(rootView.getParent()!=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            parent.removeAllViews();
        }
        final ListView lv = (ListView) rootView.findViewById(R.id.course_list3);
        recomList = lv;
        final ArrayList<Course> res = new ArrayList<>();
        this.mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uid;
        if (currentUser != null) {
            uid = currentUser.getUid();
        } else {
            uid = "0";
        }

        myRef.child("user_course_ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Setting up participating users and courses,
                // So that users that have not rated anything will not participate.
                Map<String, List<String>> allRatings = new HashMap<>();
                Set<String> all_users = new TreeSet<>();
                Set<String> all_courses = new TreeSet<>();
                for (DataSnapshot singleUserData : dataSnapshot.getChildren()) {
                    String user_id = singleUserData.getKey();
                    all_users.add(user_id);
                    allRatings.put(user_id, new ArrayList<String>());
                    for (DataSnapshot singleCourseRatings : singleUserData.getChildren()) {
                        String cid = singleCourseRatings.getKey();
                        //Log.d("Matrix", cid);
                        all_courses.add(cid);
                        allRatings.get(user_id).add(cid);
                    }
                }

                // If the user has not rated anything, cannot predict for him.
                if (all_users.contains(uid) == false) {
                    Toast.makeText(getContext(), "No courses rated!", Toast.LENGTH_LONG);
                    return;
                }

                Map<String, Integer> coursesIndex = new HashMap<>();
                Map<Integer, String> coursesReverse = new HashMap<>();
                Map<String, Integer> usersIndex = new HashMap<>();
                int i = 0;
                for (String c : all_courses) {
                    coursesIndex.put(c, i);
                    coursesReverse.put(i, c);
                    i++;
                }
                int j = 0;
                for (String u : all_users) {
                    usersIndex.put(u, j);
                    j++;
                }

                // Setup the ratings matrix for the algorithm.
                double[][] ratingsMatrix = new double[all_users.size()][all_courses.size()];
                for (String uid : all_users) {
                    for (String course : allRatings.get(uid)) {
                        i = usersIndex.get(uid);
                        j = coursesIndex.get(course);
                        ratingsMatrix[i][j] = 1.0;
                    }
                }

                // Actually running the algorithm
                Recommender.Pair<double[][], double[][]> pUV = Recommender.myRecommender(ratingsMatrix, 4, 0.5, 0.5);
                double[][] predictions = Recommender.PredictRating(pUV.getFirst(), pUV.getSecond());

                // Setup matrix for easier readability
                int user_index = usersIndex.get(uid);
                for (String uid : all_users) {
                    for (String course : all_courses) {
                        i = usersIndex.get(uid);
                        j = coursesIndex.get(course);
                        double d = predictions[i][j] * 2;
                        if (ratingsMatrix[i][j] != 0)
                            d = 1;
                        else if (d < 0.0099)
                            d = 0;
                        predictions[i][j] = ((int) (d * 1000)) / 1000.0;
                    }
                }

                // Collect results into a set of recommended courses
                Log.d("Completion", "Recommendations for ".concat(uid));
                for (int n = 0; n < all_courses.size(); n++) {
                    if (predictions[user_index][n] != 0.0 && predictions[user_index][n] != 1.0) {
                        nontrivialPredictions.add(coursesReverse.get(n));
                        Log.d("Completion", "Course: ".concat(coursesReverse.get(n)).concat(" is recommended."));
                    }
                }
                myRef.child("courses").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                        for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                            Log.d("Courses", courseSnap.getKey());
                            Course c = courseSnap.getValue(Course.class);
                            c.parseCatsReqs();
                            for(String s : nontrivialPredictions) {
                                if (c.getCourseID().equals(s)) {
                                    res.add(c);
                                }
                            }
                            CourseLineAdapter cla= new CourseLineAdapter(lv.getContext(),res);
                            lv.setAdapter(cla);
                            cla.notifyDataSetChanged();
                        }
                        for(Course c : res) {
                            Log.d("Completion", c.getName());
                        }
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
                Log.d("Matrix", "Cancellation error");
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadRecommended();
    }

    private void reloadRecommended() {
        final Set<String> nontrivialPredictions = new TreeSet<>();
        final ArrayList<Course> res = new ArrayList<>();
        myRef.child("user_course_ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Setting up participating users and courses,
                // So that users that have not rated anything will not participate.
                Map<String, List<String>> allRatings = new HashMap<>();
                Set<String> all_users = new TreeSet<>();
                Set<String> all_courses = new TreeSet<>();
                for (DataSnapshot singleUserData : dataSnapshot.getChildren()) {
                    String user_id = singleUserData.getKey();
                    all_users.add(user_id);
                    allRatings.put(user_id, new ArrayList<String>());
                    for (DataSnapshot singleCourseRatings : singleUserData.getChildren()) {
                        String cid = singleCourseRatings.getKey();
                        //Log.d("Matrix", cid);
                        all_courses.add(cid);
                        allRatings.get(user_id).add(cid);
                    }
                }
                String uid = FirebaseAuth.getInstance().getUid();
                // If the user has not rated anything, cannot predict for him.
                if (all_users.contains(uid) == false) {
                    Toast.makeText(getContext(), "No courses rated!", Toast.LENGTH_LONG);
                    return;
                }

                Map<String, Integer> coursesIndex = new HashMap<>();
                Map<Integer, String> coursesReverse = new HashMap<>();
                Map<String, Integer> usersIndex = new HashMap<>();
                int i = 0;
                for (String c : all_courses) {
                    coursesIndex.put(c, i);
                    coursesReverse.put(i, c);
                    i++;
                }
                int j = 0;
                for (String u : all_users) {
                    usersIndex.put(u, j);
                    j++;
                }

                // Setup the ratings matrix for the algorithm.
                double[][] ratingsMatrix = new double[all_users.size()][all_courses.size()];
                for (String u_id : all_users) {
                    for (String course : allRatings.get(u_id)) {
                        i = usersIndex.get(u_id);
                        j = coursesIndex.get(course);
                        ratingsMatrix[i][j] = 1.0;
                    }
                }

                // Actually running the algorithm
                Recommender.Pair<double[][], double[][]> pUV = Recommender.myRecommender(ratingsMatrix, 4, 0.5, 0.5);
                double[][] predictions = Recommender.PredictRating(pUV.getFirst(), pUV.getSecond());

                // Setup matrix for easier readability
                int user_index = usersIndex.get(uid);
                for (String u_id : all_users) {
                    for (String course : all_courses) {
                        i = usersIndex.get(u_id);
                        j = coursesIndex.get(course);
                        double d = predictions[i][j] * 2;
                        if (ratingsMatrix[i][j] != 0)
                            d = 1;
                        else if (d < 0.0099)
                            d = 0;
                        predictions[i][j] = ((int) (d * 1000)) / 1000.0;
                    }
                }

                // Collect results into a set of recommended courses
                Log.d("Completion", "Recommendations for ".concat(uid));
                for (int n = 0; n < all_courses.size(); n++) {
                    if (predictions[user_index][n] != 0.0 && predictions[user_index][n] != 1.0) {
                        nontrivialPredictions.add(coursesReverse.get(n));
                        Log.d("Completion", "Course: ".concat(coursesReverse.get(n)).concat(" is recommended."));
                    }
                }
                myRef.child("courses").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                        for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                            Log.d("Courses", courseSnap.getKey());
                            Course c = courseSnap.getValue(Course.class);
                            c.parseCatsReqs();
                            for(String s : nontrivialPredictions) {
                                if (c.getCourseID().equals(s)) {
                                    res.add(c);
                                }
                            }
                            CourseLineAdapter cla= new CourseLineAdapter(recomList.getContext(),res);
                            recomList.setAdapter(cla);
                            cla.notifyDataSetChanged();
                        }
                        for(Course c : res) {
                            Log.d("Completion", c.getName());
                        }
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
                Log.d("Matrix", "Cancellation error");
            }
        });
    }
}