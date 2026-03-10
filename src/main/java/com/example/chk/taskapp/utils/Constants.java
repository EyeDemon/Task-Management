package com.example.chk.taskapp.utils;

import android.graphics.Color;

/**
 * ════════════════════════════════════════════════════════════════════
 * Constants - Class chứa các HẰNG SỐ dùng chung trong toàn bộ app
 * ════════════════════════════════════════════════════════════════════
 *
 * Tại sao cần Constants?
 * - Tránh hard-code giá trị trong code
 * - Dễ bảo trì: Chỉ sửa 1 chỗ thay vì sửa nhiều chỗ
 * - Tránh lỗi typo (viết sai chính tả)
 * - Code dễ đọc, dễ hiểu hơn
 *
 * Ví dụ:
 * ❌ BAD:  if (status.equals("Đang Chờ"))  // Dễ viết sai
 * ✅ GOOD: if (status.equals(Constants.STATUS_PENDING))  // IDE gợi ý, không sai
 */
public class Constants {

    // ════════════════════════════════════════════════════════════════
    // 1. MỨC ƯU TIÊN (Priority Levels)
    // ════════════════════════════════════════════════════════════════
    // Dùng trong: Spinner priority, hiển thị task, filter

    public static final String PRIORITY_HIGH = "Cao";
    public static final String PRIORITY_MEDIUM = "Trung Bình";
    public static final String PRIORITY_LOW = "Thấp";

    // public static final:
    // - public: Truy cập từ mọi nơi
    // - static: Thuộc về class, không cần tạo object
    // - final: Không thể thay đổi (hằng số)

    // ════════════════════════════════════════════════════════════════
    // 2. TRẠNG THÁI TASK (Task Status)
    // ════════════════════════════════════════════════════════════════
    // Dùng trong: Database, filter, hiển thị task

    public static final String STATUS_PENDING = "Đang Chờ";      // Chưa hoàn thành
    public static final String STATUS_COMPLETED = "Hoàn Thành";  // Đã hoàn thành

    // ════════════════════════════════════════════════════════════════
    // 3. DANH MỤC TASK (Categories)
    // ════════════════════════════════════════════════════════════════
    // Dùng trong: Spinner category, database, hiển thị task

    public static final String CATEGORY_WORK = "Công Việc";      // Work
    public static final String CATEGORY_STUDY = "Học Tập";       // Study
    public static final String CATEGORY_PERSONAL = "Cá Nhân";    // Personal
    public static final String CATEGORY_HEALTH = "Sức Khỏe";     // Health
    public static final String CATEGORY_SHOPPING = "Mua Sắm";    // Shopping

    // ════════════════════════════════════════════════════════════════
    // 4. MÀU ƯU TIÊN (Priority Colors)
    // ════════════════════════════════════════════════════════════════
    // Dùng trong: TaskAdapter để hiển thị thanh màu priority

    public static final int COLOR_PRIORITY_HIGH = Color.parseColor("#FF5252");
    // #FF5252 = Đỏ (Red) cho priority cao

    public static final int COLOR_PRIORITY_MEDIUM = Color.parseColor("#FFC107");
    // #FFC107 = Vàng (Amber) cho priority trung bình

    public static final int COLOR_PRIORITY_LOW = Color.parseColor("#4CAF50");
    // #4CAF50 = Xanh lá (Green) cho priority thấp

    // Color.parseColor(): Chuyển hex color string → int color

    // ════════════════════════════════════════════════════════════════
    // 5. INTENT EXTRA KEYS
    // ════════════════════════════════════════════════════════════════
    // Dùng khi truyền data giữa các Activity qua Intent

    public static final String EXTRA_TASK_ID = "task_id";
    // Key để truyền task ID qua Intent
    // Ví dụ: intent.putExtra(Constants.EXTRA_TASK_ID, 123)

    public static final String EXTRA_USER_ID = "user_id";
    // Key để truyền user ID qua Intent

    public static final String EXTRA_TASK_OBJECT = "task_object";
    // Key để truyền cả Task object qua Intent (nếu cần)

    // ════════════════════════════════════════════════════════════════
    // 6. VALIDATION RULES
    // ════════════════════════════════════════════════════════════════
    // Dùng trong: RegisterActivity, validation input

    public static final int MIN_USERNAME_LENGTH = 3;
    // Username phải ít nhất 3 ký tự

    public static final int MIN_PASSWORD_LENGTH = 6;
    // Password phải ít nhất 6 ký tự

    // ════════════════════════════════════════════════════════════════
    // 7. MESSAGES (Thông báo)
    // ════════════════════════════════════════════════════════════════
    // Dùng trong: Toast.makeText() để hiển thị thông báo

    public static final String MSG_USERNAME_EXISTS = "Tên đăng nhập đã tồn tại";
    // Hiển thị khi đăng ký mà username đã có trong DB

    public static final String MSG_INVALID_INPUT = "Vui lòng điền đầy đủ thông tin";
    // Hiển thị khi user để trống field

    public static final String MSG_PASSWORD_MISMATCH = "Mật khẩu không khớp";
    // Hiển thị khi password và confirm password khác nhau

    public static final String MSG_LOGIN_FAILED = "Tên đăng nhập hoặc mật khẩu sai";
    // Hiển thị khi login thất bại

    public static final String MSG_TASK_ADDED = "Công việc đã được thêm";
    // Hiển thị khi thêm task thành công

    public static final String MSG_TASK_UPDATED = "Công việc đã được cập nhật";
    // Hiển thị khi update task thành công

    public static final String MSG_TASK_DELETED = "Công việc đã được xóa";
    // Hiển thị khi xóa task thành công
}