package com.hitoncloud.near.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hitoncloud.near.Fragment.HomepageFragment;
import com.hitoncloud.near.R;
import com.hitoncloud.near.Service.ListenerService;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.Fragment.CommunityFragment;
import com.hitoncloud.near.Fragment.MineFragment;
import com.hitoncloud.near.Fragment.TaskFragment;
import com.hitoncloud.near.utils.ActivityCollector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendAllowType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.tencent.TIMUser;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Timer;
import java.util.TimerTask;


/**
 * 功能：APP主页（包含了4个功能页），切换功能页，判断本地是否有账号密码信息文件
 */
public class MainActivity extends FragmentActivity implements OnClickListener {

    //底部4个选项卡
    FrameLayout frameLayout[] = new FrameLayout[4];
    ImageView img[] = new ImageView[4];
    TextView txtview[] = new TextView[4];

    //标识所有fragment
    public static final String fragment1Tag = "fragment1";
    public static final String fragment2Tag = "fragment2";
    public static final String fragment3Tag = "fragment3";
    public static final String fragment4Tag = "fragment4";
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment4;
    Fragment fragment[] = new Fragment[4];

    //标识当前fragment
    public int mark = 1;

    //定义底部选项卡数量
    private int snum = 4;

    //腾讯SDK
    String identify;
    public static final int ACCOUNT_TYPE = 12593;
    public static final int SDK_APPID = 1400039026;
    String sig;

    private boolean mBackKeyPressed = false;//连续按下2次返回退出程序按钮

    Intent listenerservice;//网络监听服务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.appcontext = getApplicationContext();
        initAppSettings();//初始化系统设置
        initAccount();//初始化账号信息
        initTencent();//初始化腾讯SDK
        initBottomtab();//初始化底部选项卡
        chickFragment(mark);
        askforpermission();
        initService();//初始化各种服务
        askforupdate();

    }

    private void askforupdate() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("version",getVersion());
        client.post("http://123.207.46.157/near/index.php/Home/App/update", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug", response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    String isupdate = object.getString("update");
                    String title = object.getString("title");
                    String content = object.getString("content");
                    final String url = object.getString("url");
                    if (isupdate.equals("1")) {
                        new MaterialDialog.Builder(MainActivity.this)
                                .title(title)
                                .content(content)
                                .positiveText("更新")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        // TODO
                                        Toast.makeText(MainActivity.this, "有更新", Toast.LENGTH_SHORT).show();
                                        String uristr = url ;
                                        Uri uri = Uri.parse(uristr);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                })
                                .negativeText("不更新").
                                onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        // TODO
                                        Toast.makeText(MainActivity.this, "不更新呢", Toast.LENGTH_SHORT).show();

                                    }
                                })

                                .show();



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(MainActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void initAppSettings() {
        ActivityCollector.addActivity(MainActivity.this);
        if (MyApplication.statusbarheight == 0)
            getStatusBarHeight(getApplicationContext());
    }

    //得到状态栏高度
    private int getStatusBarHeight(Context context) {
        // 反射手机运行的类：android.R.dimen.status_bar_height.
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            String heightStr = clazz.getField("status_bar_height").get(object).toString();
            int height = Integer.parseInt(heightStr);
            //dp-->px
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyApplication.statusbarheight = statusHeight;
        return statusHeight;
    }

    //请求所有所需权限
    private void askforpermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            showContacts();
        } else {
            //do nothing
        }
    }

    private void showContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "没有权限,请手动开启电话权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.GET_ACCOUNTS, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET, Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BROADCAST_STICKY, Manifest.permission.WRITE_SETTINGS}, 19787);
        } else {
            //do nothing
        }
    }

    //权限请求回调结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 19787:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(getApplicationContext(), "获取权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public void initTencent() {
        TIMManager.getInstance().init(getApplicationContext());
        identify = MyApplication.username;
        //获取usersig
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("username", identify);
        client.post("http://123.207.46.157/near/index.php/Admin/Txim/usersig", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug", response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    sig = object.getString("usersig");
                    String usersig = sig;
                    TIMUser user = new TIMUser();
                    user.setIdentifier(identify);
                    user.setAppIdAt3rd(String.valueOf(SDK_APPID));
                    user.setAccountType(String.valueOf(ACCOUNT_TYPE));
                    TIMManager.getInstance().login(SDK_APPID, user, usersig, new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e("debug", "imsdk login fail");
                            Log.e("debug", s);
                        }

                        @Override
                        public void onSuccess() {
                            Log.e("debug", "imsdk login succeed");
                            TIMFriendshipManager.getInstance().setAllowType(TIMFriendAllowType.TIM_FRIEND_ALLOW_ANY, new TIMCallBack(){
                                @Override
                                public void onError(int code, String desc){
                                    //错误码code和错误描述desc，可用于定位请求失败原因
                                    //错误码code列表请参见错误码表
                                    Log.e("AA", "setAllowType failed: " + code + " desc");
                                }

                                @Override
                                public void onSuccess(){
                                    Log.e("AA", "setAllowType succ");
                                }
                            });

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(MainActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void initAccount() {
        //读取本地账号密码信息
        SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
        MyApplication.username = pref.getString("username", "");
        MyApplication.password = pref.getString("password", "");
    }


    /**
     * 功能：点击底部选项卡切换显示内容
     *
     * @param arg0 点击的元素
     */
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.main_bottomtab1_fl:
                chickFragment(1);
                break;
            case R.id.main_bottomtab2_fl:
                chickFragment(2);
                break;
            case R.id.main_bottomtab3_fl:
                chickFragment(3);
                break;
            case R.id.main_bottomtab4_fl:
                chickFragment(4);
                break;
        }
    }

    /**
     * 功能：初始化底部选项卡
     */
    private void initBottomtab() {
        //底部4个选项的图片及文字资源：首页，社区，任务，我的
        img[0] = (ImageView) findViewById(R.id.image1);
        img[1] = (ImageView) findViewById(R.id.image2);
        img[2] = (ImageView) findViewById(R.id.image3);
        img[3] = (ImageView) findViewById(R.id.image4);
        txtview[0] = (TextView) findViewById(R.id.tv1);
        txtview[1] = (TextView) findViewById(R.id.tv2);
        txtview[2] = (TextView) findViewById(R.id.tv3);
        txtview[3] = (TextView) findViewById(R.id.tv4);
        //底部4个选项的点击区域：首页，社区，任务，我的
        frameLayout[0] = (FrameLayout) findViewById(R.id.main_bottomtab1_fl);
        frameLayout[1] = (FrameLayout) findViewById(R.id.main_bottomtab2_fl);
        frameLayout[2] = (FrameLayout) findViewById(R.id.main_bottomtab3_fl);
        frameLayout[3] = (FrameLayout) findViewById(R.id.main_bottomtab4_fl);
        for (int i = 0; i < frameLayout.length; i++) {
            frameLayout[i].setOnClickListener(this);
        }
    }

    /**
     * 功能：底部切换选项卡
     *
     * @param switchnumber 底部选项卡编号0,1,2,3
     */
    public void switchimg(int switchnumber) {
        //复原所有图片及文字为未选择状态
        img[0].setImageResource(R.drawable.host_off);
        img[1].setImageResource(R.drawable.task_off);
        img[2].setImageResource(R.drawable.community_off);
        img[3].setImageResource(R.drawable.mine_off);
        for (int i = 0; i < txtview.length; i++) {
            txtview[i].setTextColor(Color.parseColor("#818181"));
        }
        //更改选定的选项卡文字及图片样式
        txtview[switchnumber - 1].setTextColor(ContextCompat.getColor(this, R.color.theme));
        switch (switchnumber) {
            case 1:
                img[0].setImageResource(R.drawable.host_on);
                break;
            case 2:
                img[1].setImageResource(R.drawable.task_on);
                break;
            case 3:
                img[2].setImageResource(R.drawable.community_on);
                break;
            case 4:
                img[3].setImageResource(R.drawable.mine_on);
                break;
            default:
                Toast.makeText(this, "错误代码：001；请通过‘我的’界面反馈给开发者，谢谢您的配合", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * 功能：根据底部选项卡切换Fragment
     *
     * @param num 试图切换的选项卡
     */
    public void chickFragment(int num) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (num > mark) {
            ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
        } else if (num < mark) {
            ft.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right);
        }
        fragment1 = fm.findFragmentByTag(fragment1Tag);
        fragment2 = fm.findFragmentByTag(fragment2Tag);
        fragment3 = fm.findFragmentByTag(fragment3Tag);
        fragment4 = fm.findFragmentByTag(fragment4Tag);
        if (fragment1 != null) {
            ft.hide(fragment1);
        }
        if (fragment2 != null) {
            ft.hide(fragment2);
        }
        if (fragment3 != null) {
            ft.hide(fragment3);
        }
        if (fragment4 != null) {
            ft.hide(fragment4);
        }
        switch (num) {
            case 1:
                if (fragment1 == null) {
                    fragment1 = new HomepageFragment();
                    ft.add(R.id.fl_main, fragment1, fragment1Tag);
                } else {
                    ft.show(fragment1);
                }
                break;
            case 2:
                if (fragment2 == null) {
                    fragment2 = new TaskFragment();
                    ft.add(R.id.fl_main, fragment2, fragment2Tag);
                } else {
                    ft.show(fragment2);
                }
                break;
            case 3:
                if (fragment3 == null) {
                    fragment3 = new CommunityFragment();
                    ft.add(R.id.fl_main, fragment3, fragment3Tag);
                } else {
                    ft.show(fragment3);
                }
                break;
            case 4:
                if (fragment4 == null) {
                    fragment4 = new MineFragment();
                    ft.add(R.id.fl_main, fragment4, fragment4Tag);
                } else {
                    ft.show(fragment4);
                }
                break;
            default:
                break;
        }
        mark = num;
        switchimg(num);
        ft.commit();
    }

    //启动服务
    private void initService() {
        //启动监听服务
        Intent listenerservice = new Intent(this, ListenerService.class);
        startService(listenerservice);
    }

    /**
     * 功能：防止后台运行导致界面消失
     *
     * @param savedInstanceState 保存的状态
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        fragment[0] = fm.findFragmentByTag(fragment1Tag);
        fragment[1] = fm.findFragmentByTag(fragment2Tag);
        fragment[2] = fm.findFragmentByTag(fragment3Tag);
        fragment[3] = fm.findFragmentByTag(fragment4Tag);
        for (int i = 0; i < 4; i++) {
            FragmentTransaction ft = fm.beginTransaction();
            if (fragment[i] != null) {
                ft.hide(fragment[i]);
            }
            ft.commit();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
//      this.detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    //连续按2次返回则退出程序
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!mBackKeyPressed) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mBackKeyPressed = true;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mBackKeyPressed = false;
                    }
                }, 2000);
                return true;
            } else {
                MainActivity.this.finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    //请求用户头像
    private void httpforportrait() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("username", MyApplication.username);
        client.post("http://123.207.46.157/near/index.php/Home/Index/get_img", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject getJsonObj = jsonArray.getJSONObject(k);
                        MyApplication.url_userportrait = getJsonObj.getString("url_portrait");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            Log.e("TEST",version);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

//
//    @Override
//    public boolean onDown(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent e) {
//
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        return false;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent e) {
//
//    }

//    /**
//     * 功能：手势滑动切换界面
//     *
//     * @param e1        手势起点
//     * @param e2        手势终点
//     * @param velocityX X轴移动速度
//     * @param velocityY Y轴移动速度
//     * @return 是否滑动
//     */
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        if (abs(e1.getY() - e2.getY()) > DISTANT+100)
//            return false;
//        if (e1.getX() > e2.getX() + DISTANT && mark != 4 && MyApplication.fragmenttab) {
//            isGesture = true;
//            chickFragment(mark + 1);
//        } else if (e2.getX() > e1.getX() + DISTANT && mark != 1) {
//            isGesture = true;
//            chickFragment(mark - 1);
//        }
//        return false;
//    }
//
//    public void initGesture(){
//        detector = new GestureDetector(this, this);
//    }
}
