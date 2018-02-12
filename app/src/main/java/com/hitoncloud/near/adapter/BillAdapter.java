package com.hitoncloud.near.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hitoncloud.near.Activity.BillActivity;
import com.hitoncloud.near.List.BillList;
import com.hitoncloud.near.R;
import com.hitoncloud.near.homepage.SearchTaskdetailsAdapter;
import com.hitoncloud.near.homepage.SearchTaskdetailslist;

import java.util.List;

/**
 * Created by 蒋凌 on 2017/12/2.
 */

public class BillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BillList> billLists;//详细任务列表
    private Context mContext;

    private String name;//账单名称
    private String money;//账单支出/收入
    private String Date;//日期
    private String status;//状态
    private String ordernum;//订单号
    private String publisher;//发布者
    private String accepter;//接受者

    public BillAdapter(List<BillList> list, Context context){
        billLists = list;
        mContext=context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill,parent,false);
        holder =  new BillAdapter.BillsHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BillList billList = billLists.get(position);
        name = billList.getName();
        Date = billList.getDate();
        status = billList.getStatus();
        money = billList.getMoney();
        ordernum = billList.getOrdernum();
        publisher = billList.getPublisher();
        accepter = billList.getAccepter();



        ((BillsHolder)holder).tv_taskname.setText(name);
        ((BillsHolder)holder).tv_taskmoney.setText(money );
        ((BillsHolder)holder).tv_taskdate.setText(Date);
        ((BillsHolder)holder).tv_taskstatus.setText(status);
        ((BillsHolder)holder).ordernum = ordernum;
        ((BillsHolder)holder).publisher = publisher;
        ((BillsHolder)holder).accepter = accepter;
    }

    @Override
    public int getItemCount() {
        return billLists.size();
    }

    public static class BillsHolder extends RecyclerView.ViewHolder{


        public TextView tv_taskname;
        public TextView tv_taskmoney;
        public TextView tv_taskdate;
        public TextView tv_taskstatus;

        public String ordernum;
        public String publisher;
        public String accepter;

        public BillsHolder(View itemView) {
            super(itemView);
            tv_taskname = (TextView)itemView.findViewById(R.id.tv_billlist_taskname);
            tv_taskmoney = (TextView)itemView.findViewById(R.id.tv_billlist_taskmoney);
            tv_taskdate = (TextView)itemView.findViewById(R.id.tv_billlist_taskdate);
            tv_taskstatus = (TextView)itemView.findViewById(R.id.tv_billlist_taskstatus);
        }
    }

}
