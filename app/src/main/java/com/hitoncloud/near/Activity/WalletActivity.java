package com.hitoncloud.near.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

//
public class WalletActivity extends AppCompatActivity {

    Toolbar toolbar;

    private RelativeLayout rl_withdraw,rl_refund;

    private TextView tv_money;

    SmartRefreshLayout srl_wallet;


    String leftmoney,status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();
    }

    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_wallet);
        toolbar.setTitle("我的钱包");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WalletActivity.this.finish();
            }
        });
        //进入提现界面
        rl_withdraw = (RelativeLayout)findViewById(R.id.rl_wallet_withdraw);
        rl_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalletActivity.this,WithdrawActivity.class);
                startActivity(intent);
            }
        });
        //进入申请退款界面
        rl_refund = (RelativeLayout)findViewById(R.id.rl_wallet_refund);
        rl_refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalletActivity.this,RefundActivity.class);
                startActivity(intent);
            }
        });
        srl_wallet = (SmartRefreshLayout)findViewById(R.id.srl_wallet);
        tv_money = (TextView)findViewById(R.id.tv_wallet_leftmoney);
        srl_wallet.setOnRefreshListener(new OnRefreshListener() {
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
        params.add("username", MyApplication.username);
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
                    if (status.equals("success")) {
                        tv_money.setText(leftmoney);
                    }
                    srl_wallet.finishRefresh();
                } catch (JSONException e) {
                    srl_wallet.finishRefresh();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                srl_wallet.finishRefresh();
            }
        });

    }
}
