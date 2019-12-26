package com.example.administrator.bombtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Student;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

    public class LoginByPasswordActivity extends Activity implements View.OnClickListener{
    private EditText etusernumber;
    private EditText etpassword;
    private Button login;
    private TextView forgetpassword;
    private TextView toregister;

    private String phone;
    private  String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginbypassword);
        etusernumber =  findViewById(R.id.et_number);
        etpassword = findViewById(R.id.et_password);
        etpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        login =  findViewById(R.id.login);
        login.setOnClickListener(this);
        forgetpassword = findViewById(R.id.forgetpassword);
        forgetpassword.setOnClickListener(this);
        toregister=findViewById(R.id.toregister);
        toregister.setOnClickListener(this);

    }

    private void loginByPhone(){
        phone=etusernumber.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        password=etpassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO 此处替换为你的手机号码和密码
        BmobUser.loginByAccount(phone, password, new LogInListener<Student>() {

            @Override
            public void done(Student user, BmobException e) {
                if(user!=null){
                    if (e == null) {
                        Intent intent=new Intent(LoginByPasswordActivity.this,MenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } else {

                    }

                }
               else Toast.makeText(LoginByPasswordActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:{
                loginByPhone();
                break;}
            case R.id.forgetpassword:
                Intent intent=new Intent(LoginByPasswordActivity.this,LoginBySMSActivity.class);
                startActivity(intent);
                break;
            case R.id.toregister:
                Intent intent3=new Intent(LoginByPasswordActivity.this,RegisterActivity.class);
                startActivity(intent3);

        }

    }
}
