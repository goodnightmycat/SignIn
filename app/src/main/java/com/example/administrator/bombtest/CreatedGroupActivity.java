package com.example.administrator.bombtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Student;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class CreatedGroupActivity extends AppCompatActivity {
    private Button create_button;
    private ListView create_list_view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private ImageView back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_group);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        progressDialog = new ProgressDialog(this);

        title=findViewById(R.id.titleText_back);
        title.setText("创建的群");

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        create_button = findViewById(R.id.create_button);
        create_list_view = findViewById(R.id.create_list_view);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CreatedGroupActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        searchCreatedGroup();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchCreatedGroup();
            }
        });
    }

    private void searchCreatedGroup() {
        //查询某个用户创建的群
        progressDialog.show();
        BmobQuery<Group> query = new BmobQuery<>();
        query.addWhereEqualTo("owner", BmobUser.getCurrentUser(Student.class));
        query.include("owner");
        query.findObjects(new FindListener<Group>() {
            @Override
            public void done(final List<Group> object, BmobException e) {
                if (e == null) {
                    swipeRefreshLayout.setRefreshing(false);
                    GroupAdapter groupAdapter = new GroupAdapter(CreatedGroupActivity.this, R.layout.created_group_item, object);
                    create_list_view.setAdapter(groupAdapter);
                    create_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent();
                            intent.setClass(CreatedGroupActivity.this, ManageGroupActivity.class);
                            intent.putExtra("group", object.get(position));
                            startActivityForResult(intent, 0);
                        }
                    });
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(CreatedGroupActivity.this, "查询失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        searchCreatedGroup();
    }
}
