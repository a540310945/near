package com.hitoncloud.near.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.school.AllSchoolSqliteOpenHelper;
import com.hitoncloud.near.school.LetterView;
import com.hitoncloud.near.school.SchoolListAdapter;
import com.hitoncloud.near.school.SchoolSqliteOpenHelper;
import com.hitoncloud.near.school.Schoollist;
import com.hitoncloud.near.school.SearchResultAdapter;
import com.hitoncloud.near.utils.PingYinUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SchoolActivity extends AppCompatActivity {

    protected static final String TAG = "SchoolActivity";
    private LetterView myLetterView;//自定义的View
    private TextView tvDialog;//主界面显示字母的TextView
    private ListView lvSchool;//进行学校列表展示
    private EditText etSearch;//找学校搜索框
    private ListView lvResult;//搜索结果列表展示
    private TextView tvNoResult;//搜索无结果时文字展示

    private List<Schoollist> allSchoolList;//所有学校列表
    private List<Schoollist> nearSchoolList;//附近学校列表
    private List<Schoollist> searchSchoolList;//搜索学校列表
    private List<String> recentSchoolList;//最近访问学校列表

    public SchoolSqliteOpenHelper schoolOpenHelper;//对保存了最近访问学校的数据库操作的帮助类
    public SQLiteDatabase schoolDb;//保存最近访问学校的数据库
    public SchoolListAdapter schoolListAdapter;
    public SearchResultAdapter searchResultAdapter;
    private boolean isScroll=false;
    private boolean mReady=false;
    private Handler handler;
    private OverlayThread overlayThread; //显示首字母对话框

    private RelativeLayout rl_back;//顶部返回按钮
    LinearLayout bar;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();
        initData();
        setListener();
        initNowSchool();
        initSchoolFromServer();
        //初始化所有学校列表
        initAllSchoolData();
        initRecentVisitSchoolData();//初始化最近访问的学校数据
        initNearSchoolData();//初始化附近学校
        setAdapter();//设置适配器
        mReady=true;
    }

    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_school);
        toolbar.setTitle("选择学校");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SchoolActivity.this.finish();
            }
        });


        //右边缩写列表
        myLetterView=(LetterView) findViewById(R.id.letterview_school_rightlist);
        //中间展示字母的Textview
        tvDialog=(TextView) findViewById(R.id.tv_schoolist_dialog);
        //将中间展示字母的TextView传递到myLetterView中并在其中控制它的显示与隐藏
        myLetterView.setTextView(tvDialog);
        //注册MyLetterView中监听(跟setOnClickListener这种系统默认写好的监听一样只不过这里是我们自己写的)
        myLetterView.setOnSlidingListener(new LetterView.OnSlidingListener() {
            @Override
            public void sliding(String str) {
                tvDialog.setText(str);
            }
        });

        lvSchool=(ListView) findViewById(R.id.lv_school);//展示学校的列表
        etSearch=(EditText) findViewById(R.id.et_school_search);//搜索框
        lvResult=(ListView) findViewById(R.id.lv_result);//有结果返回列表
        tvNoResult=(TextView) findViewById(R.id.tv_noresult);//无结果返回列表
    }

    private void initData() {
        //存储学校信息的数据库初始化
        schoolOpenHelper=new SchoolSqliteOpenHelper(SchoolActivity.this);
        schoolDb=schoolOpenHelper.getWritableDatabase();
        //学校列表初始化
        allSchoolList=new ArrayList<Schoollist>();
        nearSchoolList=new ArrayList<Schoollist>();
        searchSchoolList=new ArrayList<Schoollist>();
        recentSchoolList=new ArrayList<String>();
        handler = new Handler();
        overlayThread = new OverlayThread();
    }

    private void setListener() {
        //自定义myLetterView的一个监听
        myLetterView.setOnSlidingListener(new LetterView.OnSlidingListener() {

            @Override
            public void sliding(String s) {
                isScroll=false;
                if(schoolListAdapter.alphaIndexer.get(s)!=null){
                    //根据MyLetterView滑动到的数据获得ListView应该展示的位置
                    int position = schoolListAdapter.alphaIndexer.get(s);
                    //将listView展示到相应的位置
                    lvSchool.setSelection(position);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString()==null||"".equals(s.toString())){
                    myLetterView.setVisibility(View.VISIBLE);
                    lvSchool.setVisibility(View.VISIBLE);
                    lvResult.setVisibility(View.GONE);
                    tvNoResult.setVisibility(View.GONE);
                }else{
                    searchSchoolList.clear();
                    myLetterView.setVisibility(View.GONE);
                    lvSchool.setVisibility(View.GONE);
                    getResultCityList(s.toString());
                    if (searchSchoolList.size() <= 0) {
                        lvResult.setVisibility(View.GONE);
                        tvNoResult.setVisibility(View.VISIBLE);
                    } else {
                        lvResult.setVisibility(View.VISIBLE);
                        tvNoResult.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lvSchool.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL
                        || scrollState == SCROLL_STATE_FLING) {
                    isScroll = true;
                }
            }

            @SuppressLint("DefaultLocale") @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (!isScroll) {
                    return;
                }
                if (mReady) {
                    String text;
                    String name = allSchoolList.get(firstVisibleItem).getName();
                    String pinyin = allSchoolList.get(firstVisibleItem).getPinyin();
                    if (firstVisibleItem < 4) {
                        text = name;
                    } else {
                        text = PingYinUtil.converterToFirstSpell(pinyin)
                                .substring(0, 1).toUpperCase();
                    }
                    tvDialog.setText(text);
                    tvDialog.setVisibility(View.VISIBLE);
                    handler.removeCallbacks(overlayThread);
//					 延迟一秒后执行，让中间显示的TextView为不可见
                    handler.postDelayed(overlayThread,1000);
                }
            }
        });
    }

    private void initNowSchool(){
        SharedPreferences pref = getSharedPreferences("nowschool", MODE_PRIVATE);
        MyApplication.locschool = pref.getString("nowschool", "");
    }

    private void initSchoolFromServer(){
        if(MyApplication.fixbug||MyApplication.locschool.equals(""))
        {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.add("username", MyApplication.username);
            client.post("http://123.207.46.157/near/index.php/Home/User/getCollege", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String response = new String(bytes);
                    Log.e("debug", response);
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response);
                        String nowschool = object.getString("status");
                        if (!nowschool.equals("error")) {
                            SharedPreferences.Editor editor = getSharedPreferences("nowschool", MODE_PRIVATE).edit();
                            editor.putString("nowschool", nowschool);
                            editor.apply();
                            MyApplication.locschool=nowschool;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    //Toast.makeText(SchoolActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void initAllSchoolData() {
        Schoollist schoollist = new Schoollist("当前", "0"); // 当前定位学校
        allSchoolList.add(schoollist);
        schoollist=new Schoollist("最近","1");
        allSchoolList.add(schoollist);
        schoollist=new Schoollist("附近","2");
        allSchoolList.add(schoollist);
        schoollist=new Schoollist("全部","3");
        allSchoolList.add(schoollist);
        allSchoolList.addAll(getSchoolList());
    }

    private void initRecentVisitSchoolData() {
        InsertSchool("成都东软学院");
        SQLiteDatabase recentVisitDb = schoolOpenHelper.getWritableDatabase();
        Cursor cursor = recentVisitDb.rawQuery("select * from recentcity order by date desc limit 0, 3", null);
        while (cursor.moveToNext()) {
            String recentVisitSchoolName=cursor.getString(cursor.getColumnIndex("name"));
            recentSchoolList.add(recentVisitSchoolName);
        }
        cursor.close();
        recentVisitDb.close();
    }

    private void initNearSchoolData() {
        Schoollist school=new Schoollist("成都东软学院","2");
        nearSchoolList.add(school);
        school=new Schoollist("四川外国语大学成都学院","2");
        nearSchoolList.add(school);
    }

    private void setAdapter() {
        schoolListAdapter = new SchoolListAdapter(SchoolActivity.this,allSchoolList,nearSchoolList,recentSchoolList);
        searchResultAdapter=new SearchResultAdapter(this,searchSchoolList);
        lvSchool.setAdapter(schoolListAdapter);
        lvResult.setAdapter(searchResultAdapter);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Schoollist> getSchoolList() {
        SQLiteDatabase db;
        Cursor cursor = null;
        //获取assets目录下的数据库中的所有学校的openHelper
        AllSchoolSqliteOpenHelper op=new AllSchoolSqliteOpenHelper(SchoolActivity.this);
        ArrayList<Schoollist> schooList=new ArrayList<Schoollist>();

        try {
            op.createDataBase();
            db = op.getWritableDatabase();
            cursor= db.rawQuery("select * from city",null);

            while (cursor.moveToNext()) {
                String cityName=cursor.getString(cursor.getColumnIndex("name"));
                String cityPinyin=cursor.getString(cursor.getColumnIndex("pinyin"));
                Schoollist city=new Schoollist(cityName,cityPinyin);
                schooList.add(city);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            cursor.close();
        }
        Collections.sort(schooList, comparator);
        return schooList;

    }

    /**
     * 自定义的排序规则，按照A-Z进行排序
     */
    @SuppressWarnings("rawtypes")
    Comparator comparator = new Comparator<Schoollist>() {
        @Override
        public int compare(Schoollist lhs, Schoollist rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };

    @SuppressWarnings("unchecked")
    private void getResultCityList(String keyword) {
        AllSchoolSqliteOpenHelper dbHelper = new AllSchoolSqliteOpenHelper(this);
        try {
            dbHelper.createDataBase();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(
                    "select * from city where name like \"%" + keyword
                            + "%\" or pinyin like \"%" + keyword + "%\"", null);
            Schoollist school;
            while (cursor.moveToNext()) {
                String schoolName=cursor.getString(cursor.getColumnIndex("name"));
                String schoolPinyin=cursor.getString(cursor.getColumnIndex("pinyin"));
                school = new Schoollist(schoolName,schoolPinyin);
                searchSchoolList.add(school);
            }
            cursor.close();
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将得到的集合按照自定义的comparator的规则进行排序
        Collections.sort(searchSchoolList, comparator);
    }



    //插入数据到最近访问的学校
    public void InsertSchool(String name) {
        SQLiteDatabase db = schoolOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from recentcity where name = '"
                + name + "'", null);
        if (cursor.getCount() > 0) { //
            db.delete("recentcity", "name = ?", new String[] { name });
        }
        db.execSQL("insert into recentcity(name, date) values('" + name + "', "
                + System.currentTimeMillis() + ")");
        db.close();
    }

    // 设置显示字母的TextView为不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            tvDialog.setVisibility(View.INVISIBLE);
        }
    }

}
