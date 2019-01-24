package com.coursefreak.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import com.google.firebase.database.FirebaseDatabase;

import static android.view.View.GONE;

public class LoginPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private AlertDialog.Builder builder;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "com.google.firebase.messaging.default_notification_channel_id";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default_channel_id", name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        setContentView(R.layout.activity_login_page);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(GONE);
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();

        //If user is already signed in:
        if(mAuth.getCurrentUser() != null) {
            //Welcome immediately without wait
            gotoCourses();
        }

        //If not signed up, we can start setting up all other buttons and objects.

        TextView go_to_signup = (TextView)findViewById(R.id.buttonEmailSign);
        go_to_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(v.getContext(), SignupPage.class);
                startActivity(signupIntent);
            }
        });
        this.builder = new AlertDialog.Builder(this);
        this.builder.setTitle(R.string.aboutButtonDesc);
        this.builder.setCancelable(true);
        String[] credits = getResources().getStringArray(R.array.app_credits);
        String message = TextUtils.join("\n", credits);
        this.builder.setMessage(message);
/*        ImageView seeAbout = (ImageView) findViewById(R.id.buttonViewAbout);
        if(seeAbout != null)
            seeAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.show();
                }
            });*/

        final Button sign_in_button = (Button)findViewById(R.id.buttonLogin);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_in_button.setEnabled(false);
                android.text.Editable emailInput = ((EditText)findViewById(R.id.emailTextBox)).getText();
                android.text.Editable passInput = ((EditText)findViewById(R.id.passwordTextBox)).getText();
                if(emailInput.length() < 1 || passInput.length() < 1){
                    Toast.makeText(getApplicationContext(), "Please Enter Username and password", Toast.LENGTH_LONG ).show();
                    sign_in_button.setEnabled(true);
                    return;
                }
                String user_email = emailInput.toString();
                String user_password = passInput.toString();
                spinner.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
//                            Log.d("AuthComplete","Exception:"+task.getException().getMessage());
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e){
                                Toast.makeText(getApplicationContext(), "E-mail Invalid ", Toast.LENGTH_LONG ).show();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                Toast.makeText(getApplicationContext(),"Password Invalid ", Toast.LENGTH_LONG ).show();
                            } catch (FirebaseNetworkException e){
                                Toast.makeText(getApplicationContext(),"No Network Connection", Toast.LENGTH_LONG ).show();
                            } catch (Exception e){
                            }
                            sign_in_button.setEnabled(true);
                            spinner.setVisibility(GONE);
                        }
                        else {
//                            Log.d("AuthComplete", "AuthRequestSuccess");
                            Toast.makeText(getApplicationContext(),"Welcome Back!", Toast.LENGTH_SHORT ).show();
                            gotoWelcome();
                        }
                    }
                });
            }
        });

//        Button guestViewButton = (Button)findViewById(R.id.buttonGuestView);
//        guestViewButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent guestIntent = new Intent(v.getContext(), GuestPage.class);
////                startActivity(guestIntent);
//            }
//        });



        //ActivateLamlTest();
        //TestDatabaseUtils1();
        //TestDatabasUtils2();
        //TestDatabaseUtils3();
        //TestDatabaseUtils4();
        //TestDatabaseUtils5();
        //TestDatabaseUtils6();
        //TestDatabaseUtils7();
        //TestDatabaseUtils8();
        //TestDatabaseUtils9();
        //TestDatabaseUtils10();
        //TestDatabaseUtils11();
        //TestDatabaseUtils12();
        //TestDatabaseUtils13();
        //TestDatabaseUtils14();
        //TestDatabaseUtils15();
        //TestDatabaseUtils16();
        //TestDatabaseUtils18();
        //TestDatabaseUtils19();
        //TestDatabaseUtils20();
        //ActivatePredictionsTest();
    }





    public void gotoWelcome() {
        Intent intent = new Intent(this, WalkthroughActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void gotoCourses() {
        Intent intent = new Intent(this, TabsCoursesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
//    private class GetAllCourses extends AsyncTask<DatabaseReference, Integer, Long> {
//        public List<String> arr;
//        protected Long doInBackground(DatabaseReference... mDB) {
//            return new Long(0);
//        }
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
////        FirebaseUser currentUser = mAuth.getCurrentUser();
////        TextView title = (TextView)findViewById(R.id.debugTitleBox);
////        if(currentUser != null) {
////                Intent intent = new Intent(catalouge.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                startActivity(intent);
////        }
//    }

//    private void ActivatePredictionsTest() {
//        double[][] data = new double[][] {
//                {1,  1,  0,  -1},
//                {1,  0,  -1,  0},
//                {-1,  -1,  0,  1},
//                {0,  -1,  0,  1},
//                {0,  0,   1,  1},
//                {-1, 0, 1, 1},
//        };
//
//        Recommender.Pair<double[][], double[][]> pUV = Recommender.myRecommender(data, 4, 0.5, 0.5);
//        double[][] predictions = Recommender.PredictRating(pUV.getFirst(), pUV.getSecond());
//
//        String[] names = new String[] {
//        "Alice", "Bob", "Kevin", "Jane", "Leonid", "Dummy"
//        };
//        for(int i = 0; i < 6; i++) {
//            Log.d("Completion", "Results for ".concat(names[i]));
//            for(int j = 0; j < 4; j++) {
//                Log.d("Completion", "Input Value: ".concat((new Double(data[i][j]).toString())));
//                if(data[i][j] != 0) {
//                    Log.d("Completion", "New Value: ".concat(new Double(data[i][j]).toString()));
//                } else {
//                    Log.d("Completion", "New Value: ".concat(new Double(predictions[i][j]).toString()));
//                }
//
//            }
//        }
//    }

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
//            catalog:
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
//        FirebaseUtils.allCourses(mDatabase.getReference());
//        Log.d("Courses", "Back from allCourses!");
//    }
//
    private void TestDatabasUtils2() {
//        Log.d("Rate", "Hello there.");
        FirebaseUtils.userAddPositiveRating(
                "rfZdLx1CxJM5lCCQ0po7JmG4UMn1",
                "234218",
                mDatabase.getReference());
//        Log.d("Rate", "Right after userAddPositiveRating");
    }
//
//    private void TestDatabaseUtils3() {
//        Log.d("RemRate", "Hello again.");
//        FirebaseUtils.userRemoveExistingRating(mAuth.getUid(),
//                "234218",
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
//    private void TestDatabaseUtils6() {
//        Log.d("addRev", "Entering addNewCourseReview");
//        FirebaseUtils.addNewCourseReview("234123", this.mAuth.getUid(), "Easy!", this.mDatabase.getReference());
//        Log.d("addRev","Finshed addNewREview");
//    }

//    private void TestDatabaseUtils7() {
//        Log.d("helped", "Entering reviewHelpedSomeone");
//        FirebaseUtils.reviewHelpedSomeone("-LU1v7i2isiO-G0gLAFU", "234123", 1, this.mDatabase.getReference());
//        Log.d("helped", "Finished reviewHelpedSomeone");
//    }

//    private void TestDatabaseUtils8() {
//        Log.d("getRevs", "Entering getCourseReviewsOrdered");
//        FirebaseUtils.getCourseReviewsOrdered("234123", this.mDatabase.getReference());
//        Log.d("getRevs","Finished getCourseReviewsOrdered");
//    }

//    private void TestDatabaseUtils9() {
//        FirebaseUtils.addReviewComment(
//                "-LU1v7i2isiO-G0gLAFU",
//                1,
//                "This other comment is so true",
//                "dum1",
//                "Dummy 1",
//                this.mDatabase.getReference());
//    }
//    private void TestDatabaseUtils10() {
//        Log.d("getCs", "Entering getReviewcomments");
//        FirebaseUtils.getReviewcomments("-LU1v7i2isiO-G0gLAFU", this.mDatabase.getReference());
//    }
//    private void TestDatabaseUtils11() {
//        Log.d("partner", "Entering add partner");
//        FirebaseUtils.addUserToPartners("dum2", "dum2@gmail.com", "Dum 2", "234218", this.mDatabase.getReference());
//    }
//    private void TestDatabaseUtils12() {
//        Log.d("partner", "getting partners in 234218");
//        FirebaseUtils.getCoursePossiblePartners("234218", this.mDatabase.getReference());
//    }
//    private void TestDatabaseUtils13() {
//        Log.d("partner", "removing dum2 from 234218");
//        FirebaseUtils.removeUserFromPartners("dum2", "234218", this.mDatabase.getReference());
//    }
//    private void TestDatabaseUtils14() {
//        Log.d("user", "adding dum3");
//        FirebaseUtils.addNewUserData("dum3", "Dummy 1", this.mDatabase.getReference());
//    }

//    private void TestDatabaseUtils15() {
//        Log.d("user", "adding related course");
//        UserRelatedCourse data = new UserRelatedCourse(true, false, false);
//        FirebaseUtils.addUserRelatedCourse("dum1", "234123", data, this.mDatabase.getReference());
//    }

//    private void TestDatabaseUtils16() {
//        Log.d("user", "getting user");
//        FirebaseUtils.getUserData("dum1", this.mDatabase.getReference());
//    }

//    private void TestDatabaseUtils17() {
//        Log.d("addf", "adding friend");
//        FirebaseUtils.userAddNewFriend("dum1", "dum2", this.mDatabase.getReference());
//    }
//    private void TestDatabaseUtils18() {
//        Log.d("recoms", "getting recommendations...");
//        FirebaseUtils.recGetNewUserRecommendations("dum2", this.mDatabase.getReference());
//    }
//    private void TestDatabaseUtils19() {
//        FirebaseUtils.updateCourseNumLikes("234118", 1, this.mDatabase.getReference());
//    }
//    private void TestDatabaseUtils20() {
//        FirebaseUtils.updateCourseNumCompleted("234118", 2, this.mDatabase.getReference());
//    }
//        private Task<String> addMessage(String text){
//            // Create the arguments to the callable function.
//            Map<String, Object> data = new HashMap<>();
//            data.put("text", "rate;amit;234247;p");
//            data.put("push", true);
//
//            return mFunctions
//                    .getHttpsCallable("addMessage")
//                    .call(data)
//                    .continueWith(new Continuation<HttpsCallableResult, String>() {
//                        @Override
//                        public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
//                            // This continuation runs on either success or failure, but if the task
//                            // has failed then getResult() will throw an Exception which will be
//                            // propagated down.
//                            String result = (String) task.getResult().getData();
//                            return result;
//                        }
//                    });
//        }
}
