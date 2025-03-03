package com.example.final_project_group5.entity;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.final_project_group5.dao.CartsDAO;
import com.example.final_project_group5.dao.CategoriesDAO;
import com.example.final_project_group5.dao.FeedbacksDAO;
import com.example.final_project_group5.dao.OrderDetailsDAO;
import com.example.final_project_group5.dao.OrdersDAO;
import com.example.final_project_group5.dao.ProductsDAO;
import com.example.final_project_group5.dao.UsersDAO;

@Database(
        entities = {Orders.class, Users.class, Products.class, Carts.class, Feedbacks.class, Categories.class, OrderDetails.class},
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract OrdersDAO ordersDao();
    public abstract OrderDetailsDAO OrderDetailsDao();
    public abstract UsersDAO usersDao();
    public abstract ProductsDAO productsDao();
    public abstract CartsDAO cartsDao();
    public abstract FeedbacksDAO feedbacksDao();
    public abstract CategoriesDAO categoriesDao();
}
