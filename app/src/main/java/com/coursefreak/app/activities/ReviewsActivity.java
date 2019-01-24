package com.coursefreak.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ReviewsActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private String courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        initToolbar();
        this.mDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        courseID = intent.getStringExtra("course_id");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReviewDialog();
            }
        });

        initReviews();


    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reviews");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initReviews(){

        //Get reviews from DB

//        Log.d("getRevs", "In getCourseReviewsOrdered");
        final ArrayList<Review> reviewList = new ArrayList<>();
        final DatabaseReference mDB = mDatabase.getReference();
        mDB.child("course_reviews")
                .child(courseID)
                .orderByChild("numberHelped")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot courseReview : dataSnapshot.getChildren()) {
                            Review rev = courseReview.getValue(Review.class);
                            reviewList.add(rev);
                        }
                        Collections.reverse(reviewList);
//                        for(Review r : reviewList.subList(0,Math.min(4, reviewList.size()))) {
////                            Log.d("getRevs", "Review by ".concat(r.getUserID())
////                                    .concat(" says that ").concat(r.getReviewText())
////                                    .concat(" and has helped ").concat(r.getNumHelped().toString()));
//                        }

                        // Construct the data source
                        CourseViewReviewAdapter review_adapter = new CourseViewReviewAdapter(getBaseContext(), reviewList);

                        ListView listView = (ListView) findViewById(R.id.listView_review);
                        listView.setAdapter(review_adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        reviewList.clear();
//                        Log.d("getRevs", "Database Cancel Error!");
                    }
                });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showReviewDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_review);
        dialog.setCancelable(true);
        TextView writer = dialog.findViewById(R.id.writer_name);
        writer.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_post = (EditText) dialog.findViewById(R.id.et_post);
        ((AppCompatButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((AppCompatButton) dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = et_post.getText().toString().trim();
                if (review.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill review text", Toast.LENGTH_SHORT).show();
                } else {
                    String my_uid = FirebaseAuth.getInstance().getUid();
                    String my_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    FirebaseUtils.addNewCourseReview(courseID,my_email,review,mDatabase.getReference());
                }
//                if (!adapter.isEmpty()) {
//                }
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}
