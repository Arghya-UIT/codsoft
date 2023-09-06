package com.example.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.alarmclock.database.AlarmModel;
import com.example.alarmclock.database.MyDbHelper;

import org.json.JSONArray;

import java.util.Locale;

public class CreateAlarm extends AppCompatActivity {

    private LinearLayoutCompat getTimeLayout;
    private LinearLayoutCompat getSelectedDaysLayout;
    private LinearLayoutCompat getRingtoneLayout;
    private EditText getAlarmNameTextview;
    private TextView pickedTime;
    private TextView pickedRingTone;
    private Button cancelButton;
    private Button saveButton;
    private String selectedDaysJson;
    protected String pickedTimeForStore;


    private static final int PICK_RINGTONE_REQUEST = 1;
    private Uri ringtoneUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_alarm);

        // Initialize UI elements
        pickedTime = findViewById(R.id.pickedTime);
        pickedRingTone = findViewById(R.id.pickedRingTone);
        getTimeLayout = findViewById(R.id.getTimeLayout);
        getSelectedDaysLayout = findViewById(R.id.getSelectedDaysLayout);
        getRingtoneLayout = findViewById(R.id.getRingtoneLayout);
        getAlarmNameTextview = findViewById(R.id.getAlarmNameTextview);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);

        // Set click listeners for buttons
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity
            }
        });


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
            public void onClick(View v) {
                // Handle save button click
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

                MyDbHelper dbHelper = new MyDbHelper(CreateAlarm.this);
                long rowId = dbHelper.addTask(alarmModel);
                Log.d("row-id",""+rowId);

                Intent intent = new Intent(CreateAlarm.this, AlarmReceiver.class);
                intent.putExtra("Message", alarmModel.getAlarm_name());
                intent.putExtra("sound", alarmModel.getRingtone_uri());
                intent.putExtra("RemindDate", alarmModel.getTime_for_store());
                intent.putExtra("id", rowId);

                PendingIntent intent1 = PendingIntent.getBroadcast(CreateAlarm.this, (int) alarmModel.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeMillis, intent1);

                    setResult(RESULT_OK);
                } catch (SecurityException e) {
                    // Handle the SecurityException, which may occur if your app lacks permission to set the exact alarm.
                    Log.e("Alarm Scheduling", "SecurityException: " + e.getMessage());
                    // You can inform the user or take appropriate action here.
                }

                dbHelper.close();

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

        // Create the AlertDialog.Builder instance and set the view
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Select Days");

        // Set positive button click listener
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update the selectedDays array based on CheckBox states
                boolean[] selectedDays = {
                        sundayCheckBox.isChecked(),
                        mondayCheckBox.isChecked(),
                        tuesdayCheckBox.isChecked(),
                        wednesdayCheckBox.isChecked(),
                        thursdayCheckBox.isChecked(),
                        fridayCheckBox.isChecked(),
                        saturdayCheckBox.isChecked()
                };

                // Convert selectedDays array to JSON string
                JSONArray jsonArray = new JSONArray();
                for (boolean day : selectedDays) {
                    jsonArray.put(day);
                }
                selectedDaysJson = jsonArray.toString();
                Log.d("msg-json", " " + selectedDaysJson);

            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the Cancel button click or dismiss the dialog
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
                String selectedTime24Hr = String.format(Locale.getDefault(), "%02d:%02d", hour + (amPm * 12), minute);

                // Assuming you have a TextView for displaying the selected time
                pickedTime.setText(selectedTime);
                pickedTimeForStore = selectedTime24Hr;
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
        startActivityForResult(intent, PICK_RINGTONE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_RINGTONE_REQUEST && resultCode == RESULT_OK) {
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
