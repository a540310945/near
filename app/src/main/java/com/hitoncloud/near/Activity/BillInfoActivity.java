package com.hitoncloud.near.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;

public class BillInfoActivity extends AppCompatActivity {

    Toolbar toolbar;

    private TextView tv_ordernum,tv_way,tv_money,tv_publisher,tv_accepter,tv_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_info);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_billinfo);
        toolbar.setTitle("账单详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillInfoActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        String ordernum = intent.getStringExtra("taskordernum");
        String taskname = intent.getStringExtra("taskname");
        String taskmoney = intent.getStringExtra("taskmoney");
        String taskdate = intent.getStringExtra("taskdate");
        String taskpublisher = intent.getStringExtra("taskpublisher");
        String taskaccepter = intent.getStringExtra("taskaccepter");


        tv_ordernum = (TextView)findViewById(R.id.tv_billinfo_ordernum);
        tv_way = (TextView)findViewById(R.id.tv_billinfo_way);
        tv_money = (TextView)findViewById(R.id.tv_billinfo_money);
        tv_publisher = (TextView)findViewById(R.id.tv_billinfo_publisher_value);
        tv_accepter = (TextView)findViewById(R.id.tv_billinfo_accepter_value);
        tv_date = (TextView)findViewById(R.id.tv_billinfo_date);

        tv_ordernum.setText(ordernum);
        tv_accepter.setText(taskaccepter);
        tv_publisher.setText(taskpublisher);
        tv_date.setText(taskdate);
        tv_money.setText(taskmoney);


    }
}
