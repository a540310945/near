package com.hitoncloud.near.community;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;

import java.util.ArrayList;
import java.util.List;


public class CommunityConnecterFragment extends Fragment {

    private RecyclerView mRecyclerView;//聊天列表
    private CommunicityAdapter mCommunicityAdapter;//聊天列表适配器
    private List<communicitydata> tasks = new ArrayList<communicitydata>();//聊天列表信息

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_connecter, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  initcommunitylist();

    }
    /**
     * 初始化聊天列表
     */
   private void initcommunitylist(){
        /*RecyclerView的内容*/
        if(MyApplication.username == "101020171")
            tasks.add(new communicitydata("101020172", "head4",""));
        else if((MyApplication.username == "101020172"))
            tasks.add(new communicitydata("101020171", "head4",""));
        // 拿到RecyclerView
        mRecyclerView = (RecyclerView)getActivity().findViewById(R.id.recyclerview_communicity_connecter);
        // 设置LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        mCommunicityAdapter = new CommunicityAdapter(getActivity(), tasks);
        // 为mRecyclerView设置适配器
        mRecyclerView.setAdapter(mCommunicityAdapter);
    }





}
