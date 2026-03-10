package com.example.chk.taskapp.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.chk.taskapp.database.DatabaseHelper;
import com.example.chk.taskapp.models.Category;
import java.util.ArrayList;
import java.util.List;


public class CategoryDAL {

    // ════════════════════════════════════════════════════════════════
    // PROPERTY - DatabaseHelper instance
    // ════════════════════════════════════════════════════════════════
    // COMPOSITION: CategoryDAL "có một" DatabaseHelper
    private DatabaseHelper dbHelper;

    // ════════════════════════════════════════════════════════════════
    // CONSTRUCTOR - Khởi tạo DatabaseHelper
    // ════════════════════════════════════════════════════════════════
    public CategoryDAL(Context context) {
        // Tạo DatabaseHelper với context
        // DatabaseHelper sẽ tạo/mở database
        dbHelper = new DatabaseHelper(context);
    }

    // ════════════════════════════════════════════════════════════════
    // 1. addCategory() - THÊM DANH MỤC MỚI (CREATE)
    // ════════════════════════════════════════════════════════════════
    /**
     * Thêm category mới vào database
     *
     * @param category - Category object cần thêm
     * @return long - ID của category mới (> 0 nếu thành công, -1 nếu thất bại)
     */
    public long addCategory(Category category) {
        // Lấy database ở chế độ WRITE (ghi)
        // getWritableDatabase() - method từ DatabaseHelper (kế thừa từ SQLiteOpenHelper)
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Biến lưu kết quả, mặc định -1 (thất bại)
        long result = -1;

        try {
            // ContentValues = Cặp key-value để insert vào database
            // Giống HashMap: key = tên cột, value = giá trị
            ContentValues values = new ContentValues();

            // Thêm dữ liệu vào ContentValues
            values.put(DatabaseHelper.COL_CAT_NAME, category.getName());     // name
            values.put(DatabaseHelper.COL_CAT_COLOR, category.getColor());   // color
            values.put(DatabaseHelper.COL_CAT_USER_ID, category.getUserId()); // user_id

            // INSERT INTO categories (name, color, user_id) VALUES (?, ?, ?)
            // db.insert() return: row ID nếu thành công, -1 nếu lỗi
            result = db.insert(
                    DatabaseHelper.TABLE_CATEGORIES,  // Tên table: "categories"
                    null,                             // nullColumnHack (không dùng)
                    values                            // Dữ liệu cần insert
            );
        } finally {
            // QUAN TRỌNG: Luôn đóng database sau khi dùng
            // Tránh memory leak và lock database
            db.close();
        }

        return result;  // Trả về ID của category mới
    }

    public List<Category> getAllCategoriesByUserId(int userId) {
        // Tạo ArrayList để chứa kết quả
        List<Category> categoryList = new ArrayList<>();

        // Lấy database ở chế độ READ (đọc)
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Cursor = Con trỏ trỏ đến kết quả truy vấn
        // Giống iterator, dùng để duyệt qua từng row
        Cursor cursor = null;

        try {
            // SELECT * FROM categories WHERE user_id = ? ORDER BY id ASC
            cursor = db.query(
                    DatabaseHelper.TABLE_CATEGORIES,           // Table: "categories"
                    null,                                      // Columns: null = SELECT *
                    DatabaseHelper.COL_CAT_USER_ID + " = ?",  // WHERE: user_id = ?
                    new String[]{String.valueOf(userId)},      // Values: [userId]
                    null,                                      // GROUP BY: null
                    null,                                      // HAVING: null
                    DatabaseHelper.COL_CAT_ID + " ASC"        // ORDER BY: id ASC
            );

            // Di chuyển cursor đến row đầu tiên
            // moveToFirst() return true nếu có dữ liệu
            if (cursor.moveToFirst()) {
                // Duyệt qua tất cả rows
                do {
                    // Tạo Category object từ dữ liệu trong cursor
                    Category category = new Category(
                            // getInt() - Lấy giá trị int từ cột
                            // getColumnIndexOrThrow() - Lấy index của cột (throw exception nếu không tìm thấy)
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_ID)),

                            // getString() - Lấy giá trị string từ cột
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_COLOR)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_USER_ID))
                    );

                    // Thêm category vào list
                    categoryList.add(category);

                    // moveToNext() - Di chuyển đến row tiếp theo
                    // Return false khi hết dữ liệu
                } while (cursor.moveToNext());
            }
        } finally {
            // Đóng cursor nếu không null
            if (cursor != null) {
                cursor.close();
            }
            // Đóng database
            db.close();
        }

        return categoryList;  // Trả về danh sách categories
    }


    public Category getCategoryById(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Category category = null;  // Mặc định null (không tìm thấy)

        try {
            // SELECT * FROM categories WHERE id = ?
            cursor = db.query(
                    DatabaseHelper.TABLE_CATEGORIES,
                    null,
                    DatabaseHelper.COL_CAT_ID + " = ?",      // WHERE id = ?
                    new String[]{String.valueOf(categoryId)},
                    null,
                    null,
                    null
            );

            // Chỉ lấy row đầu tiên (vì ID là unique)
            if (cursor.moveToFirst()) {
                category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_COLOR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_USER_ID))
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return category;  // Trả về Category hoặc null
    }

    // ════════════════════════════════════════════════════════════════
    // 4. updateCategory() - CẬP NHẬT DANH MỤC (UPDATE)
    // ════════════════════════════════════════════════════════════════
    /**
     * Cập nhật thông tin category
     *
     * @param category - Category object với dữ liệu mới
     * @return boolean - true nếu thành công, false nếu thất bại
     */
    public boolean updateCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;  // Số rows bị ảnh hưởng

        try {
            // Tạo ContentValues với dữ liệu mới
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_CAT_NAME, category.getName());
            values.put(DatabaseHelper.COL_CAT_COLOR, category.getColor());

            // UPDATE categories SET name=?, color=? WHERE id=?
            result = db.update(
                    DatabaseHelper.TABLE_CATEGORIES,          // Table
                    values,                                   // Dữ liệu mới
                    DatabaseHelper.COL_CAT_ID + " = ?",      // WHERE clause
                    new String[]{String.valueOf(category.getId())}  // WHERE values
            );
            // db.update() return: số rows bị update
        } finally {
            db.close();
        }

        // result > 0 nghĩa là có ít nhất 1 row bị update
        return result > 0;
    }

    // ════════════════════════════════════════════════════════════════
    // 5. deleteCategory() - XÓA DANH MỤC (DELETE)
    // ════════════════════════════════════════════════════════════════
    /**
     * Xóa category theo ID
     *
     * @param categoryId - ID của category cần xóa
     * @return boolean - true nếu thành công, false nếu thất bại
     */
    public boolean deleteCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;  // Số rows bị xóa

        try {
            // DELETE FROM categories WHERE id = ?
            result = db.delete(
                    DatabaseHelper.TABLE_CATEGORIES,          // Table
                    DatabaseHelper.COL_CAT_ID + " = ?",      // WHERE clause
                    new String[]{String.valueOf(categoryId)}  // WHERE values
            );
            // db.delete() return: số rows bị xóa
        } finally {
            db.close();
        }

        return result > 0;
    }

    // ════════════════════════════════════════════════════════════════
    // 6. getCategoryByName() - TÌM DANH MỤC THEO TÊN (READ)
    // ════════════════════════════════════════════════════════════════
    /**
     * Tìm category theo tên và user ID
     *
     * @param userId - ID của user
     * @param categoryName - Tên category cần tìm
     * @return Category - Category object (null nếu không tìm thấy)
     */
    public Category getCategoryByName(int userId, String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Category category = null;

        try {
            // SELECT * FROM categories WHERE user_id = ? AND name = ?
            cursor = db.query(
                    DatabaseHelper.TABLE_CATEGORIES,
                    null,
                    // WHERE với 2 điều kiện (AND)
                    DatabaseHelper.COL_CAT_USER_ID + " = ? AND " + DatabaseHelper.COL_CAT_NAME + " = ?",
                    new String[]{String.valueOf(userId), categoryName},  // 2 values cho 2 ?
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_COLOR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_USER_ID))
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return category;
    }

    // ════════════════════════════════════════════════════════════════
    // 7. categoryExists() - KIỂM TRA TỒN TẠI (READ)
    // ════════════════════════════════════════════════════════════════
    /**
     * Kiểm tra category có tồn tại không
     *
     * @param userId - ID của user
     * @param categoryName - Tên category
     * @return boolean - true nếu tồn tại, false nếu không
     */
    public boolean categoryExists(int userId, String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;  // Mặc định không tồn tại

        try {
            // SELECT * FROM categories WHERE user_id = ? AND name = ?
            cursor = db.query(
                    DatabaseHelper.TABLE_CATEGORIES,
                    null,
                    DatabaseHelper.COL_CAT_USER_ID + " = ? AND " + DatabaseHelper.COL_CAT_NAME + " = ?",
                    new String[]{String.valueOf(userId), categoryName},
                    null,
                    null,
                    null
            );

            // getCount() - Đếm số rows trong kết quả
            // > 0 nghĩa là có tồn tại
            exists = cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return exists;
    }

    // ════════════════════════════════════════════════════════════════
    // 8. getCategoryCount() - ĐẾM SỐ LƯỢNG (READ)
    // ════════════════════════════════════════════════════════════════
    /**
     * Đếm số lượng categories của user
     *
     * @param userId - ID của user
     * @return int - Số lượng categories
     */
    public int getCategoryCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;  // Mặc định 0

        try {
            // SELECT * FROM categories WHERE user_id = ?
            cursor = db.query(
                    DatabaseHelper.TABLE_CATEGORIES,
                    null,
                    DatabaseHelper.COL_CAT_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    null
            );

            // Đếm số rows
            count = cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return count;
    }

    // ════════════════════════════════════════════════════════════════
    // 9. closeDatabase() - ĐÓNG DATABASE
    // ════════════════════════════════════════════════════════════════
    /**
     * Đóng DatabaseHelper
     * Thường gọi khi Activity/Fragment bị destroy
     */
    public void closeDatabase() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}