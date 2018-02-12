package com.hitoncloud.near.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.Activity.AboutActivity;
import com.hitoncloud.near.Activity.BillActivity;
import com.hitoncloud.near.Activity.CouponActivity;
import com.hitoncloud.near.Activity.WalletActivity;
import com.hitoncloud.near.Activity.WebviewActivity;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.Activity.PersonalinfoActivity;
import com.hitoncloud.near.Activity.SettingActivity;
import com.hitoncloud.near.utils.GlideCircleTransform;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/*主页-我的*/
public class MineFragment extends Fragment {

    private View view;//当前view

    private RelativeLayout rl_personalinfo;//个人信息
    private RelativeLayout rl_reputation,rl_score,rl_achievement;//信誉，积分，成就
    private RelativeLayout rl_bill,rl_wallet,rl_coupon,rl_suggestion,rl_about,rl_setting;//账单，钱包，卡券，反馈，关于，设置

    private ImageView iv_portrait;//用户头像

    private TextView tv_reputation,tv_score,tv_achievement;//信誉值，积分值，成就值
    private TextView tv_leftmoney,tv_coupon;//余额，卡券数目

    private SmartRefreshLayout srl_mine;

    String leftmoney,status;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container,false);
        this.view = view;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.theme));//修改状态栏颜色
        initview();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("NewApi")
    public static void hideSystemUI(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @SuppressLint("NewApi")
    public static void showSystemUI(View view) {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    //初始化视图
    public void initview(){
        //进入个人信息界面
        rl_personalinfo = (RelativeLayout)view.findViewById(R.id.rl_mine_personalinfo);
        rl_personalinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PersonalinfoActivity.class);
                startActivity(intent);
            }
        });
        //加载用户头像
        iv_portrait = (ImageView)view.findViewById(R.id.iv_mine_portrait);
        Glide.with(getActivity()).load(MyApplication.url_userportrait).placeholder(R.drawable.default_portrait).fitCenter().transform(new GlideCircleTransform(getActivity())).crossFade().into(iv_portrait);
        //进入账单界面
        rl_bill = (RelativeLayout)view.findViewById(R.id.rl_mine_bill);
        rl_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),BillActivity.class);
                startActivity(intent);
            }
        });
        //进入钱包界面
        rl_wallet = (RelativeLayout)view.findViewById(R.id.rl_mine_wallet);
        rl_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WalletActivity.class);
                startActivity(intent);
            }
        });
        //进入卡券界面
        rl_coupon = (RelativeLayout)view.findViewById(R.id.rl_mine_coupon);
        rl_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CouponActivity.class);
                startActivity(intent);
            }
        });
        //进入问题反馈界面
        rl_suggestion = (RelativeLayout)view.findViewById(R.id.rl_mine_suggestion);
        rl_suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
//                intent.putExtra("url","http://123.207.46.157/near/index.php/Home/Index/feedback");
                intent.putExtra("url","http://116.196.93.48/imgupload/demo.html");
                intent.putExtra("title","意见反馈");
                intent.putExtra("origincode",0);
                startActivity(intent);
            }
        });
        //进入关于我们界面
        rl_about = (RelativeLayout)view.findViewById(R.id.rl_mine_about);
        rl_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }


        });
        rl_about.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url","http://116.196.93.48/game/index.html");
                intent.putExtra("title","彩蛋小游戏");
                intent.putExtra("origincode",0);
                startActivity(intent);
                return true;
            }
        });
        //进入软件设置界面
        rl_setting = (RelativeLayout)view.findViewById(R.id.rl_mine_setting);
        rl_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        srl_mine = (SmartRefreshLayout)view.findViewById(R.id.srl_mine);
        tv_leftmoney = (TextView)view.findViewById(R.id.tv_mine_leftmoney);
        srl_mine.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                httpforleftmoney();
            }
        });
        httpforleftmoney();

    }

    private void httpforleftmoney() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("username", MyApplication.username);
        client.post("http://123.207.46.157/near/index.php/Home/Index/leftmoney", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug", response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    status = object.getString("status");
                    leftmoney = object.getString("leftmoney");
                    if (status.equals("success")) {
                        tv_leftmoney.setText(leftmoney);
                    }
                    srl_mine.finishRefresh();
                } catch (JSONException e) {
                    srl_mine.finishRefresh();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                srl_mine.finishRefresh();
            }
        });

    }

}
