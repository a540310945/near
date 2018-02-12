package com.hitoncloud.near.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.IInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.hitoncloud.near.Activity.OrderActivity;
import com.hitoncloud.near.R;
import com.hitoncloud.near.RecyclerItemClickListener;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.community.ChatActivity;
import com.hitoncloud.near.community.CommunicitysessionAdapter;
import com.hitoncloud.near.community.CommunityConnecterFragment;
import com.hitoncloud.near.community.CommunitySessionFragment;
import com.hitoncloud.near.community.Communitysessionlist;
import com.hitoncloud.near.homepage.SearchTaskdetailsAdapter;
import com.hitoncloud.near.homepage.SearchTaskdetailslist;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * 社区
 */
public class CommunityFragment extends Fragment {

    private View view;//当前VIEW
    private RelativeLayout rl_none;

    private RecyclerView rv_community;//聊天列表
    private List<Communitysessionlist> tasks = new ArrayList<Communitysessionlist>();//聊天列表信息
    private com.hitoncloud.near.community.CommunicitysessionAdapter mCommunicitysessionAdapter;//聊天列表适配器

    int numofJSON = 0;
    String nickname[] = new String[99];//昵称
    String nickname2[] = new String[99];//昵称
    String userid1[] = new String[99];//聊天对方
    String userid2[] = new String[99];
    String taskname[] = new String[99];
    String headimg[] = new String[99];


    SmartRefreshLayout srl_community;

    String str;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_community, container, false);
        this.view = view;
        return view;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        str="http://123.207.46.157/near/Uploads/default.png";
        //initTencnt();

        initView();
        httpforcommunity();
    }


    private void httpforcommunity() {
            rv_community.removeAllViews();
            tasks.clear();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.add("username", MyApplication.username);
            client.post("http://123.207.46.157/near/index.php/Home/Index/chat", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String response = new String(bytes);
                    Log.e("debug111", response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        numofJSON = 0;
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject getJsonObj = jsonArray.getJSONObject(k);
                            nickname[k] = getJsonObj.getString("nickname");
                            nickname2[k] = getJsonObj.getString("nickname2");
                            userid1[k] = getJsonObj.getString("userid1");
                            userid2[k] = getJsonObj.getString("userid2");
                            taskname[k] = getJsonObj.getString("taskname");
                            numofJSON++;
                        }
                        for (int k = 0; k < numofJSON; k++) {
                            if(!MyApplication.username.equals(userid1[k]))
                            {
                                tasks.add(new Communitysessionlist(nickname[k], "head4", "任务名:"+taskname[k], str,userid1[k]));
                                Log.e("Community1",nickname[k]+taskname[k]+userid1[k]);
                            } else if(!MyApplication.username.equals(userid2[k])) {
                                tasks.add(new Communitysessionlist(nickname2[k], "head4", "任务名:" + taskname[k], str, userid2[k]));
                                Log.e("Community2",nickname2[k]+taskname[k]+userid2[k]);
                            }
                        }
                        mCommunicitysessionAdapter.notifyDataSetChanged();
                        srl_community.finishRefresh();
                    } catch (JSONException e) {
                        srl_community.finishRefresh();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                   srl_community.finishRefresh();
                }

            });
        }





    private void initView() {
        rl_none = (RelativeLayout)view.findViewById(R.id.rl_community_none);
        rl_none.setVisibility(View.GONE);
        rv_community = (RecyclerView) view.findViewById(R.id.rv_community);
        rv_community.setVisibility(View.VISIBLE);
        // 设置LinearLayoutManager
        rv_community.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设置ItemAnimator
        rv_community.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        rv_community.setHasFixedSize(true);
        // 初始化自定义的适配器
        mCommunicitysessionAdapter = new CommunicitysessionAdapter(getActivity(), tasks);
        // 为rv_community设置适配器
        rv_community.setAdapter(mCommunicitysessionAdapter);
        mCommunicitysessionAdapter.notifyDataSetChanged();
        rv_community.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv_community, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CommunicitysessionAdapter.ViewHolder1 h = (CommunicitysessionAdapter.ViewHolder1) rv_community.findViewHolderForLayoutPosition(position);
                String targetid = h.userid;
                String nickname = h.mTextName.getText().toString();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("taskpublisher", targetid);
                intent.putExtra("nickname",nickname);

                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        srl_community = (SmartRefreshLayout)view.findViewById(R.id.srl_community);
        srl_community.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                httpforcommunity();
            }
        });
    }
}
