package com.example.administrator.bombtest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Student;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class GroupMemberActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private ListView group_member;
    private Group group;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        title=findViewById(R.id.titleText_back);
        title.setText("群成员");

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        group = (Group) getIntent().getSerializableExtra("group");
        group_member = findViewById(R.id.group_member);
        group_member.setEnabled(false);
        swipeRefreshLayout = findViewById(R.id.refresh_members);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        progressDialog = new ProgressDialog(this);
        search_members();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search_members();
            }
        });

    }

    private void search_members() {
        progressDialog.show();
        BmobQuery<Student> query = new BmobQuery<>();
        query.addWhereRelatedTo("members",new BmobPointer(group));
        query.addQueryKeys("username");
        query.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if (e == null) {
                    swipeRefreshLayout.setRefreshing(false);
                    String name_list[] = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        name_list[i] = list.get(i).getUsername();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(GroupMemberActivity.this,android.R.layout.simple_list_item_1,name_list);
                    group_member.setAdapter(adapter);
                    progressDialog.dismiss();
                }else {
                    Toast.makeText(GroupMemberActivity.this,"操作成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
