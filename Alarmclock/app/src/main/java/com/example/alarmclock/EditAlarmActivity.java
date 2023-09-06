package com.example.alarmclock;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.alarmclock.database.AlarmModel;
import com.example.alarmclock.database.MyDbHelper;

import org.json.JSONArray;

import java.util.Locale;

public class EditAlarmActivity extends CreateAlarm {
    private MyDbHelper dbHelper;
    private LinearLayoutCompat getTimeLayout;
    private LinearLayoutCompat getSelectedDaysLayout;
    private LinearLayoutCompat getRingtoneLayout;
    private EditText getAlarmNameTextview;
    private TextView pickedTime;
    private TextView pickedRingTone;
    private Button cancelButton;
    private Button saveButton;
    private String selectedDaysJson;
    private String pickedTimeForStore;
    private Uri ringtoneUri;
    private static final int PICK_RINGTONE_REQUEST_EDIT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        pickedTime = findViewById(R.id.pickedTime);
        pickedRingTone = findViewById(R.id.pickedRingTone);
        getTimeLayout = findViewById(R.id.getTimeLayout);
        getSelectedDaysLayout = findViewById(R.id.getSelectedDaysLayout);
        getRingtoneLayout = findViewById(R.id.getRingtoneLayout);
        getAlarmNameTextview = findViewById(R.id.getAlarmNameTextview);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        dbHelper = new MyDbHelper(this);

        Intent intent = getIntent();
        if (intent != null) {
            int taskId = intent.getIntExtra("task_id", -1);
            if (taskId != -1) {
                Log.d("getting here task id", " " + taskId);
                AlarmModel task = dbHelper.fetchTaskById(taskId);
                if (task != null) {
                    pickedTime.setText(task.getTime_for_display());
                    pickedRingTone.setText(task.getTime_for_store());
                    getAlarmNameTextview.setText(task.getAlarm_name());

                }
            }
        }
        getTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });


        getSelectedDaysLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateSelectorDialog();
            }
        });

        getRingtoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickRingtone();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String alarmName = getAlarmNameTextview.getText().toString();
                String timeForDisplay = pickedTime.getText().toString();
                String ringtoneUriString = ringtoneUri != null ? ringtoneUri.toString() : "";

                // Create an instance of AlarmModel with the details
                AlarmModel alarmModel = new AlarmModel();
                alarmModel.setAlarm_name(alarmName);
                alarmModel.setTime_for_display(timeForDisplay);
                alarmModel.setTime_for_store(pickedTimeForStore);
                alarmModel.setSelectedDays(selectedDaysJson);
                alarmModel.setRingtone_uri(ringtoneUriString);
                alarmModel.setStatus("1");

                // Add the alarm to the database using your database helper
                MyDbHelper dbHelper = new MyDbHelper(EditAlarmActivity.this);
                dbHelper.updateTask(alarmModel,intent.getIntExtra("task_id", -1));

                setResult(RESULT_OK);

                finish(); // Close the activity
            }
        });
    }

    protected void showDateSelectorDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dailouge_select_days, null);

        final CheckBox sundayCheckBox = dialogView.findViewById(R.id.sunday);
        final CheckBox mondayCheckBox = dialogView.findViewById(R.id.monday);
        final CheckBox tuesdayCheckBox = dialogView.findViewById(R.id.tuesday);
        final CheckBox wednesdayCheckBox = dialogView.findViewById(R.id.wednesday);
        final CheckBox thursdayCheckBox = dialogView.findViewById(R.id.thursday);
        final CheckBox fridayCheckBox = dialogView.findViewById(R.id.friday);
        final CheckBox saturdayCheckBox = dialogView.findViewById(R.id.saturday);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Select Days");

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean[] selectedDays = {
                        sundayCheckBox.isChecked(),
                        mondayCheckBox.isChecked(),
                        tuesdayCheckBox.isChecked(),
                        wednesdayCheckBox.isChecked(),
                        thursdayCheckBox.isChecked(),
                        fridayCheckBox.isChecked(),
                        saturdayCheckBox.isChecked()
                };

                JSONArray jsonArray = new JSONArray();
                for (boolean day : selectedDays) {
                    jsonArray.put(day);
                }
                selectedDaysJson = jsonArray.toString();
                Log.d("msg-json"," "+selectedDaysJson);

            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    protected void showTimePickerDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dailouge_time_picker, null);

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

                int amPm = hour >= 12 ? 1 : 0;

                if (hour > 12) {
                    hour -= 12;
                } else if (hour == 0) {
                    hour = 12;
                }

                String amPmText = amPm == 1 ? "PM" : "AM";
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPmText);
                String selectedTime24Hr = String.format(Locale.getDefault(), "%02d:%02d", hour + (amPm * 12), minute);

                pickedTime.setText(selectedTime);
                pickedTimeForStore =selectedTime24Hr;
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

    protected void pickRingtone() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        startActivityForResult(intent, PICK_RINGTONE_REQUEST_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_RINGTONE_REQUEST_EDIT && resultCode == RESULT_OK) {
            ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
//             Log.d("uri tone ",".. "+ringtoneUri);

            if (ringtoneUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
                String ringtoneName = ringtone.getTitle(this);
                pickedRingTone.setText(ringtoneName);
            }
        }
    }
}