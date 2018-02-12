package com.hitoncloud.near.community;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;

import java.util.List;



/*主页-社区-列表适配器*/

public class CommunicitysessionAdapter extends RecyclerView.Adapter<CommunicitysessionAdapter.ViewHolder1>{

    private List<Communitysessionlist> tasks;

    private Context mContext;

    public CommunicitysessionAdapter(Context context , List<Communitysessionlist>  tasks)
    {
        this.mContext = context;
        this.tasks =  tasks;
    }

    @Override
    public CommunicitysessionAdapter.ViewHolder1 onCreateViewHolder(ViewGroup viewGroup, int i )
    {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.communicity_session_list, viewGroup, false);
        final ViewHolder1 cvHolder = new CommunicitysessionAdapter.ViewHolder1(v);
        return cvHolder;
    }

    @Override
    public void onBindViewHolder(CommunicitysessionAdapter.ViewHolder1 viewHolder, int i )
    {
        // 给ViewHolder设置元素
        Communitysessionlist p = tasks.get(i);
        viewHolder.mTextName.setText(p.name);
        viewHolder.mTextContent.setText(p.content);
        viewHolder.userid = p.targetid;
        Glide.with(mContext).load(p.url).into(viewHolder.mImageView);
    }

    @Override
    public int getItemCount()
    {
        // 返回数据总数
        return tasks == null ? 0 : tasks.size();
    }

    // 重写的自定义ViewHolder
    public static class ViewHolder1
            extends RecyclerView.ViewHolder
    {
        public TextView mTextName;
        public TextView mTextContent;
        public ImageView mImageView;
        View cmview;
        public String headimgurl;


        public String userid;

        public ViewHolder1( View v )
        {
            super(v);
            cmview = v;
            mTextName = (TextView) v.findViewById(R.id.communicityview_name);
            mImageView = (ImageView) v.findViewById(R.id.communicityview_pic);
            mTextContent = (TextView) v.findViewById(R.id.communicityview_content) ;

        }
    }
}
