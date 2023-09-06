package com.example.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.database.MyDbHelper;
import com.example.todolist.database.TaskModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    TextView setDate, setTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);

        LinearLayout getDate = findViewById(R.id.getDate);
        setDate = findViewById(R.id.setDate);
        LinearLayout getTime = findViewById(R.id.getTime);
        setTime = findViewById(R.id.setTime);
        TextView cancelBtn = findViewById(R.id.cancelBtn);
        TextView saveBtn = findViewById(R.id.saveBtn);
        CheckBox highPriority = findViewById(R.id.highPriority);
        EditText getTitle = findViewById(R.id.getTitle);
        EditText getDescription = findViewById(R.id.getDescription);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(setDate);
            }
        });
        getTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(setTime);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = getTitle.getText().toString();
                String description = getDescription.getText().toString();
                boolean isHighPriority = highPriority.isChecked();
                String selectedDate = setDate.getText().toString(); // Assuming you update the TextView when selecting a date
                String selectedTime = setTime.getText().toString(); // Assuming you update the TextView when selecting a time

                TaskModel taskModel = new TaskModel();
                taskModel.setTitle(title);
                taskModel.setDescription(description);
                taskModel.setTime_for_store(selectedTime);
                taskModel.setDate_for_store(selectedDate);
                taskModel.setPriority(isHighPriority ? "1" : "0");
                taskModel.setStatus("0");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
                try {
                    Date date = dateFormat.parse(selectedDate);
                    Date time = timeFormat.parse(selectedTime);

                    // Combine date and time
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    Calendar timeCalendar = Calendar.getInstance();
                    timeCalendar.setTime(time);
                    calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

                    taskModel.setDate_time_for_store(calendar.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                MyDbHelper dbHelper = new MyDbHelper(AddTaskActivity.this);

                Intent intent = new Intent(AddTaskActivity.this, AlarmReceiver.class);
                intent.putExtra("title", taskModel.getTitle()); // Use task title as the message
                intent.putExtra("description",taskModel.getDescription());
                intent.putExtra("RemindDate", taskModel.getDate_for_store() + " " + taskModel.getTime_for_store()); // Combine date and time
                intent.putExtra("id", taskModel.getId()); // Replace this with the actual ID

                PendingIntent intent1 = PendingIntent.getBroadcast(AddTaskActivity.this, taskModel.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) {
                            Log.e("Alarm Scheduling", "Cannot schedule exact alarms.");
                            return;
                        }
                    }
                }
                long alarmTimeMillis = System.currentTimeMillis() + 60000; // 10 seconds from now
                try {
                    // Set the exact alarm
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeMillis, intent1);

                    // Continue with other tasks (e.g., adding the task to the database)
                    dbHelper.addTask(taskModel);
                    setResult(RESULT_OK);
                    finish();
                    Log.d("Sending from btn", " " + title + " " + description + " " + highPriority + " " + selectedDate + " " + selectedTime);
                } catch (SecurityException e) {
                    // Handle the SecurityException, which may occur if your app lacks permission to set the exact alarm.
                    Log.e("Alarm Scheduling", "SecurityException: " + e.getMessage());
                    // You can inform the user or take appropriate action here.
                }
            }
        });

    }


    protected void showDatePickerDialog(final TextView setDate) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dailouge_get_date, null);

        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int dayOfMonth = datePicker.getDayOfMonth();

                        // Here you can handle the selected date (year, month, dayOfMonth)
                        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        // Assuming you have a TextView for displaying the selected date
                        setDate.setText(selectedDate);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    protected void showTimePickerDialog(final TextView setTime) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dailouge_get_time, null);

        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hour, minute;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                // Get AM/PM selection
                int amPm = hour >= 12 ? 1 : 0;

                // Convert to 12-hour format if needed
                if (hour > 12) {
                    hour -= 12;
                } else if (hour == 0) {
                    hour = 12;
                }

                // Handle the selected time (hour, minute, amPm) here
                String amPmText = amPm == 1 ? "PM" : "AM";
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPmText);
                // Assuming you have a TextView for displaying the selected time
                setTime.setText(selectedTime);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
