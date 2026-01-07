package com.example.trucompanion.ui.checklist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Task;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.TaskViewHolder> {

    private final List<Task> tasks;
    private final AppDatabase db;
    private final Context context;

    public ChecklistAdapter(Context context, List<Task> tasks, AppDatabase db) {
        this.context = context;
        this.tasks = tasks;
        this.db = db;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.title.setText(task.getTitle());
        holder.desc.setText(task.getDescription());

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.isCompleted());

        holder.divider.setVisibility(position == tasks.size() - 1 ? View.GONE : View.VISIBLE);

        if (task.isDefault()) {
            holder.itemView.setOnClickListener(v -> {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos == RecyclerView.NO_POSITION) return;

                Intent i = new Intent(context, ItemDetailActivity.class);
                i.putExtra("title", task.getTitle());
                i.putExtra("desc", task.getDescription());
                context.startActivity(i);
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }

        holder.checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
            int fromPosition = holder.getAdapterPosition();
            if (fromPosition == RecyclerView.NO_POSITION) return;

            task.setCompleted(isChecked);
            db.checklistDao().update(task);

            // Move item
            tasks.remove(fromPosition);
            int toPosition = isChecked ? tasks.size() : 0;
            tasks.add(toPosition, task);
            notifyItemMoved(fromPosition, toPosition);

            String message = isChecked
                    ? task.getTitle() + " completed!"
                    : task.getTitle() + " restored!";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            notifyItemRangeChanged(0, tasks.size());
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateList(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView title, desc;
        View divider;

        TaskViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.taskCheck);
            title = itemView.findViewById(R.id.taskTitle);
            desc = itemView.findViewById(R.id.taskDesc);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
