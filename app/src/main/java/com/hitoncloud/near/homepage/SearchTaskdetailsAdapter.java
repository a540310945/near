package com.hitoncloud.near.homepage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hitoncloud.near.R;

import java.util.List;



public class SearchTaskdetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private List<SearchTaskdetailslist> searchTaskdetailslists;//详细任务列表

    private Context mContext;//APP环境
    private OnItemClickListener mOnItemClickListener = null;

    private String item_taskname;//任务名字
    private String item_tasktime;//任务时限
    private String item_nickname;//任务发布者昵称
    private String item_taskpublisher;//任务发布者id
    private String item_taskloc;//任务地点
    private String item_taskmoney;//任务悬赏金额
    private String item_ordernum;//任务订单号
    private String item_taskdetails;//任务详情
    private String item_tasktel;//任务手机号
    private String item_isaccept;//任务是否被接受


    public SearchTaskdetailsAdapter(List<SearchTaskdetailslist> list, Context context){
        searchTaskdetailslists = list;
        mContext=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchtask_details,parent,false);
        holder =  new DetailsHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchTaskdetailslist searchTaskdetailslist = searchTaskdetailslists.get(position);
        item_taskname = searchTaskdetailslist.getName();
        item_tasktime = searchTaskdetailslist.getTime();
        item_taskloc = searchTaskdetailslist.getLoc();
        item_taskmoney = searchTaskdetailslist.getMoney();
        item_nickname = searchTaskdetailslist.getNickname();
        item_ordernum = searchTaskdetailslist.getOrdernum();
        item_taskdetails = searchTaskdetailslist.getDetails();
        item_tasktel = searchTaskdetailslist.getTel();
        item_taskpublisher = searchTaskdetailslist.getPublisher();
        item_isaccept = searchTaskdetailslist.getIsaccept();
        ((DetailsHolder)holder).tv_taskname.setText(item_taskname);
        ((DetailsHolder)holder).tv_taskmoney.setText(item_taskmoney );
        ((DetailsHolder)holder).tv_tasktime.setText(item_tasktime);
        ((DetailsHolder)holder).tv_taskloc.setText(item_taskloc);
        ((DetailsHolder)holder).tv_tasknickname.setText(item_nickname);
        ((DetailsHolder)holder).ordernum = item_ordernum;
        ((DetailsHolder)holder).details = item_taskdetails;
        ((DetailsHolder)holder).tel = item_tasktel;
        ((DetailsHolder)holder).publisher = item_taskpublisher;
        ((DetailsHolder)holder).isaccept = item_isaccept;
        ((DetailsHolder)holder).btn_grab.setVisibility(View.GONE);
        if(item_isaccept.equals("1"))
        {
            ((DetailsHolder)holder).tv_taskstatus.setText("已被接取");
        }else if(item_isaccept.equals("-1"))
        {
            ((DetailsHolder)holder).btn_grab.setVisibility(View.VISIBLE);
        }

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return searchTaskdetailslists.size();
    }

    public static class DetailsHolder extends RecyclerView.ViewHolder{

        public TextView tv_taskname;
        public TextView tv_taskmoney;
        public TextView tv_tasktime;
        public TextView tv_taskloc;
        public TextView tv_tasknickname;
        public TextView tv_taskstatus;
        public Button btn_grab;


        public String publisher;
        public String ordernum;
        public String tel;
        public String details;
        public String isaccept;

        public DetailsHolder(View itemView) {
            super(itemView);
            tv_taskname = (TextView) itemView.findViewById(R.id.tv_searchdetails_taskname);
            tv_taskmoney = (TextView) itemView.findViewById(R.id.tv_searchdetails_money) ;
            tv_tasktime = (TextView) itemView.findViewById(R.id.tv_searchdetails_time_value);
            tv_taskloc = (TextView)itemView.findViewById(R.id.tv_searchdetails_loc_value);
            tv_tasknickname = (TextView)itemView.findViewById(R.id.tv_searchdetails_nickname_value);
            tv_taskstatus = (TextView)itemView.findViewById(R.id.tv_searchdetails_status);
            btn_grab = (Button)itemView.findViewById(R.id.btn_searchedetails_grab);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }


}
