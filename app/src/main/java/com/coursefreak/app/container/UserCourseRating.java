package com.coursefreak.app;

public class UserCourseRating {
    public String courseName;
    public Integer rating;

    public UserCourseRating() {
        //Default constr for Firebase
    }

    public UserCourseRating(String course_name, Integer rating) {
        this.courseName = course_name;
        this.rating = rating;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getRating() {
        return this.rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
