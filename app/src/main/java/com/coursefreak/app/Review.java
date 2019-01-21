package com.coursefreak.app;

import java.util.List;

public class Review {
    public String reviewID;
    public String reviewText;
    public Integer numberHelped;
    public String userID;
    public List<ReviewComment> reviewComments;

    public Review() {
        // Default constr for Firebase
    }

    public Review(String id, String text, Integer helped, List<ReviewComment> comments, String userID) {
        this.reviewID = id;
        this.reviewText = text;
        this.numberHelped = helped;
        this.reviewComments = comments;
        this.userID = userID;
    }

    public String getReviewID() {
        return this.reviewID;
    }

    public void setReviewID(String s) {
        this.reviewID = s;
    }

    public String getReviewText() {
        return this.reviewText;
    }

    public void setReviewText(String s) {
        this.reviewText = s;
    }

    public Integer getNumHelped() {
        return this.numberHelped;
    }

    public void setNumberHelped(Integer n) {
        this.numberHelped = n;
    }

    public List<ReviewComment> getReviewComments() {
        return this.reviewComments;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String uid) {
        this.userID = uid;
    }
}
