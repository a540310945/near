package com.hitoncloud.near.homepage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hitoncloud.near.R;

import static android.view.KeyEvent.KEYCODE_BACK;

public class ScanRequestActivity extends Activity {

    String content;
    TextView tv_result;
    WebView webview;
    RelativeLayout rl_root;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_request);
        Intent intent = getIntent();
        content = intent.getStringExtra("result");
        initView();
        initWebview();




    }

    private void initView() {
        rl_root = (RelativeLayout)findViewById(R.id.rl_scanrequest_root);
        tv_result = (TextView)findViewById(R.id.tv_scanrequest_result);
    }


    private void initWebview() {

        webview = (WebView)findViewById(R.id.wb_scanrequest);
        tv_result.setVisibility(View.GONE);
        webview.setVisibility(View.VISIBLE);
        if(isurl(content))
        {
            Toast.makeText(ScanRequestActivity.this,content,Toast.LENGTH_SHORT).show();
            webview.loadUrl("http://www.baidu.com");
            //覆盖WebView默认通过第三方或者是系统浏览器打开网页的行为，使得网页可以在WebView中打开
            webview.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //返回值是true的时候是控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器打开
                    view.loadUrl(url);
                    return true;
                }
                //WebViewClient帮助WebView去处理一些页面控制和请求通知
            });
            //启用支持Javascript
            WebSettings settings = webview.getSettings();
            settings.setJavaScriptEnabled(true);
            //WebView加载页面优先使用缓存加载
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            //页面加载
            webview.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    //newProgress   1-100之间的整数
                    if (newProgress == 100) {
                        //页面加载完成，关闭ProgressDialog
                        closeDialog();
                    } else {
                        //网页正在加载，打开ProgressDialog
                        openDialog(newProgress);
                    }
                }

                private void openDialog(int newProgress) {
                    if (dialog == null) {
                        dialog = new ProgressDialog(ScanRequestActivity.this);
                        dialog.setTitle("正在加载");
                        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        dialog.setProgress(newProgress);
                        dialog.setCancelable(true);
                        dialog.show();
                    } else {
                        dialog.setProgress(newProgress);
                    }
                }

                private void closeDialog() {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                        dialog = null;
                    }
                }
            });
        }
        else{
            webview.setVisibility(View.GONE);
            tv_result.setVisibility(View.VISIBLE);
            if(!content.isEmpty())
            {
                tv_result.setText(content);
            }

        }


    }

    private boolean isurl(String content) {
        String str_http,str_https;
        if(content.length()>=7)
        {
            str_http = content.substring(0,4);
            str_https = content.substring(0,5);
            Log.e("httptest",str_http+"fuck"+str_https);
        }
        else
        {
            Log.e("httptest", String.valueOf(content.length()));
            return  false;

        }
        if(str_http.equals("http")||str_https.equals("https"))
        {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
        webview.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
        webview.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rl_root.removeView(webview);
        webview.destroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
