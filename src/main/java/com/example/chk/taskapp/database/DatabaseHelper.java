package com.example.chk.taskapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TaskManagement.db";
    public static final int DATABASE_VERSION = 3;

    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_EMAIL = "email";

    public static final String TABLE_TASKS = "tasks";
    public static final String COL_TASK_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_CATEGORY = "category";
    public static final String COL_PRIORITY = "priority";
    public static final String COL_DUE_DATE = "due_date";
    public static final String COL_STATUS = "status";
    public static final String COL_TASK_USER_ID = "user_id";

    public static final String TABLE_CATEGORIES = "categories";
    public static final String COL_CAT_ID = "id";
    public static final String COL_CAT_NAME = "name";
    public static final String COL_CAT_COLOR = "color";
    public static final String COL_CAT_USER_ID = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL)";
        db.execSQL(createUsersTable);

        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_CATEGORY + " TEXT, " +
                COL_PRIORITY + " TEXT DEFAULT 'Trung Bình', " +
                COL_DUE_DATE + " TEXT, " +
                COL_STATUS + " TEXT DEFAULT 'Đang Chờ', " +
                COL_TASK_USER_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_TASK_USER_ID + ") REFERENCES " +
                TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createTasksTable);

        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                COL_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CAT_NAME + " TEXT NOT NULL, " +
                COL_CAT_COLOR + " TEXT, " +
                COL_CAT_USER_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_CAT_USER_ID + ") REFERENCES " +
                TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createCategoriesTable);

        insertTestData(db);
    }

    private void insertTestData(SQLiteDatabase db) {
        // Test users
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" +
                COL_USERNAME + ", " + COL_PASSWORD + ", " + COL_EMAIL + ") " +
                "VALUES ('testuser', 'password123', 'test@example.com')");

        db.execSQL("INSERT INTO " + TABLE_USERS + " (" +
                COL_USERNAME + ", " + COL_PASSWORD + ", " + COL_EMAIL + ") " +
                "VALUES ('admin', 'admin123', 'admin@example.com')");

        // Test tasks for user 1
        db.execSQL("INSERT INTO " + TABLE_TASKS + " (" +
                COL_TITLE + ", " + COL_DESCRIPTION + ", " + COL_CATEGORY + ", " +
                COL_PRIORITY + ", " + COL_DUE_DATE + ", " + COL_STATUS + ", " + COL_TASK_USER_ID + ") " +
                "VALUES ('Hoàn thành dự án Android', 'Hoàn thành dự án quản lý công việc', 'Công Việc', 'Cao', '2024-12-31', 'Đang Chờ', 1)");

        db.execSQL("INSERT INTO " + TABLE_TASKS + " (" +
                COL_TITLE + ", " + COL_DESCRIPTION + ", " + COL_CATEGORY + ", " +
                COL_PRIORITY + ", " + COL_DUE_DATE + ", " + COL_STATUS + ", " + COL_TASK_USER_ID + ") " +
                "VALUES ('Mua sắm hàng gia dụng', 'Mua đồ tạp hóa tại siêu thị', 'Mua Sắm', 'Thấp', '2024-12-25', 'Đang Chờ', 1)");

        db.execSQL("INSERT INTO " + TABLE_TASKS + " (" +
                COL_TITLE + ", " + COL_DESCRIPTION + ", " + COL_CATEGORY + ", " +
                COL_PRIORITY + ", " + COL_DUE_DATE + ", " + COL_STATUS + ", " + COL_TASK_USER_ID + ") " +
                "VALUES ('Học Spring Framework', 'Nghiên cứu Spring Boot và JPA', 'Học Tập', 'Trung Bình', '2024-12-30', 'Đang Chờ', 1)");

        db.execSQL("INSERT INTO " + TABLE_TASKS + " (" +
                COL_TITLE + ", " + COL_DESCRIPTION + ", " + COL_CATEGORY + ", " +
                COL_PRIORITY + ", " + COL_DUE_DATE + ", " + COL_STATUS + ", " + COL_TASK_USER_ID + ") " +
                "VALUES ('Khám sức khỏe định kỳ', 'Đi khám bác sĩ và kiểm tra sức khỏe', 'Sức Khỏe', 'Cao', '2024-12-20', 'Hoàn Thành', 1)");

        db.execSQL("INSERT INTO " + TABLE_TASKS + " (" +
                COL_TITLE + ", " + COL_DESCRIPTION + ", " + COL_CATEGORY + ", " +
                COL_PRIORITY + ", " + COL_DUE_DATE + ", " + COL_STATUS + ", " + COL_TASK_USER_ID + ") " +
                "VALUES ('Đi họp', 'Cách giải quyết tranh chấp', 'Công Việc', 'Cao', '2024-12-25', 'Đang Chờ', 1)");

        // Test task for user 2
        db.execSQL("INSERT INTO " + TABLE_TASKS + " (" +
                COL_TITLE + ", " + COL_DESCRIPTION + ", " + COL_CATEGORY + ", " +
                COL_PRIORITY + ", " + COL_DUE_DATE + ", " + COL_STATUS + ", " + COL_TASK_USER_ID + ") " +
                "VALUES ('Gọi điện cho khách hàng', 'Thảo luận về yêu cầu và giải pháp', 'Công Việc', 'Trung Bình', '2024-12-22', 'Đang Chờ', 2)");

        db.execSQL("INSERT INTO " + TABLE_TASKS + " (" +
                COL_TITLE + ", " + COL_DESCRIPTION + ", " + COL_CATEGORY + ", " +
                COL_PRIORITY + ", " + COL_DUE_DATE + ", " + COL_STATUS + ", " + COL_TASK_USER_ID + ") " +
                "VALUES ('Đi họp', 'Cách giải quyết tranh chấp', 'Công Việc', 'Cao', '2024-12-25', 'Đang Chờ', 2)");

        // Test categories
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" +
                COL_CAT_NAME + ", " + COL_CAT_COLOR + ", " + COL_CAT_USER_ID + ") " +
                "VALUES ('Công Việc', '#FF5252', 1)");

        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" +
                COL_CAT_NAME + ", " + COL_CAT_COLOR + ", " + COL_CAT_USER_ID + ") " +
                "VALUES ('Cá Nhân', '#4CAF50', 1)");

        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" +
                COL_CAT_NAME + ", " + COL_CAT_COLOR + ", " + COL_CAT_USER_ID + ") " +
                "VALUES ('Học Tập', '#2196F3', 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}