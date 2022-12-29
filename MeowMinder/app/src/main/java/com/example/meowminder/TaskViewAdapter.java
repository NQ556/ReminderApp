package com.example.meowminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.TaskViewViewHolder>{
    private List<Task> taskList;

    public TaskViewAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_view_layout, parent, false);
        return new TaskViewAdapter.TaskViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewViewHolder holder, int position) {
        Task task = taskList.get(position);

        if (task == null)
        {
            return;
        }

        holder.taskName.setText(task.getTaskName());
    }

    @Override
    public int getItemCount() {
        if (taskList != null)
        {
            return taskList.size();
        }
        return 0;
    }

    public class TaskViewViewHolder extends RecyclerView.ViewHolder {
        private TextView taskName;

        public TaskViewViewHolder(@NonNull View itemView) {
            super(itemView);

            taskName = (TextView) itemView.findViewById(R.id.task_name);
        }
    }
}
