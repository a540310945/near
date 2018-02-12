package com.hitoncloud.near.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;


import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.RecyclerItemClickListener;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.homepage.SearchTaskbriefAdapter;
import com.hitoncloud.near.homepage.SearchTaskbrieflist;
import com.hitoncloud.near.homepage.SearchTaskdetailsAdapter;
import com.hitoncloud.near.homepage.SearchTaskdetailslist;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过搜索匹配任务。第一次通过模糊匹配找出可能的结果，第二次通过点击可能的结果列表找出精确的项
 */

public class SearchTaskActivity extends AppCompatActivity{

    MaterialEditText et_search;//搜素框
    RecyclerView rv_brief,rv_details;//简要列表，详细列表
    List<SearchTaskbrieflist> searchTaskbrieflists = new ArrayList<>();//简要信息列表
    List<SearchTaskdetailslist> searchTaskdetailslists = new ArrayList<>();//详细信息列表
    String resultforsearchtb[] = new String[99];//简要列表字符串

    int numofJSON=0;//返回的JSON数据数量
    String taskname[] = new String[99];//任务名称
    String taskmoney[] = new String[99];//任务悬赏金额
    String tasktime[] = new String[99];//任务完成期限
    String publisher[] = new String[99];//任务发布者id
    String nickname[] = new String[99];//任务发布者昵称
    String ordernum[] = new String[99];//订单号
    String loc[] = new String[99];//地点
    String taskdetails[] =new String[99];//任务详情
    String tasktel[] =new String[99];//任务电话号码

    SearchTaskbriefAdapter sbadapter;//简要信息适配器
    SearchTaskdetailsAdapter sdadapter;//详细信息适配器

    String str_taskdetails;//精确查找任务名

    Toolbar toolbar;
    SmartRefreshLayout srl_searchdetails;

    ClassicsFooter srl_details_footer;

    int countdetails = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initview();
        initedittext();//初始化输入框
    }

    private void initview() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_search);
        toolbar.setTitle("搜索任务");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchTaskActivity.this.finish();
            }
        });
        //初始化任务详情列表上拉加载
        srl_searchdetails = (SmartRefreshLayout)findViewById(R.id.srl_searchdetails);
        srl_searchdetails.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                Log.e("wocao","发起请求");
                httpfortaskdetails();
            }
        });
        srl_searchdetails.setEnableRefresh(false);
        srl_details_footer = (ClassicsFooter)findViewById(R.id.srl_taskdetails_footer);
        //初始化一级模糊搜索列表
        rv_brief = (RecyclerView)findViewById(R.id.rv_search_brief);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        rv_brief.setLayoutManager(linearLayoutManager1);
        sbadapter = new SearchTaskbriefAdapter(searchTaskbrieflists);
        rv_brief.setAdapter(sbadapter);
        //初始化二级精确搜索列表
        rv_details = (RecyclerView)findViewById(R.id.rv_search_details);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        rv_details.setLayoutManager(linearLayoutManager2);
        sdadapter = new SearchTaskdetailsAdapter(searchTaskdetailslists,SearchTaskActivity.this);
        rv_details.setAdapter(sdadapter);
    }

    private void initedittext() {
        et_search = (MaterialEditText) findViewById(R.id.met_search_text);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //重置显示列表
                rv_brief.removeAllViews();
                searchTaskbrieflists.clear();
                rv_brief.setVisibility(View.VISIBLE);
                srl_searchdetails.setVisibility(View.GONE);

                //发起模糊搜索
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                if(!et_search.getText().toString().equals(""))
                {
                    params.add("keyword",et_search.getText().toString());
                    client.post("http://123.207.46.157/near/index.php/Home/Index/fuzzySearch", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.e("debug", response);
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int k = 0; k < jsonArray.length(); k++) {
                                    JSONObject getJsonObj = jsonArray.getJSONObject(k);
                                    resultforsearchtb[k] = getJsonObj.getString("taskname");
                                    if(resultforsearchtb[k].equals("error"))
                                    {

                                    }else{
                                        SearchTaskbrieflist sf = new SearchTaskbrieflist(resultforsearchtb[k]);
                                        searchTaskbrieflists.add(sf);
                                        sbadapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                        Toast.makeText(SearchTaskActivity.this, "网络错误，请稍后重试搜索", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            }
        });

        //精确查找
        sbadapter.setOnItemClickListener(new SearchTaskbriefAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                countdetails = 0;
                //发起精确查找
                SearchTaskbriefAdapter.BriefHolder h = (SearchTaskbriefAdapter.BriefHolder) rv_brief.findViewHolderForLayoutPosition(position);
                str_taskdetails= h.getBriefname().toString();
                httpfortaskdetails();
            }
        });

        rv_details.addOnItemTouchListener(new RecyclerItemClickListener(SearchTaskActivity.this, rv_details, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SearchTaskdetailsAdapter.DetailsHolder h = (SearchTaskdetailsAdapter.DetailsHolder)rv_details.findViewHolderForLayoutPosition(position);
                String taskname = h.tv_taskname.getText().toString();
                String taskmoeny = h.tv_taskmoney.getText().toString();
                String tasktime = h.tv_tasktime.getText().toString();
                String tasknickname = h.tv_tasknickname.getText().toString();
                String taskloc = h.tv_taskloc.getText().toString();
                String ordernum = h.ordernum;
                String details = h.details;
                String taskpublisher = h.publisher;
                String tel = h.tel;
                Intent intent = new Intent(SearchTaskActivity.this, OrderActivity.class);
                intent.putExtra("taskname", taskname);
                intent.putExtra("taskmoney",taskmoeny);
                intent.putExtra("taskloc",taskloc);
                intent.putExtra("tasktime",tasktime);
                intent.putExtra("tasknickname",tasknickname);
                intent.putExtra("taskordernum",ordernum);
                intent.putExtra("taskdetails",details);
                intent.putExtra("taskphone",tel);
                intent.putExtra("taskpublisher",taskpublisher);
                intent.putExtra("ordertype",2);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    private void httpfortaskdetails() {
        //重置列表
        if(countdetails == 0)
        {
            rv_brief.setVisibility(View.GONE);
            srl_searchdetails.setVisibility(View.VISIBLE);
            rv_details.removeAllViews();
            searchTaskdetailslists.clear();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("briefname",str_taskdetails);
        params.add("start", String.valueOf(countdetails));
        client.post("http://123.207.46.157/near/index.php/Home/Index/exactSearch", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug", response);
                try {
                    MyApplication.searchtaskss = true;
                    JSONArray jsonArray = new JSONArray(response);
                    numofJSON = 0;
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject getJsonObj = jsonArray.getJSONObject(k);
                        taskname[k] = getJsonObj.getString("taskname");
                        taskmoney[k] = getJsonObj.getString("money");
                        tasktime[k] = getJsonObj.getString("limittime");
                        nickname[k] = getJsonObj.getString("nickname");
                        ordernum[k] = getJsonObj.getString("ordernum");
                        loc[k] = getJsonObj.getString("address");
                        taskdetails[k] = getJsonObj.getString("details");
                        tasktel[k] = getJsonObj.getString("tel");
                        publisher[k] = getJsonObj.getString("userid1");
                        numofJSON++;
                    }
                    for (int k = 0; k < numofJSON; k++) {
                        searchTaskdetailslists.add(new SearchTaskdetailslist(taskname[k], tasktime[k], taskmoney[k], nickname[k],ordernum[k],loc[k],taskdetails[k],tasktel[k],publisher[k],"-1"));
                    }
                    sdadapter.notifyDataSetChanged();
                    srl_details_footer.REFRESH_FOOTER_FINISH = "加载完成";
                    srl_searchdetails.finishLoadmore();
                    countdetails = countdetails+10;
                } catch (JSONException e) {
                    srl_details_footer.REFRESH_FOOTER_FINISH = "已经没有更多数据了";
                    srl_searchdetails.finishLoadmore();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                srl_details_footer.REFRESH_FOOTER_FINISH = "加载失败";
                srl_searchdetails.finishLoadmore();
            }
        });
    }

}
