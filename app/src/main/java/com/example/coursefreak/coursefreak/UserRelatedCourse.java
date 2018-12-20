package com.example.coursefreak.coursefreak;

public final class UserRelatedCourse {
    public Boolean interested;
    public Boolean completed;
    public Boolean liked;

    public UserRelatedCourse() { }

    public UserRelatedCourse(Boolean interested, Boolean completed, Boolean liked) {
        this.interested = interested;
        this.completed = completed;
        this.liked = liked;
    }

    public Boolean getInterested() {
        return this.interested;
    }

    public void setInterested(Boolean interested) {
        this.interested = interested;
    }

    public Boolean getCompleted() {
        return this.completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getLiked() {
        return this.liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }
}
