package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.database.MyDbHelper;
import com.example.todolist.database.TaskModel;

import java.util.List;

public class CustomeAdapter extends RecyclerView.Adapter<CustomeAdapter.ViewHolder> {
    private List<TaskModel> taskList;
    private Context context;
    private CustomeAdapterListener longClickListener;


    public interface CustomeAdapterListener {
        void onItemLongClick(TaskModel task);
    }

    public CustomeAdapter(Context context, List<TaskModel> taskList, CustomeAdapterListener listener) {
        this.context = context;
        this.taskList = taskList;
        longClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custome_adapter_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        // Attach the long click listener to the itemView
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    TaskModel alarm = taskList.get(position);
                    longClickListener.onItemLongClick(alarm);
                    return true;
                }
                return false;
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskModel task = taskList.get(position);

        holder.titleText.setText(task.getTitle());
        holder.showTime.setText(task.getTime_for_store());
        holder.showDate.setText(task.getDate_for_store());

        if ("1".equals(task.getPriority())) {
            holder.priorityMarker.setImageResource(R.drawable.img);
        }
        if ("1".equals(task.getStatus())) {
            holder.statusMarker.setImageResource(R.drawable.green_tick);
        }
        holder.statusMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle ImageView click event
                // Update the database value associated with this item
                MyDbHelper db=new MyDbHelper(context);
                db.updateTaskStatus(task.getId(), "1");
                // Set a new image in the ImageView
                holder.statusMarker.setImageResource(R.drawable.green_tick);
                Log.d("updating-for id ",""+task.getId());
            }
        });

        holder.editBtn.setOnClickListener(v -> {
            Intent editIntent = new Intent(context, EditTaskActivity.class);
            editIntent.putExtra("task_id", task.getId());
            context.startActivity(editIntent);
            Toast.makeText(context, "Edit clicked at position: " + position, Toast.LENGTH_SHORT).show();
            Log.d("this---db id", " " + task.getId());
        });

        holder.showDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewTaskActivity.class);
            intent.putExtra("task_id", task.getId());
            context.startActivity(intent);
            Toast.makeText(context, "Layout clicked at position: " + position, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView statusMarker;
        TextView titleText, showTime, showDate;
        ImageView editBtn, priorityMarker;
        LinearLayout showDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusMarker = itemView.findViewById(R.id.statusMarker);
            titleText = itemView.findViewById(R.id.titleText);
            showTime = itemView.findViewById(R.id.showTime);
            showDate = itemView.findViewById(R.id.showDate);
            editBtn = itemView.findViewById(R.id.editBtn);
            priorityMarker = itemView.findViewById(R.id.highPriorityMarker);
            showDetails = itemView.findViewById(R.id.showDetails);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    TaskModel task = taskList.get(position);
                    longClickListener.onItemLongClick(task);
                    return true;
                }
                return false;
            });
        }
    }
}