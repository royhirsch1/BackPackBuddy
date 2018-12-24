package com.example.coursefreak.coursefreak;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CoursePartners {
    public String courseID;
    public Map<String, CoursePartner> possiblePartners;

    public CoursePartners(String courseID) {
        this.courseID = courseID;
        this.possiblePartners = new HashMap<>();
    }

    public CoursePartners(String courseID, Map<String, CoursePartner> possiblePartners) {
        this.courseID = courseID;
        this.possiblePartners = possiblePartners;
    }

    public String getCourseID() {
        return this.courseID;
    }

    public Map<String, CoursePartner> getPossiblePartners() {
        return this.possiblePartners;
    }

    public void addPossiblePartner(String uid, String name, String email) {
        this.possiblePartners.put(uid, new CoursePartner(uid, name, email));
    }

    public void addPossiblePartner(CoursePartner cp) {
        this.possiblePartners.put(cp.getUid(), cp);
    }

    public void removePossiblePartner(String uid) {
        this.possiblePartners.remove(uid);
    }
}
