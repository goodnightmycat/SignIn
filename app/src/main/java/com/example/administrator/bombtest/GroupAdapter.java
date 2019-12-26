package com.example.administrator.bombtest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.administrator.bombtest.Model.Group;

import java.util.List;

public class GroupAdapter extends ArrayAdapter {
    private final int LayoutID;

    public GroupAdapter(@NonNull Context context, int layout, List<Group> obj) {
        super(context, layout,obj);
        LayoutID = layout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Group mGroup=(Group) getItem(position);

        ViewHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(LayoutID, null);
            holder = new ViewHolder();
            holder.group_name=convertView.findViewById(R.id.text_group_name);
            holder.group_time = convertView.findViewById(R.id.text_group_time);
            convertView.setTag(holder);
        }
        else
        {
         holder=(ViewHolder)convertView.getTag();
        }

        holder.group_name.setText(mGroup.getName());
        holder.group_time.setText(mGroup.getCreatedAt());
        return convertView;
    }

    class ViewHolder{
        TextView group_name;
        TextView group_time;

    }
}
