package com.example.chk.taskapp.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.chk.taskapp.database.DatabaseHelper;
import com.example.chk.taskapp.models.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskDAL {
    private DatabaseHelper dbHelper;

    public TaskDAL(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_TITLE, task.getTitle());
            values.put(DatabaseHelper.COL_DESCRIPTION, task.getDescription());
            values.put(DatabaseHelper.COL_CATEGORY, task.getCategory());
            values.put(DatabaseHelper.COL_PRIORITY, task.getPriority());
            values.put(DatabaseHelper.COL_DUE_DATE, task.getDueDate());
            values.put(DatabaseHelper.COL_STATUS, task.getStatus());
            values.put(DatabaseHelper.COL_TASK_USER_ID, task.getUserId());

            result = db.insert(DatabaseHelper.TABLE_TASKS, null, values);
        } finally {
            db.close();
        }

        return result;
    }

    public List<Task> getAllTasksByUserId(int userId) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_TASKS,
                    null,
                    DatabaseHelper.COL_TASK_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    DatabaseHelper.COL_TASK_ID + " DESC"
            );

            if (cursor.moveToFirst()) {
                do {
                    Task task = new Task(
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRIORITY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DUE_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_USER_ID))
                    );
                    taskList.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return taskList;
    }

    public Task getTaskById(int taskId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Task task = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_TASKS,
                    null,
                    DatabaseHelper.COL_TASK_ID + " = ?",
                    new String[]{String.valueOf(taskId)},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRIORITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DUE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_USER_ID))
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return task;
    }

    public boolean updateTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_TITLE, task.getTitle());
            values.put(DatabaseHelper.COL_DESCRIPTION, task.getDescription());
            values.put(DatabaseHelper.COL_CATEGORY, task.getCategory());
            values.put(DatabaseHelper.COL_PRIORITY, task.getPriority());
            values.put(DatabaseHelper.COL_DUE_DATE, task.getDueDate());
            values.put(DatabaseHelper.COL_STATUS, task.getStatus());

            result = db.update(
                    DatabaseHelper.TABLE_TASKS,
                    values,
                    DatabaseHelper.COL_TASK_ID + " = ?",
                    new String[]{String.valueOf(task.getId())}
            );
        } finally {
            db.close();
        }

        return result > 0;
    }

    public boolean updateTaskStatus(int taskId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_STATUS, status);

            result = db.update(
                    DatabaseHelper.TABLE_TASKS,
                    values,
                    DatabaseHelper.COL_TASK_ID + " = ?",
                    new String[]{String.valueOf(taskId)}
            );
        } finally {
            db.close();
        }

        return result > 0;
    }

    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;

        try {
            result = db.delete(
                    DatabaseHelper.TABLE_TASKS,
                    DatabaseHelper.COL_TASK_ID + " = ?",
                    new String[]{String.valueOf(taskId)}
            );
        } finally {
            db.close();
        }

        return result > 0;
    }

    public List<Task> getTasksByCategory(int userId, String category) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_TASKS,
                    null,
                    DatabaseHelper.COL_TASK_USER_ID + " = ? AND " + DatabaseHelper.COL_CATEGORY + " = ?",
                    new String[]{String.valueOf(userId), category},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                do {
                    Task task = new Task(
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRIORITY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DUE_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_USER_ID))
                    );
                    taskList.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return taskList;
    }

    public List<Task> getPendingTasks(int userId) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_TASKS,
                    null,
                    DatabaseHelper.COL_TASK_USER_ID + " = ? AND " + DatabaseHelper.COL_STATUS + " = ?",
                    new String[]{String.valueOf(userId), "Đang Chờ"},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                do {
                    Task task = new Task(
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRIORITY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DUE_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_USER_ID))
                    );
                    taskList.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return taskList;
    }

    public void closeDatabase() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}