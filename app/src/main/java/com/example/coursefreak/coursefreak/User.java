package com.example.coursefreak.coursefreak;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class User {
    public String uid;
    public String name;
    public String imagePath; // Path to profile picture
    public Map<String, String> friendsList;
    public Map<String, UserRelatedCourse> related_courses;

    public User() { }

    public User(String uid) {
        this.uid = uid;
        this.name = "";
        this.imagePath = null;
        this.friendsList = null;
        this.related_courses = null;
    }

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
        this.imagePath = "";
        this.friendsList = new HashMap<>();
        this.related_courses = new HashMap<>();
    }

    public User(String uid, String name, Map<String, UserRelatedCourse> relevantCourses) {
        this.uid = uid;
        this.name = name;
        this.imagePath = null;
        this.friendsList = null;
        this.related_courses = relevantCourses;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<String> getFriendsList() {
        return this.friendsList.values();
    }

    public void addFriend(String key, String uid) {
        if(this.friendsList != null)
            this.friendsList.put(key, uid);
    }

    public void removeFriend(String uid) {
        if(this.friendsList != null)
            for(Map.Entry<String, String> e : this.friendsList.entrySet()) {
                if(e.getValue().equals(uid)) {
                    this.friendsList.remove(e.getKey());
                }
            }
    }

    public Map<String, UserRelatedCourse> getRelated_courses() {
        if(this.related_courses == null) {
            this.related_courses = new HashMap<>();
        }
        return this.related_courses;
    }

    public void setRelevantCourses(Map<String, UserRelatedCourse> relevantCourses) {
        this.related_courses = relevantCourses;
    }

    public void relateNewCourse(String courseID, UserRelatedCourse data) {
        this.related_courses.put(courseID, data);
    }

    public void updateExistingCourse(String courseID, UserRelatedCourse data) {
        this.relateNewCourse(courseID, data);
    }

    public void removeRelatedCourse(String courseID) {
        this.related_courses.remove(courseID);
    }
}
