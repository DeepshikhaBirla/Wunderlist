package com.intimetec.wunderlist.adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.data.task.Task;
import com.intimetec.wunderlist.data.task.TaskRepository;
import com.intimetec.wunderlist.ui.ToDoListActivity;
import com.intimetec.wunderlist.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> allTasks;
    private Application mApplication;
    private Context mContext;
    private List<Task> copyTask;

    public TaskAdapter(List<Task> tasks, Application application, Context context) {
        this.allTasks = tasks;
        mApplication = application;
        mContext = context;
        copyTask = new ArrayList<>();

        copyTask.addAll(allTasks);
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
        holder.txtDate.setText(DateUtil.getDateValue(task.getDateTime()));
        holder.txtTime.setText(DateUtil.getTimeValue(task.getDateTime()));
        holder.txtCategory.setText(task.getCategory());
        holder.checkBox.setChecked(task.getIsFinished() == 1 ? true : false);
    }


    public Task getItem(int position) {
        return allTasks.get(position);
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

    public void removeItem(int position) {
        allTasks.remove(position);
    }

    public void filter(String queryText) {
        {
            allTasks.clear();

            if(queryText.isEmpty())
            {
                allTasks.addAll(copyTask);
            }
            else
            {

                for(Task task: copyTask)
                {
                    if(task.getTaskName().toLowerCase().contains(queryText.toLowerCase()))
                    {
                        allTasks.add(task);
                    }
                }

            }

            notifyDataSetChanged();
        }
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtName, txtDate, txtTime, txtCategory;
        CheckBox checkBox;
        View view;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            txtName = itemView.findViewById(R.id.view_txt_name);
            txtDate = itemView.findViewById(R.id.view_txt_date);
            txtTime = itemView.findViewById(R.id.view_txt_time);
            txtCategory = itemView.findViewById(R.id.view_txt_category);
            checkBox = itemView.findViewById(R.id.checkbox);

            checkBox.setOnClickListener(this);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int adapterPosition = getLayoutPosition();
            Task task = allTasks.get(adapterPosition);

            if (view == checkBox) {
                task.setIsFinished(checkBox.isChecked() ? 1 : 0) ;
                new UpdateTaskAsyncTask(task).execute();
                allTasks.remove(adapterPosition);
            } else {
                Intent intent = new Intent(mContext, ToDoListActivity.class);
                intent.putExtra("task", task);
                mContext.startActivity(intent);
            }
        }
    }

    private class UpdateTaskAsyncTask extends AsyncTask<Void, Void, Void> {

        private Task task;

        public UpdateTaskAsyncTask(Task task) {
            this.task = task;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            TaskRepository taskRepository = new TaskRepository(mApplication);
            taskRepository.update(task);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataSetChanged();
        }
    }
}

