package com.example.administrator.bombtest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Records;
import com.example.administrator.bombtest.Model.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SignedFragment extends Fragment{
    private Group group;
    private Spinner spinner_name, spinner_date, spinner_time;
    private String[] names;
    private ProgressDialog progressDialog;
    private List<Student> members;
    private ListView search_result;
    private int spinner_date_position,spinner_time_position;

    public SignedFragment() {
    }

    @SuppressLint("ValidFragment")
    public SignedFragment(Group group,List<Student> members,int spinner_date_position,int spinner_time_position) {
        this.group = group;
        this.members = members;
        this.spinner_date_position = spinner_date_position;
        this.spinner_time_position = spinner_time_position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signed, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        init_spinner_name();
        try {
            init_spinner_date();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        init_spinner_time();
        Button search = getActivity().findViewById(R.id.search_signed_records);
        search_result = getActivity().findViewById(R.id.search_signed_result);
        search_result.setEnabled(false);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_result();
            }
        });
        if(spinner_time_position!=0){
            search_result();
        }
    }

    private void init_spinner_date() throws ParseException {
        spinner_date = getActivity().findViewById(R.id.sp_signed_date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date current_date = new Date(System.currentTimeMillis());
        String curr_date = sdf.format(current_date);
        String start_date = group.getStart_date().substring(0,4)+"-"+group.getStart_date().substring(4,6)+"-"+group.getStart_date().substring(6);
        String end_date;
        String[] dates;
        if(Integer.parseInt(curr_date)>=Integer.parseInt(group.getEnd_date())) {
            end_date = group.getEnd_date().substring(0, 4) + "-" + group.getEnd_date().substring(4, 6) + "-" + group.getEnd_date().substring(6);
            dates = new String[getDays(start_date,end_date).size()+1];
            dates[0]="全部日期";
            for (int i = 0; i < getDays(start_date,end_date).size(); i++) {
                dates[i+1]=getDays(start_date,end_date).get(i);
            }
        }else if(Integer.parseInt(curr_date)<Integer.parseInt(group.getStart_date())){
            dates = new String[1];
            dates[0] = "全部日期";
        }else {
            end_date = curr_date.substring(0,4)+"-"+curr_date.substring(4,6)+"-"+curr_date.substring(6);
            dates = new String[getDays(start_date,end_date).size()+1];
            dates[0]="全部日期";
            for (int i = 0; i < getDays(start_date,end_date).size(); i++) {
                dates[i+1]=getDays(start_date,end_date).get(i);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, dates);
        spinner_date.setAdapter(adapter);
        spinner_date.setSelection(spinner_date_position);
    }

    private void init_spinner_name() {
        spinner_name = getActivity().findViewById(R.id.sp_signed_name);
        names = new String[members.size() + 1];
        names[0] = "全部成员";
        for (int i = 0; i < members.size(); i++) {
            names[i + 1] = members.get(i).getUsername();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, names);
        spinner_name.setAdapter(adapter);
        spinner_name.setSelection(0);
    }

    private void init_spinner_time() {
        spinner_time = getActivity().findViewById(R.id.sp_signed_time);
        String[] times = new String[group.getStarts_time().size() + 1];
        times[0] = "全部时段";
        for (int i = 0; i < group.getStarts_time().size(); i++) {
            times[i + 1] = group.getStarts_time().get(i).substring(0, 2) + ":" + group.getStarts_time().get(i).substring(2) + "-"
                    + group.getEnds_time().get(i).substring(0, 2) + ":" + group.getEnds_time().get(i).substring(2);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, times);
        spinner_time.setAdapter(adapter);
        spinner_time.setSelection(spinner_time_position);
    }

    private List<String> getDays(String bigtimeStr, String endTimeStr) throws ParseException {
        Date bigtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bigtimeStr + " 00:00:00");
        Date endtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTimeStr + " 00:00:00");
        //定义一个接受时间的集合
        List<Date> lDate = new ArrayList<>();
        lDate.add(bigtime);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(bigtime);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(endtime);
        // 测试此日期是否在指定日期之后
        while (endtime.after(calBegin.getTime()))  {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        List<String> dates = new LinkedList<>();
        for (Date date : lDate) {
            dates.add(new SimpleDateFormat("yyyy/MM/dd").format(date));
        }
        return dates;
    }

    private void search_result() {
        progressDialog.show();
        List<BmobQuery<Records>> queries = new ArrayList<>();
        BmobQuery<Records> recordsGroup_id = new BmobQuery<>();
        recordsGroup_id.addWhereEqualTo("group_id",group.getObjectId());
        queries.add(recordsGroup_id);
        if(spinner_name.getSelectedItemPosition()!=0){
            BmobQuery<Records> recordsUser_id = new BmobQuery<>();
            int position = spinner_name.getSelectedItemPosition();
            String user_id = members.get(position-1).getObjectId();
            recordsUser_id.addWhereEqualTo("user_id",user_id);
            queries.add(recordsUser_id);
        }
        if(spinner_date.getSelectedItemPosition()!=0){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String seletedDate = spinner_date.getSelectedItem().toString();
            String searchAtStart = seletedDate.substring(0,4)+"-"+seletedDate.substring(5,7)+"-"+seletedDate.substring(8)+" 00:00:00";
            String searchAtEnd = seletedDate.substring(0,4)+"-"+seletedDate.substring(5,7)+"-"+seletedDate.substring(8)+" 23:59:59";
            Date createdAtDateStart = null;
            Date createAtDateEnd = null;
            try {
                createdAtDateStart = sdf.parse(searchAtStart);
                createAtDateEnd = sdf.parse(searchAtEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            BmobDate bmobCreatedAtDateStart = new BmobDate(createdAtDateStart);
            BmobDate bmobCreateAtDateEnd = new BmobDate(createAtDateEnd);
            BmobQuery<Records> recordsDateStart = new BmobQuery<>();
            recordsDateStart.addWhereGreaterThanOrEqualTo("createdAt", bmobCreatedAtDateStart);
            BmobQuery<Records> recordsDateEnd = new BmobQuery<>();
            recordsDateEnd.addWhereLessThanOrEqualTo("createdAt", bmobCreateAtDateEnd);
            queries.add(recordsDateStart);
            queries.add(recordsDateEnd);
        }
        if(spinner_time.getSelectedItemPosition()!=0){
            BmobQuery<Records> recordsTime_Slot = new BmobQuery<>();
            int time_slot = spinner_time.getSelectedItemPosition();
            recordsTime_Slot.addWhereEqualTo("time_slot",time_slot);
            queries.add(recordsTime_Slot);
        }
        BmobQuery<Records> query = new BmobQuery<>();
        query.and(queries);
        query.findObjects(new FindListener<Records>() {
            @Override
            public void done(List<Records> list, BmobException e) {
                if(e==null){
                    List<Map<String, Object>> mapList = new ArrayList<>();
                    String[] record_user_name = new String[list.size()];
                    String[] recorded_time = new String[list.size()];
                    String[] record_date = new String[list.size()];
                    String[] record_time = new String[list.size()];
                    for (int i = 0; i <list.size(); i++) {
                        for (int j = 0; j < members.size(); j++) {
                            if(list.get(i).getUser_id().equals(members.get(j).getObjectId())){
                                record_user_name[i]=members.get(j).getUsername();
                                break;
                            }
                        }
                        recorded_time[i]= list.get(i).getCreatedAt().substring(11);
                        record_date[i] = list.get(i).getCreatedAt().substring(0,10);
                        int time_slot = list.get(i).getTime_slot()-1;
                        record_time[i] = group.getStarts_time().get(time_slot).substring(0,2)+":"+group.getStarts_time().get(time_slot).substring(2)
                                +"-"+group.getEnds_time().get(time_slot).substring(0,2)+":"+group.getEnds_time().get(time_slot).substring(2);
                    }

                    for (int i = 0; i < record_user_name.length; i++) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("user_name", record_user_name[i]);
                        map.put("recorded_time", recorded_time[i]);
                        map.put("record_date",record_date[i]);
                        map.put("record_time",record_time[i]);
                        mapList.add(map);
                    }

                    SimpleAdapter adapter = new SimpleAdapter(getContext(),
                            mapList,
                            R.layout.group_record_list_item,
                            new String[]{"user_name", "recorded_time","record_date","record_time"},
                            new int[]{R.id.record_user_name, R.id.recorded_time,R.id.record_date,R.id.record_time}){
                        //设置listview可滑动不可点击
                        @Override
                        public boolean isEnabled(int position) {
                            return false;
                        }
                    };
                    search_result.setAdapter(adapter);
                }else {
                    Toast.makeText(getContext(),"搜索失败："+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}
