package com.example.administrator.bombtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class GroupDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private Group mgroup = new Group();
    private ListView group_detail_list;
    private Button exit_group;
    private ImageView back;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        title=findViewById(R.id.titleText_back);
        title.setText("群详情");

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mgroup = (Group) getIntent().getSerializableExtra("group");
        group_detail_list = findViewById(R.id.group_detail_list);
        exit_group = findViewById(R.id.exit_group);
        List<Map<String, Object>> mapList = new ArrayList<>();
        String[] item_name = new String[]{"群名称", "群主", "群创建时间", "群公告","群号", "群成员", "签到规则"};
        final String[] item_value = new String[]{mgroup.getName(), mgroup.getOwner().getUsername(), String.valueOf(mgroup.getCreatedAt()), mgroup.getNotice(),String.valueOf(mgroup.getGroup_number()), ">", ">"};
        if(mgroup.getNotice()==null||mgroup.getNotice().length()==0){
            item_value[3]="空";
        }else {
            item_value[3]=">";
        }

        for (int i = 0; i < item_name.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("item_name", item_name[i]);
            map.put("item_value", item_value[i]);
            mapList.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(GroupDetailActivity.this,
                mapList,
                R.layout.group_detail_list_item,
                new String[]{"item_name", "item_value"},
                new int[]{R.id.item_name, R.id.item_value});
        group_detail_list.setAdapter(adapter);
        group_detail_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 3:
                        if(item_value[3].equals(">")){
                            new AlertDialog.Builder(GroupDetailActivity.this,AlertDialog.THEME_HOLO_LIGHT).setTitle("群公告").setMessage(mgroup.getNotice())
                                    .setPositiveButton("确定",null).create().show();
                        }
                        break;
                    case 5:
                        Intent intent = new Intent();
                        intent.setClass(GroupDetailActivity.this,GroupMemberActivity.class);
                        intent.putExtra("group",mgroup);
                        startActivity(intent);
                        break;
                    case 6:
                        LayoutInflater layoutInflater = LayoutInflater.from(GroupDetailActivity.this);
                        View view1 = layoutInflater.inflate(R.layout.record_rules_dialog,null);
                        TextView start_date = view1.findViewById(R.id.rules_start_date);
                        TextView end_date = view1.findViewById(R.id.rules_end_date);
                        TextView location_describe = view1.findViewById(R.id.rules_location_describe);
                        start_date.setText(mgroup.getStart_date().substring(0,4)+"-"+mgroup.getStart_date().substring(4,6)+"-"+mgroup.getStart_date().substring(6));
                        end_date.setText(mgroup.getEnd_date().substring(0,4)+"-"+mgroup.getEnd_date().substring(4,6)+"-"+mgroup.getEnd_date().substring(6));
                        location_describe.setText(mgroup.getLocation_describe());
                        new AlertDialog.Builder(GroupDetailActivity.this,AlertDialog.THEME_HOLO_LIGHT).setTitle("签到规则").setView(view1)
                                .setPositiveButton("确定",null).create().show();
                        break;
                }
            }
        });

        exit_group.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        new AlertDialog.Builder(GroupDetailActivity.this,AlertDialog.THEME_HOLO_LIGHT).setTitle("提示").setMessage("确定要退出该群吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    BmobException e1,e2;
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Student student = BmobUser.getCurrentUser(Student.class);
                        BmobRelation relation1 = new BmobRelation();
                        relation1.remove(student);
                        mgroup.setMembers(relation1);
                        mgroup.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                e1 = e;
                            }
                        });
                        BmobRelation relation2 = new BmobRelation();
                        relation2.remove(mgroup);
                        student.setJoin_group(relation2);
                        student.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                e2 = e;
                            }
                        });
                        if(e1==null&&e2==null){
                            setResult(2,new Intent());
                            finish();
                            Toast.makeText(GroupDetailActivity.this,"已退出",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(GroupDetailActivity.this,"操作失败",Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
    }
}
