package com.hitoncloud.near.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.List.BillList;
import com.hitoncloud.near.R;
import com.hitoncloud.near.RecyclerItemClickListener;
import com.hitoncloud.near.adapter.BillAdapter;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.homepage.SearchTaskdetailsAdapter;
import com.hitoncloud.near.homepage.SearchTaskdetailslist;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BillActivity extends AppCompatActivity {

    Toolbar toolbar;
    RelativeLayout rl_nobill;
    RecyclerView rv_bill;
    BillAdapter billAdapter;//账单适配器
    int numofJSON=0;

    List<BillList> billLists = new ArrayList<>();//账单列表


    String taskname[] = new String[99];
    String taskmoney[] = new String[99];
    String taskdate[] = new String[99];
    String taskpublisher[]  = new String[99];
    String taskaccepter[] =  new String[99];
    String ordernum[] = new String[99];
    String taskstatus[] = new String[99];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();
    }

    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_bill);
        toolbar.setTitle("我的账单");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillActivity.this.finish();
            }
        });
        //无账单时显示布局
        rl_nobill = (RelativeLayout)findViewById(R.id.rl_bill_none);
        //初始化账单recyclerview
        rv_bill = (RecyclerView)findViewById(R.id.rv_bill);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_bill.setLayoutManager(linearLayoutManager);
        billAdapter = new BillAdapter(billLists,BillActivity.this);

       // httpforbillinfo();

        rv_bill.setAdapter(billAdapter);

        rv_bill.addOnItemTouchListener(new RecyclerItemClickListener(BillActivity.this, rv_bill, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BillAdapter.BillsHolder h = (BillAdapter.BillsHolder)rv_bill.findViewHolderForLayoutPosition(position);
                String taskname = h.tv_taskname.getText().toString();
                String taskmoney = h.tv_taskmoney.getText().toString();
                String taskdate = h.tv_taskdate.getText().toString();
                String taskpublisher = h.publisher;
                String taskaccepter = h.accepter;
                String ordernum = h.ordernum;
                Intent intent = new Intent(BillActivity.this, BillInfoActivity.class);
                intent.putExtra("taskname", taskname);
                intent.putExtra("taskmoney",taskmoney);
                intent.putExtra("taskdate",taskdate);
                intent.putExtra("taskpublisher",taskpublisher);
                intent.putExtra("taskordernum",ordernum);
                intent.putExtra("taskaccepter",taskaccepter);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    private void httpforbillinfo() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        //params.add("briefname",str);
        client.post("http://123.207.46.157/near/index.php/Home/Index/exactSearch", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    numofJSON = 0;
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject getJsonObj = jsonArray.getJSONObject(k);
                        taskname[k] = getJsonObj.getString("taskname");
                        taskmoney[k] = getJsonObj.getString("money");
                        taskdate[k] = getJsonObj.getString("taskdate");
                        taskpublisher[k] = getJsonObj.getString("nickname");
                        ordernum[k] = getJsonObj.getString("ordernum");
                        taskaccepter[k] = getJsonObj.getString("address");
                        taskstatus[k] = getJsonObj.getString("address");
                        numofJSON++;
                    }
                    for (int k = 0; k < numofJSON; k++) {
                        billLists.add(new BillList(taskname[k],taskmoney[k],taskdate[k],taskstatus[k],ordernum[k],taskpublisher[k],taskaccepter[k]));
                    }
                    billAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });



    }
}
