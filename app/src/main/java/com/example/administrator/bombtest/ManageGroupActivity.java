package com.example.administrator.bombtest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Records;
import com.example.administrator.bombtest.Model.Student;

import java.io.Serializable;
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
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ManageGroupActivity extends AppCompatActivity implements View.OnClickListener {
    private Group group;
    private List<Student> members;
    private ListView listView;
    private TextView latest_date, latest_time, signed_number, unsigned_number;
    private String date;
    private int time_slot;
    private int spinner_date_position;
    private ProgressDialog progressDialog;
    private ImageView back;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        title=findViewById(R.id.titleText_back);
        title.setText("群管理");

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressDialog = new ProgressDialog(ManageGroupActivity.this);
        progressDialog.show();
        group = (Group) getIntent().getSerializableExtra("group");
        listView = findViewById(R.id.manage_group_list);
        LinearLayout all_signs = findViewById(R.id.all_signs);
        LinearLayout signed = findViewById(R.id.signed);
        LinearLayout unsigned = findViewById(R.id.unsigned);
        latest_date = findViewById(R.id.latest_date);
        latest_time = findViewById(R.id.latest_time);
        signed_number = findViewById(R.id.signed_number);
        unsigned_number = findViewById(R.id.unsigned_number);
        Button dissolve_group = findViewById(R.id.dissolve_group);
        dissolve_group.setOnClickListener(this);
        all_signs.setOnClickListener(this);
        signed.setOnClickListener(this);
        unsigned.setOnClickListener(this);

        final List<Map<String, Object>> mapList = new ArrayList<>();
        final String[] item_name = new String[]{"群名称", "群主", "群创建时间", "群公告", "群号", "群成员", "签到规则", "签到情况"};
        final String[] item_value = new String[]{group.getName() + ">", group.getOwner().getUsername(), String.valueOf(group.getCreatedAt()), group.getNotice(), String.valueOf(group.getGroup_number()), ">", ">", "查看更多>"};
        if (group.getNotice() == null || group.getNotice().length() == 0) {
            item_value[3] = "空>";
        } else {
            item_value[3] = ">";
        }

        for (int i = 0; i < item_name.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("item_name", item_name[i]);
            map.put("item_value", item_value[i]);
            mapList.add(map);
        }

        final SimpleAdapter adapter = new SimpleAdapter(ManageGroupActivity.this,
                mapList,
                R.layout.group_detail_list_item,
                new String[]{"item_name", "item_value"},
                new int[]{R.id.item_name, R.id.item_value});
        listView.setAdapter(adapter);
        BmobQuery<Student> query = new BmobQuery<>();
        query.addWhereRelatedTo("members", new BmobPointer(group));
        query.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if (e == null) {
                    members = list;
                    item_value[5] = String.valueOf(members.size()) + "人>";
                    Map<String, Object> map = new HashMap<>();
                    map.put("item_name", item_name[5]);
                    map.put("item_value", item_value[5]);
                    mapList.set(5, map);
                    listView.setAdapter(adapter);
                    latest_date_time();
                    List<BmobQuery<Records>> queries = new ArrayList<>();

                    BmobQuery<Records> recordsGroup_id = new BmobQuery<>();
                    recordsGroup_id.addWhereEqualTo("group_id", group.getObjectId());
                    queries.add(recordsGroup_id);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String searchAtStart = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6) + " 00:00:00";
                    String searchAtEnd = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6) + " 23:59:59";
                    Date createdAtDateStart = null;
                    Date createAtDateEnd = null;
                    try {
                        createdAtDateStart = sdf.parse(searchAtStart);
                        createAtDateEnd = sdf.parse(searchAtEnd);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    BmobDate bmobCreatedAtDateStart = new BmobDate(createdAtDateStart);
                    BmobDate bmobCreateAtDateEnd = new BmobDate(createAtDateEnd);
                    BmobQuery<Records> recordsDateStart = new BmobQuery<>();
                    recordsDateStart.addWhereGreaterThanOrEqualTo("createdAt", bmobCreatedAtDateStart);
                    BmobQuery<Records> recordsDateEnd = new BmobQuery<>();
                    recordsDateEnd.addWhereLessThanOrEqualTo("createdAt", bmobCreateAtDateEnd);
                    queries.add(recordsDateStart);
                    queries.add(recordsDateEnd);

                    BmobQuery<Records> recordsTime_Slot = new BmobQuery<>();
                    recordsTime_Slot.addWhereEqualTo("time_slot", time_slot);
                    queries.add(recordsTime_Slot);

                    BmobQuery<Records> query = new BmobQuery<>();
                    query.and(queries);
                    query.count(Records.class, new CountListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null) {
                                signed_number.setText(String.valueOf(integer) + "人");
                                unsigned_number.setText(String.valueOf(members.size() - integer) + "人");
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(ManageGroupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ManageGroupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        LayoutInflater inflater = LayoutInflater.from(ManageGroupActivity.this);
                        View view1 = inflater.inflate(R.layout.update_user_name_dialog, null);
                        final TextView edit_group_name = view1.findViewById(R.id.et_update_username);
                        edit_group_name.setHint(group.getName());
                        new AlertDialog.Builder(ManageGroupActivity.this, AlertDialog.THEME_HOLO_LIGHT).setTitle("编辑群名称").setView(view1)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (TextUtils.isEmpty(edit_group_name.getText().toString().trim())) {
                                            Toast.makeText(ManageGroupActivity.this, "群名称不得为空！", Toast.LENGTH_SHORT).show();
                                        } else {
                                            group.setName(edit_group_name.getText().toString().trim());
                                            group.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if (e == null) {
                                                        Toast.makeText(ManageGroupActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ManageGroupActivity.this, "编辑失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }).setNegativeButton("取消", null).create().show();
                        break;
                    case 3:
                        Intent intent = new Intent(ManageGroupActivity.this, GroupNoticeActivity.class);
                        intent.putExtra("group", group);
                        startActivityForResult(intent, 0);
                        break;
                    case 5:
                        Intent intent1 = new Intent(ManageGroupActivity.this, GroupMemberActivity.class);
                        intent1.putExtra("group", group);
                        intent1.putExtra("isOwner",true);
                        startActivity(intent1);
                        break;
                    case 6:
                        Intent intent6=new Intent(ManageGroupActivity.this,GroupSetActivity.class);
                        intent6.putExtra("group", group);
                        startActivityForResult(intent6,1);
                        break;
                    case 7:
                        Intent intent2 = new Intent(ManageGroupActivity.this, GroupRecordsActivity.class);
                        intent2.putExtra("group", group);
                        intent2.putExtra("members", (Serializable) members);
                        intent2.putExtra("tab",0);
                        startActivity(intent2);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dissolve_group:
                new AlertDialog.Builder(ManageGroupActivity.this, AlertDialog.THEME_HOLO_LIGHT).setTitle("提示").setMessage("确定要解散该群吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                group.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            setResult(0, new Intent());
                                            finish();
                                            Toast.makeText(ManageGroupActivity.this, "已解散", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(ManageGroupActivity.this, "操作失败" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("取消", null).create().show();
                break;
            case R.id.all_signs:
                int tab_index = 0;
                Intent intent = new Intent(ManageGroupActivity.this,GroupRecordsActivity.class);
                intent.putExtra("group", group);
                intent.putExtra("members", (Serializable) members);
                intent.putExtra("tab", tab_index);
                intent.putExtra("spinner_date_position",spinner_date_position);
                intent.putExtra("spinner_time_position",time_slot);
                startActivity(intent);
                break;
            case R.id.signed:
                tab_index =1;
                Intent intent1 = new Intent(ManageGroupActivity.this,GroupRecordsActivity.class);
                intent1.putExtra("group", group);
                intent1.putExtra("members", (Serializable) members);
                intent1.putExtra("tab", tab_index);
                intent1.putExtra("spinner_date_position",spinner_date_position);
                intent1.putExtra("spinner_time_position",time_slot);
                startActivity(intent1);
                break;
            case R.id.unsigned:
                tab_index =2;
                Intent intent2 = new Intent(ManageGroupActivity.this,GroupRecordsActivity.class);
                intent2.putExtra("group", group);
                intent2.putExtra("members", (Serializable) members);
                intent2.putExtra("tab", tab_index);
                intent2.putExtra("spinner_date_position",spinner_date_position);
                intent2.putExtra("spinner_time_position",time_slot);
                startActivity(intent2);
                break;
        }
    }

    private void latest_date_time() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmm");
        try {
            Date current = sdf.parse(sdf.format(new Date(System.currentTimeMillis())));
            Date start = sdf.parse(group.getStart_date() + " " + group.getStarts_time().get(0));
            Date end = sdf.parse(group.getEnd_date() + " " + group.getStarts_time().get(group.getStarts_time().size() - 1));
            String curr_date = sdf.format(current).substring(0,8);
            String start_date = group.getStart_date();
            String end_date = group.getEnd_date();
            if (current.before(start)) {
                date = group.getStart_date();
                time_slot = 1;
                if(getDays(start_date,curr_date).size()==0){
                    spinner_date_position=0;
                }else {
                    spinner_date_position=1;
                }
                latest_date.setText(date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6));
                latest_time.setText(group.getStarts_time().get(time_slot - 1).substring(0, 2) + ":" + group.getStarts_time().get(time_slot - 1).substring(2) +
                        "至" + group.getEnds_time().get(time_slot - 1).substring(0, 2) + ":" + group.getEnds_time().get(time_slot - 1).substring(2));
            } else if (current.after(end)) {
                date = group.getEnd_date();
                time_slot = group.getStarts_time().size();
                spinner_date_position=getDays(start_date,end_date).size();
                latest_date.setText(date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6));
                latest_time.setText(group.getStarts_time().get(time_slot - 1).substring(0, 2) + ":" + group.getStarts_time().get(time_slot - 1).substring(2) +
                        "至" + group.getEnds_time().get(time_slot - 1).substring(0, 2) + ":" + group.getEnds_time().get(time_slot - 1).substring(2));
            } else {
                int current_time = Integer.parseInt(sdf.format(new Date(System.currentTimeMillis())).substring(9));
                if (current_time < Integer.parseInt(group.getStarts_time().get(0))) {
                    time_slot = group.getStarts_time().size();
                    Date yesterday = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
                    date = sdf.format(yesterday).substring(0, 8);
                } else {
                    for (int i = 0; i < group.getStarts_time().size(); i++) {
                        if (current_time >= Integer.parseInt(group.getStarts_time().get(i))) {
                            time_slot = i + 1;
                        }
                    }
                    date = sdf.format(current).substring(0, 8);
                }
                spinner_date_position=getDays(start_date,date).size();
                latest_date.setText(date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6));
                latest_time.setText(group.getStarts_time().get(time_slot - 1).substring(0, 2) + ":" + group.getStarts_time().get(time_slot - 1).substring(2) +
                        "至" + group.getEnds_time().get(time_slot - 1).substring(0, 2) + ":" + group.getEnds_time().get(time_slot - 1).substring(2));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private List<String> getDays(String bigtimeStr, String endTimeStr) throws ParseException {
        Date bigtime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse(bigtimeStr + " 00:00:00");
        Date endtime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse(endTimeStr + " 00:00:00");
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
}
