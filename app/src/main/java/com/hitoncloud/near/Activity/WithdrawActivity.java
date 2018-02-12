package com.hitoncloud.near.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.mine.Leftmoney;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;
import com.scwang.smartrefresh.header.BezierCircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;

import org.apache.http.Header;
import org.apache.http.cookie.SM;
import org.json.JSONException;
import org.json.JSONObject;

public class WithdrawActivity extends AppCompatActivity {

    Toolbar toolbar;

    private ImageView iv_alipay_arrow;
    private RelativeLayout rl_alipay,rl_alipay_body;

    private SmartRefreshLayout srl_withdraw;

    private boolean isshowalipay = false;//显示支付宝提现

    MaterialEditText met_account,met_money;
    Button btn_withdraw;

    String leftmoney,status;

    BezierCircleHeader srl_withdraw_header;

    TextView tv_leftmoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();
    }

    private void initView() {
        tv_leftmoney = (TextView)findViewById(R.id.tv_withdraw_leftmoney_value);
        toolbar = (Toolbar)findViewById(R.id.toolbar_withdraw);
        toolbar.setTitle("提现");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WithdrawActivity.this.finish();
            }
        });
        //显示或隐藏支付宝提现功能
        iv_alipay_arrow = (ImageView)findViewById(R.id.iv_withdraw_alipay_arrowright);
        rl_alipay = (RelativeLayout)findViewById(R.id.rl_withdraw_alipay);
        rl_alipay_body = (RelativeLayout) findViewById(R.id.rl_withdraw_alipay_body);
        rl_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isshowalipay)
                {
                    iv_alipay_arrow.setImageResource(R.drawable.icon_arrowright);
                    rl_alipay_body.setVisibility(View.INVISIBLE);
                    isshowalipay = false;

                }else
                {
                    iv_alipay_arrow.setImageResource(R.drawable.icon_arrowdown);
                    rl_alipay_body.setVisibility(View.VISIBLE);
                    isshowalipay = true;
                }

            }
        });
        met_account = (MaterialEditText)findViewById(R.id.met_withdraw_account);
        met_money = (MaterialEditText)findViewById(R.id.met_withdraw_money);
        btn_withdraw = (Button)findViewById(R.id.btn_withdraw_ensure);
        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpforwithdraw();
            }
        });

        srl_withdraw_header = (BezierCircleHeader)findViewById(R.id.srl_withdraw_header);
        srl_withdraw = (SmartRefreshLayout)findViewById(R.id.srl_withdraw);
        srl_withdraw.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                httpforleftmoney();
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
                    status = object.getString("status");
                    leftmoney = object.getString("leftmoney");
                    if(status.equals("success"))
                    {
                        tv_leftmoney.setText(leftmoney);
                    }
                    srl_withdraw.finishRefresh();
                } catch (JSONException e) {
                    srl_withdraw.finishRefresh();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                srl_withdraw.finishRefresh();
            }
        });





    }

    private void httpforwithdraw()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("username", MyApplication.username);
        params.add("money",met_money.getText().toString());
        params.add("target_id", met_account.getText().toString());
        Log.e("spmoney",met_money.getText().toString());
        Log.e("target_id",met_account.getText().toString());
        client.post("http://123.207.46.157/near/index.php/Home/Index/apply_withdraw", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug", response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    status = object.getString("status");
                    if(status.equals("success"))
                    {
                        Toast.makeText(WithdrawActivity.this, "请求已经发出，钱将在1-2个工作日转入您的支付宝账户", Toast.LENGTH_SHORT).show();
                    }else if(status.equals("error"))
                    {
                        Toast.makeText(WithdrawActivity.this,"余额不足",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                String response = new String(bytes);
                Log.e("debug", response);
                Toast.makeText(WithdrawActivity.this, "网络错误,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
