package com.example.jobapplicationportal.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

    private String title;
    private String description;
    private String skills;
    private String salary;
    private String id;
    private String date;

    // Engagement Fields
    private Map<String, Boolean> likes = new HashMap<>();
    private Map<String, Boolean> dislikes = new HashMap<>();
    private List<Comment> comments = new ArrayList<>();
    private int likeCount = 0;
    private int dislikeCount = 0;

    public Data() {
        // Required for Firebase
    }

    public Data(String title, String description, String skills, String salary, String id, String date) {
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.salary = salary;
        this.id = id;
        this.date = date;
    }

    // --- Standard Getters/Setters ---
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // --- Engagement Logic ---
    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes != null ? likes : new HashMap<>();
        this.likeCount = this.likes.size();
    }

    public Map<String, Boolean> getDislikes() {
        return dislikes;
    }

    public void setDislikes(Map<String, Boolean> dislikes) {
        this.dislikes = dislikes != null ? dislikes : new HashMap<>();
        this.dislikeCount = this.dislikes.size();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments != null ? comments : new ArrayList<>();
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    // --- Inner Comment Class ---
    public static class Comment {
        private String id;
        private String userId;
        private String userName;
        private String content;
        private long timestamp;

        public Comment() {
        }

        public Comment(String id, String userId, String userName, String content) {
            this.id = id;
            this.userId = userId;
            this.userName = userName;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }

        public String getId() {
            return id;
        }

        public String getUserName() {
            return userName;
        }

        public String getContent() {
            return content;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}