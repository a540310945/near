package com.hitoncloud.near.community;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hitoncloud.near.R;
import com.hitoncloud.near.RecyclerItemClickListener;
import com.hitoncloud.near.application.MyApplication;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;


public class CommunitySessionFragment extends Fragment {

    private RecyclerView mRecyclerView;//聊天列表
    private com.hitoncloud.near.community.CommunicitysessionAdapter mCommunicitysessionAdapter;//聊天列表适配器
    private List<Communitysessionlist> tasks = new ArrayList<Communitysessionlist>();//聊天列表信息

    String str;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_session, container, false);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        str="http://img5.imgtn.bdimg.com/it/u=1712317116,2031280619&fm=27&gp=0.jpg";
        initTencnt();

        initcommunitylist();
    }

    private void initTencnt() {
        TIMFriendshipManager.getInstance().setNickName("cat", new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e("debug11", "setNickName f");
            }

            @Override
            public void onSuccess() {
                Log.e("debug11", "setNickName succ");
            }
        });
        //获取好友资料
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>(){
            @Override
            public void onError(int code, String desc){
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e("debugtim", "getFriendList failed: " + code + " desc");
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result){
                int i=0;
                for(TIMUserProfile res : result){
                    if((i++)==0)
                        continue;
                    tasks.add(new Communitysessionlist(res.getIdentifier(), "head4","",str,"1"));
                    Log.e("debugtim", "identifier: " + res.getIdentifier() + " nickName: " + res.getNickName()
                            + " remark: " + res.getRemark());
                }
                mCommunicitysessionAdapter.notifyDataSetChanged();
            }
        });


    }

    /**
     * 初始化聊天列表
     */
   private void initcommunitylist(){
        // 拿到RecyclerView
        mRecyclerView = (RecyclerView)getActivity().findViewById(R.id.recyclerview_communicity_session);
        // 设置LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
           @Override
           public void onItemClick(View view, int position) {
               Intent intent = new Intent(getActivity(),ChatActivity.class);
               startActivity(intent);

           }

           @Override
           public void onItemLongClick(View view, int position) {

           }
       }));
           // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
       mCommunicitysessionAdapter = new CommunicitysessionAdapter(getActivity(),tasks );
        // 为mRecyclerView设置适配器
        mRecyclerView.setAdapter(mCommunicitysessionAdapter);
    }





}
