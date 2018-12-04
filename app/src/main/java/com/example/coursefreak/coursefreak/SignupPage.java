package com.example.coursefreak.coursefreak;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupPage extends AppCompatActivity {
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);
        this.mAuth = FirebaseAuth.getInstance();

        // ----- Setup signup button
        Button sign_up_button = (Button)findViewById(R.id.signupButton);
        if(sign_up_button == null)
            Log.wtf("signupbutton", new NullPointerException());
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = ((EditText)findViewById(R.id.signupEmailTextBox)).getText().toString();
                String user_password = ((EditText)findViewById(R.id.passwordTextBox)).getText().toString();
                if (user_email.length() != 0 && user_password.length() >= 8)
                    mAuth.createUserWithEmailAndPassword(user_email, user_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(SignupPage.this,
                                        "Welcome ".concat(user.getEmail()),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignupPage.this,
                                        task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });

    }
}
