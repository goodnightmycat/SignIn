package com.example.administrator.bombtest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.bombtest.Model.Group;
import com.example.administrator.bombtest.Model.Student;

import java.util.ArrayList;
import java.util.List;

public class GroupRecordsActivity extends AppCompatActivity {
    private Group group;
    private List<String> tabs = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private List<Student> members = new ArrayList<>();
    private int tab_index,spinner_date_position,spinner_time_position;
    private ImageView back;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_records);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        title=findViewById(R.id.titleText_back);
        title.setText("签到记录");

        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        group = (Group) getIntent().getSerializableExtra("group");
        members = (List<Student>)getIntent().getSerializableExtra("members");
        tab_index =  getIntent().getIntExtra("tab",0);
        spinner_date_position = getIntent().getIntExtra("spinner_date_position",0);
        spinner_time_position = getIntent().getIntExtra("spinner_time_position",0);
        init_data();
        init_view();
    }

    private void init_view() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(tab_index).select();
    }

    private void init_data() {
        tabs.add("全部");
        tabs.add("已签到");
        tabs.add("未签到");

        fragments.add(new AllSignsFragment(group,members,spinner_date_position,spinner_time_position));
        fragments.add(new SignedFragment(group,members,spinner_date_position,spinner_time_position));
        fragments.add(new UnsignedFragment(group,members,spinner_date_position,spinner_time_position));
    }

    class TabAdapter extends FragmentPagerAdapter {
        TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        //显示标签上的文字
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        }
    }
}
