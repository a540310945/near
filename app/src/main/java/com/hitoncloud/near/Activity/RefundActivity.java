package com.hitoncloud.near.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;

public class RefundActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();
    }

    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_refund);
        toolbar.setTitle("申请退款");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RefundActivity.this.finish();
            }
        });
    }
}
