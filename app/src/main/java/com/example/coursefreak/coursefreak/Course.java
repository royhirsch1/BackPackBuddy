package com.example.coursefreak.coursefreak;

public class Course {
    public String name;
    public Integer pos;
    public Integer neg;
    public Integer reviewNumber;
    public Course() {
        // Default constructor required.
    }
    public Course(String name, Integer pos, Integer neg, Integer revNum) {
        this.name = name;
        this.pos = pos;
        this.neg = neg;
        this.reviewNumber = revNum;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPos() {
        return this.pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public Integer getNeg() {
        return this.neg;
    }

    public void setNeg(Integer neg) {
        this.neg = neg;
    }

    public Integer getReviewNumber() {
        return this.reviewNumber;
    }

    public void setReviewNumber(Integer reviewNumber) {
        this.reviewNumber = reviewNumber;
    }
}
