package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.database.MyDbHelper;
import com.example.todolist.database.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity implements CustomeAdapter.CustomeAdapterListener {

    private ArrayList<TaskModel> taskList;
    private CustomeAdapter adapter;
    private MyDbHelper dbHelper;
    private ActionMode actionMode;
    private TaskModel selectedTask;
    private final int ADD_TASK_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        FloatingActionButton addTaskbtn = findViewById(R.id.addTaskBtn);

        dbHelper = new MyDbHelper(this);
        taskList = new ArrayList<>();
        adapter = new CustomeAdapter(this, taskList, this);


        RecyclerView contactListView = findViewById(R.id.displayNormalTasks);
        contactListView.setLayoutManager(new LinearLayoutManager(this));
        contactListView.setAdapter(adapter);

        fetchContact();


        addTaskbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchContact();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();
        }
    }

    private void fetchContact() {
        taskList.clear();
        taskList.addAll(dbHelper.fetchTask());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemLongClick(TaskModel task) {
        if (actionMode != null) {
            return;
        }

        selectedTask = task;

        // Start the contextual ActionMode
        actionMode = startSupportActionMode(actionModeCallback);

    }

    protected ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.context_menu, menu); // Your context menu XML
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.menu_delete) {
                // Handle delete action here
                if (selectedTask != null) {
                    dbHelper.deleteFromDB(selectedTask.getId()); // Adjust this method based on your dbHelper
                    taskList.remove(selectedTask);
                    adapter.notifyDataSetChanged();
                    selectedTask = null;
                }

                mode.finish(); // Finish the ActionMode
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };


}

