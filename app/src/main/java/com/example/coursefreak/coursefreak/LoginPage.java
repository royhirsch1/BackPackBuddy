package com.example.coursefreak.coursefreak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import la.matrix.DenseMatrix;
import la.matrix.Matrix;
import la.matrix.SparseMatrix;
import ml.recovery.MatrixCompletion;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import static ml.utils.ArrayOperator.colon;
import static ml.utils.ArrayOperator.minusAssign;
import static ml.utils.Matlab.linearIndexing;
import static ml.utils.Matlab.linearIndexingAssignment;
import static ml.utils.Time.tic;
import static ml.utils.Time.toc;

import com.example.coursefreak.coursefreak.FirebaseUtils;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LoginPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
        Button go_to_signup = (Button)findViewById(R.id.buttonEmailSign);
        go_to_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(v.getContext(), SignupPage.class);
                startActivity(signupIntent);
            }
        });

        Button guestViewButton = (Button)findViewById(R.id.buttonGuestView);
        guestViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent guestIntent = new Intent(v.getContext(), GuestPage.class);
//                startActivity(guestIntent);
            }
        });

        //ActivateLamlTest();
        //TestDatabaseUtils1();
        //TestDatabasUtils2();
        //TestDatabaseUtils3();
        //TestDatabaseUtils4();
        //TestDatabaseUtils5();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        TextView title = (TextView)findViewById(R.id.debugTitleBox);
        if(currentUser != null) {
            title.setText("Welcome ".concat(currentUser.getEmail()));
        }
    }

//    private void ActivateLamlTest() {
//        Log.d("Completion", "OVER HERE");
//        double[][] data = new double[][] {
//                {1,    1,  0, -1},
//                {-1,   -1,  1,  0},
//                {-1,   0,  1,  1},
//                {-1,  -1,  0,  1},
//        };
//        Matrix A = new DenseMatrix(4,4);
//        /*
//            Courses:
//            Operating Systems, Computer Architecture, Algorithms, Data Structures
//            Alice:
//                Likes Operating Systems, Comp Arch,
//                Dislikes Algorithms
//            Bob:
//                Likes Algorithms, Data Structures
//            Kevin:
//                Likes Algorithms,
//                Dislikes Comp Arch
//            Jane:
//                Likes Algorithms,
//                Dislikes Operating Systems
//         */
//        for(int i = 0; i < 4; i++) {
//            for(int j = 0; j < 4; j++) {
//                A.setEntry(i, j, data[i][j]);
//            }
//        }
//        int[] indices = new int[] {
//                0, 1, 2, 3, 4, 5 ,6, 7, 8, 9, 10, 11, 12, 13, 14, 15
//        };
//        indices = linearIndexing(indices, colon(0,5));
//        for(int i = 0; i < indices.length; i++) {
//            Log.d("Completion", Integer.toString(indices[i]));
//        }
//        Matrix Omega = new DenseMatrix(new double[][] {
//                {1,1,1,1},
//                {1,1,1,1},
//                {1,1,1,1},
//                {1,1,1,1},
//        });
//        //linearIndexingAssignment(Omega, indices, 1);
//        double[][] aux = Omega.getData();
//        Log.d("Completion", "----------------");
//        MatrixCompletion matrixCompletion = new MatrixCompletion();
//        matrixCompletion.feedData(A);
//        matrixCompletion.feedIndices(Omega);
//        tic();
//        matrixCompletion.run();
//        Log.d("Completion", "Time is ".concat(new Double(toc()).toString()));
//        Matrix A_hat = matrixCompletion.GetLowRankEstimation();
//        String[] names = new String[] {
//                "Alice", "Bob", "Kevin", "Jane", "Leonid"
//        };
//        for(int i = 0; i < 4; i++) {
//            Log.d("Completion", "Results for ".concat(names[i]));
//            for(int j = 0; j < 4; j++) {
//                Log.d("Completion", "Input Value: ".concat((new Double(A.getEntry(i,j))).toString()));
//                if(A.getEntry(i, j) != 0) {
//                    Log.d("Completion", "New Value: ".concat(new Double(A.getEntry(i,j)).toString()));
//                } else {
//                    Log.d("Completion", "New Value: ".concat(new Double(A_hat.getEntry(i,j) * 10000000.0).toString()));
//                }
//
//            }
//        }
//    }

//    private void TestDatabaseUtils1() {
//        Log.d("Courses", "HELLOOO~~~~");
//        List<Course> all_courses = new ArrayList<>();
//        FirebaseUtils.allCourses(mDatabase.getReference(), all_courses);
//        Log.d("Courses", "Back from allCourses!");
//        String name; Integer pos; Integer rev;
//        if(all_courses.size() > 0 && all_courses.get(0).getPos() == -1) {
//            Log.d("Courses","ERROR");
//            Log.d("Courses", all_courses.get(0).getName());
//            return;
//        }
//        for(Course c : all_courses) {
//            name = c.getName(); pos = c.getPos(); rev = c.getReviewNumber();
//            Log.d("Courses", name.concat(" ").concat(pos.toString()).concat(" ").concat(rev.toString()));
//        }
//    }
//
//    private void TestDatabasUtils2() {
//        Log.d("Rate", "Hello there.");
//        FirebaseUtils.userAddPositiveRating(mAuth.getUid(),
//                "234118 - Computer Organization",
//                mDatabase.getReference());
//        Log.d("Rate", "Right after userAddPositiveRating");
//    }
//
//    private void TestDatabaseUtils3() {
//        Log.d("RemRate", "Hello again.");
//        FirebaseUtils.userRemoveExistingRating(mAuth.getUid(),
//                "234141 - Combinatorics",
//                mDatabase.getReference());
//        Log.d("RemRate","Right after userRemoveRating");
//    }
//
//    private void TestDatabaseUtils4() {
//        Log.d("AllRate","This is the last time you'll see me.");
//        FirebaseUtils.userRatings(mAuth.getUid(),
//                mDatabase.getReference());
//        Log.d("AllRate", "After userRatings");
//    }
//
//    private void TestDatabaseUtils5() {
//        Log.d("Matrix","Testing matrix...");
//        FirebaseUtils.getRatingsMatrix(mDatabase.getReference());
//        Log.d("Matrix", "After getting Matrix");
//    }
}
