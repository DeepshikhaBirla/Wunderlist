package com.intimetec.wunderlist.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.data.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> allTasks;

    public TaskAdapter(List<Task> tasks) {
        this.allTasks = tasks;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = allTasks.get(position);

        holder.txtName.setText(task.getTaskName());
        holder.txtDate.setText(task.getTaskDate());
        holder.txtTime.setText(task.getTaskTime());
        holder.txtCategory.setText(task.getCategory());

    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtName, txtDate, txtTime, txtCategory;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.view_txt_name);
            txtDate = itemView.findViewById(R.id.view_txt_date);
            txtTime = itemView.findViewById(R.id.view_txt_time);
            txtCategory = itemView.findViewById(R.id.view_txt_category);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
