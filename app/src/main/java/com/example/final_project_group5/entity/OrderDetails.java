package com.example.final_project_group5.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "order_details",
        foreignKeys = {
                @ForeignKey(entity = Orders.class, parentColumns = "id", childColumns = "order_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Products.class, parentColumns = "id", childColumns = "product_id", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("order_id"), @Index("product_id")}
)
public class OrderDetails {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "order_id")
    private int orderId;

    @ColumnInfo(name = "product_id")
    private int productId;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "price")
    private double price;

    public OrderDetails() {
    }

    public OrderDetails(int id, int orderId, int productId, int quantity, double price) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
