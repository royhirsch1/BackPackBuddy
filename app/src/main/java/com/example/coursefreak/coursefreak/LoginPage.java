package com.example.coursefreak.coursefreak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginPage extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        this.mAuth = FirebaseAuth.getInstance();
        Button go_to_signup = (Button)findViewById(R.id.signupRedirectButton);
        go_to_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(v.getContext(), SignupPage.class);
                startActivity(signupIntent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        TextView title = (TextView)findViewById(R.id.titleBox);
        if(currentUser != null) {
            title.setText("Welcome ".concat(currentUser.getEmail()));
        }
    }
}
