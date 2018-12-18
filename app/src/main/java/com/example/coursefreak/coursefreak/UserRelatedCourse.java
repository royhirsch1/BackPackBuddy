package com.example.coursefreak.coursefreak;

public final class UserRelatedCourse {
    public Boolean interested;
    public Boolean completed;
    public Boolean liked;

    public UserRelatedCourse(Boolean interested, Boolean completed, Boolean liked) {
        this.interested = interested;
        this.completed = completed;
        this.liked = liked;
    }

    public Boolean getInterested() {
        return this.interested;
    }

    public Boolean getCompleted() {
        return this.completed;
    }

    public Boolean getLiked() {
        return this.liked;
    }
}
