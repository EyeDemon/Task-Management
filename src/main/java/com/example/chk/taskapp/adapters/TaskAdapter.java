package com.example.chk.taskapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chk.taskapp.R;
import com.example.chk.taskapp.activities.EditTaskActivity;
import com.example.chk.taskapp.dal.TaskDAL;
import com.example.chk.taskapp.models.Task;
import com.example.chk.taskapp.utils.Constants;
import com.example.chk.taskapp.utils.DateUtils;

import java.util.List;

// KẾ THỪA: RecyclerView.Adapter - Để hiển thị danh sách tasks
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;
    private TaskDAL taskDAL;

    public interface OnTaskDeleteListener {
        void onTaskDeleted();
    }

    private OnTaskDeleteListener deleteListener;

    public TaskAdapter(Context context, List<Task> taskList, OnTaskDeleteListener deleteListener) {
        this.context = context;
        this.taskList = taskList;
        this.deleteListener = deleteListener;
        this.taskDAL = new TaskDAL(context);
    }

    // OVERRIDE: Tạo ViewHolder mới - inflate layout thành View
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    // OVERRIDE: Hiển thị dữ liệu task lên UI
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Hiển thị title
        holder.tvTaskTitle.setText(task.getTitle());

        // Hiển thị description (nếu có)
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            holder.tvTaskDescription.setText(task.getDescription());
            holder.tvTaskDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvTaskDescription.setVisibility(View.GONE);
        }

        // Hiển thị category (nếu có)
        if (task.getCategory() != null && !task.getCategory().isEmpty()) {
            holder.tvTaskCategory.setText(task.getCategory());
            holder.tvTaskCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvTaskCategory.setVisibility(View.GONE);
        }

        // Hiển thị due date, đổi màu đỏ nếu quá hạn
        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            holder.tvTaskDueDate.setText("Hạn: " + DateUtils.formatDate(task.getDueDate()));
            holder.tvTaskDueDate.setVisibility(View.VISIBLE);

            if (DateUtils.isOverdue(task.getDueDate())) {
                holder.tvTaskDueDate.setTextColor(Color.RED);
            }
        } else {
            holder.tvTaskDueDate.setVisibility(View.GONE);
        }

        // Hiển thị thanh màu priority
        int priorityColor = getPriorityColor(task.getPriority());
        holder.viewPriorityIndicator.setBackgroundColor(priorityColor);

        // Setup checkbox
        holder.cbTaskStatus.setOnCheckedChangeListener(null);
        holder.cbTaskStatus.setChecked(task.isCompleted());

        // Đổi style cho task đã hoàn thành
        if (task.isCompleted()) {
            holder.tvTaskTitle.setTextColor(Color.GRAY);
            holder.tvTaskTitle.setAlpha(0.6f);
        } else {
            holder.tvTaskTitle.setTextColor(Color.BLACK);
            holder.tvTaskTitle.setAlpha(1.0f);
        }

        // Xử lý khi check/uncheck checkbox
        holder.cbTaskStatus.setOnCheckedChangeListener(null);
        holder.cbTaskStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                if (task == null || task.getId() <= 0) {
                    return;
                }

                String newStatus = isChecked ? Constants.STATUS_COMPLETED : Constants.STATUS_PENDING;
                boolean success = taskDAL.updateTaskStatus(task.getId(), newStatus);

                if (success) {
                    task.setStatus(newStatus);
                    notifyItemChanged(position); // KẾ THỪA: Cập nhật UI
                } else {
                    holder.cbTaskStatus.setOnCheckedChangeListener(null);
                    holder.cbTaskStatus.setChecked(!isChecked);
                    holder.cbTaskStatus.setOnCheckedChangeListener((bv, ic) -> {});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Xử lý click button Edit
        holder.btnEditTask.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTaskActivity.class);
            intent.putExtra(Constants.EXTRA_TASK_ID, task.getId());
            context.startActivity(intent);
        });

        // Xử lý click button Delete
        holder.btnDeleteTask.setOnClickListener(v -> {
            taskDAL.deleteTask(task.getId());
            taskList.remove(position);
            notifyItemRemoved(position); // KẾ THỪA: Xóa với animation
            notifyItemRangeChanged(position, taskList.size()); // KẾ THỪA: Update positions

            if (deleteListener != null) {
                deleteListener.onTaskDeleted();
            }
        });
    }

    // OVERRIDE: Trả về số lượng items
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // Chuyển priority string thành màu
    private int getPriorityColor(String priority) {
        if (priority == null) return Constants.COLOR_PRIORITY_MEDIUM;

        if (Constants.PRIORITY_HIGH.equals(priority)) {
            return Constants.COLOR_PRIORITY_HIGH;
        } else if (Constants.PRIORITY_LOW.equals(priority)) {
            return Constants.COLOR_PRIORITY_LOW;
        } else {
            return Constants.COLOR_PRIORITY_MEDIUM;
        }
    }

    // Cập nhật danh sách mới
    public void updateTaskList(List<Task> newList) {
        this.taskList = newList;
        notifyDataSetChanged(); // KẾ THỪA: Refresh toàn bộ
    }

    // KẾ THỪA: RecyclerView.ViewHolder - Cache views để tái sử dụng
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle;
        TextView tvTaskDescription;
        TextView tvTaskCategory;
        TextView tvTaskDueDate;
        CheckBox cbTaskStatus;
        View viewPriorityIndicator;
        ImageButton btnEditTask;
        ImageButton btnDeleteTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView); // Gọi constructor của RecyclerView.ViewHolder

            // Tìm và cache views (chỉ 1 lần)
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskDescription = itemView.findViewById(R.id.tv_task_description);
            tvTaskCategory = itemView.findViewById(R.id.tv_task_category);
            tvTaskDueDate = itemView.findViewById(R.id.tv_task_due_date);
            cbTaskStatus = itemView.findViewById(R.id.cb_task_status);
            viewPriorityIndicator = itemView.findViewById(R.id.view_priority_indicator);
            btnEditTask = itemView.findViewById(R.id.btn_edit_task);
            btnDeleteTask = itemView.findViewById(R.id.btn_delete_task);
        }
    }
}