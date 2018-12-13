package com.example.coursefreak.coursefreak;

public class Course {
    public String course_name;
    public Integer pos;
    public Integer neg;
    public Integer reviewNumber;
    public Course() {
        // Default constructor required.
    }
    public Course(String course_name, Integer pos, Integer neg, Integer revNum) {
        this.course_name = course_name;
        this.pos = pos;
        this.neg = neg;
        this.reviewNumber = revNum;
    }
}
