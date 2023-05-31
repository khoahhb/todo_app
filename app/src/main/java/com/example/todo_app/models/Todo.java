package com.example.todo_app.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "todos")
public class Todo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String status;
    private String createdDate;
    private String completedDate;

    public Todo() { }

    public Todo(String title, String description, String status, String createdDate, String completedDate) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
        this.completedDate = completedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Todo todoTemp = (Todo) obj;
        assert todoTemp != null;
        return this.id == todoTemp.id &&
                this.title.equals(todoTemp.title) &&
                this.description.equals(todoTemp.description) &&
                this.status.equals(todoTemp.status) &&
                this.createdDate.equals(todoTemp.createdDate) &&
                this.completedDate.equals(todoTemp.completedDate);
    }

    @NonNull
    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", completedDate='" + completedDate + '\'' +
                '}';
    }
}