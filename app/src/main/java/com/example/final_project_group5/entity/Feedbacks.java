package com.example.final_project_group5.entity;

public class Feedbacks {
    private int id;
    private int userId;
    private int productId;
    private String content;
    private double rating;
    private String createdAt;
    private String response;
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
