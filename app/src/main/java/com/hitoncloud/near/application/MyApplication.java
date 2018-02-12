package com.hitoncloud.near.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;

import com.hitoncloud.near.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMManager;
import com.tencent.TIMOfflinePushListener;
import com.tencent.TIMOfflinePushNotification;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.tencent.smtt.sdk.QbSdk;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class MyApplication extends Application {
    private static MyApplication mInstance;
    public static Context mContext;

    public static int screenWidth;//屏幕宽度
    public static int screenHeight;//屏幕高度
    public static float screenDensity;//屏幕密度
    public static int statusbarheight = 0;//状态栏高度

    public static boolean fragmenttab = true;//是否课切换fragment
    public static int statusheight = 0;//状态栏高度
    public static boolean isSysUIHide = false;//状态栏和导航栏是否隐藏

    public static String username = "";//临时保存用户名
    public static String password = "";//临时保存MD5密码
    public static String macaddress = "";//临时保存物理设备锁
    public static String nickname = "";//昵称
    public static String leftmoney = "0";//余额
    public static String illegal = "0";//是否被封号
    public static String sex = "男";//性别
    public static String headimg = "R.drawable.head4";//头像地址

    public static boolean isLogin = true; //登录状态
    public static boolean fixbug = false; //修复BUG

    public static String locschool = "还未选择学校";

    public static boolean searchtaskss=true;//搜索任务判断可否点击adapter

    public static int payfinish=0;

    public static Context appcontext;


    public static String url_userportrait = "";//用户头像网络地址
    public static boolean isupdate_userportrait = false;//是否需要更新头像



    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mInstance = this;
        initoffline();
        initScreenSize();

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean b) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + b);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };

        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);





    }



    /**
     * 备注:在一些难以通过getContext()获取环境的情况下使用
     * @return 应用本身环境
     */
    public static Context getInstance() {
        return mInstance;
    }

    /**
     * 功能:初始化当前设备屏幕宽高
     */
    private  void initScreenSize() {
        DisplayMetrics curMetrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = curMetrics.widthPixels;
        screenHeight = curMetrics.heightPixels;
        screenDensity = curMetrics.density;
    }

    private  void initoffline() {
        //离线推送消息
        if(MsfSdkUtils.isMainProcess(this)) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify){
                        //消息被设置为需要提醒
                        notification.doNotify(getApplicationContext(), R.mipmap.appicon);
                        Log.e("debug", "offline");
                    }else
                    {
                        Log.e("debug", "offlinewocao");
                    }
                }
            });
        }
    }

    @Override

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
       MultiDex.install(this);

    }
}
