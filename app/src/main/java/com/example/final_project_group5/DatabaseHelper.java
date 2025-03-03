package com.example.final_project_group5;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "electronics_store.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "address TEXT, " +
                "phone TEXT, " +
                "role INTEGER NOT NULL CHECK (role IN(1,2,3)))");

        db.execSQL("CREATE TABLE Categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Carts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INT NOT NULL, " +
                "product_id INT NOT NULL," +
                "quantity INT NOT NULL," +
                "addedAt TEXT," +
                "FOREIGN KEY (user_id) REFERENCES Users(id)," +
                "FOREIGN KEY (product_id) REFERENCES Products(id))");

        db.execSQL("CREATE TABLE Products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "image TEXT NOT NULL, " +
                "rating REAL NOT NULL," +
                "discount REAL," +
                "cores TEXT," +
                "boost_clock TEXT," +
                "memories TEXT," +
                "tdp TEXT," +
                "category_id INTEGER, " +
                "FOREIGN KEY (category_id) REFERENCES Categories(id))");

        db.execSQL("CREATE TABLE Orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "order_date TEXT NOT NULL, " +
                "total_price REAL NOT NULL, " +
                "status TEXT NOT NULL CHECK(status IN('PENDING', 'COMPLETED')), " +
                "FOREIGN KEY (user_id) REFERENCES Users(id))");

        db.execSQL("CREATE TABLE Feedbacks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "content TEXT NOT NULL, " +
                "rating REAL NOT NULL, " +
                "created_at TEXT NOT NULL," +
                "response TEXT NOT NULL," +
                "response_by INTEGER NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES Users(id), " +
                "FOREIGN KEY (product_id) REFERENCES Products(id))");

        db.execSQL("CREATE TABLE OrderDetails (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "price REAL NOT NULL, " +
                "FOREIGN KEY (order_id) REFERENCES Orders(id), " +
                "FOREIGN KEY (product_id) REFERENCES Products(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng cũ nếu cần
        db.execSQL("DROP TABLE IF EXISTS OrderDetails");
        db.execSQL("DROP TABLE IF EXISTS Orders");
        db.execSQL("DROP TABLE IF EXISTS Products");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Carts");
        db.execSQL("DROP TABLE IF EXISTS Feedbacks");

        // Tạo lại cơ sở dữ liệu
        onCreate(db);
    }
}
