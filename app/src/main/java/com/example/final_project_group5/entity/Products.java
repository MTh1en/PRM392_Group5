package com.example.final_project_group5.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(
        tableName = "products",
        foreignKeys = {
                @ForeignKey(entity = Categories.class, parentColumns = "id", childColumns = "category_id", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("category_id")}
)
public class Products {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "rating")
    private double rating;

    @ColumnInfo(name = "discount")
    private Double discount;

    @ColumnInfo(name = "cores")
    private String cores;

    @ColumnInfo(name = "boost_clock")
    private String boostClock;

    @ColumnInfo(name = "memories")
    private String memories;

    @ColumnInfo(name = "tdp")
    private String tdp;

    @ColumnInfo(name = "category_id")
    private int categoryId;

    // Constructors
    public Products() {
    }

    public Products(int id, String name, String description, double price, String image, double rating, Double discount, String cores, String boostClock, String memories, String tdp, int categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.rating = rating;
        this.discount = discount;
        this.cores = cores;
        this.boostClock = boostClock;
        this.memories = memories;
        this.tdp = tdp;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getCores() {
        return cores;
    }

    public void setCores(String cores) {
        this.cores = cores;
    }

    public String getBoostClock() {
        return boostClock;
    }

    public void setBoostClock(String boostClock) {
        this.boostClock = boostClock;
    }

    public String getMemories() {
        return memories;
    }

    public void setMemories(String memories) {
        this.memories = memories;
    }

    public String getTdp() {
        return tdp;
    }

    public void setTdp(String tdp) {
        this.tdp = tdp;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
