package com.example.chk.taskapp.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chk.taskapp.R;
import com.example.chk.taskapp.dal.TaskDAL;
import com.example.chk.taskapp.models.Task;
import com.example.chk.taskapp.utils.Constants;
import java.util.Calendar;

/**
 * ════════════════════════════════════════════════════════════════════
 * EditTaskActivity - Màn hình chỉnh sửa task
 * ════════════════════════════════════════════════════════════════════
 *
 * KẾ THỪA: extends AppCompatActivity
 *
 * Tại sao kế thừa?
 * - AppCompatActivity cung cấp lifecycle methods (onCreate, onResume...)
 * - Cung cấp các methods: setContentView(), findViewById(), finish()...
 * - Hỗ trợ Material Design và backward compatibility
 *
 * Chức năng:
 * - Hiển thị thông tin task hiện tại
 * - Cho phép user chỉnh sửa
 * - Cập nhật task vào database
 */
public class EditTaskActivity extends AppCompatActivity {

    // ════════════════════════════════════════════════════════════════
    // PROPERTIES - UI Components
    // ════════════════════════════════════════════════════════════════
    private EditText etTaskTitle;        // Input tiêu đề
    private EditText etTaskDescription;  // Input mô tả
    private Spinner spinnerCategory;     // Dropdown chọn category
    private Spinner spinnerPriority;     // Dropdown chọn priority
    private Spinner spinnerStatus;       // Dropdown chọn status
    private EditText etDueDate;          // Input due date (dùng DatePicker)
    private Button btnUpdateTask;        // Button cập nhật
    private Button btnCancel;            // Button hủy

    // ════════════════════════════════════════════════════════════════
    // PROPERTIES - Data & Logic
    // ════════════════════════════════════════════════════════════════
    private TaskDAL taskDAL;         // Để tương tác với database
    private Task currentTask;        // Task đang được edit
    private String selectedDate = "";// Ngày được chọn từ DatePicker
    private int taskId;              // ID của task (nhận từ Intent)

    /**
     * ════════════════════════════════════════════════════════════════
     * onCreate() - OVERRIDE từ AppCompatActivity
     * ════════════════════════════════════════════════════════════════
     *
     * Lifecycle Method:
     * - Được gọi khi Activity được tạo lần đầu
     * - Khởi tạo UI và logic
     *
     * Flow:
     * 1. Setup UI (findViewById)
     * 2. Nhận taskId từ Intent
     * 3. Load task từ database
     * 4. Hiển thị dữ liệu lên UI
     * 5. Setup listeners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Gọi onCreate() của lớp cha (AppCompatActivity)
        // BẮT BUỘC gọi super.onCreate() đầu tiên
        super.onCreate(savedInstanceState);

        // KẾ THỪA: setContentView() từ AppCompatActivity
        // Chức năng: Inflate layout XML thành View hierarchy
        // Set layout activity_edit_task.xml cho Activity này
        setContentView(R.layout.activity_edit_task);

        // ════════════════════════════════════════════════════════════
        // BƯỚC 1: findViewById() - Tìm và lấy references đến Views
        // ════════════════════════════════════════════════════════════
        // KẾ THỪA: findViewById() từ Activity
        // Chức năng: Tìm View trong layout bằng ID

        etTaskTitle = findViewById(R.id.et_task_title);
        etTaskDescription = findViewById(R.id.et_task_description);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerPriority = findViewById(R.id.spinner_priority);
        spinnerStatus = findViewById(R.id.spinner_status);
        etDueDate = findViewById(R.id.et_due_date);
        btnUpdateTask = findViewById(R.id.btn_update_task);
        btnCancel = findViewById(R.id.btn_cancel);

        // ════════════════════════════════════════════════════════════
        // BƯỚC 2: Validation - Kiểm tra view có null không
        // ════════════════════════════════════════════════════════════
        if (etDueDate == null) {
            // Nếu không tìm thấy view → có lỗi trong XML layout
            Toast.makeText(this, "Lỗi: Không tìm thấy view et_due_date", Toast.LENGTH_SHORT).show();
            return;  // Dừng execution
        }

        // ════════════════════════════════════════════════════════════
        // BƯỚC 3: Khởi tạo TaskDAL
        // ════════════════════════════════════════════════════════════
        taskDAL = new TaskDAL(this);  // this = Context của Activity

        // ════════════════════════════════════════════════════════════
        // BƯỚC 4: Nhận taskId từ Intent
        // ════════════════════════════════════════════════════════════
        // KẾ THỪA: getIntent() từ Activity
        // Chức năng: Lấy Intent đã khởi chạy Activity này

        // getIntExtra(key, defaultValue)
        // - Lấy int từ Intent với key "task_id"
        // - Nếu không có → return -1 (default)
        taskId = getIntent().getIntExtra(Constants.EXTRA_TASK_ID, -1);

        // Validate taskId
        if (taskId == -1) {
            // Không có taskId → không thể edit
            Toast.makeText(this, "Lỗi: Task không tìm thấy", Toast.LENGTH_SHORT).show();

            // KẾ THỪA: finish() từ Activity
            // Chức năng: Đóng Activity hiện tại, quay về Activity trước
            finish();
            return;
        }

        // ════════════════════════════════════════════════════════════
        // BƯỚC 5: Load task từ database và hiển thị
        // ════════════════════════════════════════════════════════════
        loadTask();  // Load task và fill data vào UI

        // ════════════════════════════════════════════════════════════
        // BƯỚC 6: Setup Spinners (dropdowns)
        // ════════════════════════════════════════════════════════════
        setupCategorySpinner();   // Setup dropdown categories
        setupPrioritySpinner();   // Setup dropdown priorities
        setupStatusSpinner();     // Setup dropdown statuses

        // ════════════════════════════════════════════════════════════
        // BƯỚC 7: Setup DatePicker
        // ════════════════════════════════════════════════════════════
        // Click vào EditText → Mở DatePickerDialog
        etDueDate.setOnClickListener(v -> showDatePickerDialog());

        // Ngăn bàn phím hiển thị khi click vào EditText
        etDueDate.setFocusable(false);      // Không focus được
        etDueDate.setCursorVisible(false);  // Ẩn cursor

        // ════════════════════════════════════════════════════════════
        // BƯỚC 8: Setup Button Listeners
        // ════════════════════════════════════════════════════════════
        // Click Update → Gọi updateTask()
        btnUpdateTask.setOnClickListener(v -> updateTask());

        // Click Cancel → Đóng Activity
        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * loadTask() - Load task từ database và hiển thị lên UI
     * ════════════════════════════════════════════════════════════════
     *
     * Flow:
     * 1. Lấy task từ database bằng taskId
     * 2. Nếu tìm thấy → Fill dữ liệu vào các EditText
     * 3. Lưu selectedDate để dùng cho DatePicker
     */
    private void loadTask() {
        // Lấy task từ database
        currentTask = taskDAL.getTaskById(taskId);

        if (currentTask != null) {
            // Fill dữ liệu vào UI
            etTaskTitle.setText(currentTask.getTitle());
            etTaskDescription.setText(currentTask.getDescription());

            // Lưu due date
            selectedDate = currentTask.getDueDate();

            // Hiển thị due date nếu có
            if (selectedDate != null && !selectedDate.isEmpty()) {
                etDueDate.setText(selectedDate);
            }
        }
        // Note: Spinners sẽ được set trong setupXXXSpinner() methods
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * setupCategorySpinner() - Setup dropdown categories
     * ════════════════════════════════════════════════════════════════
     *
     * Flow:
     * 1. Tạo array categories từ Constants
     * 2. Tạo ArrayAdapter với array này
     * 3. Set adapter cho spinner
     * 4. Set selection dựa trên category hiện tại của task
     */
    private void setupCategorySpinner() {
        // Array các categories
        String[] categories = {
                Constants.CATEGORY_WORK,      // "Công Việc"
                Constants.CATEGORY_STUDY,     // "Học Tập"
                Constants.CATEGORY_PERSONAL,  // "Cá Nhân"
                Constants.CATEGORY_HEALTH,    // "Sức Khỏe"
                Constants.CATEGORY_SHOPPING   // "Mua Sắm"
        };

        // ArrayAdapter: Kết nối array data với Spinner
        // Constructor: (context, layout, data)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,                                       // Context
                android.R.layout.simple_spinner_item,      // Layout cho item
                categories                                  // Data array
        );

        // Set layout cho dropdown list khi mở spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter cho spinner
        spinnerCategory.setAdapter(adapter);

        // Set selection dựa trên task hiện tại
        if (currentTask != null) {
            // Tìm vị trí của category hiện tại trong array
            int position = adapter.getPosition(currentTask.getCategory());
            // Set spinner chọn vị trí đó
            spinnerCategory.setSelection(position);
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * setupPrioritySpinner() - Setup dropdown priorities
     * ════════════════════════════════════════════════════════════════
     *
     * Giống setupCategorySpinner() nhưng cho priority
     */
    private void setupPrioritySpinner() {
        String[] priorities = {
                Constants.PRIORITY_HIGH,    // "Cao"
                Constants.PRIORITY_MEDIUM,  // "Trung Bình"
                Constants.PRIORITY_LOW      // "Thấp"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                priorities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        if (currentTask != null) {
            int position = adapter.getPosition(currentTask.getPriority());
            spinnerPriority.setSelection(position);
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * setupStatusSpinner() - Setup dropdown statuses
     * ════════════════════════════════════════════════════════════════
     *
     * Giống setupCategorySpinner() nhưng cho status
     */
    private void setupStatusSpinner() {
        String[] statuses = {
                Constants.STATUS_PENDING,    // "Đang Chờ"
                Constants.STATUS_COMPLETED   // "Hoàn Thành"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statuses
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        if (currentTask != null) {
            int position = adapter.getPosition(currentTask.getStatus());
            spinnerStatus.setSelection(position);
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * showDatePickerDialog() - Hiển thị dialog chọn ngày
     * ════════════════════════════════════════════════════════════════
     *
     * Flow:
     * 1. Lấy ngày hiện tại từ Calendar
     * 2. Tạo DatePickerDialog với ngày hiện tại
     * 3. Set listener để nhận ngày được chọn
     * 4. Hiển thị dialog
     */
    private void showDatePickerDialog() {
        // Calendar.getInstance() - Lấy ngày giờ hiện tại
        Calendar calendar = Calendar.getInstance();

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,  // Context

                // Lambda: Callback khi user chọn ngày
                // Parameters: (view, year, monthOfYear, dayOfMonth)
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Format ngày: yyyy-MM-dd
                    // monthOfYear bắt đầu từ 0 nên +1
                    selectedDate = String.format("%04d-%02d-%02d",
                            year,
                            monthOfYear + 1,  // January = 0 → +1
                            dayOfMonth
                    );

                    // Hiển thị ngày đã chọn lên EditText
                    etDueDate.setText(selectedDate);
                },

                // Ngày mặc định hiển thị trong dialog
                calendar.get(Calendar.YEAR),        // Năm hiện tại
                calendar.get(Calendar.MONTH),       // Tháng hiện tại
                calendar.get(Calendar.DAY_OF_MONTH) // Ngày hiện tại
        );

        // Hiển thị dialog
        datePickerDialog.show();
    }

    /**
     * ════════════════════════════════════════════════════════════════
     * updateTask() - Cập nhật task vào database
     * ════════════════════════════════════════════════════════════════
     *
     * Flow:
     * 1. Lấy dữ liệu từ UI
     * 2. Validate dữ liệu
     * 3. Update vào currentTask object
     * 4. Gọi TaskDAL để update database
     * 5. Hiển thị kết quả và đóng Activity
     */
    private void updateTask() {
        // ════════════════════════════════════════════════════════════
        // BƯỚC 1: Lấy dữ liệu từ UI
        // ════════════════════════════════════════════════════════════
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();

        // getSelectedItem() - Lấy item đang được chọn trong Spinner
        // toString() - Chuyển object thành String
        String category = spinnerCategory.getSelectedItem().toString();
        String priority = spinnerPriority.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        // ════════════════════════════════════════════════════════════
        // BƯỚC 2: Validate dữ liệu
        // ════════════════════════════════════════════════════════════

        // Validate title
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return;  // Dừng lại, không update
        }

        // Validate due date
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày hạn chót", Toast.LENGTH_SHORT).show();
            return;
        }

        // ════════════════════════════════════════════════════════════
        // BƯỚC 3: Update currentTask object
        // ════════════════════════════════════════════════════════════
        currentTask.setTitle(title);
        currentTask.setDescription(description);
        currentTask.setCategory(category);
        currentTask.setPriority(priority);
        currentTask.setStatus(status);
        currentTask.setDueDate(selectedDate);

        // ════════════════════════════════════════════════════════════
        // BƯỚC 4: Update vào database
        // ════════════════════════════════════════════════════════════
        // taskDAL.updateTask() return true nếu thành công
        boolean success = taskDAL.updateTask(currentTask);

        // ════════════════════════════════════════════════════════════
        // BƯỚC 5: Hiển thị kết quả
        // ════════════════════════════════════════════════════════════
        if (success) {
            // Thành công
            Toast.makeText(this, Constants.MSG_TASK_UPDATED, Toast.LENGTH_SHORT).show();

            // Đóng Activity, quay về TaskListActivity
            finish();
        } else {
            // Thất bại
            Toast.makeText(this, "Lỗi khi cập nhật công việc", Toast.LENGTH_SHORT).show();
        }
    }
}