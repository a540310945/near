package com.hitoncloud.near.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.community.ChatActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderActivity extends AppCompatActivity {

    Toolbar toolbar;

    String taskmoney, taskname, taskloc, tasktime, taskphone, taskdetails, taskordernum, tasktype, tasknickname, tasktel, taskpublisher;
    int ordertype;//用来决定此页面的功能 1:发布任务订单 2:抢单

    TextView tv_taskname, tv_tasktime, tv_taskloc, tv_taskdetails, tv_taskmoney, tv_tasknickname, tv_taskcontact;
    TextView tv_accepterkey;
    Button btn_ensure, btn_session, btn_phone, btn_cancel;//

    SmartRefreshLayout srl_order;

    String requesturl;

    private LinearLayout ll_order_ensure;//底部布局
    private TextView tv_ensuremoney;//确认给钱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        getIntentData();
        initView();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        taskname = intent.getStringExtra("taskname");
        taskmoney = intent.getStringExtra("taskmoney");
        taskloc = intent.getStringExtra("taskloc");
        tasktime = intent.getStringExtra("tasktime");
        taskphone = intent.getStringExtra("taskphone");
        taskdetails = intent.getStringExtra("taskdetails");
        taskordernum = intent.getStringExtra("taskordernum");
        tasknickname = intent.getStringExtra("tasknickname");
        taskpublisher = intent.getStringExtra("taskpublisher");
        tasktype = intent.getStringExtra("tasktype");
        ordertype = intent.getIntExtra("ordertype", 0);

    }


    private void initView() {
        srl_order = (SmartRefreshLayout) findViewById(R.id.srl_order);
        srl_order.setEnableRefresh(false);
        tv_ensuremoney = (TextView) findViewById(R.id.tv_order_ensuremoney);
        ll_order_ensure = (LinearLayout) findViewById(R.id.ll_order_ensure);
        toolbar = (Toolbar) findViewById(R.id.toolbar_order);
        toolbar.setTitle("订单");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderActivity.this.finish();
            }
        });
        tv_taskname = (TextView) findViewById(R.id.tv_order_taskname_value);
        tv_tasktime = (TextView) findViewById(R.id.tv_order_tasktime_value);
        tv_taskloc = (TextView) findViewById(R.id.tv_order_taskloc_value);
        tv_taskdetails = (TextView) findViewById(R.id.tv_order_taskdetails_value);
        tv_taskmoney = (TextView) findViewById(R.id.tv_order_taskmoney_value);
        tv_tasknickname = (TextView) findViewById(R.id.tv_order_nickname_value);
        tv_taskcontact = (TextView) findViewById(R.id.tv_order_contact_value);
        tv_accepterkey = (TextView) findViewById(R.id.tv_order_nickname_key);
        btn_phone = (Button) findViewById(R.id.btn_order_phone);
        btn_session = (Button)findViewById(R.id.btn_order_session);
        btn_ensure = (Button)findViewById(R.id.btn_order_ensure);

        btn_cancel = (Button)findViewById(R.id.btn_order_cancel);

        switch (ordertype)
        {
            case 0:
                btn_ensure.setText("出错了,请刷新页面");
                break;
            case 1:
                btn_phone.setVisibility(View.GONE);
                btn_session.setVisibility(View.GONE);
                btn_ensure.setVisibility(View.VISIBLE);
                tv_tasknickname.setText(MyApplication.nickname);
                btn_ensure.setText("去支付");
                btn_ensure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =  new Intent(OrderActivity.this,PayForTaskActivity.class);
                        intent.putExtra("taskmoney", taskmoney);
                        intent.putExtra("taskname", taskname);
                        intent.putExtra("taskordernum",taskordernum);
                        startActivity(intent);
                    }
                });


                httpfororderinfo();
                break;
            case 2:
                httpfororderinfo();
                ll_order_ensure.setVisibility(View.VISIBLE);
                btn_ensure.setVisibility(View.VISIBLE);
                btn_ensure.setBackgroundResource(R.drawable.btn_order_cancel);
                tv_ensuremoney.setText(taskmoney);
                tv_tasknickname.setText(tasknickname);
                btn_ensure.setText("确认抢单");
                btn_ensure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendRequset(1);
                    }
                });
                break;
            case 3:
                httpfororderinfo();
                btn_cancel.setVisibility(View.VISIBLE);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        canceltask();
                    }
                });
                btn_ensure.setVisibility(View.VISIBLE);
                btn_phone.setVisibility(View.GONE);
                btn_ensure.setText("确认完成");
                btn_ensure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finishtask();
                    }
                });
                break;
            case 4:
                httpfororderinfo();
                btn_ensure.setVisibility(View.VISIBLE);
                btn_ensure.setText("如需放弃任务，请联系发布者");
                break;
            default:
                break;
        }
    }

    private void finishtask() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("orderNum", taskordernum);
        client.post("http://123.207.46.157/near/index.php/Admin/Manager/finishTask", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    String status = object.getString("status");
                    if(status.equals("success"))
                    {
                        Toast.makeText(OrderActivity.this,"任务已经完成，钱会在1-2个工作日内转入发布者的账户",Toast.LENGTH_SHORT).show();
                        OrderActivity.this.finish();
                    }else{
                        Toast.makeText(OrderActivity.this, "出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });


    }

    private void canceltask() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("orderNum", taskordernum);
        client.post("http://123.207.46.157/near/index.php/Home/Index/apply_back", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug", response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    String status = object.getString("status");
                    if(status.equals("success"))
                    {
                        Toast.makeText(OrderActivity.this,"取消任务成功",Toast.LENGTH_SHORT).show();
                        OrderActivity.this.finish();
                    }else{
                        Toast.makeText(OrderActivity.this, "出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });

    }

    //显示任务信息
    private void httpfororderinfo()
    {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.add("orderNum", taskordernum);
            client.post("http://123.207.46.157/near/index.php/Admin/Manager/showDetail", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String response = new String(bytes);
                    Log.e("OrderActivity", response);
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response);
                        String name = object.getString("taskname");
                        String time = object.getString("limittime");
                        String loc = object.getString("address");
                        String details = object.getString("details");
                        String money = object.getString("money");
                        String nickname = object.getString("nickname");
                        final String nickname2 = object.getString("nickname2");
                        final String accepterid = object.getString("userid2");
                        String isaccept = object.getString("isaccept");
                        String isdelete = object.getString("isdelete");
                        String ispay = object.getString("status");
                        final String tel = object.getString("tel");

                        if(details.equals(""))
                            tv_taskdetails.setText("发布者似乎没有填写特别的信息");
                        else
                            tv_taskdetails.setText(details);
                        tv_taskname.setText(name);
                        tv_tasktime.setText(time);
                        tv_taskloc.setText(loc);
                        tv_taskmoney.setText(money);
                        tv_tasknickname.setText(nickname);
                        if(ordertype ==3)
                        {
                            btn_session.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(OrderActivity.this, ChatActivity.class);
                                    intent.putExtra("taskpublisher",accepterid);
                                    intent.putExtra("nickname",nickname2);
                                    startActivity(intent);
                                }
                            });
                            if(!tel.equals(""))
                            {
                                btn_phone.setVisibility(View.VISIBLE);
                                btn_phone.setOnClickListener(new View.OnClickListener() {
                                    @SuppressLint("MissingPermission")
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+tel));
                                        startActivity(intent);
                                    }
                                });
                            }
                            tv_accepterkey.setText("接受者：");
                            tv_tasknickname.setText(nickname2);
                            tv_taskcontact.setText("仅能通过会话联系");
                        }else
                        {
                            if(tel.equals("")) {
                                tv_taskcontact.setText("仅能通过会话联系");
                                btn_phone.setVisibility(View.GONE);
                            }
                            else
                                tv_taskcontact.setText(tel);
                            btn_session.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(OrderActivity.this, ChatActivity.class);
                                    intent.putExtra("taskpublisher",taskpublisher);
                                    intent.putExtra("nickname",tasknickname);
                                    startActivity(intent);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(OrderActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
    }


    private void sendRequset(final int code)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if(code == 1) //抢单
        {
            params.add("userid2", MyApplication.username);
            params.add("orderNum", taskordernum);
            requesturl = "http://123.207.46.157/near/index.php/Admin/Manager/acceptTask";
        }

        client.post(requesturl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("OrderActivity", response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    String status = object.getString("status");
                    if(code == 1)
                    {
                        if(status.equals("error"))
                        {
                            Toast.makeText(OrderActivity.this, "你不能接受自己的任务", Toast.LENGTH_SHORT).show();
                        }else if(status.equals("success"))
                        {
                            Toast.makeText(OrderActivity.this, "抢单成功，任务已经加入您的任务列表", Toast.LENGTH_SHORT).show();
                            OrderActivity.this.finish();
                        }else
                        {
                            Toast.makeText(OrderActivity.this, "单子被别人抢走啦", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(OrderActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(MyApplication.payfinish==1)
        {
            finish();
        }
    }
}
