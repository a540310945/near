package com.hitoncloud.near.task;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hitoncloud.near.R;
import com.hitoncloud.near.homepage.SearchTaskdetailslist;

import java.util.List;


public class TaskpostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private List<Taskpostlist> Taskpostlists;//详细任务列表

    private Context mContext;//APP环境
    private OnItemClickListener mOnItemClickListener = null;

    private String item_taskname;//任务名字
    private String item_tasktime;//任务时限
    private String item_taskputter;//任务发布者
    private String item_taskid;//任务id
    private String item_taskmoney;//任务悬赏金额
    private String item_ordernum;//任务订单号

    public TaskpostAdapter(List<Taskpostlist> list, Context context){
        Taskpostlists = list;
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
        Taskpostlist Taskpostlist = Taskpostlists.get(position);
        item_taskname = Taskpostlist.getName();
        item_tasktime = Taskpostlist.getPuttime();
        item_taskmoney = Taskpostlist.getMoney();
        item_taskputter = Taskpostlist.getPutter();
        item_ordernum = Taskpostlist.getOrdernum();
        ((DetailsHolder)holder).mCardName.setText(item_taskname);
        ((DetailsHolder)holder).mCardLimitDate.setText(item_tasktime);
        ((DetailsHolder)holder).mCardPublisher.setText(item_taskputter);
        ((DetailsHolder)holder).mCardMoney.setText(item_taskmoney );
        ((DetailsHolder)holder).ordernum = item_ordernum;
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return Taskpostlists.size();
    }

    static class DetailsHolder extends RecyclerView.ViewHolder{

        public TextView mCardName;
        public TextView mCardLimitDate;
        public TextView mCardPublisher;
        public TextView mCardMoney;
        public String ordernum;

        public DetailsHolder(View itemView) {
            super(itemView);
            mCardName = (TextView) itemView.findViewById(R.id.tv_searchdetails_taskname);
            mCardMoney = (TextView) itemView.findViewById(R.id.tv_searchdetails_money) ;
            mCardLimitDate = (TextView) itemView.findViewById(R.id.tv_searchdetails_time_value);
            mCardPublisher = (TextView)itemView.findViewById(R.id.tv_searchdetails_nickname_value);
        }

        public CharSequence getTaskname()
        {
            return  mCardName.getText();
        }

        public CharSequence getTaskMoney()
        {
            return  mCardMoney.getText();
        }

        public CharSequence getTaskLimitDate()
        {
            return  mCardLimitDate.getText();
        }

        public CharSequence getTaskPublisher()
        {
            return  mCardPublisher.getText();
        }

        public CharSequence getordernum()
        {
            return ordernum;
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
        void onItemClick(View view, int position);
    }


}
