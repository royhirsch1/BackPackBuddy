package com.coursefreak.app.fragment;

import android.content.Context;
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
import android.widget.Toast;

import com.coursefreak.app.Course;
import com.coursefreak.app.CourseLineAdapter;
import com.coursefreak.app.R;
import com.coursefreak.app.Recommender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class recommended extends Fragment {
    private View rootView;
    private ListView recomList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private FirebaseAuth mAuth;

    final ArrayList<Course> res = new ArrayList<>();

    private interested interestedFragment;
    private catalog catalogFragment;

    private Context ctx;

    public recommended() { }

    public void setCatalogFragment(catalog catalogFragment) {
        this.catalogFragment = catalogFragment;
    }

    public void setInterestedFragment(interested interestedFragment) {
        this.interestedFragment = interestedFragment;
    }

    public void updateBookmarkedCourse(String courseID) {
        if(this.recomList == null)
            return;
//        Log.d("bkmrk", "lilili");
        for(int i = 0; i < this.recomList.getAdapter().getCount(); i++) {
            View v = this.recomList.getChildAt(i);
            if(v == null)
                continue;
            String text = ((TextView)v.findViewById(R.id.textViewCourseID)).getText().toString();
//            Log.d("bkmrk", text);
            if(text.contains(courseID)) {
                ImageView bookmarkView = v.findViewById(R.id.bookmarkBtn);
                bookmarkView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_bookmark));
                bookmarkView.setTag(R.drawable.bookmark_ribbon);
                break;
            }
        }
    }

    public void removeBookmarkedCourse(String courseID) {
//        Log.d("bkmrk", "lalala");
        if(this.recomList == null)
            return;
        for(int i = 0; i < this.recomList.getAdapter().getCount(); i++) {
            View v = this.recomList.getChildAt(i);
            if(v == null)
                continue;
            String text = ((TextView)v.findViewById(R.id.textViewCourseID)).getText().toString();
//            Log.d("bkmrk", text);
            if(text.contains(courseID)) {
//                Log.d("bkmrk", "lalala");
                ImageView bookmarkView = v.findViewById(R.id.bookmarkBtn);
                bookmarkView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_bookmark_border));
                bookmarkView.setTag(R.drawable.bookmark_outline);
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
            rootView = inflater.inflate(R.layout.recommended, null);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        final Set<String> nontrivialPredictions = new TreeSet<>();
        if(rootView.getParent()!=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            parent.removeAllViews();
        }
        final ListView lv = rootView.findViewById(R.id.course_list3);
        recomList = lv;
        this.mAuth = FirebaseAuth.getInstance();
        ctx = getContext();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        this.reloadRecommended();

        return rootView;
    }

    //@Override
//    public void onResume() {
//        super.onResume();
//        reloadRecommended();
//    }

    public void reloadRecommended() {
        final Set<String> nontrivialPredictions = new TreeSet<>();
        myRef.child("user_course_ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                res.clear();
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
                    Toast.makeText(ctx, "No courses rated!", Toast.LENGTH_LONG);
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
                int r = Math.min(ratingsMatrix.length,ratingsMatrix[0].length);
                double[][] U = new double[ratingsMatrix.length][r];
                double[][] V = new double[ratingsMatrix[0].length][r];

                Recommender.myRecommender(ratingsMatrix, r, 0.5, 0.5, U, V);

                double[][] predictions = new double[ratingsMatrix.length][ratingsMatrix[0].length];
                Recommender.zeroMatrix(predictions);

                Recommender.PredictRating(U, V, predictions);

                // Setup matrix for easier readability
                int user_index = usersIndex.get(uid);
                for (String u_id : all_users) {
                    for (String course : all_courses) {
                        i = usersIndex.get(u_id);
                        j = coursesIndex.get(course);
                        double d = predictions[i][j] * 2;
//                        Log.d("Completion", "A^["+Integer.toString(i)+"]["+Integer.toString(j)+"] = "+Double.toString(d));
                        if (ratingsMatrix[i][j] != 0)
                            d = 1;
                        else if (d < 0.0099)
                            d = 0;
                        predictions[i][j] = ((int) (d * 1000)) / 1000.0;
//                        Log.d("Completion", "A^["+Integer.toString(i)+"]["+Integer.toString(j)+"] = "+Double.toString(predictions[i][j]));
//                        Log.d("Completion", "---");
                    }
//                    Log.d("Completion", "------");
                }

                // Collect results into a set of recommended courses
//                Log.d("Completion", "Recommendations for ".concat(uid));
                for (int n = 0; n < all_courses.size(); n++) {
//                    Log.d("Completion", Double.toString(predictions[user_index][n]));
                    if (predictions[user_index][n] != 0.0 && predictions[user_index][n] != 1.0) {
                        nontrivialPredictions.add(coursesReverse.get(n));
//                        Log.d("Completion", "Course: ".concat(coursesReverse.get(n)).concat(" is recommended."));
                    }
                }
                myRef.child("courses").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                        res.clear();
                        for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
//                            Log.d("Courses", courseSnap.getKey());
                            Course c = courseSnap.getValue(Course.class);
                            c.parseCatsReqs();
                            for(String s : nontrivialPredictions) {
                                if (c.getCourseID().equals(s)) {
                                    res.add(c);
                                }
                            }
                            res.remove(null);
                            CourseLineAdapter cla = new CourseLineAdapter(recomList.getContext(), res,
                                    recommended.this.catalogFragment,
                                    recommended.this,
                                    recommended.this.interestedFragment);
                            res.removeAll(Collections.singleton(null));
                            recomList.setAdapter(cla);
                            cla.notifyDataSetChanged();
                        }
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
//                Log.d("Matrix", "Cancellation error");
            }
        });
    }
}