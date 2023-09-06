package com.example.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MyDbHelper extends SQLiteOpenHelper {

    public MyDbHelper(Context context) {
        super(context, Params.DB_NAME, null, Params.DB_VIRSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + Params.TABLE_NAME + "("
                + Params.KEY_ID + " INTEGER PRIMARY KEY,"
                + Params.KEY_TITLE + " TEXT,"
                + Params.KEY_DESCRIPTION + " TEXT,"
                + Params.KEY_DATE + " TEXT,"
                + Params.KEY_TIME + " TEXT,"
                + Params.KEY_PRIORITY + " TEXT,"
                + Params.KEY_DATE_TIME + " DATETIME,"
                + Params.KEY_STATUS + " TEXT"
                + ")";

        Log.d("db---arghya", "query " + create);
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Implement if needed
    }

    public void addTask(TaskModel taskModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Params.KEY_TITLE, taskModel.getTitle());
        values.put(Params.KEY_DESCRIPTION, taskModel.getDescription());
        values.put(Params.KEY_DATE, taskModel.getDate_for_store());
        values.put(Params.KEY_TIME, taskModel.getTime_for_store());
        values.put(Params.KEY_PRIORITY, taskModel.getPriority());
        values.put(String.valueOf(Params.KEY_DATE_TIME), getDateTimeString(taskModel.getDate_time_for_store()));
        values.put(Params.KEY_STATUS, taskModel.getStatus());

        db.insert(Params.TABLE_NAME, null, values);
        Log.d("dbarghya", "db created ");

        db.close();
    }

    private String getDateTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return dateFormat.format(date);
    }

    public ArrayList<TaskModel> fetchTask() {
        ArrayList<TaskModel> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + Params.TABLE_NAME +
                " ORDER BY " + Params.KEY_DATE_TIME + " ASC";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                TaskModel taskModel = new TaskModel();
                taskModel.setId(cursor.getInt(0));
                taskModel.setTitle(cursor.getString(1));
                taskModel.setDescription(cursor.getString(2));
                taskModel.setDate_for_store(cursor.getString(3));
                taskModel.setTime_for_store(cursor.getString(4));
                taskModel.setPriority(cursor.getString(5));
                taskModel.setStatus(cursor.getString(7));


                taskList.add(taskModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return taskList;
    }

    public TaskModel fetchTaskById(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + Params.TABLE_NAME + " WHERE " + Params.KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(select, new String[]{String.valueOf(taskId)});

        TaskModel taskModel = null;

        if (cursor.moveToFirst()) {
            taskModel = new TaskModel();
            taskModel.setId(cursor.getInt(0));
            taskModel.setTitle(cursor.getString(1));
            taskModel.setDescription(cursor.getString(2));
            taskModel.setDate_for_store(cursor.getString(3));
            taskModel.setTime_for_store(cursor.getString(4));
            taskModel.setPriority(cursor.getString(5));
//            taskModel.setStatus(cursor.getString(7));
        }

        cursor.close();
        return taskModel;
    }

    public void updateTask(TaskModel taskModel, int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Params.KEY_TITLE, taskModel.getTitle());
        values.put(Params.KEY_DESCRIPTION, taskModel.getDescription());
        values.put(Params.KEY_DATE, taskModel.getDate_for_store());
        values.put(Params.KEY_TIME, taskModel.getTime_for_store());
        values.put(Params.KEY_PRIORITY, taskModel.getPriority());

        db.update(
                Params.TABLE_NAME,
                values,
                Params.KEY_ID + " = ?",
                new String[]{String.valueOf(taskId)}
        );

        db.close();
    }

    public void deleteFromDB(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                Params.TABLE_NAME,
                Params.KEY_ID + " = ?",
                new String[]{String.valueOf(taskId)}
        );
        db.close();
        Log.d("deleted from db", " " + taskId);
    }

    public void updateTaskStatus(int taskId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Params.KEY_STATUS, newStatus);

        try {
            int rowsAffected = db.update(
                    Params.TABLE_NAME,
                    values,
                    Params.KEY_ID + " = ?",
                    new String[]{String.valueOf(taskId)}
            );

            // Check if the update was successful
            if (rowsAffected > 0) {
                // Update successful
                Log.d("updated-successful",""+taskId+" "+newStatus);
            } else {
                // No rows were affected; the task with the given ID might not exist
                // You can log this information or handle it accordingly
                Log.d("not-updated",""+taskId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions here, such as SQLiteException
        } finally {
            db.close();
        }
    }

}

