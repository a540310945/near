package com.hitoncloud.near.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.utils.ActivityCollector;

public class SettingActivity extends AppCompatActivity {

    Toolbar toolbar;
    RelativeLayout rl_logout,rl_clear;//注销，清除缓存

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        ActivityCollector.addActivity(SettingActivity.this);
        initView();

    }

    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_setting);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingActivity.this.finish();
            }
        });
        //清除缓存
        rl_clear = (RelativeLayout)findViewById(R.id.rl_setting_clear);
        rl_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingActivity.this,"已经清除不用的缓存",Toast.LENGTH_SHORT).show();
            }
        });
        //注销
        rl_logout = (RelativeLayout)findViewById(R.id.rl_setting_logout);
        rl_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("logininfo", MODE_PRIVATE).edit();
                editor.putString("username", "");
                editor.putString("password", "");
                MyApplication.username = "";
                MyApplication.password = "";
                editor.apply();
                Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                ActivityCollector.finishAllActivity();

            }
        });


    }


}
