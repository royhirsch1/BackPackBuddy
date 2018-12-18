package com.example.coursefreak.coursefreak;

import java.util.Set;

public final class CoursePartners {
    public String courseID;
    public Set<String> possiblePartners;

    public CoursePartners(String courseID, Set<String> possiblePartners) {
        this.courseID = courseID;
        this.possiblePartners = possiblePartners;
    }

    public String getCourseID() {
        return this.courseID;
    }

    public Set<String> getPossiblePartners() {
        return this.possiblePartners;
    }

    public void addPossiblePartner(String uid) {
        this.possiblePartners.add(uid);
    }

    public void removePossiblePartner(String uid) {
        this.possiblePartners.remove(uid);
    }
}
