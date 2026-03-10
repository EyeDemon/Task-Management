package com.example.chk.taskapp.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chk.taskapp.R;
import com.example.chk.taskapp.adapters.TaskAdapter;
import com.example.chk.taskapp.dal.TaskDAL;
import com.example.chk.taskapp.models.Task;
import com.example.chk.taskapp.utils.Constants;
import com.example.chk.taskapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

// Màn hình chính hiển thị danh sách tasks
public class TaskListActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView recyclerViewTasks;        // Hiển thị danh sách tasks
    private FloatingActionButton fabAddTask;       // Button thêm task mới
    private MaterialButton btnFilterAll;           // Button lọc "Tất cả"
    private MaterialButton btnFilterPending;       // Button lọc "Đang chờ"
    private MaterialButton btnFilterCompleted;     // Button lọc "Hoàn thành"
    private MaterialButton btnLogout;              // Button đăng xuất
    private LinearLayout emptyState;               // View hiển thị khi list rỗng

    // Data & Logic
    private TaskAdapter taskAdapter;               // Adapter cho RecyclerView
    private TaskDAL taskDAL;                       // Truy cập database
    private SessionManager sessionManager;         // Quản lý session user
    private int currentUserId;                     // ID user đang login
    private List<Task> allTasks;                   // Tất cả tasks
    private String currentFilter = "all";          // Filter hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // Tìm views
        recyclerViewTasks = findViewById(R.id.recycler_view_tasks);
        fabAddTask = findViewById(R.id.fab_add_task);
        btnFilterAll = findViewById(R.id.btn_filter_all);
        btnFilterPending = findViewById(R.id.btn_filter_pending);
        btnFilterCompleted = findViewById(R.id.btn_filter_completed);
        btnLogout = findViewById(R.id.btn_logout);
        emptyState = findViewById(R.id.empty_state);

        sessionManager = new SessionManager(this);
        taskDAL = new TaskDAL(this);
        currentUserId = sessionManager.getUserId();

        // Kiểm tra đã login chưa
        if (currentUserId == -1) {
            // Chưa login → Chuyển sang LoginActivity
            startActivity(new Intent(TaskListActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Setup UI
        setupRecyclerView();      // Setup RecyclerView với adapter
        loadTasks();              // Load tasks từ database
        setupFilterButtons();     // Setup 3 filter buttons
        setupLogoutButton();      // Setup logout button

        // Click FAB → Mở AddTaskActivity
        fabAddTask.setOnClickListener(v -> {
            startActivity(new Intent(TaskListActivity.this, AddTaskActivity.class));
        });
    }

    // Setup RecyclerView với LinearLayoutManager và TaskAdapter
    private void setupRecyclerView() {
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));

        // Tạo adapter với callback khi xóa task
        taskAdapter = new TaskAdapter(this, List.of(), () -> {
            loadTasks();  // Reload khi xóa
        });

        recyclerViewTasks.setAdapter(taskAdapter);
    }

    // Setup 3 filter buttons
    private void setupFilterButtons() {
        // Click "Tất cả" → Hiển thị tất cả tasks
        btnFilterAll.setOnClickListener(v -> {
            currentFilter = "all";
            filterTasks();
            updateFilterButtonStates();
        });

        // Click "Đang chờ" → Chỉ hiển thị tasks pending
        btnFilterPending.setOnClickListener(v -> {
            currentFilter = "pending";
            filterTasks();
            updateFilterButtonStates();
        });

        // Click "Hoàn thành" → Chỉ hiển thị tasks completed
        btnFilterCompleted.setOnClickListener(v -> {
            currentFilter = "completed";
            filterTasks();
            updateFilterButtonStates();
        });

        updateFilterButtonStates();
    }

    // Setup logout button
    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();  // Xóa session
            Toast.makeText(TaskListActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TaskListActivity.this, LoginActivity.class));
            finish();
        });
    }

    // Update màu sắc các filter buttons dựa trên filter hiện tại
    private void updateFilterButtonStates() {
        // Reset tất cả buttons về màu mặc định
        btnFilterAll.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.primary_color)));
        btnFilterPending.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.primary_color)));
        btnFilterCompleted.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.primary_color)));

        btnFilterAll.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnFilterPending.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnFilterCompleted.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        btnFilterAll.setTextColor(getResources().getColor(R.color.primary_color));
        btnFilterPending.setTextColor(getResources().getColor(R.color.primary_color));
        btnFilterCompleted.setTextColor(getResources().getColor(R.color.primary_color));

        // Highlight button đang được chọn
        switch (currentFilter) {
            case "all":
                btnFilterAll.setBackgroundColor(getResources().getColor(R.color.primary_color));
                btnFilterAll.setTextColor(getResources().getColor(R.color.white));
                break;
            case "pending":
                btnFilterPending.setBackgroundColor(getResources().getColor(R.color.primary_color));
                btnFilterPending.setTextColor(getResources().getColor(R.color.white));
                break;
            case "completed":
                btnFilterCompleted.setBackgroundColor(getResources().getColor(R.color.primary_color));
                btnFilterCompleted.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    // Load tất cả tasks từ database
    private void loadTasks() {
        allTasks = taskDAL.getAllTasksByUserId(currentUserId);
        filterTasks();
    }

    // Lọc tasks theo filter hiện tại
    private void filterTasks() {
        List<Task> filteredTasks = new ArrayList<>();

        switch (currentFilter) {
            case "pending":
                // Chỉ lấy tasks có status = "Đang Chờ"
                for (Task task : allTasks) {
                    if (Constants.STATUS_PENDING.equals(task.getStatus())) {
                        filteredTasks.add(task);
                    }
                }
                break;
            case "completed":
                // Chỉ lấy tasks có status = "Hoàn Thành"
                for (Task task : allTasks) {
                    if (Constants.STATUS_COMPLETED.equals(task.getStatus())) {
                        filteredTasks.add(task);
                    }
                }
                break;
            case "all":
            default:
                // Lấy tất cả
                filteredTasks = allTasks;
                break;
        }

        // Hiển thị empty state nếu list rỗng
        if (filteredTasks.isEmpty()) {
            recyclerViewTasks.setVisibility(RecyclerView.GONE);
            emptyState.setVisibility(LinearLayout.VISIBLE);
        } else {
            recyclerViewTasks.setVisibility(RecyclerView.VISIBLE);
            emptyState.setVisibility(LinearLayout.GONE);
            taskAdapter.updateTaskList(filteredTasks);
        }
    }

    // Được gọi khi quay lại Activity này (sau khi add/edit task)
    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();  // Reload tasks để cập nhật UI
    }
}