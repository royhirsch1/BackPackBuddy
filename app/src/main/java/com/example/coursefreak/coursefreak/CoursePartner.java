package com.example.coursefreak.coursefreak;

public final class CoursePartner {
    public String uid;
    public String name;

    public CoursePartner() { }

    public CoursePartner(String id, String name) {
        this.uid = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }
}
