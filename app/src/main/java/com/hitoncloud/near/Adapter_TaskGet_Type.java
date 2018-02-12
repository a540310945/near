package com.hitoncloud.near;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/*任务接受列表适配器*/
public class Adapter_TaskGet_Type extends RecyclerView.Adapter<Adapter_TaskGet_Type.ViewHolder> implements View.OnClickListener {

    private List<List_taskget_type> tasks;
    private Context mContext;



    private OnItemClickListener mOnItemClickListener = null;

    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }


    public Adapter_TaskGet_Type(Context context , List<List_taskget_type>  tasks)
    {
        this.mContext = context;
        this.tasks = tasks;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int viewTpye )
    {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tasktypeitem,viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int i )
    {
        // 给ViewHolder设置元素
        List_taskget_type p = tasks.get(i);
        viewHolder.mTextView.setText(p.getTasktypename());

        viewHolder.itemView.setTag(i);
       // Drawable drawable = ContextCompat.getDrawable(mContext,p.getImageResourceId(mContext));
       // viewHolder.mImageView.setImageDrawable(drawable);
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
        public ImageView mImageView;

        public ViewHolder( View v )
        {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.TaskGet_Type_name);
            //mImageView = (ImageView) v.findViewById(R.id.cardview_redcircle);
        }

    }
}

