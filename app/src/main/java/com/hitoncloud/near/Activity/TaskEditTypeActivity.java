package com.hitoncloud.near.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class TaskEditTypeActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent backIntent;
    int tasktypeid;

    RelativeLayout rl_ensure;//确定

    MaterialSpinner ms_first,ms_second;//一级分类任务，二级分类任务
    String firsttype,secondtype="";//主任务，子任务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit_type);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();
    }

    private void initView() {
        //初始化顶部
        toolbar = (Toolbar)findViewById(R.id.toolbar_taskedittype);
        toolbar.setTitle("任务分类");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backtolastactivity();
            }
        });
        rl_ensure = (RelativeLayout)findViewById(R.id.rl_taskedittype_ensure);
        rl_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backtolastactivity();
            }
        });
        //初始化spinner
        ms_first = (MaterialSpinner)findViewById(R.id.ms_taskedittype_first);
        ms_second = (MaterialSpinner)findViewById(R.id.ms_taskedittype_second);
        ms_first.setItems("其它","飞毛腿", "修理", "游戏", "租赁", "学习生活","运动");
        firsttype = "其它";
        ms_first.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                firsttype = item;
                ms_second.setVisibility(View.VISIBLE);
                switch (position)
                {
                    case 0:
                        ms_second.setVisibility(View.INVISIBLE);
                        ms_second.setItems("");
                        break;
                    case 1:
                        ms_second.setItems("拿快递","代购","其它");
                        secondtype = "拿快递";
                        ms_second.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                secondtype = item.toString();
                            }
                        });
                        break;
                    case 2:
                        ms_second.setItems("修电脑","修其它");
                        secondtype = "修电脑";
                        ms_second.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                secondtype = item.toString();
                            }
                        });
                        break;
                    case 3:
                        ms_second.setItems("代练","开黑","交易");
                        secondtype = "代练";
                        ms_second.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                secondtype = item.toString();
                            }
                        });
                        break;
                    case 4:
                        ms_second.setItems("我要借入","我要借出");
                        secondtype = "我要借入";
                        ms_second.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                secondtype = item.toString();
                            }
                        });
                        break;
                    case 5:
                        ms_second.setItems("组队学习","上课","出游","生活娱乐");
                        secondtype = "组队学习";
                        ms_second.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                secondtype = item.toString();
                            }
                        });
                        break;
                    case 6:
                        ms_second.setItems("组队减肥","约跑","组队健身");
                        secondtype = "组队减肥";
                        ms_second.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                secondtype = item.toString();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });


    }

    private void backtolastactivity() {
        if(firsttype.equals("其它"))
            secondtype = "其它";
        backIntent = new Intent();
        backIntent.putExtra("firsttype",firsttype);
        backIntent.putExtra("secondtype",secondtype);
        // 设置结果，并进行传送
        TaskEditTypeActivity.this.setResult(10002, backIntent);
        TaskEditTypeActivity.this.finish();
    }
}
