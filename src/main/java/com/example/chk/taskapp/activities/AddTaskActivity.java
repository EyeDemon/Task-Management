package com.example.chk.taskapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.example.chk.taskapp.utils.SessionManager;

import java.util.Calendar;

// KẾ THỪA: extends AppCompatActivity
// - AppCompatActivity là lớp cha cung cấp các tính năng cơ bản cho Activity
// - Hỗ trợ Material Design và backward compatibility
public class AddTaskActivity extends AppCompatActivity {

    private TaskDAL taskDAL;
    private SessionManager sessionManager;
    private EditText etDueDate;

    // OVERRIDE: Phương thức onCreate() được kế thừa từ AppCompatActivity
    // - Được gọi khi Activity được tạo lần đầu
    // - Khởi tạo UI và các components
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Gọi phương thức onCreate() của lớp cha (AppCompatActivity)
        super.onCreate(savedInstanceState);

        // KẾ THỪA: setContentView() từ AppCompatActivity
        // - Thiết lập layout cho Activity
        setContentView(R.layout.activity_add_task);

        taskDAL = new TaskDAL(this);
        sessionManager = new SessionManager(this);

        // KẾ THỪA: findViewById() từ Activity (qua AppCompatActivity)
        // - Tìm và lấy reference đến View từ layout
        Button btnAddTask = findViewById(R.id.btnAddTask);
        Button btnCancel = findViewById(R.id.btnCancel);
        EditText etTitle = findViewById(R.id.etTitle);
        EditText etDescription = findViewById(R.id.etDescription);
        Spinner spCategory = findViewById(R.id.spCategory);
        Spinner spPriority = findViewById(R.id.spPriority);
        etDueDate = findViewById(R.id.etDueDate);

        // Setup Category Spinner
        String[] categories = {
                Constants.CATEGORY_WORK,
                Constants.CATEGORY_STUDY,
                Constants.CATEGORY_PERSONAL,
                Constants.CATEGORY_HEALTH,
                Constants.CATEGORY_SHOPPING
        };

        // KẾ THỪA: this (context) được truyền từ Activity
        // - Activity là subclass của Context
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // Setup Priority Spinner
        String[] priorities = {
                Constants.PRIORITY_HIGH,
                Constants.PRIORITY_MEDIUM,
                Constants.PRIORITY_LOW
        };
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                priorities
        );
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(priorityAdapter);

        // DatePicker cho etDueDate - Click vào EditText để mở lịch
        etDueDate.setOnClickListener(v -> showDatePickerDialog());
        // Ngăn bàn phím hiển thị khi click vào EditText
        etDueDate.setFocusable(false);
        etDueDate.setCursorVisible(false);

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String category = spCategory.getSelectedItem().toString();
                String priority = spPriority.getSelectedItem().toString();
                String dueDate = etDueDate.getText().toString().trim();

                if (title.isEmpty()) {
                    // KẾ THỪA: Toast sử dụng context từ Activity
                    Toast.makeText(AddTaskActivity.this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dueDate.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "Vui lòng chọn ngày hạn chót", Toast.LENGTH_SHORT).show();
                    return;
                }

                int userId = sessionManager.getUserId();
                Task task = new Task(0, title, description, category, priority, dueDate, Constants.STATUS_PENDING, userId);

                long result = taskDAL.addTask(task);
                if (result > 0) {
                    Toast.makeText(AddTaskActivity.this, Constants.MSG_TASK_ADDED, Toast.LENGTH_SHORT).show();

                    // KẾ THỪA: startActivity() từ Activity (qua AppCompatActivity)
                    // - Khởi chạy Activity mới
                    startActivity(new Intent(AddTaskActivity.this, TaskListActivity.class));

                    // KẾ THỪA: finish() từ Activity (qua AppCompatActivity)
                    // - Đóng Activity hiện tại
                    finish();
                } else {
                    Toast.makeText(AddTaskActivity.this, "Thêm task thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddTaskActivity.this, TaskListActivity.class));
                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                    etDueDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }
}