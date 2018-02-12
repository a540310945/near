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
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*首页-任务-已接取*/
public class ReleaseTaskFragment extends Fragment {

    private RecyclerView rv_releasetask;
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
    String isaccept[] = new String[99];//是否被接受

    SearchTaskdetailsAdapter sdadapter;//详细信息适配器

    View view;

    SmartRefreshLayout srl_releasetask;

    ClassicsHeader srl_release_header;
    ClassicsFooter srl_release_footer;

    int countrelease = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_releasetask, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inittask();

    }

    private void inittask() {
        rv_releasetask = (RecyclerView)view.findViewById(R.id.rv_releasetask);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        linearLayoutManager.setReverseLayout(true);
        rv_releasetask.setLayoutManager(linearLayoutManager);
        sdadapter = new SearchTaskdetailsAdapter(searchTaskdetailslists,getActivity());
        rv_releasetask.setAdapter(sdadapter);
        rv_releasetask.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv_releasetask, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SearchTaskdetailsAdapter.DetailsHolder h = (SearchTaskdetailsAdapter.DetailsHolder) rv_releasetask.findViewHolderForLayoutPosition(position);
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
                intent.putExtra("ordertype",3);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        srl_releasetask  = (SmartRefreshLayout)view.findViewById(R.id.srl_releasetask);
        srl_release_header = (ClassicsHeader)view.findViewById(R.id.srl_releasetask_header);
        srl_release_footer = (ClassicsFooter)view.findViewById(R.id.srl_releasetask_footer);
        srl_releasetask.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                countrelease = 0;
                httpforreleasetask();
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                Log.e("wocao","发起请求");
                //httpforreleasetask();
            }
        });
        srl_releasetask.setEnableLoadmore(false);
       // httpforreleasetask();
    }

    @Override
    public void onResume() {
        super.onResume();
        countrelease = 0;
        httpforreleasetask();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        countrelease = 0;
        httpforreleasetask();
    }


    private void httpforreleasetask() {
        if(countrelease == 0)
        {
            //清空数据
            rv_releasetask.removeAllViews();
            searchTaskdetailslists.clear();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("username", MyApplication.username);
        client.post("http://123.207.46.157/near/index.php/Admin/Manager/showTasking", params, new AsyncHttpResponseHandler() {
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
                    srl_release_footer.REFRESH_FOOTER_FINISH = "加载完成";
                    srl_releasetask.finishRefresh();
                    srl_releasetask.finishLoadmore();
                } catch (JSONException e) {
                    srl_release_footer.REFRESH_FOOTER_FINISH = "已经没有更多数据了";
                    srl_releasetask.finishRefresh();
                    srl_releasetask.finishLoadmore();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                srl_release_footer.REFRESH_FOOTER_FINISH = "加载失败";
                srl_releasetask.finishLoadmore();
                srl_releasetask.finishRefresh();
            }
        });

    }
}
