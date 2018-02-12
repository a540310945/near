package com.hitoncloud.near.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;

public class AboutActivity extends AppCompatActivity {

    Toolbar toolbar;

    private ImageView iv_developer_arrow;
    private RelativeLayout rl_developer,rl_developer_body;

    private boolean isshowdeveloper = false;//显示支付宝提现

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();
    }

    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_about);
        toolbar.setTitle("关于我们");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity.this.finish();
            }
        });
        //显示或隐藏开发者介绍
        iv_developer_arrow = (ImageView)findViewById(R.id.iv_about_developer_arrowright);
        rl_developer = (RelativeLayout)findViewById(R.id.rl_about_developer);
        rl_developer_body = (RelativeLayout) findViewById(R.id.rl_about_developer_body);
        rl_developer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isshowdeveloper)
                {
                    iv_developer_arrow.setImageResource(R.drawable.icon_arrowright);
                    rl_developer_body.setVisibility(View.INVISIBLE);
                    isshowdeveloper = false;

                }else
                {
                    iv_developer_arrow.setImageResource(R.drawable.icon_arrowdown);
                    rl_developer_body.setVisibility(View.VISIBLE);
                    isshowdeveloper = true;
                }

            }
        });


    }
}
