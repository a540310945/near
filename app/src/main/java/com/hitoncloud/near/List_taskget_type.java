package com.hitoncloud.near;

import android.content.Context;


/*任务接受数据*/
public class List_taskget_type {
    private String tasktypename;//任务类型名称

    private String picName;//图片





    public List_taskget_type(String name){
        this.tasktypename= name;

    }

    //通过图片名获取图片id
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

    public String getTasktypename() {
        return tasktypename;
    }
}
