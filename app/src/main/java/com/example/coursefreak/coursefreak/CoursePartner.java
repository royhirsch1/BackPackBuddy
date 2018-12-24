package com.example.coursefreak.coursefreak;

public final class CoursePartner {
    public String uid;
    public String name;
    public String email;

    public CoursePartner() { }

    public CoursePartner(String id, String name, String email) {
        this.uid = id;
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
