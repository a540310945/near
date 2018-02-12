package com.hitoncloud.near.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hitoncloud.near.R;

import java.util.List;



public class SearchTaskbriefAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<SearchTaskbrieflist> searchTaskbrieflists;//简要任务列表

    private OnItemClickListener mOnItemClickListener = null;

    public SearchTaskbriefAdapter(List<SearchTaskbrieflist> list){
        searchTaskbrieflists = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchtask_brief,parent,false);
        holder = new BriefHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SearchTaskbrieflist searchTaskbrieflist = searchTaskbrieflists.get(position);
       ((BriefHolder)holder).tv.setText(searchTaskbrieflist.getStr());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return searchTaskbrieflists.size();
    }

    public class BriefHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public BriefHolder(View itemView) {
            super(itemView);
            tv= (TextView)itemView.findViewById(R.id.tv_searchtaskbrief);
        }

        public CharSequence getBriefname()
        {
            return  tv.getText();
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
