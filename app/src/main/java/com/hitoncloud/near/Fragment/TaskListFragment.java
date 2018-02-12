package com.hitoncloud.near.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hitoncloud.near.Activity.OrderActivity;
import com.hitoncloud.near.Activity.SearchTaskActivity;
import com.hitoncloud.near.R;
import com.hitoncloud.near.RecyclerItemClickListener;
import com.hitoncloud.near.TaskGet_taskDetails;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.homepage.SearchTaskdetailsAdapter;
import com.hitoncloud.near.homepage.SearchTaskdetailslist;
import com.hitoncloud.near.task.TaskListAdapter;
import com.hitoncloud.near.task.TaskListList;
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

public class TaskListFragment extends Fragment {

    View view;
    private int typeid;

    private RecyclerView rv_tasklist;
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

    SearchTaskdetailsAdapter sdadapter;//详细信息适配器

    SmartRefreshLayout srl_tasklist;

    ClassicsHeader srl_tasklist_header;
    ClassicsFooter srl_tasklist_footer;

    int counttasklist = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task_list, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();//从activity传过来的Bundle
        typeid = bundle.getInt("typeid",0);
        inittask();
    }

    private void inittask() {
        rv_tasklist= (RecyclerView)view.findViewById(R.id.rv_tasklist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        linearLayoutManager.setReverseLayout(true);
        rv_tasklist.setLayoutManager(linearLayoutManager);
        sdadapter = new SearchTaskdetailsAdapter(searchTaskdetailslists,getActivity());
        rv_tasklist.setAdapter(sdadapter);
        rv_tasklist.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv_tasklist, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SearchTaskdetailsAdapter.DetailsHolder h = (SearchTaskdetailsAdapter.DetailsHolder) rv_tasklist.findViewHolderForLayoutPosition(position);
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
                intent.putExtra("ordertype",2);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        srl_tasklist  = (SmartRefreshLayout)view.findViewById(R.id.srl_tasklist);
        srl_tasklist_header = (ClassicsHeader)view.findViewById(R.id.srl_tasklist_header);
        srl_tasklist_footer = (ClassicsFooter)view.findViewById(R.id.srl_tasklist_footer);
        srl_tasklist.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                counttasklist = 0;
                httpfortasklist();
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                Log.e("wocao","发起请求");
                httpfortasklist();
            }
        });
    }


    private void httpfortasklist() {
        if(counttasklist == 0)
        {
            //清空数据
            rv_tasklist.removeAllViews();
            searchTaskdetailslists.clear();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("username", MyApplication.username);
        params.add("start", String.valueOf(counttasklist));
        params.add("typeid", String.valueOf(typeid));
        client.post("http://123.207.46.157/near/index.php/Admin/Manager/showTask", params, new AsyncHttpResponseHandler() {
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
                        numofJSON++;
                    }
                    for (int k = 0; k < numofJSON; k++) {
                        searchTaskdetailslists.add(new SearchTaskdetailslist(taskname[k], tasktime[k], taskmoney[k], nickname[k],ordernum[k],loc[k],taskdetails[k],tasktel[k],publisher[k],"-1"));
                    }
                    sdadapter.notifyDataSetChanged();
                    srl_tasklist_footer.REFRESH_FOOTER_FINISH = "加载完成";
                    srl_tasklist.finishRefresh();
                    srl_tasklist.finishLoadmore();
                    counttasklist = counttasklist+10;
                } catch (JSONException e) {
                    srl_tasklist_footer.REFRESH_FOOTER_FINISH = "已经没有更多数据了";
                    srl_tasklist.finishRefresh();
                    srl_tasklist.finishLoadmore();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                srl_tasklist_footer.REFRESH_FOOTER_FINISH = "加载失败";
                srl_tasklist.finishLoadmore();
                srl_tasklist.finishRefresh();
            }
        });

    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rv_tasklist.removeAllViews();
        searchTaskdetailslists.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        httpfortasklist();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        httpfortasklist();
    }




}
