package com.hitoncloud.near.Activity;


import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;

import android.view.View;

import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.utils.GlideCircleTransform;
import com.hitoncloud.near.utils.StringUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class PersonalinfoActivity extends AppCompatActivity {

    private static final String TAG= "PersonalinfoActivity";

    Toolbar toolbar;

    private ImageView iv_portrait;

    private RelativeLayout rl_nickname,rl_sex,rl_portrait;
    private TextView tv_nickname,tv_sex;

    private String nickname,sex;
    RelativeLayout rl_uploadheadimg;//上传头像

    RelativeLayout back;
    TextView tv_name;

    private String[] datas = {"上传头像"};

    private String tmp_nickname="",tmp_sex="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personinfo);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        initView();
        getpersonalinfo();

    }



    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_personalinfo);
        toolbar.setTitle("个人信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonalinfoActivity.this.finish();
            }
        });
        //加载用户头像
        rl_portrait = (RelativeLayout)findViewById(R.id.rl_personalinfo_portrait);
        iv_portrait = (ImageView)findViewById(R.id.iv_personalinfo_portrait);
        Glide.with(this).load(MyApplication.url_userportrait).placeholder(R.drawable.default_portrait).fitCenter().transform(new GlideCircleTransform(this)).crossFade().into(iv_portrait);
        rl_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PersonalinfoActivity.this,"头像暂时不能修改",Toast.LENGTH_SHORT).show();
            }
        });

        //用户昵称
        rl_nickname = (RelativeLayout)findViewById(R.id.rl_personalinfo_nickname);
        tv_nickname = (TextView)findViewById(R.id.tv_personalinfo_nickname_value);
        rl_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(PersonalinfoActivity.this)
                        .backgroundColor(getResources().getColor(R.color.white))
                        .negativeColor(getResources().getColor(R.color.red))
                        .widgetColor(getResources().getColor(R.color.theme))
                        .positiveColor(getResources().getColor(R.color.theme))
                        .linkColor(getResources().getColor(R.color.theme))
                        .itemsColor(getResources().getColor(R.color.theme))
                        .titleColor(getResources().getColor(R.color.theme))
                        .dividerColor(getResources().getColor(R.color.theme))
                        .neutralColor(getResources().getColor(R.color.theme))
                        .contentColor(getResources().getColor(R.color.theme))
                        .title("更改昵称")
                        .content("昵称最少1个字")
                        .inputRangeRes(1, 20, R.color.red)
                        .inputType(InputType.TYPE_CLASS_TEXT )
                        .input(null, "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                tmp_nickname = input.toString();
                                httpforpersonalinfo();
                            }
                        }).show();
            }
        });
        //用户性别
        rl_sex = (RelativeLayout)findViewById(R.id.rl_personalinfo_sex);
        tv_sex = (TextView)findViewById(R.id.tv_personalinfo_sex_value);
        rl_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(PersonalinfoActivity.this)
                        .backgroundColor(getResources().getColor(R.color.white))
                        .negativeColor(getResources().getColor(R.color.red))
                        .widgetColor(getResources().getColor(R.color.theme))
                        .positiveColor(getResources().getColor(R.color.theme))
                        .linkColor(getResources().getColor(R.color.theme))
                        .itemsColor(getResources().getColor(R.color.theme))
                        .titleColor(getResources().getColor(R.color.theme))
                        .dividerColor(getResources().getColor(R.color.theme))
                        .neutralColor(getResources().getColor(R.color.theme))
                        .contentColor(getResources().getColor(R.color.theme))
                        .title("性别")
                        .items("男","女")
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                tmp_sex = text.toString();
                                httpforpersonalinfo();
                                return true;
                            }
                        })
                        .positiveText("选择")
                        .show();
            }


        });

    }



    private void httpforpersonalinfo(){
        if(tmp_nickname.equals(""))
            tmp_nickname = tv_nickname.getText().toString();
        if(tmp_sex.equals(""))
            tmp_sex = tv_sex.getText().toString();

        final MaterialDialog md = new MaterialDialog.Builder(this)
                .title("请稍后")
                .content("数据上传中……")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .autoDismiss(false)
                .show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("username",MyApplication.username);
        params.add("nickname", tmp_nickname);
        params.add("sex", tmp_sex);
        client.post("http://123.207.46.157/near/index.php/Home/User/updateInfo", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                response = StringUtils.JSONTokener(response);
                Log.e("debug", response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")) {
                        tv_nickname.setText(tmp_nickname);
                        tv_sex.setText(tmp_sex);
                        setpersonalinfo(tmp_nickname,tmp_sex);
                        md.hide();
                        Toast.makeText(PersonalinfoActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    } else {
                        md.hide();
                        Toast.makeText(PersonalinfoActivity.this,"好像没有什么改变",Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "请求更新个人信息出现未知错误");
                    }
                } catch (JSONException e) {
                    md.hide();
                    Toast.makeText(PersonalinfoActivity.this,"修改失败，请重试",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "请求更新个人信息崩溃"+e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                md.hide();
                Toast.makeText(PersonalinfoActivity.this,"请检查您的网络",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setpersonalinfo(String str_nickname,String str_sex) {
        SharedPreferences.Editor editor = getSharedPreferences("personalinfo", MODE_PRIVATE).edit();
        editor.putString("nickname", str_nickname);
        editor.putString("sex", str_sex);
        editor.apply();
    }

    public void getpersonalinfo() {
        SharedPreferences pref = getSharedPreferences("personalinfo", MODE_PRIVATE);
        String str_nickname = pref.getString("nickname", "");
        String str_sex = pref.getString("sex","");
        tv_nickname.setText(str_nickname);
        tv_sex.setText(str_sex);
    }


//
//    private void inituploadheadimg() {
//        mShade = (FrameLayout) findViewById(R.id.fl_personinfo_shade);
//        mShade.setVisibility(View.GONE);
//        rl_uploadheadimg = (RelativeLayout) findViewById(R.id.rl_personinfo_uploadheadimg);
//        rl_uploadheadimg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PersonalinfoActivity.this,HeadimgUploadActivity.class);
//                startActivity(intent);
//
////                mShade.setVisibility(View.VISIBLE);
////                // TODO: 2016/5/17 构建一个popupwindow的布局
////                View popupView = getLayoutInflater().inflate(R.layout.communitypluspopupwindow, null);
////
////                // TODO: 2016/5/17 为了演示效果，简单的设置了一些数据，实际中大家自己设置数据即可，相信大家都会。
////                ListView lsvMore = (ListView) popupView.findViewById(R.id.lv_community_plus);
////                lsvMore.setAdapter(new ArrayAdapter<String>(PersonalinfoActivity.this, android.R.layout.simple_list_item_1, datas));
////
////
////                // TODO: 2016/5/17 创建PopupWindow对象，指定宽度和高度
////                PopupWindow window = new PopupWindow(popupView, MyApplication.screenWidth - 80, 400);
////                // TODO: 2016/5/17 设置动画
////                window.setAnimationStyle(R.style.popup_window_anim);
////                // TODO: 2016/5/17 设置背景颜色
////                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
////                // TODO: 2016/5/17 设置可以获取焦点
////                window.setFocusable(true);
////                // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
////                window.setOutsideTouchable(true);
////                // TODO：更新popupwindow的状态
////                window.update();
////                // TODO: 2016/5/17 以下拉的方式显示，并且可以设置显示的位置
////                window.showAsDropDown(bar, 40, (MyApplication.screenHeight + -400) / 2 - MyApplication.statusheight);
////                window.setOnDismissListener(new PopupWindow.OnDismissListener() {
////                    @Override
////                    public void onDismiss() {
////                        mShade.setVisibility(View.GONE);
////
////                    }
////                });
//            }
//        });
//    }
}
