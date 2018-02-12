package com.hitoncloud.near.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.alipay.AuthResult;
import com.hitoncloud.near.alipay.PayResult;
import com.hitoncloud.near.application.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class PayForTaskActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView iv_banner;

    Button btn_pay;
    String taskmoney,taskname,taskordernum;

    RelativeLayout rl_alipay,rl_nearpay;
    TextView tv_taskmoeny,tv_taskname,tv_money,tv_leftmoney;

    private ImageView iv_alipay,iv_nearpay;

    private RadioGroup rb_group;

    private int payway =1;

    private String leftmoney;


    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(PayForTaskActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        MyApplication.payfinish =1;
                        PayForTaskActivity.this.finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PayForTaskActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(PayForTaskActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(PayForTaskActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payfortask);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        getIntentData();
        initView();
        initPay();
        httpforleftmoney();


    }

    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_payfortask);
        toolbar.setTitle("支付");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayForTaskActivity.this.finish();
            }
        });

        iv_banner = (ImageView)findViewById(R.id.iv_payfortask_banner);
        Glide.with(this).load(R.drawable.banner_pay1).into(iv_banner);

        tv_taskmoeny = (TextView)findViewById(R.id.tv_payfortask_taskmoney);
        tv_taskname = (TextView)findViewById(R.id.tv_payfortask_taskname);
        tv_money = (TextView)findViewById(R.id.tv_payfortask_money);
        tv_leftmoney = (TextView)findViewById(R.id.tv_payfortask_leftmoney_value);
        tv_money.setText(taskmoney);
        tv_taskmoeny.setText("¥"+taskmoney);
        tv_taskname.setText(taskname);

        rl_alipay = (RelativeLayout)findViewById(R.id.rl_payfortask_way_ali);
        rl_nearpay = (RelativeLayout)findViewById(R.id.rl_payfortask_way_near);
        iv_alipay = (ImageView) findViewById(R.id.iv_payfortask_alipay_check);
        iv_nearpay = (ImageView)findViewById(R.id.iv_payfortask_nearpay_check);
        rl_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_nearpay.setVisibility(View.INVISIBLE);
                iv_alipay.setVisibility(View.VISIBLE);
                payway =1;
            }
        });

        rl_nearpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_nearpay.setVisibility(View.VISIBLE);
                iv_alipay.setVisibility(View.INVISIBLE);
                payway =2;
            }
        });

       httpforleftmoney();


    }

    private void httpforleftmoney() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("username",MyApplication.username);
        client.post("http://123.207.46.157/near/index.php/Home/Index/leftmoney", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug", response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    leftmoney = object.getString("leftmoney");
                    tv_leftmoney.setText(leftmoney);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });


    }

    private void getIntentData() {
        Intent intent = getIntent();
        taskmoney = intent.getStringExtra("taskmoney");
        taskname = intent.getStringExtra("taskname");
        taskordernum = intent.getStringExtra("taskordernum");

    }

    private void initPay() {
        if(taskmoney.equals("0"))
            payway = 3;
        btn_pay =(Button) findViewById(R.id.btn_payfortask_pay);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(payway==1)
                {
                    //alipay
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("orderNum", taskordernum);
                    params.add("money", taskmoney);
                    client.post("http://123.207.46.157/near/index.php/Admin/Alipay/alipay_before", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.e("debug",response);
                            String tmp = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(tmp);
                                final String orderInfo = object.getString("orderinfo");
                                System.out.println(orderInfo);
                                Runnable payRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        PayTask alipay = new PayTask(PayForTaskActivity.this);
                                        Map<String, String> result = alipay.payV2(orderInfo, true);
                                        Log.i("msp", result.toString());
                                        Message msg = new Message();
                                        msg.what = SDK_PAY_FLAG;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };
                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(PayForTaskActivity.this, "网络错误，请稍后重复", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(payway == 2)
                {
                    //nearpay
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("orderNum", taskordernum);
                    params.add("money", taskmoney);
                    client.post("http://123.207.46.157/near/index.php/Admin/Alipay/leftmoney_pay", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.e("debug",response);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                               String orderInfo = object.getString("status");
                               if(orderInfo.equals("success"))
                               {
                                   Toast.makeText(PayForTaskActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                                   MyApplication.payfinish=1;
                                   PayForTaskActivity.this.finish();
                               }else if(orderInfo.equals("error"))
                               {
                                   Toast.makeText(PayForTaskActivity.this,"余额不足",Toast.LENGTH_SHORT).show();
                               }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(PayForTaskActivity.this, "网络错误，请稍后重复", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(payway ==3)
                {
                    //0元pay
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("orderNum", taskordernum);
                    params.add("money", taskmoney);
                    client.post("http://123.207.46.157/near/index.php/Admin/Alipay/free_pay", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.e("debug",response);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                                String orderInfo = object.getString("status");
                                if(orderInfo.equals("success"))
                                {
                                    Toast.makeText(PayForTaskActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                                    MyApplication.payfinish=1;
                                    PayForTaskActivity.this.finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(PayForTaskActivity.this, "网络错误，请稍后重复", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }
        });
    }
}
