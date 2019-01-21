package com.coursefreak.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PartnerView extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private String courseID;
    private String my_uid = FirebaseAuth.getInstance().getUid();
    private String my_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private CoursePartner me = new CoursePartner(my_uid, "null", my_email);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_view);

        Intent intent = getIntent();
        String course_name = intent.getStringExtra("course_name");
        TextView course_title = findViewById(R.id.textView_partner);
        course_title.setText(course_name);
        courseID = intent.getStringExtra("course_id");


        this.mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference mDB = mDatabase.getReference();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get Partners from DB

        final ArrayList<CoursePartner> partnersList = new ArrayList<>();
        final Switch switch_partner = findViewById(R.id.switch_partner);

        //final CoursePartners partners = new CoursePartners(courseID);
        mDB.child("course_partners").child(courseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot partnerSnapshot : dataSnapshot.getChildren()) {
                    CoursePartner cp = partnerSnapshot.getValue(CoursePartner.class);
                    if(cp.getUid().equals(me.getUid())){
                        switch_partner.setChecked(true);
                    }
//                    Log.d("partner", "adding ".concat(cp.getName()));
                    partnersList.add(cp);
                }

                PartnerListAdapter partner_adapter = new PartnerListAdapter(getBaseContext(), partnersList);
                ListView listView = (ListView) findViewById(R.id.list_partners);
                listView.setAdapter(partner_adapter);

                switch_partner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Snackbar.make(buttonView, "Request Accepted", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        if(isChecked){
                            mDB.child("course_partners")
                                    .child(courseID)
                                    .child(me.getUid())
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
                                        .child(me.getUid())
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

                //End of get data from DB

    }

}
