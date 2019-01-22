package com.coursefreak.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.coursefreak.app.utils.Tools;
import com.coursefreak.app.utils.ViewAnimation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseDataActivity extends AppCompatActivity {

    private View parent_view;

    private NestedScrollView nested_scroll_view;
    private ImageButton bt_toggle_req, bt_toggle_rev;
    private Button bt_more, bt_write, bt_hide_req, bt_hide_rev;
    private View lyt_expand_req, lyt_expand_reviews;

    private int mProgressStatus_avg = 0;
    private int mProgressStatus_pop = 0;
    private Handler mBarHandler_avg = new Handler();
    private Handler mBarHandler_pop = new Handler();
    private TextView textViewAverage;
    private TextView textViewPopularity;
    private Course course;
    private String courseID;

    private FirebaseDatabase mDatabase;

    private TextView txt_no_item;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_data);
        parent_view = findViewById(android.R.id.content);
        this.mDatabase = FirebaseDatabase.getInstance();

        initCourseData();
        initToolbar();
        initButtonListeners();
        initDropDowns();
        initReviews();
    }

    private void initCourseData(){
        course = new Course("234218","Example Course",2.8,6,8,76.8,"no","hw;pairs");

        Intent intent = getIntent();
        if(intent != null && intent.getSerializableExtra("course") != null){
            course = (Course)intent.getSerializableExtra("course");
        }
        courseID=course.getCourseID();


        //Update Title & Number & AUs
        final TextView textViewTitle = findViewById(R.id.textView_CourseTitle);
        textViewTitle.setText(course.getName());
        final TextView textViewID = findViewById(R.id.textView_courseID);
        textViewID.setText(course.getCourseID());
        final TextView textViewCredit = findViewById(R.id.textView_creditAU);
        textViewCredit.setText(course.getPoints().toString()+" AU");

        //Update Average & Popularity
        runProgressBars();

        //Requirements//

        ImageView homework_img = findViewById(R.id.icon_homework);
        ImageView exam_img = findViewById(R.id.icon_exam);
        ImageView pairwork_img = findViewById(R.id.icon_pairwork);

        List<String> req_list = course.getParsedRequirements();
        if(req_list.contains("hw")){
            homework_img.setImageResource(R.drawable.icon_v);
        }
        if(req_list.contains("exam")){
            homework_img.setImageResource(R.drawable.icon_v);
        }
        if(req_list.contains("pairs")){
            homework_img.setImageResource(R.drawable.icon_v);
        }

        //End Of Requirements//
    }

    private void runProgressBars(){

        final int popularity_rate = course.getNumCompleted() > 0 ? (course.getNumLikes())*100/(course.getNumCompleted()) : 0;
        final int course_avg = course.getAverage().intValue();

        // -- Progress Bar Average -- //

        final ProgressBar progressbar_avg = (ProgressBar) findViewById(R.id.progressBar_avg);
        textViewAverage = findViewById(R.id.textView_average_number);

        //set bar style features
        int barColor_avg = getResources().getColor(R.color.colorFacebook);
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
        int barColor_pop = getResources().getColor(R.color.colorFacebook);
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
                        if(popularity_rate>0){
                            textViewPopularity.setText(Integer.toString(((int)popularity_rate))+"%");
                        }else{
                            textViewPopularity.setText("No Data");
                        }

                        textViewPopularity.setVisibility(View.VISIBLE);
                    }
                });

            }
        }).start();


        // -- End Of Progress Bar Popularity -- //
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Course Info: "+course.getCourseID());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initDropDowns() {

        // req section
        bt_toggle_req = (ImageButton) findViewById(R.id.bt_toggle_text);
        bt_hide_req = (Button) findViewById(R.id.bt_write);
        lyt_expand_req = (View) findViewById(R.id.lyt_expand_text);
        lyt_expand_req.setVisibility(View.GONE);

        bt_toggle_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionText(bt_toggle_req);
            }
        });

        bt_hide_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionText(bt_toggle_req);
            }
        });

        // rev section
        bt_toggle_rev = (ImageButton) findViewById(R.id.bt_toggle_input);
        bt_hide_rev = (Button) findViewById(R.id.bt_hide_rev);
        bt_more = (Button) findViewById(R.id.bt_more);
        bt_write = (Button) findViewById(R.id.bt_write);
        lyt_expand_reviews = (View) findViewById(R.id.lyt_expand_input);
        lyt_expand_reviews.setVisibility(View.GONE);

        bt_toggle_rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionInput(bt_toggle_rev);
            }
        });

        bt_hide_rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionInput(bt_toggle_rev);
            }
        });

        bt_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReviewDialog();
            }
        });
        bt_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(parent_view, "Will be added soon", Snackbar.LENGTH_SHORT).show();
            }
        });

        // nested scrollview
        nested_scroll_view = (NestedScrollView) findViewById(R.id.nested_scroll_view);
    }

    private void toggleSectionText(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimation.expand(lyt_expand_req, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, lyt_expand_req);
                }
            });
        } else {
            ViewAnimation.collapse(lyt_expand_req);
        }
    }

    private void toggleSectionInput(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimation.expand(lyt_expand_reviews, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, lyt_expand_reviews);
                }
            });
        } else {
            ViewAnimation.collapse(lyt_expand_reviews);
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initButtonListeners(){

        FloatingActionButton fabPartners = findViewById(R.id.fab_partners);
        fabPartners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPartnersDialog();
            }
        });

        FloatingActionButton fabUG = findViewById(R.id.fab_UG);
        fabUG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextActivity = new Intent(getApplicationContext(), UGView.class);
                nextActivity.putExtra("course_id",course.getCourseID());
                startActivity(nextActivity);
            }
        });

        FloatingActionButton fabShare = findViewById(R.id.fab_share);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Will be added soon", Toast.LENGTH_SHORT).show();

            }
        });

        FloatingActionButton fabReview = findViewById(R.id.fab_review);
        fabReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReviewDialog();

            }
        });



    }



    private void showPartnersDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_partners);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        final Switch switch_partner = dialog.findViewById(R.id.switch_partner);
        final ListView listView = dialog.findViewById(R.id.list_partners);
        final ArrayList<CoursePartner> partnersList = new ArrayList<>();
        final String myUid = FirebaseAuth.getInstance().getUid();
        final String myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final DatabaseReference mDB = mDatabase.getReference();

        mDB.child("course_partners").child(courseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot partnerSnapshot : dataSnapshot.getChildren()) {
                    CoursePartner cp = partnerSnapshot.getValue(CoursePartner.class);
                    if(cp.getUid().equals(myUid)){
                        switch_partner.setChecked(true);
                    }
//                    Log.d("partner", "adding ".concat(cp.getName()));
                    partnersList.add(cp);
                }

                partnersList.remove(null);
                PartnerListAdapter partner_adapter = new PartnerListAdapter(getBaseContext(), partnersList);
                listView.setAdapter(partner_adapter);

                switch_partner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CoursePartner me = new CoursePartner(myUid,"unknown",myEmail);
                        Snackbar.make(parent_view, "Request Accepted", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        if(isChecked){
                            mDB.child("course_partners")
                                    .child(courseID)
                                    .child(myUid)
                                    .setValue(me)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful()) {
//                                                Log.d("addP", "Database Error!");
                                            } else {
//                                                Log.d("addP", "Success adding possible partner");
                                            }
                                        }
                                    });
                        }else{
                            mDB.child("course_partners")
                                    .child(courseID)
                                    .child(myUid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful()) {
//                                                    Log.d("removeP", "Database Error!");
                                            } else {
//                                                    Log.d("removeP", "Success adding possible partner");
                                            }
                                        }
                                    });

                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d("allP", "Error! Database Cancelled!");
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }
    private void initReviews(){

        //Get reviews from DB

//        Log.d("getRevs", "In getCourseReviewsOrdered");
        final ArrayList<Review> reviewList = new ArrayList<>();
        final DatabaseReference mDB = mDatabase.getReference();
        mDB.child("course_reviews")
                .child(course.getCourseID())
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
                    Toast.makeText(getApplicationContext(), "Will be added soon", Toast.LENGTH_SHORT).show();
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
