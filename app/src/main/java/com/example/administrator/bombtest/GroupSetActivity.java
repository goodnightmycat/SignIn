package com.example.administrator.bombtest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Student;

import java.util.Calendar;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class GroupSetActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView start_date, end_date;
    private Calendar calendar;
    private String desc;
    private Group mGroup = new Group();
    private double lat=1000;
    private double lon=1000;
    private String location_describe;
    private EditText et_location_describe;
    private ImageView back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_group);
        mGroup=(Group)getIntent().getSerializableExtra("group");

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        title=findViewById(R.id.titleText_back);
        title.setText("修改签到规则");

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    private void init() {
        calendar = Calendar.getInstance();
        Button btn_create = findViewById(R.id.btn_create);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);

        start_date.setOnClickListener(this);
        end_date.setOnClickListener(this);

        btn_create.setOnClickListener(this);

        Button bt_set_location=findViewById(R.id.bt_set_location);
        bt_set_location.setOnClickListener(this);

        et_location_describe=findViewById(R.id.et_location_describe);
    }


    private void create() {
        mGroup.setOwner(BmobUser.getCurrentUser(Student.class));

        //先判断EditText是否为空，再取值

        //先判断是否选择开始日期，再去取值
        if (!TextUtils.equals(start_date.getText(), "开始日期")) {
            mGroup.setStart_date(
                    start_date.getText().toString().substring(0, 4)
                            + start_date.getText().toString().substring(5, 7)
                            + start_date.getText().toString().substring(8, 10));
        } else {
            Toast.makeText(this, "开始日期不得为空", Toast.LENGTH_LONG).show();
            return;
        }
        //判断结束日期并取值
        if (!TextUtils.equals(end_date.getText(), "结束日期")) {
            mGroup.setEnd_date(end_date.getText().toString().substring(0, 4)
                    + end_date.getText().toString().substring(5, 7)
                    + end_date.getText().toString().substring(8, 10));
        } else {
            Toast.makeText(this, "结束日期不得为空", Toast.LENGTH_LONG).show();
            return;
        }


boolean flag=true;

        if(lat==1000&&lon==1000)
        {
            flag=false;
            Toast.makeText(GroupSetActivity.this, "位置尚未设置，请重新设置", Toast.LENGTH_LONG).show();
        }

        if(flag) {


            BmobGeoPoint location = new BmobGeoPoint(lon, lat);

            mGroup.setLocation(location);
            mGroup.setLocation_describe(et_location_describe.getText().toString());

            mGroup.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null) {
                        Toast.makeText(GroupSetActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else  Toast.makeText(GroupSetActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }
        }

    private void choose_date(final TextView textView) {
        final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                desc = String.format("%d-%02d-%02d", year, month, dayOfMonth);
                textView.setText(desc);
            }
        };
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.show();
        datePickerDialog.getButton(BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = datePickerDialog.getDatePicker().getYear();
                int month = datePickerDialog.getDatePicker().getMonth() + 1;
                int day = datePickerDialog.getDatePicker().getDayOfMonth();
                int choosed_date = year * 10000 + month * 100 + day;

                //判断条件，选择的日期不能早于当前日期
                Boolean condition = (year > calendar.get(Calendar.YEAR)) ||
                        (year == calendar.get(Calendar.YEAR) && month > calendar.get(Calendar.MONTH) + 1) ||
                        (year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH) + 1 && day >= calendar.get(Calendar.DAY_OF_MONTH));

                if((textView==start_date&&TextUtils.equals(end_date.getText(),"结束日期"))||(textView==end_date&&TextUtils.equals(start_date.getText(),"开始日期"))){
                    if(condition){
                        listener.onDateSet(datePickerDialog.getDatePicker(), year, month, day);
                        datePickerDialog.dismiss();
                    }else {
                        Toast.makeText(GroupSetActivity.this, "选择日期无效", Toast.LENGTH_LONG).show();
                    }
                }else if(textView==start_date&&!TextUtils.equals(end_date.getText(),"结束日期")){
                    int temp_end = Integer.parseInt(end_date.getText().toString().substring(0, 4) + end_date.getText().toString().substring(5, 7) + end_date.getText().toString().substring(8, 10));
                    if(condition&&choosed_date<=temp_end){
                        listener.onDateSet(datePickerDialog.getDatePicker(), year, month, day);
                        datePickerDialog.dismiss();
                    }else {
                        Toast.makeText(GroupSetActivity.this, "选择日期无效", Toast.LENGTH_LONG).show();
                    }
                }else if(textView==end_date&&!TextUtils.equals(start_date.getText(),"开始日期")){
                    int temp_start = Integer.parseInt(start_date.getText().toString().substring(0, 4) + start_date.getText().toString().substring(5, 7) + start_date.getText().toString().substring(8, 10));
                    if(condition&&choosed_date>=temp_start){
                        listener.onDateSet(datePickerDialog.getDatePicker(), year, month, day);
                        datePickerDialog.dismiss();
                    }else {
                        Toast.makeText(GroupSetActivity.this, "选择日期无效", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.start_date:
                choose_date(start_date);
                break;
            case R.id.end_date:
                choose_date(end_date);
                break;

            case R.id.btn_create:
                create();
                break;
            case R.id.bt_set_location:
            {  Intent intent=new Intent(GroupSetActivity.this,LocationActivity.class);
                startActivityForResult(intent,1);
                break;}
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&resultCode==1)
        {
            lon=data.getDoubleExtra("lon",1000);
            lat=data.getDoubleExtra("lat",1000);
            location_describe=data.getStringExtra("location_describe");
            et_location_describe.setText(location_describe);
        }
    }
}
