package com.hitoncloud.near.mine;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


/**
 * 功能：余额查询
 */
public class Leftmoney extends Activity {

    Button btn_bug;
    LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leftmonry);
        back = (LinearLayout)findViewById(R.id.ll_leftmoney_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Leftmoney.this.finish();
            }
        });
        btn_bug = (Button)findViewById(R.id.withdraw);

        btn_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.add("username","1");
                params.add("account", MyApplication.username);
                params.add("amount","0.1");
                client.post("http://123.207.46.157/near/index.php/Admin/Alipay/withDraw", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", response);
                        Toast.makeText(Leftmoney.this, "钱已经提取到您的支付宝账户", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        String response = new String(bytes);
                        Log.e("debug", response);
                        Toast.makeText(Leftmoney.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
