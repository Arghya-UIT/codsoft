package com.example.alarmclock;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmclock.database.AlarmModel;
import com.example.alarmclock.database.MyDbHelper;

import java.util.List;

public class CustomeAdapter extends RecyclerView.Adapter<CustomeAdapter.ViewHolder> {
    private List<AlarmModel> alarmList;
    private Context context;
    private CustomeAdapterListener longClickListener;

    public interface CustomeAdapterListener {
        void onItemLongClick(AlarmModel alarm);
    }

    public CustomeAdapter(Context context, List<AlarmModel> alarmList, CustomeAdapterListener listener) {
        this.context = context;
        this.alarmList = alarmList;
        this.longClickListener = listener;
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
                    AlarmModel alarm = alarmList.get(position);
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
        AlarmModel alarm = alarmList.get(position);

        holder.alarmName.setText(alarm.getAlarm_name());
        holder.showTime.setText(alarm.getTime_for_display());
        holder.seletedDays.setText(alarm.getSelectedDays());

        if ("1".equals(alarm.getStatus())) {
            holder.toggleBtn.setChecked(true);
        }
        holder.toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDbHelper db =new MyDbHelper(context);
                if("1".equals(alarm.getStatus())){
                    db.updateAlarmStatus(alarm.getId(),"0");
                    holder.toggleBtn.setChecked(false);
                }else{
                    db.updateAlarmStatus(alarm.getId(),"1");
                    holder.toggleBtn.setChecked(true);
                }

            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(context, EditAlarmActivity.class);
                editIntent.putExtra("task_id", alarm.getId());
                context.startActivity(editIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView alarmName;
        TextView showTime;
        TextView seletedDays;
        TextView editBtn;
        Switch toggleBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmName = itemView.findViewById(R.id.alarmName);
            showTime = itemView.findViewById(R.id.showTime);
            seletedDays = itemView.findViewById(R.id.selectedDays);
            editBtn = itemView.findViewById(R.id.editBtn);
            toggleBtn = itemView.findViewById(R.id.alarmOnOffBtn);
        }
    }
}
