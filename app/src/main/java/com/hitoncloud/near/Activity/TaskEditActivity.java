package com.hitoncloud.near.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.utils.VerificationUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rey.material.widget.CheckBox;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/*主页-首页-任务发布*/
public class TaskEditActivity extends AppCompatActivity {

    private static final String TAG= "TaskEditActivity";

    boolean mBackKeyPressed = false;//判断连续2次按下返回按钮

    Toolbar toolbar;//顶部栏

    String starttime,endtime;//起始时间-结束时间
    String firsttype,secondtype;//一级分类任务，二级分类任务

    TextView tv_tasktime,tv_tasktype;//时间,任务类型
    CheckBox cb_phone;//电话可选联系
    RelativeLayout rl_ensure,rl_tasktime,rl_tasktype;//确定按钮，时间栏，任务类型
    LinearLayout ll_phone;//手机号
    EditText et_taskname,et_taskloc,et_taskphone,et_taskdetails,et_taskmoney;//任务名称，任务地点，任务电话（选填），任务详情描述
    SmartRefreshLayout srl;

    String taskname,taskloc,tasktime,tasktype,taskmoney,taskphone,taskdetails;
    boolean isphone = false;

    boolean allowpublish =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();
    }

    private void initView() {
        //初始化顶部
        toolbar = (Toolbar)findViewById(R.id.toolbar_taskedit);
        toolbar.setTitle("任务编辑");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskEditActivity.this.finish();
            }
        });

        //初始化任务名称
        et_taskname = (EditText)findViewById(R.id.et_taskedit_taskname);
        et_taskname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER);
            }
        });
        //初始化任务地点
        et_taskloc = (EditText)findViewById(R.id.et_taskedit_taskloc);
        et_taskloc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER);
            }
        });
        //初始化任务限定时间
        tv_tasktime = (TextView)findViewById(R.id.tv_taskedit_timehint);
        rl_tasktime = (RelativeLayout)findViewById(R.id.rl_taskedit_tasktime);
        rl_tasktime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskEditActivity.this,TaskEditTimeActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        //初始化任务类型
        tv_tasktype = (TextView)findViewById(R.id.tv_taskedit_typehint);
        rl_tasktype = (RelativeLayout)findViewById(R.id.rl_taskedit_tasktype);
        rl_tasktype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskEditActivity.this,TaskEditTypeActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        //初始化悬赏金额
        et_taskmoney = (EditText)findViewById(R.id.et_taskedit_taskmoney);
        limitmoney();//限制金额输入
        //初始化联系方式
        ll_phone = (LinearLayout)findViewById(R.id.ll_taskedit_taskphone);
        et_taskphone = (EditText)findViewById(R.id.et_taskedit_taskphone);
        cb_phone = (CheckBox)findViewById(R.id.cb_taskedit_phone);
        cb_phone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    isphone = true;
                    ll_phone.setVisibility(View.VISIBLE);
                }else{
                    isphone = false;
                    ll_phone.setVisibility(View.GONE);
                }
            }
        });
        //初始化任务详情
        et_taskdetails = (EditText)findViewById(R.id.et_taskedit_taskdetails);
        rl_ensure = (RelativeLayout)findViewById(R.id.rl_taskedit_ensure);
        rl_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpfortask();
            }
        });
        //初始化刷新惯性布局
        srl = (SmartRefreshLayout)findViewById(R.id.srl_taskedit);
        srl.setEnableRefresh(false);

    }

    //请求生成订单
    private void httpfortask() {
        taskname = et_taskname.getText().toString();
        taskloc = et_taskloc.getText().toString();
        tasktime = tv_tasktime.getText().toString();
        tasktype = tv_tasktype.getText().toString();
        taskmoney = et_taskmoney.getText().toString();
        taskphone = et_taskphone.getText().toString();
        taskdetails = et_taskdetails.getText().toString();
        if(!VerificationUtils.isCellPhoneNumber(taskphone))
        {
            isphone = false;
            Log.e(TAG, "手机号码错误"+taskphone);
        }
        if (taskname.equals("") || taskloc.equals("") || tasktime.equals("任务限定时间") || tasktype.equals("选择任务类型") || taskmoney.equals("")) {
            Toast.makeText(TaskEditActivity.this, "请完善任务信息", Toast.LENGTH_SHORT).show();
        }else if(allowpublish){
            allowpublish =false;
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.add("userid1",MyApplication.username);
            params.add("taskName", taskname);
            params.add("address", taskloc);
            params.add("limitTime", tasktime);
            params.add("type", tasktype);
            params.add("money", taskmoney);
            params.add("details", taskdetails);
            if(isphone)
                params.add("tel", taskphone);
            client.post("http://123.207.46.157/near/index.php/Admin/Manager/addTask", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.e("debug", response);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                                String status = object.getString("status");
                                String taskordernum = object.getString("orderNum");
                                if (status.equals("success")) {

                                    Intent intent = new Intent(TaskEditActivity.this, OrderActivity.class);
                                    intent.putExtra("taskname", taskname);
                                    intent.putExtra("taskloc", taskloc);
                                    intent.putExtra("tasktime", tasktime);
                                    intent.putExtra("tasktype", tasktype);
                                    intent.putExtra("taskmoney", taskmoney);
                                    if(isphone)
                                        intent.putExtra("taskphone", taskphone);
                                    else
                                        intent.putExtra("taskphone", "");
                                    intent.putExtra("taskdetails", taskdetails);
                                    intent.putExtra("taskordernum",taskordernum);
                                    intent.putExtra("ordertype",1);
                                    startActivity(intent);
                                    allowpublish =true;
                                 } else {
                                    allowpublish =true;
                                     Log.e(TAG, "请求订单出现未知错误");
                                 }
                            } catch (JSONException e) {
                                allowpublish =true;
                                Log.e(TAG, "请求订单崩溃");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Log.e(TAG, "请求订单失败");
                            allowpublish =true;
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == 10001) {
              if (requestCode == 1) {
                  starttime= data.getStringExtra("starttime");
                  endtime = data.getStringExtra("endtime");
                  tv_tasktime.setText(starttime+"-"+endtime);

              }
         }
         if(resultCode == 10002){
             if(requestCode ==2){
                 firsttype= data.getStringExtra("firsttype");
                 secondtype = data.getStringExtra("secondtype");
                 tv_tasktype.setText(firsttype+"-"+secondtype);
             }
         }
    }


    //连续按2次返回则退出程序
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!mBackKeyPressed) {
                Toast.makeText(this, "再按一次返回，离开页面数据将不会保留哦", Toast.LENGTH_SHORT).show();
                mBackKeyPressed = true;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mBackKeyPressed = false;
                    }
                }, 2000);
                return true;
            } else {
                TaskEditActivity.this.finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    //限制金额框输入
    private void limitmoney(){
        /*限制金额输入框的小数点为1个*/
        et_taskmoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_taskmoney.getText().toString().indexOf(".") >= 0) {
                    if (et_taskmoney.getText().toString().indexOf(".", et_taskmoney.getText().toString().indexOf(".") + 1) > 0) {
                        et_taskmoney.setText(et_taskmoney.getText().toString().substring(0, et_taskmoney.getText().toString().length() - 1));
                        et_taskmoney.setSelection(et_taskmoney.getText().toString().length());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });
    }


    public static String JSONTokener(String in) {
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MyApplication.payfinish==1)
        {
            MyApplication.payfinish=0;
            TaskEditActivity.this.finish();
        }
    }
}
