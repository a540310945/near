package com.hitoncloud.near.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;

//任务类型展示
//入口：首页-》任务达人

public class TaskTypeActivity extends AppCompatActivity {

    Toolbar toolbar;
    FrameLayout fl_run,fl_game,fl_rent,fl_deal,fl_fix,fl_life,fl_other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_type);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();

    }

    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_tasktype);
        toolbar.setTitle("任务达人");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskTypeActivity.this.finish();
            }
        });
        fl_run = (FrameLayout)findViewById(R.id.fl_taskcard_run);
        fl_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskTypeActivity.this, TaskAcceptActivity.class);
                intent.putExtra("tasktype",1);
                startActivity(intent);
            }
        });
        fl_game = (FrameLayout)findViewById(R.id.fl_taskcard_game);
        fl_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskTypeActivity.this, TaskAcceptActivity.class);
                intent.putExtra("tasktype",2);
                startActivity(intent);
            }
        });
        fl_rent = (FrameLayout)findViewById(R.id.fl_taskcard_rent);
        fl_rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskTypeActivity.this, TaskAcceptActivity.class);
                intent.putExtra("tasktype",3);
                startActivity(intent);
            }
        });
        fl_deal = (FrameLayout)findViewById(R.id.fl_taskcard_deal);
        fl_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskTypeActivity.this, TaskAcceptActivity.class);
                intent.putExtra("tasktype",4);
                startActivity(intent);
            }
        });
        fl_fix = (FrameLayout)findViewById(R.id.fl_taskcard_fix);
        fl_fix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskTypeActivity.this, TaskAcceptActivity.class);
                intent.putExtra("tasktype",5);
                startActivity(intent);
            }
        });
        fl_life = (FrameLayout)findViewById(R.id.fl_taskcard_life);
        fl_life.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskTypeActivity.this, TaskAcceptActivity.class);
                intent.putExtra("tasktype",6);
                startActivity(intent);
            }
        });
        fl_other = (FrameLayout)findViewById(R.id.fl_taskcard_other);
        fl_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskTypeActivity.this, TaskAcceptActivity.class);
                intent.putExtra("tasktype",7);
                startActivity(intent);
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_tasktype,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuitem){
        switch (menuitem.getItemId())
        {
            case R.id.plus:
                Intent intent = new Intent(TaskTypeActivity.this,TaskEditActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(MyApplication.payfinish==1)
        {
            finish();
        }
    }
}
