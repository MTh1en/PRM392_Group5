package com.example.final_project_group5.entity;

public class Feedback {
    private String id;
    private int userId;
    private int productId;
    private String title;
    private String comment;
    private float rating;
    private String createAt;

    public Feedback() {
    }

    public Feedback(String id, int userId, int productId, String title, String comment, float rating, String createAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.title = title;
        this.comment = comment;
        this.rating = rating;
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
