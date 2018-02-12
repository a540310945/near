package com.hitoncloud.near.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.widget.Toast;

import com.hitoncloud.near.Activity.MainActivity;


/**
 *  负责人：蒋凌
 *  说明：用于各种监听服务
 */

public class ListenerService extends Service{

    public static final String TAG = "ListenerService";
    private NetworkChangeReceiver networkChangeReceiver;//网络监测
    private IntentFilter networkFilter;

    @Override
    public void onCreate() {
        super.onCreate();
        ListenNetwork();//开启网络监听

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver); //退出时摧毁网络监听器

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void ListenNetwork() {
        networkFilter = new IntentFilter();
        networkFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, networkFilter);
    }


    //网络监听器
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo !=null && networkInfo.isAvailable() )
            {
                Toast.makeText(context,"你回到了网络的怀抱",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"你失去了网络的怀抱",Toast.LENGTH_SHORT).show();
            }

        }
    }


}
