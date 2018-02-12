package com.hitoncloud.near.school;



public class Schoollist {

    private String name;//学校的名字
    private String pinyin;//学校名字的拼音

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPinyin() {
        return pinyin;
    }
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public Schoollist(String name, String pinyin){
        super();
        this.name = name;
        this.pinyin = pinyin;
    }

}
