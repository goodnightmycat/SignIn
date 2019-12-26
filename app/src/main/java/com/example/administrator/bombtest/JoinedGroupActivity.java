package com.example.administrator.bombtest;

import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import java.util.List;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.content.Intent;

import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Student;


public class JoinedGroupActivity extends AppCompatActivity {
    private ListView mlistview;
    public Student student;
    private Button add_group;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private ImageView back;
    private TextView title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_group);
        ActionBar actionBar=getSupportActionBar();
          actionBar.hide();

        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        title=findViewById(R.id.titleText_back);
        title.setText("加入的群");

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mlistview = findViewById(R.id.joined_group_listview);
        swipeRefreshLayout= findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        add_group=findViewById(R.id.bt_join_group);
        add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(JoinedGroupActivity.this,JoinGroupActivity.class);
                startActivityForResult(intent,4);
            }
        });
        searchJoinedGroup();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchJoinedGroup();
            }
        });
    }

    private void searchJoinedGroup() {
        //查询某个用户加入的群
        BmobQuery<Group> query = new BmobQuery<>();
        student= BmobUser.getCurrentUser(Student.class);
        query.addWhereRelatedTo("join_group",new BmobPointer(student));
        query.include("owner");
        query.findObjects(new FindListener<Group>() {
            @Override
            public void done(final List<Group> object, BmobException e) {
                if(e==null){
                    swipeRefreshLayout.setRefreshing(false);
                    GroupAdapter groupAdapter = new GroupAdapter(JoinedGroupActivity.this, R.layout.joined_group_item,object);
                    mlistview.setAdapter(groupAdapter);
                    mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent();
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("group",object.get(position));
                            intent.putExtra("data",bundle);
                            intent.setClass(JoinedGroupActivity.this,GroupActivity.class);
                            startActivityForResult(intent,3);
                        }
                    });
                    progressDialog.dismiss();
                }else{
                    Toast.makeText(JoinedGroupActivity.this,"查询失败："+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        searchJoinedGroup();
    }
}

