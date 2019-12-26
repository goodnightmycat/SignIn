package com.example.administrator.bombtest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity{
    private BottomNavigationView bottomNavigationView;
    private FuncFragment mFuncFragment;
    private SetFragment mSetFragment;
    private Fragment[] fragments;
    private int lastfragment;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        initFragment();
    }

    private void initFragment() {
        mFuncFragment=new FuncFragment();
        mSetFragment=new SetFragment();
        fragments = new Fragment[]{mFuncFragment,mSetFragment};
        lastfragment=0;
        getSupportFragmentManager().beginTransaction().replace(R.id.container,mFuncFragment).show(mFuncFragment).commit();
        bottomNavigationView=findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.navigation_home: {
                        if (lastfragment != 0) {
                            switchFragment(lastfragment, 0);
                            lastfragment = 0;
                        }
                        return true;

                    }

                    case R.id.navigation_dashboard:
                    {
                        if(lastfragment!=1)
                        {
                            switchFragment(lastfragment,1);
                            lastfragment=1;

                        }
                        return true;

                    }
                }
                return false;
            }
        });

    }

    private void switchFragment(int lastfragment,int index)
    {

        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if(!fragments[index].isAdded())
        {
            transaction.add(R.id.container,fragments[index]);

        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }


}





