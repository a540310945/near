package com.hitoncloud.near.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.RouteInfo;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.Activity.SearchTaskActivity;
import com.hitoncloud.near.Activity.TaskTypeActivity;
import com.hitoncloud.near.Activity.WebviewActivity;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.Activity.SchoolActivity;
import com.hitoncloud.near.utils.GlideCircleTransform;
import com.hitoncloud.near.utils.StringUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sunfusheng.marqueeview.MarqueeView;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.util.ArrayList;
import java.util.List;

/**
 * 功能：定位学校，搜索任务，扫一扫，信息轮播，发接任务入口，校园专属功能入口，无限下拉资讯
 */

public class HomepageFragment extends Fragment  {

    private static String TAG = "HomepageFragment";

    private View view;

    private Toolbar toolbar;
    private RelativeLayout rl_school,rl_search,rl_news;//选择学校，搜索，学院新闻

    private RelativeLayout rl_task,rl_enjoy,rl_secondhand,rl_lost;//任务，乐享，二手，失物
    private ImageView iv_taskmaster,iv_enjoymaster,iv_secondhand,iv_lost;//任务达人，乐享达人，二手交易，失物招领图标

    private MarqueeView mv_announcement;//通知内容
    private ImageView iv_announcement;//通知图标

    private Banner banner;//轮播广告
    ArrayList<Uri> images = new ArrayList<>();//轮播图片列表
    String bannerimgurl[]= new String[10];//轮播图片网址
    String bannerhref[] = new String[10];//轮播指向目标网址
    String bannerdescription[] = new String[10];//轮播描述
    String bannercode[] = new String[10];//轮播描述

    String title[] = new String[10];//新闻标题
    TextView tv_hotnews[] = new TextView[3];//新闻热点前3

    String announcecontent[] = new String[10];//通知内容
    String annoucetitle[] = new String[10];//通知标题
    String annoucetarget[] = new String[10];//通知
    String annoucecode[] = new String[10];//webview配置代码
    List<String> info = new ArrayList<>();//公告通知列表

    List<String> nolist = new ArrayList<>();//无效字符串列表


    SmartRefreshLayout srl_homepage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        this.view=view;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();//初始化视图
    }

    private void initView(){
        toolbar = (Toolbar)view.findViewById(R.id.toolbar_homepage);
        srl_homepage = (SmartRefreshLayout)view.findViewById(R.id.srl_homepage);
        srl_homepage.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                httpforhomepageinfo();
            }
        });
        //初始化选择学校
        rl_school = (RelativeLayout)view.findViewById(R.id.rl_homepage_school);
        rl_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SchoolActivity.class);
                startActivity(intent);
            }
        });
        //初始化搜索
        rl_search = (RelativeLayout)view.findViewById(R.id.rl_homepage_search);
        rl_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchTaskActivity.class);
                startActivity(intent);
            }
        });
        //初始化任务达人
        rl_task = (RelativeLayout)view.findViewById(R.id.rl_homepage_task);
        rl_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TaskTypeActivity.class);
                startActivity(intent);
            }
        });
        iv_taskmaster = (ImageView)view.findViewById(R.id.iv_homepage_taskmaster);
        Glide.with(this).load(R.drawable.img_taskmaster).into(iv_taskmaster);
        //初始化乐享
        rl_enjoy = (RelativeLayout) view.findViewById(R.id.rl_homepage_enjoy);
        rl_enjoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"程序猿正在拼死开发中，敬请期待哦,这周内会修复",Toast.LENGTH_LONG).show();
            }
        });
        iv_enjoymaster = (ImageView)view.findViewById(R.id.iv_homepage_enjoymaster);
        Glide.with(this).load(R.drawable.img_enjoymaster).into(iv_enjoymaster);
        //初始化通知
        mv_announcement = (MarqueeView)view.findViewById(R.id.tv_homepage_announcement);
        //初始化二手交易
        rl_secondhand = (RelativeLayout)view.findViewById(R.id.rl_homepage_secondhand);
        rl_secondhand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(),"真的很抱歉，代码出现紧急问题，暂时不开放，如果需要请先在任务中的其它选项卡发布,这周内会修复",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url","http://123.207.46.157/near/index.php/Home/Secondhand/index");
                intent.putExtra("title","二手交易");
                intent.putExtra("origincode",0);
                startActivity(intent);
            }
        });
        iv_secondhand = (ImageView)view.findViewById(R.id.iv_homepage_secondhand);
        Glide.with(this).load(R.drawable.img_secondhand).into(iv_secondhand);
        //初始化失物招领
        rl_lost = (RelativeLayout)view.findViewById(R.id.rl_homepage_lost);
        rl_lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(),"真的很抱歉，代码出现紧急问题，暂时不开放，如果需要请先在任务中的其它选项卡发布",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url","http://123.207.46.157/near/index.php/Home/Lost/index");
                intent.putExtra("title","失物招领");
                intent.putExtra("origincode",0); 
                startActivity(intent);
            }
        });
        iv_lost = (ImageView)view.findViewById(R.id.iv_homepage_lost);
        Glide.with(this).load(R.drawable.img_lost).into(iv_lost);
        //初始化新闻
        rl_news = (RelativeLayout)view.findViewById(R.id.rl_homepage_news);
        rl_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url","http://123.207.46.157/near/index.php/Home/News/index");
                intent.putExtra("title","学院新闻");
                intent.putExtra("origincode",0);
                startActivity(intent);
            }
        });
        tv_hotnews[0] = (TextView)view.findViewById(R.id.tv_homepage_newshot1);
        tv_hotnews[1] = (TextView)view.findViewById(R.id.tv_homepage_newshot2);
        tv_hotnews[2] = (TextView)view.findViewById(R.id.tv_homepage_newshot3);
        //初始化图片轮播
        banner = (Banner)view.findViewById(R.id.banner_homepage);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(getActivity()).load(path).centerCrop().into(imageView);

            }
        });
        banner.setDelayTime(2000);//设置轮播时间
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url",bannerhref[position]);
                intent.putExtra("title",bannerdescription[position]);
                intent.putExtra("origincode",bannercode[position]);
                startActivity(intent);
            }
        });
        httpforhomepageinfo();//获取首页所有信息
    }

    private void httpforhomepageinfo()
    {
        //发起网络请求获取广告
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("info","info");//修改：以后记得改名字
        client.post("http://123.207.46.157/near/index.php/Home/Index/homepage_info", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug1", response);
                response = StringUtils.JSONTokener(response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    mv_announcement.startWithList(nolist, R.anim.anim_bottom_in, R.anim.anim_top_out);
                    info.clear();
                    images.clear();
                    Log.e("debug1", "123");
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject getJsonObj = jsonArray.getJSONObject(k);

                        if(k<3)
                        {
                            bannerimgurl[k] = getJsonObj.getString("img_address");
                            bannerdescription[k]=getJsonObj.getString("img_desc");
                            bannerhref[k]=getJsonObj.getString("img_target_address");
                            bannercode[k]=getJsonObj.getString("code");
                            images.add(Uri.parse((String) bannerimgurl[k])) ;
                        }else if(k>=3&&k<=5)
                        {
                            title[k-3] = getJsonObj.getString("title");
                        }else if(k>=6)
                        {
                            annoucetitle[k-6] = getJsonObj.getString("title");
                            annoucetarget[k-6] = getJsonObj.getString("target_address");
                            announcecontent[k-6] = getJsonObj.getString("content");
                            annoucecode[k-6] = getJsonObj.getString("code");
                        }
                    }
                    Log.e("debug1", "124");
                    for(int j=0;j<3;j++)
                    {
                        tv_hotnews[j].setText(title[j]);
                    }

                    Log.e("debug1", "125");
                    for(int j=0;j<3;j++)
                    {
                        info.add(announcecontent[j]);
                    }

                    mv_announcement.startWithList(info, R.anim.anim_bottom_in, R.anim.anim_top_out);
                    iv_announcement = (ImageView)view.findViewById(R.id.iv_homepage_announcement);
                    Glide.with(HomepageFragment.this).load(R.drawable.img_announcement).into(iv_announcement);
                    mv_announcement.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, TextView textView) {
                            Intent intent = new Intent(getActivity(), WebviewActivity.class);
                            intent.putExtra("url",annoucetarget[position]);
                            intent.putExtra("title",annoucetitle[position]);
                            intent.putExtra("origincode",annoucecode[position]);
                            startActivity(intent);
                        }
                    });
                    Log.e("debug1", "127");
                    banner.setImages(images);//设置图片源
                    banner.start();
                    Log.e("debug1", "128");
                    srl_homepage.finishRefresh();
                } catch (JSONException e) {
                    Log.e("debug1", "w哦操操操"+e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e(TAG,"获取首页信息失败");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        banner.startAutoPlay();
        mv_announcement.startFlipping();
    }

    @Override
    public void onPause() {
        super.onPause();
        banner.stopAutoPlay();
        mv_announcement.stopFlipping();
    }


}
