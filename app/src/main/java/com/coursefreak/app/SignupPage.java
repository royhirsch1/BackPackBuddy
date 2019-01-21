package com.coursefreak.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView errorTextNotify;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);
        this.mAuth = FirebaseAuth.getInstance();

        // ----- Setup signup button
        Button sign_up_button = findViewById(R.id.buttonEmailSign);
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.text.Editable emailInput = ((EditText)findViewById(R.id.emailTextBox_SU)).getText();
                android.text.Editable passInput = ((EditText)findViewById(R.id.passwordTextBox_SU)).getText();
                android.text.Editable passConfInput = ((EditText)findViewById(R.id.confirmPasswordBox)).getText();
                if(emailInput.length() < 1 || passInput.length() < 1 || passConfInput.length() < 1){
                    Toast.makeText(getApplicationContext(), "Please Enter Username and password", Toast.LENGTH_LONG ).show();
                    return;
                }
                final String user_email = emailInput.toString();
                String user_password = passInput.toString();
                String confirm_password = passConfInput.toString();
                if(user_password.equals(confirm_password) == false) {
                    Toast.makeText(getApplicationContext(), R.string.badPasswordMatchString, Toast.LENGTH_LONG ).show();
                    return;
                }
                else if(!isEmailValid(user_email)) {
                    Toast.makeText(getApplicationContext(), R.string.badEmailNotValid, Toast.LENGTH_LONG ).show();

                    return;
                }
                //password length is already verified by firebase. minimal length: 6
                else {
                    mAuth.createUserWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_SHORT ).show();
                                        final FirebaseUser user = mAuth.getCurrentUser();
                                        DatabaseReference mDB = FirebaseDatabase.getInstance().getReference();
                                        if(mDB == null)
                                            Toast.makeText(SignupPage.this, "Connection Error", Toast.LENGTH_LONG).show();
                                        Map<String, Object> user_data = new HashMap<>();
                                        user_data.put("name", user_email);
                                        user_data.put("uid", user.getUid());
                                        mDB.child("users").child(user.getUid()).setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(!task.isSuccessful()) {
                                                    //Toast.makeText(SignupPage.this, "Connection Error", Toast.LENGTH_LONG).show();
                                                } else {
//                                                    Toast.makeText(SignupPage.this,
//                                                            "Welcome ".concat(user.getEmail()),
//                                                            Toast.LENGTH_SHORT).show();
                                                    gotoWelcome();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(SignupPage.this,
                                                task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

//                    Toast.makeText(SignupPage.this,
//                        "Welcome ".concat(user_email),
//                        Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public void gotoWelcome() {
        Intent intent = new Intent(this, WalkthroughActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
