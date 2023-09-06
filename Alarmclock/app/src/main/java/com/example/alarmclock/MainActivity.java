package com.example.alarmclock;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import androidx.appcompat.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmclock.database.AlarmModel;
import com.example.alarmclock.database.MyDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CustomeAdapter.CustomeAdapterListener{

    private ArrayList<AlarmModel> alarmaList;
    private MyDbHelper dbHelper;
    private CustomeAdapter adapter;
    private ActionMode actionMode;
    private AlarmModel selectedAlarm;
    private int MAKE_NEW_ALARM_ACTIVITY = 1;
    private int ADD_TASK_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView collaspedText = findViewById(R.id.collapsed_text);
        FloatingActionButton addNewAlarmBtn = findViewById(R.id.addNewAlarmBtn);

        dbHelper = new MyDbHelper(this);
        alarmaList = new ArrayList<>();
        adapter = new CustomeAdapter(this,  alarmaList,  this);


        RecyclerView contactListView = findViewById(R.id.alarmListListview);
        contactListView.setLayoutManager(new LinearLayoutManager(this));

        contactListView.setAdapter(adapter);
        fetchContact();

        collaspedText.setText(formattedDate);
        toolbar.setTitle("active alarms");
        setSupportActionBar(toolbar);

        addNewAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an explicit intent to open the NewAlarmActivity
                Intent intent = new Intent(MainActivity.this, CreateAlarm.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onItemLongClick(AlarmModel alarm) {
        if (actionMode != null) {
            return;
        }

        selectedAlarm = alarm;

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
                if (selectedAlarm != null) {
                    dbHelper.deleteAlarm(selectedAlarm.getId()); // Adjust this method based on your dbHelper
                    alarmaList.remove(selectedAlarm);
                    adapter.notifyDataSetChanged();
                    selectedAlarm = null;
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
        alarmaList.clear();
        alarmaList.addAll(dbHelper.fetchTask());
        adapter.notifyDataSetChanged();
    }


}