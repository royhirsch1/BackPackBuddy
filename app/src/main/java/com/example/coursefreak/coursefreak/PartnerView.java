package com.example.coursefreak.coursefreak;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

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
import java.util.Map;

public class PartnerView extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private String courseID;
    private CoursePartner me = new CoursePartner("my_uid", "Roy", "roy.hirsch@gmail.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_view);
        courseID="234218";

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
                    if(cp.getUid()==FirebaseAuth.getInstance().getUid()){
                        switch_partner.setChecked(true);
                    }
                    Log.d("partner", "adding ".concat(cp.getName()));
                    partnersList.add(cp);
                }

                PartnerListAdapter partner_adapter = new PartnerListAdapter(getBaseContext(), partnersList);
                ListView listView = (ListView) findViewById(R.id.list_partners);
                listView.setAdapter(partner_adapter);

                switch_partner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Snackbar.make(buttonView, "Updating Data...", Snackbar.LENGTH_LONG)
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
                                                Log.d("addP", "Database Error!");
                                            } else {
                                                Log.d("addP", "Success adding possible partner");
                                            }
                                        }
                                    });
                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("allP", "Error! Database Cancelled!");
            }
        });

                //End of get data from DB

    }

}
