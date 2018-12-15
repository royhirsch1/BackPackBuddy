package com.example.coursefreak.coursefreak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.*;

public class Courses extends AppCompatActivity {



    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("courses");
    myRef.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            HashMap question = dataSnapshot.getValue(HashMap.class);

            System.out.println(question.get("0"));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        myRef.child("courses").;
    }


    // Read from the database
    myRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String value = dataSnapshot.getValue(String.class);
            Log.d(TAG, "Value is: " + value);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    });
}
