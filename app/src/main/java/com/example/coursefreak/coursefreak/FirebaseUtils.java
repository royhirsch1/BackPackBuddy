package com.example.coursefreak.coursefreak;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import la.matrix.DenseMatrix;
import la.matrix.Matrix;
import ml.recovery.MatrixCompletion;
public final class FirebaseUtils {
    /*
        This class is for different database utils for
        writing or reading from the database.
     */

    private FirebaseUtils() { }

    // Returns list of all courses in the system in Course objects
    // Given the reference to the root of the database.
    // Upon error, returns list with 1 course whose name is the error.
    public static void allCourses(DatabaseReference mDB) {
        Log.d("Courses", "In allCourses");
        final List<Course> res = new ArrayList<>();
        mDB.child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                    Log.d("Courses", courseSnap.getKey());
                    Course c = courseSnap.getValue(Course.class);
                    c.parseCatsReqs();
                    res.add(c);
                }
                for(Course c : res) {
                    if(c == null) {
                        Log.d("Courses", "ERROR! Null Course");
                        break;
                    }
                    Log.d("Courses",
                            c.getName()
                            .concat(c.getCourseID()).concat(" ")
                            .concat(c.getName()).concat(" ")
                            .concat(c.getPoints().toString()).concat(" ")
                            .concat(c.getNumLikes().toString()).concat(" ")
                            .concat(c.getNumCompleted().toString()).concat(" ")
                            .concat(c.getAverage().toString()));
                    Log.d("Courses", "Course Categories:");
                    for(String s : c.parsedCategories) {
                        Log.d("Courses", s);
                    }
                    Log.d("Courses", "Course Requirements:");
                    for(String s : c.parsedRequirements) {
                        Log.d("Courses", s);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                res.clear();
                Log.d("Courses", "Database Error");
            }
        });
    }

    // Returns a map of all of a single user's ratings, with key being course name.
    // Given a uid of the user and a database reference.
    // Upon error, returns map with one element whose key is the error and value is -1.
    public static void userRatings(String uid, DatabaseReference mDB) {
        final List<String> res = new ArrayList<>();
        Log.d("UserRates", "In userRatings");
        Log.d("UserRates", "Ratings for ".concat(uid));
        mDB.child("user_course_ratings").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ratingSnap : dataSnapshot.getChildren()) {
                    String cid = ratingSnap.getKey().toString();
                    if (cid != null && cid.length() > 0) {
                        res.add(cid);
                    }
                    else {
                        Log.d("UserRates", "ERROR: Null");
                        break;
                    }
                }
                for(String cid : res) {
                    Log.d("UserRates", "Liked course: ".concat(cid));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                res.clear();
                Log.d("AllRate", "ERROR Cancelled");
            }
        });
//        Log.d("Ratings", "Ratings for ".concat(uid));
//        for(Map.Entry<String, Integer> e : res.entrySet()) {
//            String str = "Rate for ";
//            str.concat(e.getKey()).concat(" is ");
//            str.concat(e.getValue().toString());
//            Log.d("Ratings", str);
//            str = "";
//        }
    }

    // Returns a Boolean that is true if succeeded, and false otherwise.
    // Given a uid and course name, adds a positive rating for the user in that course,
    // (and given reference to the root)
    // Upon error, returns a string that specifies the error. String is empty otherwise.
    // Assume: The course name is valid.
    public static void userAddPositiveRating(String uid, final String course_name, DatabaseReference mDB) {
        final String resStr = new String();
        mDB.child("user_course_ratings")
                .child(uid)
                .child(course_name)
                .setValue(1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    resStr.concat("Did not push!");
                }
            }
        });
        if(resStr.length() > 0) {
            Log.d("Rate", "Rating ERROR");
        }
        else {
            Log.d("Rate", "Rating Success");
        }
    }

    public static void userRemoveExistingRating(String uid, final String course_name, DatabaseReference mDB) {
        final String resStr = new String();
        Log.d("RemRate", "We meet again Mr. Anderson");
        mDB.child("user_course_ratings").child(uid).child(course_name).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Log.d("RemRate", "ERROR");
                }
            }
        });
    }

    // Returns matrix of all user ratings for the Matrix Recovery algorithm.
    public static double[][] getRatingsMatrix(DatabaseReference mDB) {
        mDB.child("user_course_ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, List<String>> allRatings = new HashMap<>();
                Set<String> all_users = new TreeSet<>();
                Set<String> all_courses = new TreeSet<>();
                for(DataSnapshot singleUserData : dataSnapshot.getChildren()) {
                    String user_id = singleUserData.getKey();
                    //Log.d("Matrix", user_id);
                    all_users.add(user_id);
                    allRatings.put(user_id, new ArrayList<String>());
                    for(DataSnapshot singleCourseRatings : singleUserData.getChildren()) {
                        String cid = singleCourseRatings.getKey();
                        //Log.d("Matrix", cid);
                        all_courses.add(cid);
                        allRatings.get(user_id).add(cid);
                    }
                }
                Log.d("Matrix", "Participating courses found: ".concat(Integer.toString(all_courses.size())));
                Log.d("Matrix", "Participating users found: ".concat((Integer.toString(all_users.size()))));
                Map<String, Integer> coursesIndex = new HashMap<>();
                Map<Integer, String> coursesReverse = new HashMap<>();
                Map<String, Integer> usersIndex   = new HashMap<>();
                Map<Integer, String> usersReverse = new HashMap<>();
                int i = 0;
                for(String c : all_courses) {
                    coursesIndex.put(c, i);
                    coursesReverse.put(i, c);
                    i++;
                }
                int j = 0;
                for(String u : all_users) {
                    usersIndex.put(u, j);
                    usersReverse.put(j, u);
                    j++;
                }
                double[][] ratingsMatrix = new double[all_users.size()][all_courses.size()];
                for(String uid : all_users) {
                    for(String course : allRatings.get(uid)) {
                        ratingsMatrix[usersIndex.get(uid)][coursesIndex.get(course)] = 1.0;
                    }
                }
                for(int m = 0; m < all_users.size(); m++) {
                    Log.d("Matrix", usersReverse.get(m).concat(" 's course ratings"));
                    for(int n = 0; n < all_courses.size(); n++) {
                        Log.d("Matrix", "Rating for "
                            .concat(coursesReverse.get(n))
                            .concat(" is ")
                            .concat(Double.toString(ratingsMatrix[m][n])));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Matrix", "Cancellation error");
            }
        });
        return null;
    }

    public static void addNewCourseReview(String courseID, String uid, String text, DatabaseReference mDB) {
        Log.d("addRev", "In addNewCourseReview");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("reviewText", text);
        data.put("numberHelped", 0);
        data.put("userID", uid);
        mDB.child("course_reviews")
            .child(courseID)
            .push()
            .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Log.d("addRev", "ERROR! No Success!");
                }
                else {
                    Log.d("addRev", "Review added!");
                }
            }
        });
    }

    public static void reviewHelpedSomeone(String reviewID, String courseID, Integer curNumberHelped, DatabaseReference mDB) {
        Integer nextNum = curNumberHelped + 1;
        Log.d("helped", "In reviewHelpedSomeone");
        mDB.child("course_reviews").child(courseID).child(reviewID).child("numberHelped").setValue(nextNum).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Log.d("helped", "ERROR! review not updated!");
                }
                else {
                    Log.d("helped", "Review update success!");
                }
            }
        });
    }

    public static void getCourseReviewsOrdered(final String courseID, DatabaseReference mDB) {
        Log.d("getRevs", "In getCourseReviewsOrdered");
        final List<Review> reviewList = new ArrayList<>();
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
                for(Review r : reviewList.subList(0,Math.min(4, reviewList.size()))) {
                    Log.d("getRevs", "Review by ".concat(r.getUserID())
                            .concat(" says that ").concat(r.getReviewText())
                            .concat(" and has helped ").concat(r.getNumHelped().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                reviewList.clear();
                Log.d("getRevs", "Database Cancel Error!");
            }
        });
    }

    public static void addReviewComment(String rid, Integer nC, String txt, String uid, String name, DatabaseReference mDB) {
        Log.d("addC", "In addReviewComment");
        mDB.child("review_comments")
                .child(rid)
                .child(nC.toString())
                .setValue(new ReviewComment(uid, txt, nC, name)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Log.d("addC", "ERROR! No success");
                } else {
                    Log.d("addC","Success! Comment added");
                }
            }
        });
        Log.d("addC", "Finished addReviewComment");
    }

    public static void getReviewcomments(String rid, DatabaseReference mDB) {
        Log.d("getCs", "In getReviewComments");
        final List<ReviewComment> comments = new ArrayList<>();
        mDB.child("review_comments")
                .child(rid)
                .orderByChild("commentNumber")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    ReviewComment c = commentSnapshot.getValue(ReviewComment.class);
                    comments.add(c);
                }
                for(ReviewComment c : comments) {
                    Log.d("getCs", "Review #"
                            .concat(c.getCommentNumber().toString())
                            .concat(" says ")
                            .concat(c.getCommentText())
                            .concat(" written by ")
                            .concat(c.getName()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("getCs", "Database Cancel Error!");
            }
        });
    }

    public static void addUserToPartners(String uid, String courseID, DatabaseReference mDB) {
        Log.d("addP", "In addP");
        mDB.child("course_partners")
                .child(courseID)
                .child(uid)
                .setValue(1)
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
        Log.d("addP","Finished addP");
    }

    public static void removeUserFromPartners(String uid, String courseID, DatabaseReference mDB) {
        Log.d("remP", "In remP");
        mDB.child("course_partners")
                .child(courseID)
                .child(uid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Log.d("remP", "ERROR Partner not removed correctly!");
                } else {
                    Log.d("remP", "Partner removed successfully!");
                }
            }
        });
        Log.d("remP", "finished remP");
    }

    public static void getCoursePossiblePartners(String courseID, DatabaseReference mDB) {
        Log.d("allP", "In allP");
        final CoursePartners partners = new CoursePartners(courseID);
        mDB.child("course_partners").child(courseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot partnerSnapshot : dataSnapshot.getChildren()) {
                    String uid = partnerSnapshot.getKey();
                    //Log.d("partner", "adding ".concat(uid));
                    partners.addPossiblePartner(uid, uid);
                }
                for(CoursePartner s : partners.getPossiblePartners().values()) {
                    Log.d("allP", "Possible partner ID: ".concat(s.getName()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("allP", "Error! Database Cancelled!");
            }
        });
    }

    public static void addNewUserData(String uid, String name, DatabaseReference mDB) {
        Log.d("user", "In add new user");
        User u = new User(uid, name);
        mDB.child("users").child(uid).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Log.d("user", "ERROR");
                } else {
                    Log.d("user", "added new user!");
                }
            }
        });
    }

//    public static void userSetProfilePicture(String uid, String path, DatabaseReference mDB) {
//    }

//    public static void userAddNewFriend(String uid, String fid, DatabaseReference mDB) {
//        Log.d("user", "adding friend");
//        mDB.child("users").child(uid).child("friendsList").push().setValue(fid).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(!task.isSuccessful()) {
//                    Log.d("addf", "ERROR");
//                } else {
//                    Log.d("addf", "success friend added");
//                }
//            }
//        });
//    }

    public static void addUserRelatedCourse(String uid, String courseID, UserRelatedCourse data, DatabaseReference mDB) {
        Log.d("user", "adding course".concat(courseID));
        mDB.child("users").child(uid).child("related_courses").child(courseID).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Log.d("user", "ERROR");
                } else {
                    Log.d("user", "success");
                }
            }
        });
    }

    public static void getUserData(final String uid, DatabaseReference mDB) {
        Log.d("user", "getting data");
        mDB.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if(u == null)
                    Log.d("user", "ERROR");
                else {
                    Log.d("user", u.getUid().concat(u.getName()));
                    for(Map.Entry<String, UserRelatedCourse> e : u.getRelated_courses().entrySet()) {
                        Log.d("user", e.getKey());
                        Log.d("user", "completed".concat(e.getValue().getCompleted().toString())
                        .concat("interested").concat(e.getValue().getInterested().toString())
                        .concat("liked").concat(e.getValue().getLiked().toString()));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("user", "Database ERROR");
            }
        });
    }

    public static void recGetNewUserRecommendations(final String uid, DatabaseReference mDB) {
        final Set<String> nontrivialPredictions = new TreeSet<>();

        mDB.child("user_course_ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Setting up participating users and courses,
                // So that users that have not rated anything will not participate.
                Map<String, List<String>> allRatings = new HashMap<>();
                Set<String> all_users = new TreeSet<>();
                Set<String> all_courses = new TreeSet<>();
                for(DataSnapshot singleUserData : dataSnapshot.getChildren()) {
                    String user_id = singleUserData.getKey();
                    all_users.add(user_id);
                    allRatings.put(user_id, new ArrayList<String>());
                    for(DataSnapshot singleCourseRatings : singleUserData.getChildren()) {
                        String cid = singleCourseRatings.getKey();
                        //Log.d("Matrix", cid);
                        all_courses.add(cid);
                        allRatings.get(user_id).add(cid);
                    }
                }

                // If the user has not rated anything, cannot predict for him.
                if(all_users.contains(uid) == false) {
                    return;
                }

                Map<String, Integer> coursesIndex = new HashMap<>();
                Map<Integer, String> coursesReverse = new HashMap<>();
                Map<String, Integer> usersIndex   = new HashMap<>();
                int i = 0;
                for(String c : all_courses) {
                    coursesIndex.put(c, i);
                    coursesReverse.put(i, c);
                    i++;
                }
                int j = 0;
                for(String u : all_users) {
                    usersIndex.put(u, j);
                    j++;
                }

                // Setup the ratings matrix for the algorithm.
                double[][] ratingsMatrix = new double[all_users.size()][all_courses.size()];
                for(String uid : all_users) {
                    for(String course : allRatings.get(uid)) {
                        i = usersIndex.get(uid);
                        j = coursesIndex.get(course);
                        ratingsMatrix[i][j] = 1.0;
                    }
                }

                // Actually running the algorithm
                Recommender.Pair<double[][], double[][]> pUV = Recommender.myRecommender(ratingsMatrix, 4, 0.5, 0.5);
                double[][] predictions = Recommender.PredictRating(pUV.getFirst(), pUV.getSecond());

                // Setup matrix for easier readability
                int user_index = usersIndex.get(uid);
                for(String uid : all_users) {
                    for(String course : all_courses) {
                        i = usersIndex.get(uid);
                        j = coursesIndex.get(course);
                        double d = predictions[i][j] * 2;
                        if(ratingsMatrix[i][j] != 0)
                            d = 1;
                        else if(d < 0.0099)
                            d = 0;
                        predictions[i][j] = ((int)(d*1000))/1000.0;
                    }
                }

                // Collect results into a set of recommended courses
                Log.d("Completion", "Recommendations for ".concat(uid));
                for(int n = 0; n < all_courses.size(); n++) {
                    if(predictions[user_index][n] != 0.0 && predictions[user_index][n] != 1.0) {
                        nontrivialPredictions.add(coursesReverse.get(n));
                        Log.d("Completion", "Course: ".concat(coursesReverse.get(n)).concat(" is recommended."));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Matrix", "Cancellation error");
            }
        });
    }
}
