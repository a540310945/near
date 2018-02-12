package com.hitoncloud.near.task;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import java.util.List;

/*任务接受列表适配器*/
public class TaskAcceptTypeAdapter extends RecyclerView.Adapter<TaskAcceptTypeAdapter.ViewHolder> implements View.OnClickListener {

    private List<TasktpyeList> tasktypelist;
    private Context mContext;
    private int tabnum;//导航选项卡数量



    private OnItemClickListener mOnItemClickListener = null;

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public TaskAcceptTypeAdapter(Context context , List<TasktpyeList>  tasktypelist,int tabnum)
    {
        this.mContext = context;
        this.tasktypelist = tasktypelist;
        this.tabnum = tabnum;
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
        TasktpyeList p = tasktypelist.get(i);
        viewHolder.mTextView.setText(p.getTasktypename());
        viewHolder.itemView.setTag(i);
        ViewGroup.LayoutParams lp = viewHolder.ll.getLayoutParams();
        if(tabnum>3)
            tabnum=3;
        lp.width = MyApplication.screenWidth/tabnum;
        viewHolder.ll.setLayoutParams(lp);
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
        return tasktypelist == null ? 0 : tasktypelist.size();
    }


    // 重写的自定义ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTextView;
        public ImageView mImageView;
        public LinearLayout ll;

        public ViewHolder( View v )
        {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.TaskGet_Type_name);
            ll = (LinearLayout)v.findViewById(R.id.ll_tasktypeitem);

            //mImageView = (ImageView) v.findViewById(R.id.cardview_redcircle);
        }

    }
}

