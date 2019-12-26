package com.example.administrator.bombtest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Student;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditText;
    private TextView start_date, end_date;
    private TextView start_time_1, start_time_2, start_time_3, start_time_4, start_time_5;
    private TextView end_time_1, end_time_2, end_time_3, end_time_4, end_time_5;
    private Calendar calendar;
    private String desc;
    private RelativeLayout choose_time_1, choose_time_2, choose_time_3, choose_time_4, choose_time_5;
    private Group mGroup = new Group();
    private ArrayList<TextView> temp_starts_time = new ArrayList<>(5);
    private ArrayList<TextView> temp_ends_time = new ArrayList<>(5);
    private double lat=1000;
    private double lon=1000;
    private String location_describe;
    private CheckBox checkBox;
    private EditText et_location_describe;
    private ImageView back;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        init();
    }

    private void init() {

        title=findViewById(R.id.titleText_back);
        title.setText("创建群");

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        calendar = Calendar.getInstance();
        mEditText = findViewById(R.id.create_group_name);
        Button btn_create = findViewById(R.id.btn_create);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        start_time_1 = findViewById(R.id.start_time_1);
        start_time_2 = findViewById(R.id.start_time_2);
        start_time_3 = findViewById(R.id.start_time_3);
        start_time_4 = findViewById(R.id.start_time_4);
        start_time_5 = findViewById(R.id.start_time_5);
        end_time_1 = findViewById(R.id.end_time_1);
        end_time_2 = findViewById(R.id.end_time_2);
        end_time_3 = findViewById(R.id.end_time_3);
        end_time_4 = findViewById(R.id.end_time_4);
        end_time_5 = findViewById(R.id.end_time_5);
        choose_time_1 = findViewById(R.id.choose_time_1);
        choose_time_2 = findViewById(R.id.choose_time_2);
        choose_time_3 = findViewById(R.id.choose_time_3);
        choose_time_4 = findViewById(R.id.choose_time_4);
        choose_time_5 = findViewById(R.id.choose_time_5);
        ImageButton add = findViewById(R.id.add);
        ImageButton delete_1 = findViewById(R.id.delete_1);
        ImageButton delete_2 = findViewById(R.id.delete_2);
        ImageButton delete_3 = findViewById(R.id.delete_3);
        ImageButton delete_4 = findViewById(R.id.delete_4);
        ImageButton delete_5 = findViewById(R.id.delete_5);
        choose_time_2.setVisibility(View.GONE);
        choose_time_3.setVisibility(View.GONE);
        choose_time_4.setVisibility(View.GONE);
        choose_time_5.setVisibility(View.GONE);
        start_date.setOnClickListener(this);
        end_date.setOnClickListener(this);
        start_time_1.setOnClickListener(this);
        start_time_2.setOnClickListener(this);
        start_time_3.setOnClickListener(this);
        start_time_4.setOnClickListener(this);
        start_time_5.setOnClickListener(this);
        end_time_1.setOnClickListener(this);
        end_time_2.setOnClickListener(this);
        end_time_3.setOnClickListener(this);
        end_time_4.setOnClickListener(this);
        end_time_5.setOnClickListener(this);
        add.setOnClickListener(this);
        btn_create.setOnClickListener(this);
        delete_1.setOnClickListener(this);
        delete_2.setOnClickListener(this);
        delete_3.setOnClickListener(this);
        delete_4.setOnClickListener(this);
        delete_5.setOnClickListener(this);

        temp_starts_time.add(0, start_time_1);
        temp_starts_time.add(1, start_time_2);
        temp_starts_time.add(2, start_time_3);
        temp_starts_time.add(3, start_time_4);
        temp_starts_time.add(4, start_time_5);
        temp_ends_time.add(0, end_time_1);
        temp_ends_time.add(1, end_time_2);
        temp_ends_time.add(2, end_time_3);
        temp_ends_time.add(3, end_time_4);
        temp_ends_time.add(4, end_time_5);

        Button bt_set_location=findViewById(R.id.bt_set_location);
        bt_set_location.setOnClickListener(this);
        checkBox=findViewById(R.id.cb_join_group);

        et_location_describe=findViewById(R.id.et_location_describe);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                if (!add_choose()) {
                    Toast.makeText(this, "已达上限", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.delete_1:
                start_time_1.setText("开始时间");
                end_time_1.setText("结束时间");
                choose_time_1.setVisibility(View.GONE);
                break;
            case R.id.delete_2:
                start_time_2.setText("开始时间");
                end_time_2.setText("结束时间");
                choose_time_2.setVisibility(View.GONE);
                break;
            case R.id.delete_3:
                start_time_3.setText("开始时间");
                end_time_3.setText("结束时间");
                choose_time_3.setVisibility(View.GONE);
                break;
            case R.id.delete_4:
                start_time_4.setText("开始时间");
                end_time_4.setText("结束时间");
                choose_time_4.setVisibility(View.GONE);
                break;
            case R.id.delete_5:
                start_time_5.setText("开始时间");
                end_time_5.setText("结束时间");
                choose_time_5.setVisibility(View.GONE);
                break;
            case R.id.start_date:
                choose_date(start_date);
                break;
            case R.id.end_date:
                choose_date(end_date);
                break;
            case R.id.start_time_1:
                choose_time(start_time_1);
                break;
            case R.id.start_time_2:
                choose_time(start_time_2);
                break;
            case R.id.start_time_3:
                choose_time(start_time_3);
                break;
            case R.id.start_time_4:
                choose_time(start_time_4);
                break;
            case R.id.start_time_5:
                choose_time(start_time_5);
                break;
            case R.id.end_time_1:
                choose_time(end_time_1);
                break;
            case R.id.end_time_2:
                choose_time(end_time_2);
                break;
            case R.id.end_time_3:
                choose_time(end_time_3);
                break;
            case R.id.end_time_4:
                choose_time(end_time_4);
                break;
            case R.id.end_time_5:
                choose_time(end_time_5);
                break;
            case R.id.btn_create:
                create();
                break;
            case R.id.bt_set_location:
            {  Intent intent=new Intent(CreateGroupActivity.this,LocationActivity.class);
                startActivityForResult(intent,1);
                break;}
        }
    }

    private void create() {
        mGroup.setOwner(BmobUser.getCurrentUser(Student.class));

        //先判断EditText是否为空，再取值
        if (!TextUtils.isEmpty(mEditText.getText())) {
            mGroup.setName(mEditText.getText().toString());
        } else {
            Toast.makeText(this, "群名称不得为空", Toast.LENGTH_LONG).show();
            return;
        }
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

        // TODO:判断时间日期的正确性，结束日期不能早于开始日期

        ArrayList<String> starts_time = new ArrayList   <>();
        ArrayList<String> ends_time = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (!temp_starts_time.get(i).getText().toString().equals("开始时间")&&!temp_ends_time.get(i).getText().toString().equals("结束时间")) {
                starts_time.add(temp_starts_time.get(i).getText().toString().substring(0, 2) + temp_starts_time.get(i).getText().toString().substring(3));
                ends_time.add(temp_ends_time.get(i).getText().toString().substring(0, 2) + temp_ends_time.get(i).getText().toString().substring(3));

            }

        }
        boolean flag=true;
        for (int i = 0; i < 5; i++) {

            if (!temp_starts_time.get(i).getText().toString().equals("开始时间") && temp_ends_time.get(i).getText().toString().equals("结束时间"))
            {
                Toast.makeText(CreateGroupActivity.this, "结束时间不能为空，请重新设置！", Toast.LENGTH_LONG).show();
                flag=false;
                break;


            }
                if(temp_starts_time.get(i).getText().toString().equals("开始时间") && !temp_ends_time.get(i).getText().toString().equals("结束时间"))
                {
                    Toast.makeText(CreateGroupActivity.this, "结束时间不能为空，请重新设置！", Toast.LENGTH_LONG).show();
                    flag=false;
                    break;

            }
        }
        int start[]=new int[5];
        int end[]=new int[5];
        MyTime[] myTimes=new MyTime[5];

        for(int i=0;i<starts_time.size();i++)
        {
            start[i]=Integer.parseInt(starts_time.get(i));
            end[i]=Integer.parseInt(ends_time.get(i));
            myTimes[i]=new MyTime(start[i],end[i]);

         if(start[i]>end[i])
         {    Toast.makeText(CreateGroupActivity.this, "结束时间不能晚于开始时间，请重新设置！", Toast.LENGTH_LONG).show();
                flag=false;
                break;}
        }
        if (flag) {
            Log.i("aaaa", " "+String.valueOf(start[0]));
            int temp;
            int max;


            for(int i=0;i<starts_time.size();i++)
{
    for(int j=i;j<starts_time.size();j++)
    {
        max=start[i];
        if(start[j]<max)
        {
            temp=start[j];
            start[j]=start[i];
            start[i]=temp;

        }


    }
}

            for(int i=0;i<starts_time.size();i++)
            {
                for(int j=0;j<starts_time.size();j++)
                {
                    if(start[i]==myTimes[j].getMstart())
                    {
                        end[i]=myTimes[j].getMend();
                        break;
                    }
                }
            }
            for(int i=0;i<starts_time.size()-1;i++)
            {
                if(start[i+1]<end[i])
                {
                    Toast.makeText(CreateGroupActivity.this, "时间段冲突，请重新设置！", Toast.LENGTH_LONG).show();
                    flag=false;break;
                }
            }


            Log.i("aaaa"," "+String.valueOf(start[0])+" "+String.valueOf(end[0])+" "+String.valueOf(start[1])+" "+String.valueOf(end[1])+" ");

        }


if(lat==1000&&lon==1000)
{
    flag=false;
    Toast.makeText(CreateGroupActivity.this, "位置尚未设置，请重新设置", Toast.LENGTH_LONG).show();
}

if(flag)

{
    ArrayList<String> starts_time1 = new ArrayList<>();
    ArrayList<String> ends_time1 = new ArrayList<>();
    String a;
    String b;
    for(int i=0;i<starts_time.size();i++)
    {
        a=String.valueOf(start[i]);
       if(start[i]<1000) {
           a = "0"+a;
       }


        b=String.valueOf(end[i]);
        if(end[i]<1000) {
            b = "0"+b;
        }


        starts_time1.add(a);
        ends_time1.add(b);


    }
    BmobGeoPoint location = new BmobGeoPoint(lon, lat);
    long t = System.currentTimeMillis();//获得当前时间的毫秒数
    Random rd = new Random(t);//作为种子数传入到Random的构造器中
    int number = Math.abs(rd.nextInt() % 1000000);
    if (number < 100000) {
        number = number * 10;
    }
    mGroup.setStarts_time(starts_time1);
    mGroup.setEnds_time(ends_time1);
    mGroup.setLocation(location);
    mGroup.setLocation_describe(et_location_describe.getText().toString());
    mGroup.setGroup_number(number);

    mGroup.save(new SaveListener<String>() {
        @Override
        public void done(String s, BmobException e) {
            if (e == null) {
                if (checkBox.isChecked()) {
                 final    Student student1=BmobUser.getCurrentUser(Student.class);
                    BmobRelation relation1 = new BmobRelation();
                    relation1.add(student1);
                    mGroup.setMembers(relation1);
                    mGroup.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            BmobRelation relation2 = new BmobRelation();
                            relation2.add(mGroup);
                            student1.setJoin_group(relation2);
                            student1.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        setResult(4,new Intent());
                                        finish();
                                        Toast.makeText(CreateGroupActivity.this, "创建群成功" , Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(CreateGroupActivity.this, "加入群失败"+e.getMessage() , Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });


                } else {
                    setResult(1, new Intent());
                    finish();
                    Toast.makeText(CreateGroupActivity.this, "创建群成功" , Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(CreateGroupActivity.this, "创建群失败,再点一次试试" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    });
}
    }

    class MyTime{
        private int mstart;
        private int mend;
        public MyTime(int a,int b)
        {
            mstart=a;
            mend=b;
        }

        public int getMend() {
            return mend;
        }

        public int getMstart() {
            return mstart;
        }

    }

    private Boolean add_choose() {
        if (choose_time_1.getVisibility() != View.VISIBLE) {
            choose_time_1.setVisibility(View.VISIBLE);
            return true;
        } else if (choose_time_2.getVisibility() != View.VISIBLE) {
            choose_time_2.setVisibility(View.VISIBLE);
            return true;
        } else if (choose_time_3.getVisibility() != View.VISIBLE) {
            choose_time_3.setVisibility(View.VISIBLE);
            return true;
        } else if (choose_time_4.getVisibility() != View.VISIBLE) {
            choose_time_4.setVisibility(View.VISIBLE);
            return true;
        } else if (choose_time_5.getVisibility() != View.VISIBLE) {
            choose_time_5.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
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
                        Toast.makeText(CreateGroupActivity.this, "选择日期无效", Toast.LENGTH_LONG).show();
                    }
                }else if(textView==start_date&&!TextUtils.equals(end_date.getText(),"结束日期")){
                    int temp_end = Integer.parseInt(end_date.getText().toString().substring(0, 4) + end_date.getText().toString().substring(5, 7) + end_date.getText().toString().substring(8, 10));
                    if(condition&&choosed_date<=temp_end){
                        listener.onDateSet(datePickerDialog.getDatePicker(), year, month, day);
                        datePickerDialog.dismiss();
                    }else {
                        Toast.makeText(CreateGroupActivity.this, "选择日期无效", Toast.LENGTH_LONG).show();
                    }
                }else if(textView==end_date&&!TextUtils.equals(start_date.getText(),"开始日期")){
                    int temp_start = Integer.parseInt(start_date.getText().toString().substring(0, 4) + start_date.getText().toString().substring(5, 7) + start_date.getText().toString().substring(8, 10));
                    if(condition&&choosed_date>=temp_start){
                        listener.onDateSet(datePickerDialog.getDatePicker(), year, month, day);
                        datePickerDialog.dismiss();
                    }else {
                        Toast.makeText(CreateGroupActivity.this, "选择日期无效", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void choose_time(final TextView textView) {
        final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                desc = String.format("%02d:%02d", hourOfDay, minute);
                textView.setText(desc);
            }
        };
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.show();
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
