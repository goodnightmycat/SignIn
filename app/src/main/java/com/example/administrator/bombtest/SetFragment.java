package com.example.administrator.bombtest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bombtest.Model.Student;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class SetFragment extends Fragment implements View.OnClickListener {

    private Button logoutBtn;
    private ListView mListView;
    private EditText editName;

    private TextView title;
    private String menu[] = {"用户名: " + BmobUser.getCurrentUser(Student.class).getUsername(), "手机号: " + BmobUser.getCurrentUser(Student.class).getMobilePhoneNumber(),"密码：******"};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getActivity().findViewById(R.id.listview);



        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, menu);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        LayoutInflater inflater =  LayoutInflater.from(getActivity());
                        View view1 = inflater.inflate(R.layout.update_user_name_dialog,null);
                        editName = view1.findViewById(R.id.et_update_username);
                        new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT).setTitle("修改用户名").setView(view1)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(TextUtils.isEmpty(editName.getText().toString().trim())) {
                                            Toast.makeText(getActivity(), "用户名不得为空！", Toast.LENGTH_LONG).show();

                                        }
                                        else {

                                            Student user = BmobUser.getCurrentUser(Student.class);
                                            user.setUsername(editName.getText().toString());
                                            user.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if (e == null) {
                                                        Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_LONG).show();
                                                        menu[0] = "用户名: " + editName.getText().toString();
                                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, menu);
                                                        mListView.setAdapter(adapter);

                                                    } else {
                                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }).setNegativeButton("取消", null).create().show();
                        break;
                    }
                    case 1:
                        break;
                    case 2:
                        LayoutInflater inflater =  LayoutInflater.from(getActivity());
                        View view1 = inflater.inflate(R.layout.update_user_password_dialog,null);
                        final EditText old_password = view1.findViewById(R.id.old_password);
                        final EditText new_password = view1.findViewById(R.id.new_password);
                        new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT).setTitle("修改密码").setView(view1)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        BmobUser.updateCurrentUserPassword(old_password.getText().toString(), new_password.getText().toString(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e==null){
                                                    Toast.makeText(getActivity(),"修改成功",Toast.LENGTH_LONG).show();
                                                }else {
                                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }).setNegativeButton("取消", null).create().show();
                        break;
                }

            }
        });
        logoutBtn = getActivity().findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).setTitle("提示").setMessage("确定要退出吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobUser.logOut();
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), LoginByPasswordActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }).setNegativeButton("取消", null).create().show();

    }
}
