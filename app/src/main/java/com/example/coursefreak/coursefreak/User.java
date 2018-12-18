package com.example.coursefreak.coursefreak;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class User {
    public String uid;
    public String name;
    public String imagePath; // Path to profile picture
    public Set<String> friendsList;
    public Map<String, UserRelatedCourse> relevantCourses;

    public User(String uid, String name, Map<String, UserRelatedCourse> relevantCourses) {
        this.uid = uid;
        this.name = name;
        this.imagePath = null;
        this.friendsList = null;
        this.relevantCourses = relevantCourses;
    }

    public User(String uid, String name, Set<String> friendsList, Map<String, UserRelatedCourse> relevantCourses) {
        this.uid = uid;
        this.name = name;
        this.imagePath = null;
        this.friendsList = friendsList;
        this.relevantCourses = relevantCourses;
    }

    public String getUid() {
        return this.uid;
    }

    public String getName() {
        return this.name;
    }

    public Set<String> getFriendsList() {
        return this.friendsList;
    }

    public void addFriend(String uid) {
        if(this.friendsList != null)
            this.friendsList.add(uid);
    }

    public void removeFriend(String uid) {
        if(this.friendsList != null)
            this.friendsList.remove(uid);
    }

    public Map<String, UserRelatedCourse> getRelevantCourses() {
        return this.relevantCourses;
    }

    public void relateNewCourse(String courseID, UserRelatedCourse data) {
        this.relevantCourses.put(courseID, data);
    }

    public void updateExistingCourse(String courseID, UserRelatedCourse data) {
        this.relateNewCourse(courseID, data);
    }

    public void removeRelatedCourse(String courseID) {
        this.relevantCourses.remove(courseID);
    }
}
