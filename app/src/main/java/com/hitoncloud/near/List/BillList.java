package com.hitoncloud.near.List;

import android.content.Context;

import org.w3c.dom.ProcessingInstruction;

/**
 * Created by 蒋凌 on 2017/12/2.
 */

public class BillList {

    private String name;//账单名称
    private String money;//账单支出/收入
    private String Date;//日期
    private String status;//状态
    private String ordernum;//订单号
    private String publisher;//发布者
    private String accepter;//接受者

    public BillList(String name,String money,String Date,String status,String ordernum,String publisher,String accepter)
    {
        this.name = name;
        this.money = money;
        this.Date = Date;
        this.status = status;
        this.ordernum = ordernum;
        this.publisher = publisher;
        this.accepter = accepter;
    }

    public String getName() {
        return name;
    }

    public String getMoney() {
        return money;
    }

    public String getStatus() {
        return status;
    }

    public String getDate()
    {
        return Date;
    }

    public String getOrdernum(){
        return ordernum;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getAccepter() {
        return accepter;
    }

}
