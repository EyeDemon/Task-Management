package com.example.chk.taskapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ════════════════════════════════════════════════════════════════════
 * DateUtils - Class chứa các methods tiện ích xử lý ngày tháng
 * ════════════════════════════════════════════════════════════════════
 *
 * Chức năng:
 * - Format ngày từ định dạng này sang định dạng khác
 * - Kiểm tra ngày quá hạn
 * - Tính số ngày còn lại
 * - Kiểm tra ngày hôm nay
 *
 * Tất cả methods đều STATIC → Gọi trực tiếp: DateUtils.formatDate()
 */
public class DateUtils {

    // ════════════════════════════════════════════════════════════════
    // 1. formatDate() - Format ngày từ yyyy-MM-dd sang dd/MM/yyyy
    // ════════════════════════════════════════════════════════════════
    /**
     * Chuyển đổi: "2024-12-25" → "25/12/2024"
     *
     * @param dateString - Ngày dạng "yyyy-MM-dd" (từ database)
     * @return String - Ngày dạng "dd/MM/yyyy" (hiển thị cho user)
     */
    public static String formatDate(String dateString) {
        try {
            // SimpleDateFormat = Class format ngày tháng

            // Input format: yyyy-MM-dd (ví dụ: 2024-12-25)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            // Output format: dd/MM/yyyy (ví dụ: 25/12/2024)
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Parse string → Date object
            Date date = inputFormat.parse(dateString);

            // Format Date → string mới
            return outputFormat.format(date);

        } catch (Exception e) {
            // Nếu lỗi (format sai) → return string gốc
            return dateString;
        }
    }

    // VÍ DỤ:
    // Input:  "2024-12-25"
    // Output: "25/12/2024"

    // ════════════════════════════════════════════════════════════════
    // 2. getTodayDate() - Lấy ngày hôm nay
    // ════════════════════════════════════════════════════════════════
    /**
     * Lấy ngày hôm nay theo định dạng yyyy-MM-dd
     *
     * @return String - Ngày hôm nay (ví dụ: "2024-12-22")
     */
    public static String getTodayDate() {
        // SimpleDateFormat với format yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // new Date() = Ngày giờ hiện tại
        // format() = Chuyển Date → String
        return sdf.format(new Date());
    }

    // VÍ DỤ:
    // Hôm nay là 22/12/2024
    // Return: "2024-12-22"

    // ════════════════════════════════════════════════════════════════
    // 3. getDaysRemaining() - Tính số ngày còn lại
    // ════════════════════════════════════════════════════════════════
    /**
     * Tính số ngày còn lại đến due date
     *
     * @param dueDate - Ngày hạn chót (yyyy-MM-dd)
     * @return long - Số ngày còn lại
     *               > 0: Còn X ngày
     *               = 0: Hôm nay
     *               < 0: Quá hạn X ngày
     */
    public static long getDaysRemaining(String dueDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            // Parse string → Date object
            Date dueDateObj = sdf.parse(dueDate);

            // Ngày hôm nay
            Date today = new Date();

            // getTime() = Lấy timestamp (milliseconds từ 1/1/1970)
            // Tính chênh lệch milliseconds
            long diffTime = dueDateObj.getTime() - today.getTime();

            // Chuyển milliseconds → days
            // 1 ngày = 1000ms × 60s × 60m × 24h = 86,400,000ms
            return diffTime / (1000 * 60 * 60 * 24);

        } catch (Exception e) {
            // Nếu lỗi → return -1 (coi như quá hạn)
            return -1;
        }
    }

    // VÍ DỤ:
    // Hôm nay: 2024-12-22
    // Due date: 2024-12-25
    // Return: 3 (còn 3 ngày)
    //
    // Due date: 2024-12-20
    // Return: -2 (quá hạn 2 ngày)

    // ════════════════════════════════════════════════════════════════
    // 4. isOverdue() - Kiểm tra quá hạn
    // ════════════════════════════════════════════════════════════════
    /**
     * Kiểm tra task có quá hạn không
     *
     * @param dueDate - Ngày hạn chót
     * @return boolean - true nếu quá hạn, false nếu chưa
     */
    public static boolean isOverdue(String dueDate) {
        // Gọi getDaysRemaining()
        // Nếu < 0 → Quá hạn
        return getDaysRemaining(dueDate) < 0;
    }

    // VÍ DỤ:
    // Due date: 2024-12-20 (đã qua)
    // Return: true (quá hạn)
    //
    // Due date: 2024-12-25 (chưa đến)
    // Return: false (chưa quá hạn)

    // ════════════════════════════════════════════════════════════════
    // 5. isToday() - Kiểm tra có phải hôm nay không
    // ════════════════════════════════════════════════════════════════
    /**
     * Kiểm tra date có phải là hôm nay không
     *
     * @param dateString - Ngày cần kiểm tra
     * @return boolean - true nếu là hôm nay
     */
    public static boolean isToday(String dateString) {
        // So sánh với ngày hôm nay
        return dateString.equals(getTodayDate());
    }

    // VÍ DỤ:
    // Hôm nay: 2024-12-22
    // Input: "2024-12-22"
    // Return: true
    //
    // Input: "2024-12-25"
    // Return: false

    // ════════════════════════════════════════════════════════════════
    // 6. getMonthYear() - Lấy tháng và năm
    // ════════════════════════════════════════════════════════════════
    /**
     * Chuyển đổi: "2024-12-25" → "December 2024"
     *
     * @param dateString - Ngày dạng yyyy-MM-dd
     * @return String - Tháng và năm (ví dụ: "December 2024")
     */
    public static String getMonthYear(String dateString) {
        try {
            // Input format: yyyy-MM-dd
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            // Output format: MMMM yyyy (tháng đầy đủ + năm)
            // MMMM = Tháng đầy đủ (January, February...)
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

            // Parse và format
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);

        } catch (Exception e) {
            return dateString;
        }
    }

    // VÍ DỤ:
    // Input:  "2024-12-25"
    // Output: "December 2024" (tiếng Anh)
    // Hoặc:   "Tháng 12 2024" (tiếng Việt, tùy Locale)
}