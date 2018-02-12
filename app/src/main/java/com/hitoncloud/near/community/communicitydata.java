package com.hitoncloud.near.community;

import android.content.Context;



/*主页-社区-列表数据*/

public class communicitydata {
    String name;//聊天对象名称
    String picName;
    String content;//聊天消息


    public communicitydata(String name,String picName,String content){
        this.name = name;
        this.picName = picName;
        this.content = content;
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
