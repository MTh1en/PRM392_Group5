package com.example.final_project_group5.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "feedbacks",
        foreignKeys = {
                @ForeignKey(entity = Users.class, parentColumns = "id", childColumns = "user_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Products.class, parentColumns = "id", childColumns = "product_id", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("user_id"), @Index("product_id")}
)
public class Feedbacks {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "product_id")
    private int productId;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "rating")
    private double rating;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "response")
    private String response;

    @ColumnInfo(name = "response_by")
    private int responseBy;

    public Feedbacks() {
    }

    public Feedbacks(int id, int userId, int productId, String content, double rating, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public Feedbacks(int id, int userId, int productId, String content, double rating, String createdAt, String response, int responseBy) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
        this.response = response;
        this.responseBy = responseBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getResponseBy() {
        return responseBy;
    }

    public void setResponseBy(int responseBy) {
        this.responseBy = responseBy;
    }
}
