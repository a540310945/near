package com.hitoncloud.near.task;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hitoncloud.near.Activity.OrderActivity;
import com.hitoncloud.near.R;
import com.hitoncloud.near.RecyclerItemClickListener;
import com.hitoncloud.near.TaskGet_taskDetails;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.homepage.SearchTaskdetailsAdapter;
import com.hitoncloud.near.homepage.SearchTaskdetailslist;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*主页-任务-已发布任务列表*/
public class AcceptTaskFragment extends Fragment {

    private RecyclerView rv_accepttask;
    List<SearchTaskdetailslist> searchTaskdetailslists = new ArrayList<>();//详细信息列表

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
    String isaccept[] = new String[11];//是否被接受

    SearchTaskdetailsAdapter sdadapter;//详细信息适配器


    View view;


    SmartRefreshLayout srl_accepttask;

    ClassicsHeader srl_accept_header;
    ClassicsFooter srl_accept_footer;

    int countaccept = 0;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_accepttask, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inittask();

    }

    private void inittask() {
        rv_accepttask = (RecyclerView)view.findViewById(R.id.rv_accepttask);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        linearLayoutManager.setReverseLayout(true);
        rv_accepttask.setLayoutManager(linearLayoutManager);
        sdadapter = new SearchTaskdetailsAdapter(searchTaskdetailslists,getActivity());
        rv_accepttask.setAdapter(sdadapter);
        rv_accepttask.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv_accepttask, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SearchTaskdetailsAdapter.DetailsHolder h = (SearchTaskdetailsAdapter.DetailsHolder) rv_accepttask.findViewHolderForLayoutPosition(position);
                String taskname = h.tv_taskname.getText().toString();
                String taskmoeny = h.tv_taskmoney.getText().toString();
                String tasktime = h.tv_tasktime.getText().toString();
                String tasknickname = h.tv_tasknickname.getText().toString();
                String taskloc = h.tv_taskloc.getText().toString();
                String ordernum = h.ordernum;
                String details = h.details;
                String taskpublisher = h.publisher;
                String tel = h.tel;
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("taskname", taskname);
                intent.putExtra("taskmoney",taskmoeny);
                intent.putExtra("taskloc",taskloc);
                intent.putExtra("tasktime",tasktime);
                intent.putExtra("tasknickname",tasknickname);
                intent.putExtra("taskordernum",ordernum);
                intent.putExtra("taskdetails",details);
                intent.putExtra("taskphone",tel);
                intent.putExtra("taskpublisher",taskpublisher);
                intent.putExtra("ordertype",4);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        srl_accepttask  = (SmartRefreshLayout)view.findViewById(R.id.srl_accepttask);
        srl_accept_header = (ClassicsHeader)view.findViewById(R.id.srl_accepttask_header);
        srl_accept_footer = (ClassicsFooter)view.findViewById(R.id.srl_accepttask_footer);
        srl_accepttask.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                countaccept = 0;
                httpforaccepttask();
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                Log.e("wocao","发起请求");
                httpforaccepttask();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        countaccept = 0;
        rv_accepttask.removeAllViews();
        searchTaskdetailslists.clear();
        httpforaccepttask();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        countaccept = 0;
        rv_accepttask.removeAllViews();
        searchTaskdetailslists.clear();
        httpforaccepttask();
    }

    private void httpforaccepttask() {
        if(countaccept == 0)
        {
            //清空数据
            rv_accepttask.removeAllViews();
            searchTaskdetailslists.clear();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("username", MyApplication.username);
        params.add("start", String.valueOf(countaccept));
        client.post("http://123.207.46.157/near/index.php/Admin/Manager/showTasked", params, new AsyncHttpResponseHandler() {
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
                        tasktime[k] = getJsonObj.getString("limittime");
                        nickname[k] = getJsonObj.getString("nickname");
                        ordernum[k] = getJsonObj.getString("ordernum");
                        loc[k] = getJsonObj.getString("address");
                        taskdetails[k] = getJsonObj.getString("details");
                        tasktel[k] = getJsonObj.getString("tel");
                        publisher[k] = getJsonObj.getString("userid1");
                        isaccept[k] = getJsonObj.getString("isaccept");
                        numofJSON++;
                    }
                    for (int k = 0; k < numofJSON; k++) {
                        searchTaskdetailslists.add(new SearchTaskdetailslist(taskname[k], tasktime[k], taskmoney[k], nickname[k],ordernum[k],loc[k],taskdetails[k],tasktel[k],publisher[k],isaccept[k]));
                    }
                    sdadapter.notifyDataSetChanged();
                    srl_accept_footer.REFRESH_FOOTER_FINISH = "加载完成";
                    srl_accepttask.finishRefresh();
                    srl_accepttask.finishLoadmore();
                    countaccept = countaccept+10;
                } catch (JSONException e) {
                    srl_accept_footer.REFRESH_FOOTER_FINISH = "已经没有更多数据了";
                    srl_accepttask.finishRefresh();
                    srl_accepttask.finishLoadmore();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                srl_accept_footer.REFRESH_FOOTER_FINISH = "加载失败";
                srl_accepttask.finishLoadmore();
                srl_accepttask.finishRefresh();
            }
        });




    }
}
