package com.hitoncloud.near.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.widget.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*主页-首页-任务发布*/
public class TaskEditTimeActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent backIntent;


    RelativeLayout rl_starttime,rl_endtime,rl_ensure;
    TextView tv_starttime,tv_endtime;

    private CustomDatePicker customDatePicker1, customDatePicker2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edittime);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();



    }

    private void initView() {
        tv_starttime = (TextView)findViewById(R.id.tv_taskedittime_starttime_value);
        tv_endtime = (TextView)findViewById(R.id.tv_taskedittime_endtime_value);
        //初始化顶部
        toolbar = (Toolbar)findViewById(R.id.toolbar_taskedittime);
        toolbar.setTitle("任务限定时间");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backtolastactivity();
            }
        });
        rl_ensure = (RelativeLayout)findViewById(R.id.rl_taskedittime_ensure);
        rl_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backtolastactivity();
            }
        });

        rl_starttime = (RelativeLayout)findViewById(R.id.rl_taskedittime_starttime);
        rl_starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDatePicker1.show(tv_starttime.getText().toString());

            }
        });

        rl_endtime = (RelativeLayout)findViewById(R.id.rl_taskedittime_endtime);
        rl_endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDatePicker2.show(tv_endtime.getText().toString());

            }
        });
        initDatePicker();
    }

    private void backtolastactivity() {
        backIntent = new Intent();
        backIntent.putExtra("starttime", tv_starttime.getText().toString());
        backIntent.putExtra("endtime", tv_endtime.getText().toString());
        // 设置结果，并进行传送
        TaskEditTimeActivity.this.setResult(10001, backIntent);
        TaskEditTimeActivity.this.finish();
    }




    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

        Date nowDate = new Date();//当前时间
        String startDate = sdf.format(nowDate);//可选时间范围起点
        String endDate = sdf.format(new Date(nowDate.getTime()+(long)1000*60*60*24*30));//可选时间范围终点
        String tmpDate = sdf.format(new Date(nowDate.getTime()+(long)1000*60*60*24));//默认截止时间

        tv_starttime.setText(startDate);
        tv_endtime.setText(tmpDate);

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                tv_starttime.setText(time);
            }
        }, startDate, endDate); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(true); // 不显示时和分
        customDatePicker1.setIsLoop(true); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                tv_endtime.setText(time);
            }
        }, startDate, endDate); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MyApplication.payfinish==1)
        {
            MyApplication.payfinish=0;
            TaskEditTimeActivity.this.finish();
        }
    }




}
