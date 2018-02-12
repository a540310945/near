package com.hitoncloud.near.Activity;



import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.apache.http.HttpStatus;

import static android.view.KeyEvent.KEYCODE_BACK;



public class WebviewActivity extends AppCompatActivity {

    Toolbar toolbar;
    WebView webView;

    String myurl;//传入的网址
    String title;//网页新闻

    /*
    * 0：不使用缓存
    * 1：使用缓存
    */
    int origincode;//通过特定值处理特定问题


    boolean loadError = false;//判断加载网页是否成功



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        Intent intent = getIntent();
        myurl = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        origincode = intent.getIntExtra("origincode",-1);
             initView();


    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_webview);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView = (WebView) findViewById(R.id.wv_webview);
        initWebview();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.stopLoading();//防止WEBVIEW卡死
                WebviewActivity.this.finish();
            }
        });

    }

    private void initWebview() {
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        if(origincode==0)
        {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//无缓存
        }else if(origincode==1)
        {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//使用缓存
        }


        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {//网页页面开始加载的时候
//                emptyView.setEmptyView(EmptyView.EMPTY_LOADING);//初始化一个显示正在加载的视图
//                rl_detail.setVisibility(View.VISIBLE);
//                rl_detail.removeAllViews();
//                rl_detail.addView(emptyView);//在加载页面开始的时候显示一个正在加载的视图，
//                webView.setEnabled(false);// 当加载网页的时候将网页进行隐藏
//                ll_container_btn.setVisibility(View.GONE);
//                btn_collect.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }

            /**
             * 网页加载结束的时候执行的回调方法
             * @param view
             * @param url
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:showInfoFromJava('" + MyApplication.username+ "')");
                if(loadError)
                     view.loadUrl("file:///android_assets/nonetwork.html");
                //网页加载结束的时候
//                if (!loadError) {//当网页加载成功的时候判断是否加载成功
//                    rl_detail.setVisibility(View.GONE);//加载成功的话，则隐藏掉显示正在加载的视图，显示加载了网页内容的WebView
//                    webView.setEnabled(true);
//                    ll_container_btn.setVisibility(View.VISIBLE);
//                    btn_collect.setVisibility(View.VISIBLE);
//                } else { //加载失败的话，初始化页面加载失败的图，然后替换正在加载的视图页面
//                    rl_detail.removeAllViews();
//                    emptyView.setEmptyView(EmptyView.EMPTY_EMPTY, "您找的页面暂时走丢了...");
//                    rl_detail.addView(emptyView);
//                }

            }


            /**
             * 页面加载错误时执行的方法，但是在6.0以下，有时候会不执行这个方法
             * @param view
             * @param errorCode
             * @param description
             * @param failingUrl
             */
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                loadError = true;
            }


        });

        webView.setWebChromeClient(new WebChromeClient(){





            /**
             * 当WebView加载之后，返回 HTML 页面的标题 Title
             * @param view
             * @param title
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                //判断标题 title 中是否包含有“error”字段，如果包含“error”字段，则设置加载失败，显示加载失败的视图
                if(!TextUtils.isEmpty(title)&&title.toLowerCase().contains("error")){
                    loadError = true;
                }

            }
        });
        webView.loadUrl(myurl);



//        webView.loadUrl("http://123.207.46.157/near/index.php/Home/News/index");

//        webView.loadUrl("http://123.207.46.157/near/index.php/Home/News/index");
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
//                switch(errorCode)
//                {
//                    case HttpStatus.SC_NOT_FOUND:
//                        view.loadUrl("file:///android_assets/nonetwork.html");
//                        break;
//                }
//            }
//        });

  
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }else
        {
            webView.stopLoading();
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
           // webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }




}
