package com.example.chk.taskapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * ════════════════════════════════════════════════════════════════════
 * SessionManager - Quản lý session (phiên đăng nhập) của user
 * ════════════════════════════════════════════════════════════════════
 *
 * Chức năng:
 * - Lưu thông tin user khi đăng nhập
 * - Kiểm tra user đã login chưa
 * - Lấy thông tin user đang login
 * - Xóa session khi logout
 *
 * Lưu trữ: SharedPreferences (file XML local trên thiết bị)
 */
public class SessionManager {

    // ════════════════════════════════════════════════════════════════
    // PROPERTIES
    // ════════════════════════════════════════════════════════════════

    // SharedPreferences = Nơi lưu trữ key-value local
    private SharedPreferences preferences;

    // Editor = Tool để WRITE vào SharedPreferences
    private SharedPreferences.Editor editor;

    // ════════════════════════════════════════════════════════════════
    // CONSTANTS - Tên file và các keys
    // ════════════════════════════════════════════════════════════════

    // Tên file SharedPreferences
    private static final String PREF_NAME = "TaskAppPreferences";

    // Keys để lưu/đọc dữ liệu
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // ════════════════════════════════════════════════════════════════
    // CONSTRUCTOR - Khởi tạo SessionManager
    // ════════════════════════════════════════════════════════════════
    /**
     * Khởi tạo SessionManager
     *
     * @param context - Context của Activity/Application
     */
    public SessionManager(Context context) {
        // Lấy SharedPreferences với tên "TaskAppPreferences"
        // MODE_PRIVATE = Chỉ app này truy cập được
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Tạo Editor để write data
        editor = preferences.edit();
    }

    // ════════════════════════════════════════════════════════════════
    // 1. createLoginSession() - LƯU SESSION khi login thành công
    // ════════════════════════════════════════════════════════════════
    /**
     * Lưu thông tin user vào session
     * Gọi khi user đăng nhập thành công
     *
     * @param userId - ID của user
     * @param username - Tên đăng nhập
     * @param email - Email của user
     */
    public void createLoginSession(int userId, String username, String email) {
        // putInt() - Lưu int với key
        editor.putInt(KEY_USER_ID, userId);

        // putString() - Lưu String với key
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);

        // putBoolean() - Lưu boolean (đánh dấu đã login)
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // commit() - GHI VÀO FILE (quan trọng!)
        // Nếu không commit() → Data không được lưu
        editor.commit();
    }

    // KẾT QUẢ: File TaskAppPreferences.xml lưu:
    // <int name="user_id" value="1" />
    // <string name="username">john</string>
    // <string name="email">john@example.com</string>
    // <boolean name="is_logged_in" value="true" />

    // ════════════════════════════════════════════════════════════════
    // 2. isLoggedIn() - KIỂM TRA đã login chưa
    // ════════════════════════════════════════════════════════════════
    /**
     * Kiểm tra user đã đăng nhập chưa
     *
     * @return boolean - true nếu đã login, false nếu chưa
     */
    public boolean isLoggedIn() {
        // getBoolean(key, defaultValue)
        // - Đọc giá trị boolean với key "is_logged_in"
        // - Nếu không tìm thấy → return false (default)
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // VÍ DỤ:
    // Đã login → return true
    // Chưa login → return false
    // File không tồn tại → return false (default)

    // ════════════════════════════════════════════════════════════════
    // 3. getUserId() - LẤY USER ID từ session
    // ════════════════════════════════════════════════════════════════
    /**
     * Lấy ID của user đang login
     *
     * @return int - userId (hoặc -1 nếu chưa login)
     */
    public int getUserId() {
        // getInt(key, defaultValue)
        // - Đọc giá trị int với key "user_id"
        // - Nếu không tìm thấy → return -1 (default)
        return preferences.getInt(KEY_USER_ID, -1);
    }

    // VÍ DỤ:
    // Đã login → return 1 (hoặc ID thực)
    // Chưa login → return -1

    // ════════════════════════════════════════════════════════════════
    // 4. getUsername() - LẤY USERNAME từ session
    // ════════════════════════════════════════════════════════════════
    /**
     * Lấy username của user đang login
     *
     * @return String - username (hoặc "" nếu chưa login)
     */
    public String getUsername() {
        // getString(key, defaultValue)
        // - Đọc giá trị String với key "username"
        // - Nếu không tìm thấy → return "" (empty)
        return preferences.getString(KEY_USERNAME, "");
    }

    // ════════════════════════════════════════════════════════════════
    // 5. getEmail() - LẤY EMAIL từ session
    // ════════════════════════════════════════════════════════════════
    /**
     * Lấy email của user đang login
     *
     * @return String - email (hoặc "" nếu chưa login)
     */
    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    // ════════════════════════════════════════════════════════════════
    // 6. logout() - XÓA SESSION khi logout
    // ════════════════════════════════════════════════════════════════
    /**
     * Xóa tất cả dữ liệu session
     * Gọi khi user đăng xuất
     */
    public void logout() {
        // clear() - Xóa TẤT CẢ dữ liệu trong SharedPreferences
        editor.clear();

        // commit() - Ghi thay đổi vào file
        editor.commit();
    }

    // KẾT QUẢ: File TaskAppPreferences.xml trở thành rỗng
    // User phải đăng nhập lại lần sau
}