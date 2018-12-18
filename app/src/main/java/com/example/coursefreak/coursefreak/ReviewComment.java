package com.example.coursefreak.coursefreak;

public class ReviewComment {

    // UserID of the user that made the comment.
    public String uid;

    // Text of the comment.
    public String commentText;

    public ReviewComment(String uid, String text) {
        this.uid = uid;
        this.commentText = text;
    }

    public String getUid() {
        return this.uid;
    }

    public String getCommentText() {
        return this.commentText;
    }
}
