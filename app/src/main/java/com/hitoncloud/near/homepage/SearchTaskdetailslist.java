package com.hitoncloud.near.homepage;

import android.content.Context;

/**
 * Created by 蒋凌 on 2017/9/13.
 */

public class SearchTaskdetailslist {
    private String name;//任务名称
    private String money;//任务悬赏
    private String time;//任务要求时间
    private String publisher;//任务发布者id号
    private String nickname;//任务发布者昵称
    private String ordernum;//任务订单号
    private String loc;//任务地点
    private String details;//任务详细信息
    private String tel;//电话号码

    private String isaccept;//任务状态



    private int tasktype;//任务类型

    public SearchTaskdetailslist(String name,String time, String money, String nickname, String ordernum,String loc,String details,String tel,String publisher,String isaccept)
    {
        this.name = name;
        this.nickname = nickname;
        this.money=money;
        this.time=time;
        this.loc = loc;
        this.ordernum = ordernum;
        this.details = details;
        this.tel = tel;
        this.publisher = publisher;
        this.isaccept = isaccept;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTime() {
        return time;
    }

    public String getMoney() {
        return money;
    }

    public String getLoc() {
        return loc;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public String getDetails() {
        return details;
    }

    public String getTel() {
        return tel;
    }

    public int getTasktype() {
        return tasktype;
    }

    public String getIsaccept() {
        return isaccept;
    }


}
