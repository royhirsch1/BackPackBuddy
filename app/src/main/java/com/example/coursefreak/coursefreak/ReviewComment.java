package com.example.coursefreak.coursefreak;

public class ReviewComment {

    // UserID of the user that made the comment.
    public String uid;

    // Text of the comment.
    public String commentText;

    public Integer commentNumber;

    public String name;

    public ReviewComment() { }

    public ReviewComment(String uid, String text, Integer num, String name) {
        this.uid = uid;
        this.commentText = text;
        this.commentNumber = num;
        this.name = name;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String s) {
        this.uid = s;
    }

    public String getCommentText() {
        return this.commentText;
    }

    public String getName() {
        return name;
    }

    public void setCommentText(String s) {
        this.commentText = s;
    }

    public Integer getCommentNumber() {
        return this.commentNumber;
    }

    public void setCommentNumber(Integer n) {
        this.commentNumber = n;
    }

    public void setName(String name) {
        this.name = name;
    }
}
