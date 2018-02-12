package com.hitoncloud.near.homepage;

/**
 * Created by 蒋凌 on 2017/8/24.
 */

public class Cardlist {
    private String title;
    private int imgtypeid;
    private String time;
    private String click;
    /**
     * 关于type的说明
     * 0:新闻卡片;1:任务助手;2:失物中心
     */
    private int type=0;

    public Cardlist(String title, int type){
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int gettype() {
        return type;
    }
}
