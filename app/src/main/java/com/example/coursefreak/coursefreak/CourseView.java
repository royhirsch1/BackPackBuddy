package com.example.coursefreak.coursefreak;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseView extends AppCompatActivity {

    //
    //
    private int mProgressStatus_avg = 0;
    private int mProgressStatus_pop = 0;
    private Handler mBarHandler_avg = new Handler();
    private Handler mBarHandler_pop = new Handler();
    private TextView textViewAverage;
    private TextView textViewPopularity;


    private TextView textViewReview;

    private FirebaseDatabase mDatabase;

    //TODO toast-function not available - delete
    private View.OnClickListener na_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast toast_na = Toast.makeText(getApplicationContext(),
                    "This function is not available",
                    Toast.LENGTH_SHORT);
            toast_na.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view);



        final Course course = new Course("234123","Course Name",3.5,64,90,58.6,"later","later");
        final int popularity_rate = (course.getNumLikes())*100/(course.getNumCompleted());
        final int course_avg = course.getAverage().intValue();

        //Update course data texts
        final TextView textViewTitle = findViewById(R.id.textView_CourseTitle);
        textViewTitle.setText(course.getName());
        final TextView textViewCredit = findViewById(R.id.textView_creditAU);
        textViewCredit.setText(course.getPoints().toString()+" AU");

        final ImageButton but_share = findViewById(R.id.button_share);
        final ImageButton but_comment = findViewById(R.id.button_comment);
        final ImageButton but_link = findViewById(R.id.button_link);
        final ImageButton but_partner = findViewById(R.id.button_partner);
        but_share.setOnClickListener(na_OnClickListener);
        but_comment.setOnClickListener(na_OnClickListener);
        but_link.setOnClickListener(na_OnClickListener);
        but_partner.setOnClickListener(na_OnClickListener);

        //Get reviews from DB
        String courseIDtemp="234123";
        this.mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDB = mDatabase.getReference();

        Log.d("getRevs", "In getCourseReviewsOrdered");
        final ArrayList<Review> reviewList = new ArrayList<>();
        mDB.child("course_reviews")
                .child(courseIDtemp)
                .orderByChild("numberHelped")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot courseReview : dataSnapshot.getChildren()) {
                            Review rev = courseReview.getValue(Review.class);
                            reviewList.add(rev);
                        }
                        Collections.reverse(reviewList);
                        for(Review r : reviewList.subList(0,Math.min(4, reviewList.size()))) {
                            Log.d("getRevs", "Review by ".concat(r.getUserID())
                                    .concat(" says that ").concat(r.getReviewText())
                                    .concat(" and has helped ").concat(r.getNumHelped().toString()));
                        }

                        // Construct the data source
                        CourseViewReviewAdapter review_adapter = new CourseViewReviewAdapter(getBaseContext(), reviewList);

                        ListView listView = (ListView) findViewById(R.id.listView_review);
                        listView.setAdapter(review_adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        reviewList.clear();
                        Log.d("getRevs", "Database Cancel Error!");
                    }
                });

        //Data processing

        // -- Progress Bar Average -- //

        final ProgressBar progressbar_avg = (ProgressBar) findViewById(R.id.progressBar_avg);
        textViewAverage = findViewById(R.id.textView_average_number);

        //set bar style features
        int barColor_avg = getResources().getColor(R.color.colorPrimaryDark);
        Drawable progressDrawable_avg = progressbar_avg.getProgressDrawable().mutate();
        progressDrawable_avg.setColorFilter(barColor_avg, android.graphics.PorterDuff.Mode.SRC_IN);
        progressbar_avg.setProgressDrawable(progressDrawable_avg);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mProgressStatus_avg < course_avg){
                    mProgressStatus_avg++;
                    android.os.SystemClock.sleep(25);
                    mBarHandler_avg.post(new Runnable() {
                        @Override
                        public void run() {
                            progressbar_avg.setProgress(mProgressStatus_avg);
                        }
                    });
                }
                mBarHandler_avg.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewAverage.setText(Integer.toString((course_avg)));
                        textViewAverage.setVisibility(View.VISIBLE);
                    }
                });

            }
        }).start();
        // -- End Of Progress Bar Average -- //

        // -- Progress Bar Popularity -- //

        final ProgressBar progressbar_pop = (ProgressBar) findViewById(R.id.progressBar_pop);
        textViewPopularity = findViewById(R.id.textView_popularity_number);

        //set bar style features
        int barColor_pop = getResources().getColor(R.color.colorPrimaryDark);
        Drawable progressDrawable_pop = progressbar_pop.getProgressDrawable().mutate();
        progressDrawable_pop.setColorFilter(barColor_pop, android.graphics.PorterDuff.Mode.SRC_IN);
        progressbar_pop.setProgressDrawable(progressDrawable_pop);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mProgressStatus_pop < popularity_rate){
                    mProgressStatus_pop++;
                    android.os.SystemClock.sleep(25);
                    mBarHandler_pop.post(new Runnable() {
                        @Override
                        public void run() {
                            progressbar_pop.setProgress(mProgressStatus_pop);
                        }
                    });
                }
                mBarHandler_pop.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewPopularity.setText(Integer.toString(((int)popularity_rate))+"%");
                        textViewPopularity.setVisibility(View.VISIBLE);
                    }
                });

            }
        }).start();


        // -- End Of Progress Bar Popularity -- //

    }

}
