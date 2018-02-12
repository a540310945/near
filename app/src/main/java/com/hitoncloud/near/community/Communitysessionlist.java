package com.hitoncloud.near.community;

import android.content.Context;



/*主页-社区-列表数据*/

public class Communitysessionlist {
    String name;//聊天对象名称
    String picName;
    String content;//聊天消息
    String url;//图片网址

    String targetid;//目标ID


    public Communitysessionlist(String name, String picName, String content,String url,String targetid){
        this.name = name;
        this.picName = picName;
        this.content = content;
        this.url = url;
        this.targetid = targetid;
    }

    public int getImageResourceId( Context context )
    {
        try
        {
            return context.getResources().getIdentifier(this.picName, "drawable", context.getPackageName());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }
}
