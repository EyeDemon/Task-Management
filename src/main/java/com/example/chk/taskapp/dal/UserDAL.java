package com.example.chk.taskapp.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.chk.taskapp.database.DatabaseHelper;
import com.example.chk.taskapp.models.User;

public class UserDAL {
    private DatabaseHelper dbHelper;

    public UserDAL(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean registerUser(String username, String password, String email) {
        if (usernameExists(username) || emailExists(email)) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_USERNAME, username);
            values.put(DatabaseHelper.COL_PASSWORD, password);
            values.put(DatabaseHelper.COL_EMAIL, email);

            result = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        } finally {
            db.close();
        }

        return result != -1;
    }

    public User loginUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        User user = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COL_USERNAME + " = ? AND " + DatabaseHelper.COL_PASSWORD + " = ?",
                    new String[]{username, password},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL));
                user = new User(id, username, password, email);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return user;
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COL_USERNAME + " = ?",
                    new String[]{username},
                    null,
                    null,
                    null
            );

            exists = cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return exists;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COL_EMAIL + " = ?",
                    new String[]{email},
                    null,
                    null,
                    null
            );

            exists = cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return exists;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        User user = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PASSWORD));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL));
                user = new User(userId, username, password, email);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return user;
    }

    public boolean updateUser(int userId, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_EMAIL, email);
            values.put(DatabaseHelper.COL_PASSWORD, password);

            result = db.update(
                    DatabaseHelper.TABLE_USERS,
                    values,
                    DatabaseHelper.COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
        } finally {
            db.close();
        }

        return result > 0;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;

        try {
            result = db.delete(
                    DatabaseHelper.TABLE_USERS,
                    DatabaseHelper.COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
        } finally {
            db.close();
        }

        return result > 0;
    }

    public void closeDatabase() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}