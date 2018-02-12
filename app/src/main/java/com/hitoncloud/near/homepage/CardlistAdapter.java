package com.hitoncloud.near.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hitoncloud.near.R;

import java.util.List;

public class CardlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Cardlist> mCardList;
    private int type;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        if(viewType==0)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carditem_news,parent,false);
            holder = new NewsHolder(view);
        }else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carditem_taskhelper,parent,false);
            holder = new TaskHelperHolder(view);
        }


        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Cardlist cardlist = mCardList.get(position);
        if(holder instanceof NewsHolder)
        {
            //((NewsHolder)holder).title.setText(cardlist.getTitle());
        }
        else if(holder instanceof TaskHelperHolder){
            //((TaskHelperHolder)holder).title.setText(cardlist.getTitle());
        }

    }


    @Override
    public int getItemViewType(int position) {
        return mCardList.get(position).gettype() ;
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    //新闻
    public class NewsHolder extends RecyclerView.ViewHolder {
        TextView title;
        public NewsHolder(View itemView) {
            super(itemView);
          //  title = (TextView)itemView.findViewById(R.id.news);
        }
    }

    //任务助手
    public class TaskHelperHolder extends RecyclerView.ViewHolder {
        TextView title;
        public TaskHelperHolder(View itemView) {
            super(itemView);
        }
    }

    public CardlistAdapter(List<Cardlist> list, int type){
        mCardList = list;
        this.type = type;
    }


}
