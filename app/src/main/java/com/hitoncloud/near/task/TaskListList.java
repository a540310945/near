package com.hitoncloud.near.task;

import android.content.Context;

/**
 * Created by 蒋凌 on 2017/10/27.
 */

public class TaskListList {

    private String name;//任务名称
    private String puttime;//任务发布时间
    private String money;//任务悬赏
    private String putter;//任务接受者id号
    private String ordernum;//任务订单号

    private String picName;//图片

    private String details;//任务详细信息
    private Long telnum;//电话号码
    private int tasktype;//任务类型
    private String location;//任务地点
    private String limittime;//任务要求时间


    public TaskListList(String name, String picName, String time, String money, String putter,String ordernum){
        this.name = name;
        this.picName = picName;
        this.putter = putter;
        this.money=money;
        this.puttime=time;
        this.ordernum = ordernum;
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

    public String getName() {
        return name;
    }

    public String getPuttime() {
        return puttime;
    }

    public String getMoney() {
        return money;
    }

    public String getPutter() {
        return putter;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public String getDetails() {
        return details;
    }

    public Long getTelnum() {
        return telnum;
    }

    public int getTasktype() {
        return tasktype;
    }

    public String getLocation() {
        return location;
    }

    public String getLimittime() {
        return limittime;
    }



}
