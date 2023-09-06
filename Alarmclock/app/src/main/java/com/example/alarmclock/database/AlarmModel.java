package com.example.alarmclock.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AlarmModel implements Serializable {
    private long id;
    private String alarm_name;
    private String time_for_display;
    private String time_for_store;
    private String selectedDays;
    private String ringtone_uri;
    private String status;

    public AlarmModel(){

    }





    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(String selectedDays) {
        this.selectedDays = selectedDays;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlarm_name() {
        return alarm_name;
    }

    public void setAlarm_name(String alarm_name) {
        this.alarm_name = alarm_name;
    }

    public String getTime_for_display() {
        return time_for_display;
    }

    public void setTime_for_display(String time_for_display) {
        this.time_for_display = time_for_display;
    }

    public String getTime_for_store() {
        return time_for_store;
    }

    public void setTime_for_store(String time_for_store) {
        this.time_for_store = time_for_store;
    }

    public String getRingtone_uri() {
        return ringtone_uri;
    }

    public void setRingtone_uri(String ringtone_uri) {
        this.ringtone_uri = ringtone_uri;
    }

    @Override
    public String toString() {
        return "AlarmModel{" +
                "alarm_name='" + alarm_name + '\'' +
                ", time_for_display='" + time_for_display + '\'' +
                ", time_for_store='" + time_for_store + '\'' +
                ", ringtone_uri='" + ringtone_uri + '\'' +
                ", selectedDays=" + selectedDays + '\'' +
                ", status=" + status +
                '}';
    }


}
