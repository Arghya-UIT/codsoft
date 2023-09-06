package com.example.todolist;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.database.MyDbHelper;
import com.example.todolist.database.TaskModel;

public class ViewTaskActivity extends AppCompatActivity {
    private MyDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtask);

        TextView getDate = findViewById(R.id.getDate);
        TextView getTime = findViewById(R.id.getTime);
        TextView editBtn = findViewById(R.id.editBtn);
        TextView okBtn = findViewById(R.id.okBtn);
        ImageView highPriority = findViewById(R.id.highPriority);
        TextView getTitle = findViewById(R.id.getTitle);
        TextView getDescription = findViewById(R.id.getDescription);

        dbHelper = new MyDbHelper(this);

        Intent intent = getIntent();
        if (intent != null) {
            int taskId = intent.getIntExtra("task_id", -1);
            if (taskId != -1) {
                Log.d("getting here task id", " " + taskId);
                TaskModel task = dbHelper.fetchTaskById(taskId);
                if (task != null) {
                    getDate.setText(task.getDate_for_store());
                    getTime.setText(task.getTime_for_store());
                    getDescription.setText(task.getDescription());
                    getTitle.setText(task.getTitle());
                    if ("1".equals(task.getPriority())) {
                        highPriority.setImageResource(R.drawable.img);
                    }
                }
            }
        }

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open EditTaskActivity with the task ID
                Intent editIntent = new Intent(ViewTaskActivity.this, EditTaskActivity.class);
                editIntent.putExtra("task_id", intent.getIntExtra("task_id", -1)); // Pass the task ID
                startActivity(editIntent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close(); // Close the database connection when the activity is destroyed
        }
    }
}
