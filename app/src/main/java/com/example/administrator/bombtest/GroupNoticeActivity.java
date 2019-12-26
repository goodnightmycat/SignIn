package com.example.administrator.bombtest;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Group;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class GroupNoticeActivity extends AppCompatActivity {
    private ImageView back;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notice);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        title=findViewById(R.id.titleText_back);
        title.setText("修改群公告");

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Group group = (Group)getIntent().getSerializableExtra("group");
        final EditText group_notice = findViewById(R.id.et_group_notice);
        Button save_notice = findViewById(R.id.btn_save_notice);
        group_notice.setText(group.getNotice());
        save_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.setNotice(group_notice.getText().toString().trim());
                group.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Toast.makeText(GroupNoticeActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(GroupNoticeActivity.this,"保存失败："+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
