package com.hitoncloud.near.Activity;

import android.content.Intent;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hitoncloud.near.myview.MyViewPager;
import com.hitoncloud.near.task.TaskAcceptTypeAdapter;
import com.hitoncloud.near.Fragment.TaskListFragment;
import com.hitoncloud.near.task.TasktpyeList;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TaskAcceptActivity extends AppCompatActivity {

    private int cardtype=0;//任务类型
    private String typename[] = new String[10];//任务类型名称
    private String titile = "任务";//任务一级分类
    private int typeid[] = new int[10];//任务类型ID号码

    private Toolbar toolbar;

    private ImageView tabline;//选项卡滚动条
    private int tabwidth=0;//选项卡宽度
    private int tabnum=0;//选项卡数量
    private RecyclerView rv_navigation;//导航栏
    private LinearLayoutManager layoutmanager;//布局管理器
    private List<TasktpyeList> tasktypelist = new ArrayList<>();//任务导航列表
    private int firstPosition;//首个可见的任务类型导航
    private int lastPosition;//最后一个可见的任务类型导航
    private TaskAcceptTypeAdapter tasktypeAdapter;

    Boolean isTAB = true;//判断是否可以滑动

    private MyViewPager mViewpager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragmentList;
    private TaskListFragment taskListFragment[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_accept);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        getTasktype();
        initView();
    }

    private void getTasktype() {
        Intent intent = getIntent();
        cardtype = intent.getIntExtra("tasktype",0);
        switch (cardtype)
        {
            case 1:
                tabnum=3;
                titile = "飞毛腿";
                typename[0] = "拿快递";
                typeid[0] = 11;
                typename[1] = "代购";
                typeid[1] = 12;
                typename[2] = "其它";
                typeid[2] = 13;
                break;
            case 2:
                tabnum=3;
                titile = "游戏";
                typename[0] = "开黑";
                typeid[0] = 21;
                typename[1] = "代练";
                typeid[1] = 22;
                typename[2] = "交易";
                typeid[2] = 23;
                break;
            case 3:
                tabnum=2;
                titile = "租赁";
                typename[0] = "我要借出";
                typeid[0] = 31;
                typename[1] = "我要借入";
                typeid[1] = 32;
                break;
            case 4:
                tabnum=2;
                titile = "运动";
                typename[0] = "组队减肥";
                typeid[0] = 41;
                typename[1] = "约跑";
                typeid[1] = 42;
                break;
            case 5:
                tabnum=2;
                titile = "修理";
                typename[0] = "修电脑";
                typeid[0] = 51;
                typename[1] = "杂七杂八";
                typeid[1] = 52;
                break;
            case 6:
                tabnum=3;
                titile = "学习生活";
                typename[0] = "上课";
                typeid[0] = 61;
                typename[1] = "组队学习";
                typeid[1] = 62;
                typename[2] = "出游";
                typeid[2] = 63;
                break;
            case 7:
                tabnum=1;
                titile = "其它";
                typename[0] = "其它";
                typeid[0] = 71;
                break;
            default:
                tabnum=0;
                break;
        }
        addtasktype();//将任务类型添加到导航列表中
    }


    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_taskaccept);
        toolbar.setTitle(titile);//设置toolbar标题
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskAcceptActivity.this.finish();
            }
        });

        tabline = (ImageView)findViewById(R.id.iv_taskaccept_tab);
        rv_navigation = (RecyclerView)findViewById(R.id.rv_accept_navigation);
        mViewpager = (MyViewPager)findViewById(R.id.vp_taskaccept);

        adjustNavigation();//自动适配导航栏
        adjustViewPager();//自动适配任务列表


    }

    //自动适配导航栏
    private void adjustNavigation() {
        if(tabnum>0&&tabnum<3)
            tabwidth = MyApplication.screenWidth/tabnum;
        else if(tabnum>=3)
            tabwidth = MyApplication.screenWidth/3;
        else
            tabwidth = 0;
        if(tabnum==1)
            tabline.setVisibility(View.INVISIBLE);
        LinearLayout.LayoutParams Paramstabline =(LinearLayout.LayoutParams) tabline.getLayoutParams();
        Paramstabline.width = tabwidth;
        tabline.setLayoutParams(Paramstabline);
        layoutmanager = new LinearLayoutManager(this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                super.smoothScrollToPosition(recyclerView, state, position);
                LinearSmoothScroller linearSmoothScroller =
                        new LinearSmoothScroller(recyclerView.getContext()) {
                            @Nullable
                            @Override
                            public PointF computeScrollVectorForPosition(int targetPosition) {
                                return super.computeScrollVectorForPosition(targetPosition);
                            }

                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return 0.3f;
                            }

                        };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);

            }
        };
        layoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_navigation.setLayoutManager(layoutmanager);
        rv_navigation.setHasFixedSize(true);        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        tasktypeAdapter = new TaskAcceptTypeAdapter(this,tasktypelist,tabnum);
        rv_navigation.setAdapter(tasktypeAdapter);
        tasktypeAdapter.setOnItemClickListener(new TaskAcceptTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                isTAB=false;
                //不带闪烁的跳转到指定任务对应的viewpager
                try {
                    Field field = mViewpager.getClass().getField("mCurItem");//参数mCurItem是系统自带的
                    field.setAccessible(true);
                    field.setInt(mViewpager, position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();//更新适配器
                mViewpager.setCurrentItem(position);
                isTAB=true;
            }
        });

        //任务导航滑动事件处理
        rv_navigation.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                tablinescroll(dx);
            }
        });

    }

    //自动适配任务列表
    private void adjustViewPager() {
        mFragmentList = new ArrayList<Fragment>();
        taskListFragment = new TaskListFragment[tabnum];
        for(int i=0;i<tabnum;i++)
        {
            taskListFragment[i] = new TaskListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("typeid",typeid[i]);
            taskListFragment[i].setArguments(bundle);
            mFragmentList.add(taskListFragment[i]);
        }
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };
        mViewpager.setAdapter(mAdapter);
        mViewpager.setOffscreenPageLimit(tabnum);

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //滑动条随着viewpager变化
                if(isTAB)
                {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tabline.getLayoutParams();
                    layoutParams.leftMargin = (int) ((position + positionOffset) * tabline.getWidth());
                    tabline.setLayoutParams(layoutParams);
                }

            }

            @Override
            public void onPageSelected(int position) {
                //处理超出视野外的任务导航
                firstPosition = layoutmanager.findFirstVisibleItemPosition();
                lastPosition = layoutmanager.findLastVisibleItemPosition();
                if (position >= lastPosition) {
                    rv_navigation.smoothScrollToPosition(position);
                } else if (position <= firstPosition) {
                    rv_navigation.smoothScrollToPosition(position);
                }
                switch (position)
                {

                    default:
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                Intent intent = new Intent(TaskAcceptActivity.this,TaskEditActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    //添加任务类型到导航列表
    private void addtasktype() {
        for(int i=0;i<tabnum;i++) {
            tasktypelist.add(new TasktpyeList(typename[i]));
        }
    }

    //滑动条随着导航栏滚动而移动
    public int tablinescroll(int x) {
        tabline.setX(tabline.getX() - x);
        return 0;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }
}
