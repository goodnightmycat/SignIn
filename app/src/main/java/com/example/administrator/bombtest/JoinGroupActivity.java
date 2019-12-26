package com.example.administrator.bombtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Student;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class JoinGroupActivity extends AppCompatActivity {
    private Group mGroup;
    private Student student;
    private Button join;
    private LinearLayout linearLayout;
    private TextView search_result,group_name,group_owner_name,group_members_count,group_location_describe;
    private ImageView back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        title=findViewById(R.id.titleText_back);
        title.setText("加入群");

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        student = BmobUser.getCurrentUser(Student.class);
        final EditText group_number = findViewById(R.id.et_group_number);
        Button search_group = findViewById(R.id.btn_search_group);
        join = findViewById(R.id.btn_join);
        linearLayout = findViewById(R.id.ll_search_result);
        search_result = findViewById(R.id.tv_search_result);
        group_name = findViewById(R.id.tv_search_group_name);
        group_owner_name = findViewById(R.id.tv_search_group_owner);
        group_members_count = findViewById(R.id.tv_search_group_members_count);
        group_location_describe=findViewById(R.id.tv_search_group_location_describe);

        search_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(JoinGroupActivity.this);
                progressDialog.show();
                BmobQuery<Group> query  =new BmobQuery<>();
                query.addWhereEqualTo("group_number",Integer.parseInt(group_number.getText().toString()));
                query.include("owner");
                query.findObjects(new FindListener<Group>() {
                    @Override
                    public void done(List<Group> list, BmobException e) {
                        if (e == null) {
                            progressDialog.dismiss();
                            if(list.isEmpty()){
                                search_result.setText("没有查找到相关的群");
                                linearLayout.setVisibility(View.VISIBLE);
                                group_name.setVisibility(View.INVISIBLE);
                                group_owner_name.setVisibility(View.INVISIBLE);
                                group_members_count.setVisibility(View.INVISIBLE);
                                group_location_describe.setVisibility(View.INVISIBLE);

                            }else {
                                mGroup = list.get(0);
                                search_result.setText("查找到群"+group_number.getText().toString()+"的信息如下：");
                                group_name.setText("群名称："+mGroup.getName());
                                group_owner_name.setText("群主："+mGroup.getOwner().getUsername());


                                group_members_count(mGroup);
                                group_location_describe.setText("签到位置："+mGroup.getLocation_describe());

                                linearLayout.setVisibility(View.VISIBLE);
                                group_name.setVisibility(View.VISIBLE);
                                group_owner_name.setVisibility(View.VISIBLE);
                                group_members_count.setVisibility(View.VISIBLE);
                                group_location_describe.setVisibility(View.VISIBLE);
                                join.setEnabled(true);
                            }
                        } else {
                            Toast.makeText(JoinGroupActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobRelation relation1 = new BmobRelation();
                relation1.add(student);
                mGroup.setMembers(relation1);
                mGroup.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        BmobRelation relation2 = new BmobRelation();
                        relation2.add(mGroup);
                        student.setJoin_group(relation2);
                        student.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    setResult(4,new Intent());
                                    finish();
                                    Toast.makeText(JoinGroupActivity.this,"加入成功",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(JoinGroupActivity.this,"加入失败"+e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void group_members_count(Group group) {
        BmobQuery<Student> query = new BmobQuery<>();
        query.addWhereRelatedTo("members",new BmobPointer(group));
        query.count(Student.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if(e==null){
                    group_members_count.setText("群成员："+String.valueOf(integer));
                }else {
                    group_members_count.setText(e.getMessage());
                }
            }
        });
    }
}
