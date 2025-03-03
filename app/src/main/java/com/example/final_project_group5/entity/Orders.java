package com.example.final_project_group5.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "orders",
        foreignKeys = {
                @ForeignKey(entity = Users.class, parentColumns = "id", childColumns = "user_id", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("user_id")}
)
public class Orders {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "order_date")
    private String orderDate;

    @ColumnInfo(name = "total_price")
    private double totalPrice;

    @ColumnInfo(name = "status")
    private String status;

    public Orders() {
    }

    public Orders(int id, int userId, String orderDate, double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.status = status;
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
