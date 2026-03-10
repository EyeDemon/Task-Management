package com.example.chk.taskapp.models;

import com.example.chk.taskapp.utils.Constants;

public class Task {
    private int id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String dueDate;
    private String status;
    private int userId;

    public Task(int id, String title, String description, String category,
                String priority, String dueDate, String status, int userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.userId = userId;
    }

    public Task(String title, String description, String category,
                String priority, String dueDate, int userId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = Constants.STATUS_PENDING;
        this.userId = userId;
    }

    public Task() {
        this.status = Constants.STATUS_PENDING;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getPriority() {
        return priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public int getUserId() {
        return userId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isCompleted() {
        return Constants.STATUS_COMPLETED.equals(status);
    }

    public void markAsCompleted() {
        this.status = Constants.STATUS_COMPLETED;
    }

    public void markAsPending() {
        this.status = Constants.STATUS_PENDING;
    }
}