package com.hitoncloud.near;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import static com.hitoncloud.near.R.layout.cardview;

/*任务接受列表适配器*/
public class Adapter_TaskGet extends RecyclerView.Adapter<Adapter_TaskGet.ViewHolder> {

    private List<List_taskget> tasks;
    private Context mContext;


    private String item_taskname;
    private String item_tasktime;
    private String item_taskputter;
    private String item_taskid;
    private String item_taskmoney;
    private String item_ordernum;




    public Adapter_TaskGet(Context context , List<List_taskget>  tasks)
    {
        this.mContext = context;
        this.tasks = tasks;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int viewTpye )
    {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(cardview, viewGroup, false);
        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int i )
    {
        // 给ViewHolder设置元素
        List_taskget p = tasks.get(i);
        item_taskname = p.getName();
        item_tasktime = p.getPuttime();
        item_taskmoney = p.getMoney();
        item_taskputter = p.getPutter();
        item_ordernum = p.getOrdernum();
        viewHolder.mTextView.setText(item_taskname);
        viewHolder.mCardTime.setText(item_tasktime);
        viewHolder.mCardPutter.setText(item_taskputter);
        viewHolder.mCardMoney.setText(item_taskmoney );
        Drawable drawable = ContextCompat.getDrawable(mContext,p.getImageResourceId(mContext));
        viewHolder.mImageView.setImageDrawable(drawable);
        viewHolder.ordernum = item_ordernum;
    }

    @Override
    public int getItemCount()
    {
        // 返回数据总数
        return tasks == null ? 0 : tasks.size();
    }

    // 重写的自定义ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTextView;
        public TextView mCardTime;
        public TextView mCardPutter;
        public TextView mCardMoney;
        public ImageView mImageView;

        public String ordernum;

        RelativeLayout cdr;

        public ViewHolder( View v )
        {
            super(v);
            cdr = (RelativeLayout)v.findViewById(R.id.cardview_relative);
            mTextView = (TextView) v.findViewById(R.id.cardview_name);
            mImageView = (ImageView) v.findViewById(R.id.cardview_redcircle);
            mCardMoney = (TextView) v.findViewById(R.id.cardview_money) ;
            mCardTime = (TextView) v.findViewById(R.id.cardview_time);
            mCardPutter = (TextView)v.findViewById(R.id.cardview_putter);


        }

        public CharSequence getTaskname()
        {
            return  mTextView.getText();
        }

        public CharSequence getMoney()
        {
            return  mCardMoney.getText();
        }

        public CharSequence getDate()
        {
            return  mCardTime.getText();
        }

        public CharSequence getPutter()
        {
            return  mCardPutter.getText();
        }

        public CharSequence getordernum()
        {
            return ordernum;
        }
    }
}

