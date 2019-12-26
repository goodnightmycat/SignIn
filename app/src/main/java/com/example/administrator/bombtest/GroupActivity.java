package com.example.administrator.bombtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CalendarView;
import android.widget.ArrayAdapter;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.administrator.bombtest.Face.Base64Util;
import com.example.administrator.bombtest.Face.facGetToken.FaceGetList;
import com.example.administrator.bombtest.Face.faceMatch.FaceMatch;
import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Records;
import com.example.administrator.bombtest.Model.Student;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class GroupActivity extends Activity {
    private Group mgroup = new Group();
    private int CurrentDate, CurrentTime;
    private int Start_Date, End_Date;
    private ArrayList<String> Starts_Time = new ArrayList<>();
    private ArrayList<String> Ends_Time = new ArrayList<>();
    private boolean timeisRight = false;
    private boolean dateisRight = false;
    private int current_time_slot = -1;
    private Records records = new Records();
    private Button mbtn_record, group_detail;
    private ListView record_list_view;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String createdAtStart, createdAtEnd;
    private String searchAtStart, searchAtEnd;
    private String RecordsResult[];
    private BmobDate bmobCreatedAtDateStart, bmobCreateAtDateEnd;
    private boolean isFirstLocation = true;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener;
    private BmobGeoPoint group_location;
    private BDLocation mLocation;
    private int year, month, day;
    private boolean isRecorded = false;
    private int recordedTimeSlot[];
    private String image;
    private AlarmManager alarmManager;
    private BmobGeoPoint geolocation;
    private ProgressDialog progressDialog;
    private ImageView back;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        alarmManager = (AlarmManager) GroupActivity.this.getSystemService(Context.ALARM_SERVICE);


        Bundle bundle=getIntent().getBundleExtra("data");
        mgroup = (Group) bundle.getSerializable("group");
        title=findViewById(R.id.titleText_back);
        title.setText(mgroup.getName());

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mbtn_record = findViewById(R.id.btn_record);
        group_detail = findViewById(R.id.group_detail);
        Start_Date = Integer.parseInt(mgroup.getStart_date());
        End_Date = Integer.parseInt(mgroup.getEnd_date());
        Starts_Time = mgroup.getStarts_time();
        Ends_Time = mgroup.getEnds_time();
        RecordsResult = new String[mgroup.getStarts_time().size()];
        recordedTimeSlot = new int[Starts_Time.size()];
        for (int i = 0; i < Starts_Time.size(); i++) {
            recordedTimeSlot[i] = 0;
        }
        group_location = mgroup.getLocation();
        record_list_view = findViewById(R.id.record_list_view);
        record_list_view.setEnabled(false);
        CalendarView calendarView = findViewById(R.id.calendarView);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        myListener = new BaiduLocationListener();
        mLocationClient = new LocationClient(this);
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);


        //配置定位参数
        initLocation();
        //开始定位
        mLocationClient.start();

        //获取服务器时间
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {
                if (e == null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
                    String date = dateFormat.format(new Date(aLong * 1000L));
                    String time = timeFormat.format(new Date(aLong * 1000L));
                    CurrentDate = Integer.parseInt(date);
                    CurrentTime = Integer.parseInt(time);
                    searchAtStart = String.valueOf(CurrentDate).substring(0, 4) + "-" + String.valueOf(CurrentDate).substring(4, 6) + "-" + String.valueOf(CurrentDate).substring(6) + " 00:00:00";
                    searchAtEnd = String.valueOf(CurrentDate).substring(0, 4) + "-" + String.valueOf(CurrentDate).substring(4, 6) + "-" + String.valueOf(CurrentDate).substring(6) + " 23:59:59";
                    Date createdAtDateStart = null;
                    try {
                        createdAtDateStart = sdf.parse(searchAtStart);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    Date createAtDateEnd = null;
                    try {
                        createAtDateEnd = sdf.parse(searchAtEnd);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    bmobCreatedAtDateStart = new BmobDate(createdAtDateStart);
                    bmobCreateAtDateEnd = new BmobDate(createAtDateEnd);
                    //查询条件一：user_id
                    BmobQuery<Records> recordsUser_id = new BmobQuery<>();
                    recordsUser_id.addWhereEqualTo("user_id", BmobUser.getCurrentUser(Student.class).getObjectId());
                    //查询条件二：group_id
                    BmobQuery<Records> recordsGroup_id = new BmobQuery<>();
                    recordsGroup_id.addWhereEqualTo("group_id", mgroup.getObjectId());
                    //查询条件三：createAt
                    BmobQuery<Records> recordsDateStart = new BmobQuery<>();
                    recordsDateStart.addWhereGreaterThanOrEqualTo("createdAt", bmobCreatedAtDateStart);
                    BmobQuery<Records> recordsDateEnd = new BmobQuery<>();
                    recordsDateEnd.addWhereLessThanOrEqualTo("createdAt", bmobCreateAtDateEnd);

                    List<BmobQuery<Records>> queries = new ArrayList<>();
                    queries.add(recordsUser_id);
                    queries.add(recordsGroup_id);
                    queries.add(recordsDateStart);
                    queries.add(recordsDateEnd);


                    //最终的查询条件
                    BmobQuery<Records> query = new BmobQuery<>();
                    query.and(queries);
                    query.addQueryKeys("time_slot");
                    query.findObjects(new FindListener<Records>() {
                        @Override
                        public void done(List<Records> list, BmobException e) {
                            if (e == null) {
                                for (int i = 0; i < list.size(); i++) {
                                    recordedTimeSlot[i] = list.get(i).getTime_slot();
                                }
                                mbtn_record.setEnabled(true);
                                Verify();
                            } else {
                                Toast.makeText(GroupActivity.this, "失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(GroupActivity.this, "失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        //查选择日期的签到记录
        try {
            search_records(year, month, day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mbtn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.SECOND, 0);
                int nextTime = CurrentTime;
                for (int i = 0; i < Starts_Time.size(); i++) {
                    if (Integer.parseInt(Starts_Time.get(i)) > CurrentTime) {
                        if (nextTime > Integer.parseInt(Starts_Time.get(i)) || nextTime == CurrentTime) {
                            nextTime = Integer.parseInt(Starts_Time.get(i));

                        }
                    }
                }
                if (nextTime == CurrentTime) {
                    nextTime = Integer.parseInt(Starts_Time.get(0));
                    c.add(Calendar.DATE, 1);
                }
                c.set(Calendar.HOUR_OF_DAY, nextTime / 100);
                c.set(Calendar.MINUTE, nextTime % 100);

                Intent intent = new Intent(GroupActivity.this, AlarmReceiver.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("group", mgroup);
                intent.putExtra("data", bundle);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(GroupActivity.this, mgroup.getGroup_number(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 60000, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 60000, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 60000, pendingIntent);
                }


            }
        });

        group_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("group", mgroup);
                intent.setClass(GroupActivity.this, GroupDetailActivity.class);
                startActivityForResult(intent,2);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                try {
                    search_records(year, month + 1, dayOfMonth);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @SuppressLint("DefaultLocale")
    private void search_records(final int year, int month, int day) throws ParseException {
        final int CalendarDate = year * 10000 + month * 100 + day;
        if (CalendarDate < Start_Date || CalendarDate > End_Date) {
            String wrong_date_result[] = {"超出签到日期范围"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(GroupActivity.this, android.R.layout.simple_list_item_1, wrong_date_result);
            record_list_view.setAdapter(adapter);
            record_list_view.setEnabled(false);
            return;
        }
        for (int i = 0; i < mgroup.getStarts_time().size(); i++) {
            RecordsResult[i] = Starts_Time.get(i) + Ends_Time.get(i);
        }
        createdAtStart = String.valueOf(year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) + " 00:00:00";
        createdAtEnd = String.valueOf(year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) + " 23:59:59";
        Date createdAtDateStart = sdf.parse(createdAtStart);
        Date createAtDateEnd = sdf.parse(createdAtEnd);
        bmobCreatedAtDateStart = new BmobDate(createdAtDateStart);
        bmobCreateAtDateEnd = new BmobDate(createAtDateEnd);
        //查询条件一：user_id
        BmobQuery<Records> recordsUser_id = new BmobQuery<>();
        recordsUser_id.addWhereEqualTo("user_id", BmobUser.getCurrentUser(Student.class).getObjectId());
        //查询条件二：group_id
        BmobQuery<Records> recordsGroup_id = new BmobQuery<>();
        recordsGroup_id.addWhereEqualTo("group_id", mgroup.getObjectId());
        //查询条件三：createAt
        BmobQuery<Records> recordsDateStart = new BmobQuery<>();
        recordsDateStart.addWhereGreaterThanOrEqualTo("createdAt", bmobCreatedAtDateStart);
        BmobQuery<Records> recordsDateEnd = new BmobQuery<>();
        recordsDateEnd.addWhereLessThanOrEqualTo("createdAt", bmobCreateAtDateEnd);

        List<BmobQuery<Records>> queries = new ArrayList<>();
        queries.add(recordsUser_id);
        queries.add(recordsGroup_id);
        queries.add(recordsDateStart);
        queries.add(recordsDateEnd);

        //最终的查询条件
        BmobQuery<Records> query = new BmobQuery<>();
        query.and(queries);
        query.findObjects(new FindListener<Records>() {
            @Override
            public void done(List<Records> list, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        RecordsResult[list.get(i).getTime_slot() - 1] = Starts_Time.get(list.get(i).getTime_slot() - 1) + Ends_Time.get(list.get(i).getTime_slot() - 1) + String.valueOf(list.get(i).getCreatedAt());
                    }
                    RecordAdapter adapter = new RecordAdapter(GroupActivity.this, R.layout.records_list_item, RecordsResult);
                    record_list_view.setAdapter(adapter);
                    record_list_view.setEnabled(true);
                    record_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(CalendarDate==CurrentDate&&position==current_time_slot-1){
                                if(RecordsResult[position].length()>8){
                                    Toast.makeText(GroupActivity.this,"已经签到了，不要重复签到！",Toast.LENGTH_LONG).show();
                                }else {
                                    double targetlat = group_location.getLatitude();
                                    double targetlon = group_location.getLongitude();
                                    if(isnearTarget(targetlat,targetlon))
                                    {
                                        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");

                                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {//判断是否有相机应用
                                            startActivityForResult(takePictureIntent, 1);
                                        }
                                    }
                                    else

                                        Toast.makeText(GroupActivity.this,"请到指定位置签到！",Toast.LENGTH_LONG).show();


                                }
                            }else {
                                if(RecordsResult[position].length()>8){
                                    Toast.makeText(GroupActivity.this,"显示签到详情",Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(GroupActivity.this,"所选时间无效，不可签到",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(GroupActivity.this, "查询失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //添加数据
    void addRecord() {
        records.setGroup_id(mgroup.getObjectId());
        records.setUser_id(BmobUser.getCurrentUser(Student.class).getObjectId());
        records.setTime_slot(current_time_slot);
        records.setLocation(geolocation);
        records.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Toast.makeText(GroupActivity.this, "签到成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GroupActivity.this, "签到失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        onCreate(null);
    }


    //验证日期时间和是否重复签到
    public Boolean Verify() {
        dateisRight = CurrentDate >= Start_Date && CurrentDate <= End_Date;
        int starts_time, ends_time;
        for (int i = 0; i < Starts_Time.size(); i++) {
            starts_time = Integer.parseInt(Starts_Time.get(i));
            ends_time = Integer.parseInt(Ends_Time.get(i));
            if (starts_time <= CurrentTime && CurrentTime <= ends_time) {
                timeisRight = true;
                current_time_slot = i + 1;
                break;
            }
        }
        for (int i = 0; i < Starts_Time.size(); i++) {
            if (current_time_slot == recordedTimeSlot[i]) {
                isRecorded = true;
                break;
            }
        }
        return dateisRight && timeisRight && (!isRecorded);
    }

    public class BaiduLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            if (isFirstLocation) {
                isFirstLocation = false;
                mLocation = location;
            }

        }

    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public Boolean isnearTarget(double target_getlat, double target_getlon) {
        LatLng target = new LatLng(target_getlat, target_getlon);
        LatLng location = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        if (DistanceUtil.getDistance(target, location) >= 100)
            return false;
        else
        {
            geolocation=new BmobGeoPoint(mLocation.getLongitude(),mLocation.getLatitude());
            return true;}
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationClient.stop();
        mLocationClient.unRegisterLocationListener(myListener);

    }


    class Myhandler extends  Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            if(msg.obj.toString()=="true")
                addRecord();
            else
                Toast.makeText(GroupActivity.this,"人脸信息不匹配!",Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            progressDialog = new ProgressDialog(GroupActivity.this);
            progressDialog.setTitle("人脸识别中");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            Bundle extras = data.getExtras();
            Bitmap face=(Bitmap)extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            face.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[]image1;
            image1 = baos.toByteArray();
            face.recycle();
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            image= Base64Util.encode(image1);
            final   Handler handler=new Myhandler();
            new Thread() {
                @Override
                public void run() {
                    boolean right=false;

                    String  face_token = FaceGetList.getFaceToken(BmobUser.getCurrentUser(Student.class).getMobilePhoneNumber(), "sign");
                    Message message = Message.obtain();

                    right = FaceMatch.ismatch(image, face_token);
                    if (!right) {
                        message.obj ="false";
                    } else {
                        message.obj = "true";

                    }
                    handler.sendMessage(message);

                }  }.start();}
        if(requestCode==2)
        {
            setResult(3,new Intent());

            finish();
        }




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
