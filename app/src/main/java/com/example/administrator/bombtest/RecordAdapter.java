package com.example.administrator.bombtest;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecordAdapter extends ArrayAdapter {
    private final int LayoutID;
    private final String time_slot[];

    public RecordAdapter(@NonNull Context context, int resource, String[] objects) {
        super(context, resource, objects);
        LayoutID = resource;
        time_slot = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {



        View view = LayoutInflater.from(getContext()).inflate(LayoutID,null);
        TextView time_slot_start = view.findViewById(R.id.time_slot_start);
        TextView time_slot_end = view.findViewById(R.id.time_slot_end);


        time_slot_start.setText(time_slot[position].substring(0,2)+":"+time_slot[position].substring(2,4));


        if(time_slot[position].length()>8){
            LinearLayout result_layout = view.findViewById(R.id.result_layout);
            TextView record_time = view.findViewById(R.id.record_time);
            TextView record_result = view.findViewById(R.id.record_result);
            time_slot_end.setText(time_slot[position].substring(4,6)+":"+time_slot[position].substring(6,8));
            record_time.setText(time_slot[position].substring(8));
            result_layout.setVisibility(View.VISIBLE);
            record_result.setText("已签到");
            record_result.setTextColor(Color.rgb(0,255,0));
        }else {
            time_slot_end.setText(time_slot[position].substring(4,6)+":"+time_slot[position].substring(6));
        }
        return view;
    }

}
