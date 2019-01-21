package com.coursefreak.app;

import java.util.Set;

public final class UserRatings {
    public String uid;
    public Set<String> likedCourses;

    public UserRatings(String uid, Set<String> likedCourses) {
        this.uid = uid;
        this.likedCourses = likedCourses;
    }

    public String getUid() {
        return this.uid;
    }

    public Set<String> getLikedCourses() {
        return this.likedCourses;
    }

    public void addLikedCourse(String courseID) {
        this.likedCourses.add(courseID);
    }

    public void clearCourses() {
        this.likedCourses.clear();
    }
}
