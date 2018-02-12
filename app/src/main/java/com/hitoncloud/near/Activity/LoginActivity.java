package com.hitoncloud.near.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.utils.StatusBarUtils;
import com.hitoncloud.near.utils.VerificationUtils;
import com.hitoncloud.near.utils.encryption;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 功能：用户登录系统，跳转到忘记密码，跳转到注册
 */
public class LoginActivity extends Activity {

    private ImageView mDynamicBackground;//动态背景
    private Button btn_login,btn_reg,btn_forget;//登录按钮,注册按钮,找回密码按钮
    private Button btn_regcode,btn_forgetcode;//注册，找回密码验证码按钮
    private RelativeLayout rl_login,rl_reg,rl_forget;//登录界面，注册界面，忘记密码界面

    private TextView tv_LoginForget, tv_LoginReg;//登录界面文本按钮：忘记密码，注册,
    public EditText et_LoginUsername,et_LoginPassword;//登录界面文本框：用户名，登录密码

    private TextView tv_RegForget,tv_RegLogin;//注册界面文本按钮：忘记密码，登录,
    private EditText et_RegCollege,et_RegUsername,et_RegCode,et_RegPassword,et_RegEnsurePassword;//注册界面文本框：昵称，用户名，验证码，密码，确认密码
    private EditText et_ForgetUsername,et_ForgetCode,et_ForgetPassword,et_ForgetEnsurePassword;//忘记密码界面文本框：用户名，验证码,密码，确认密码

    private TextView tv_ForgetLogin,tv_ForgetReg;//忘记密码界面文本按钮：登录，注册,

    private boolean isanim = false;//判断动画当前是否在运行
    private boolean isfinish = false;//判断页面是否需要结束

    private int status = 0;//0:登陆界面 1:注册界面 2:忘记密码界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setWindowStatusBarTransparent(this);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.text_color));//修改状态栏颜色
        setContentView(R.layout.activity_login);
        initView();//初始化View
        initAccount();//初始化账号信息
        if(!isfinish)
        {
            initLogin();//初始化登录界面
            initReg();//初始化注册界面
            initFroget();//初始化忘记密码
        }
    }

    private void initView() {
        mDynamicBackground = (ImageView) findViewById(R.id.iv_dynamicbg);//动态背景
        //动态背景移动,移动参数参照R.anim.login_bg
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.login_bg);
                mDynamicBackground.startAnimation(animation);
            }
        }, 200);

        rl_login = (RelativeLayout)findViewById(R.id.rl_login_root);//登录整体布局
        tv_LoginForget = (TextView) findViewById(R.id.tv_login_forget);//登录-》忘记密码
        tv_LoginReg = (TextView) findViewById(R.id.tv_login_reg);//登录-》注册
        btn_login = (Button) findViewById(R.id.btn_login_login);//登录按钮
        et_LoginUsername = (EditText) findViewById(R.id.et_login_phonenumber);//登录用户名
        et_LoginPassword = (EditText) findViewById(R.id.et_login_password);//登录密码

        rl_reg = (RelativeLayout)findViewById(R.id.rl_reg_root);//注册整体布局
        tv_RegForget = (TextView)findViewById(R.id.tv_reg_forget);//注册-》忘记密码
        tv_RegLogin= (TextView)findViewById(R.id.tv_reg_login);//注册-》返回登录
        btn_reg = (Button)findViewById(R.id.btn_reg_reg);//注册按钮
        btn_regcode = (Button)findViewById(R.id.btn_reg_getcode);//注册验证码按钮
        et_RegCollege = (EditText)findViewById(R.id.et_reg_college);//注册选择学校
        et_RegUsername = (EditText)findViewById(R.id.et_reg_phonenumber);//注册用户名
        et_RegCode = (EditText)findViewById(R.id.et_reg_code);//注册验证码
        et_RegPassword = (EditText)findViewById(R.id.et_reg_password);//注册密码
        et_RegEnsurePassword = (EditText)findViewById(R.id.et_reg_ensurepassword);//注册确认密码

        rl_forget = (RelativeLayout)findViewById(R.id.rl_forget_root);//忘记密码整体布局
        tv_ForgetLogin = (TextView) findViewById(R.id.tv_forget_login);//忘记密码-》返回登录
        tv_ForgetReg = (TextView)findViewById(R.id.tv_forget_reg);//忘记密码-》注册
        btn_forget = (Button)findViewById(R.id.btn_forget_forget);//忘记密码按钮
        btn_forgetcode = (Button)findViewById(R.id.btn_forget_getcode);//忘记密码验证码按钮
        et_ForgetUsername = (EditText)findViewById(R.id.et_forget_phonenumber);//注册用户名
        et_ForgetCode = (EditText)findViewById(R.id.et_forget_code);//注册验证码
        et_ForgetPassword = (EditText)findViewById(R.id.et_forget_password);//注册密码
        et_ForgetEnsurePassword = (EditText)findViewById(R.id.et_forget_ensurepassword);//忘记密码确认密码
    }

    private void initAccount() {
        //读取本地是否有账号密码信息
        SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
        MyApplication.username = pref.getString("username", "");
        MyApplication.password = pref.getString("password", "");
        //根据信息判断是否直接登录
        if ((!(MyApplication.username.equals("")))&& (!(MyApplication.password .equals("")))) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            isfinish = true;
            LoginActivity.this.finish();
       }
    }

    private void initLogin() {
        //通过登录界面进入注册界面
        tv_LoginReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isanim)
                {
                    isanim = true;
                    status = 1;
                    TranslateAnimation mHideAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                            0f, Animation.RELATIVE_TO_PARENT, -1.0f);
                    mHideAction.setDuration(500);
                    TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                            1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                    mShowAction.setDuration(500);
                    rl_reg.startAnimation(mShowAction);
                    rl_login.startAnimation(mHideAction);
                    rl_reg.setVisibility(View.VISIBLE);
                    rl_login.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rl_login.setVisibility(View.GONE);
                            isanim = false;
                        }
                    },500);
                }
            }
        });

        //通过登录界面进入忘记密码界面
        tv_LoginForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isanim)
                {
                    isanim = true;
                    status = 2;
                    TranslateAnimation mHideAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                            0.0f, Animation.RELATIVE_TO_PARENT, 1.0f);
                    mHideAction.setDuration(500);
                    TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                            -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                    mShowAction.setDuration(500);
                    rl_forget.startAnimation(mShowAction);
                    rl_login.startAnimation(mHideAction);
                    rl_forget.setVisibility(View.VISIBLE);
                    rl_login.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rl_login.setVisibility(View.GONE);
                            isanim = false;
                        }
                    },500);
                }
            }
        });

        //登录
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = et_LoginUsername.getText().toString();//获取用户输入的用户名
                final String password = et_LoginPassword.getText().toString();//获取用户输入的密码
                final String secpassword = encryption.md5(password);//对密码加密
                if (username.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("username", username);
                    params.add("password", secpassword);
                    client.post("http://123.207.46.157/near/index.php/Home/User/login", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.e("debug", response);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                                String status = object.getString("status");
                                if (status.equals("success")) {
                                    //保存用户信息到本地，避免二次登录
                                    MyApplication.isLogin = true;
                                    MyApplication.username = username;
                                    MyApplication.password = secpassword;
                                    SharedPreferences.Editor editor = getSharedPreferences("logininfo", MODE_PRIVATE).edit();
                                    editor.putString("username", username);
                                    editor.putString("password", secpassword);
                                    editor.apply();
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "用户名或密码错误，请重试", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e("debug","登录界面登录出错"+e);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(LoginActivity.this, "网络不通畅哦~,请检查您的网络", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initReg() {
        //通过注册界面进入登录界面
        tv_RegLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isanim)
                {
                    isanim = true;
                    status = 0;
                    TranslateAnimation mHideAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                            0f, Animation.RELATIVE_TO_PARENT, 1.0f);
                    mHideAction.setDuration(500);
                    TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                            -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                    mShowAction.setDuration(500);
                    rl_login.startAnimation(mShowAction);
                    rl_reg.startAnimation(mHideAction);
                    rl_login.setVisibility(View.VISIBLE);
                    rl_reg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rl_reg.setVisibility(View.GONE);
                            isanim = false;
                        }
                    },500);
                }
            }
        });
        //通过注册界面进入忘记密码界面
        tv_RegForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isanim)
                {
                    isanim = true;
                    status = 1;
                    TranslateAnimation mHideAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT,
                            0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                    mHideAction.setDuration(500);
                    TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                            0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                    mShowAction.setDuration(500);
                    rl_forget.startAnimation(mShowAction);
                    rl_reg.startAnimation(mHideAction);
                    rl_forget.setVisibility(View.VISIBLE);
                    rl_reg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rl_reg.setVisibility(View.GONE);
                            isanim = false;
                        }
                    },500);
                }
            }
        });

        //注册界面验证码
        btn_regcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_RegUsername.getText().toString();
                if (username.equals("")) {
                    Toast.makeText(LoginActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else if (!VerificationUtils.isCellPhoneNumber(username)) {
                    Toast.makeText(LoginActivity.this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                } else {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("username", username);
                    client.post("http://123.207.46.157/near/index.php/Home/User/identify", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.e("debug", response);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                                String status = object.getString("status");
                                if (status.equals("error")) {
                                    Toast.makeText(LoginActivity.this, "出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                                } else if (status.equals("exists")) {
                                    Toast.makeText(LoginActivity.this, "手机号已被注册", Toast.LENGTH_SHORT).show();
                                } else if (status.equals("success")) {
                                    Toast.makeText(LoginActivity.this, "短信已发送，请注意查收", Toast.LENGTH_SHORT).show();
                                    XGPushManager.registerPush(getApplicationContext(), MyApplication.username,
                                            new XGIOperateCallback() {
                                                @Override
                                                public void onSuccess(Object data, int flag) {
                                                    Log.d("TPush", "注册成功，设备token为：" + data);
                                                }

                                                @Override
                                                public void onFail(Object data, int errCode, String msg) {
                                                    Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
                                                }
                                            });

                                    btn_regcode.setBackgroundResource(R.drawable.rs_select_btn_gray);
                                    btn_regcode.setEnabled(false);
                                    timer_reg.start();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(LoginActivity.this, "您好像没有联网哦", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //注册界面注册
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String college = et_RegCollege.getText().toString();
                final String username = et_RegUsername.getText().toString().trim();
                String password = et_RegPassword.getText().toString().trim();
                String enpassword = et_RegEnsurePassword.getText().toString().trim();
                String code = et_RegCode.getText().toString().trim();
                if(college.trim().equals("还未选择学校"))
                {
                    Toast.makeText(LoginActivity.this, "您还没选择学校哦", Toast.LENGTH_SHORT).show();
                }else if( username.trim().equals(""))
                {
                    Toast.makeText(LoginActivity.this, "手机号不能为空哦", Toast.LENGTH_SHORT).show();
                }else if(password.trim().equals("") )
                {
                    Toast.makeText(LoginActivity.this, "密码不能为空哦", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(enpassword))
                {
                    Toast.makeText(LoginActivity.this, "两次密码不一致哦", Toast.LENGTH_SHORT).show();
                }else if(code.trim().equals(""))
                {
                    Toast.makeText(LoginActivity.this, "您还没有输入验证码哦", Toast.LENGTH_SHORT).show();
                }
                else {
                    final String secpassword = encryption.md5(password);//对密码加密
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("username", username);
                    params.add("password", secpassword);
                    params.add("college", college);
                    params.add("identify", code);
                    client.post("http://123.207.46.157/near/index.php/Home/User/reg", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.e("debug", response);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                                String status = object.getString("status");
                                if (status.equals("error")) {
                                    Toast.makeText(LoginActivity.this, "验证码输入不正确", Toast.LENGTH_SHORT).show();
                                } else if (status.equals("success")) {
                                    Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    tv_RegLogin.performClick();
                                } else if (status.equals("exists")) {
                                    Toast.makeText(LoginActivity.this, "手机号已被注册", Toast.LENGTH_SHORT).show();
                                } else if (status.equals("overdue")) {
                                    Toast.makeText(LoginActivity.this, "验证码已过期", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(LoginActivity.this, "您好像没有联网哦", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //选择学校
        et_RegCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SchoolActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initFroget() {
        //通过忘记密码界面进入登录界面
        tv_ForgetLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isanim)
                {
                    isanim = true;
                    status = 0;
                    TranslateAnimation mHideAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                            0f, Animation.RELATIVE_TO_PARENT, -1.0f);
                    mHideAction.setDuration(500);
                    TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                            1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                    mShowAction.setDuration(500);
                    rl_login.startAnimation(mShowAction);
                    rl_forget.startAnimation(mHideAction);
                    rl_login.setVisibility(View.VISIBLE);
                    rl_forget.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rl_forget.setVisibility(View.GONE);
                            isanim = false;
                        }
                    },500);
                }
            }
        });

        //通过忘记密码界面进入注册界面
        tv_ForgetReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isanim)
                {
                    isanim = true;
                    status = 1;
                    TranslateAnimation mHideAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT,
                            0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                    mHideAction.setDuration(500);
                    TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                            0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                    mShowAction.setDuration(500);
                    rl_reg.startAnimation(mShowAction);
                    rl_forget.startAnimation(mHideAction);
                    rl_reg.setVisibility(View.VISIBLE);
                    rl_forget.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rl_forget.setVisibility(View.GONE);
                            isanim = false;
                        }
                    },500);
                }
            }
        });

        //忘记密码发送验证码
        btn_forgetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_ForgetUsername.getText().toString();
                if (username.equals("")) {
                    Toast.makeText(LoginActivity.this, "手机号不能为空哦", Toast.LENGTH_SHORT).show();
                } else if (!VerificationUtils.isCellPhoneNumber(username)) {
                    Toast.makeText(LoginActivity.this, "手机号格式错误诶", Toast.LENGTH_SHORT).show();
                } else {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("username", username);
                    params.add("isforget","1");
                    client.post("http://123.207.46.157/near/index.php/Home/User/identify", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.e("debug", response);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                                String status = object.getString("status");
                                if (status.equals("error")) {
                                    Toast.makeText(LoginActivity.this, "出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                                } else if (status.equals("unreg")) {
                                    Toast.makeText(LoginActivity.this, "账号未注册", Toast.LENGTH_SHORT).show();
                                }
                                else if (status.equals("success")) {
                                    Toast.makeText(LoginActivity.this, "短信已发送，请注意查收", Toast.LENGTH_SHORT).show();
                                    btn_forgetcode.setBackgroundResource(R.drawable.rs_select_btn_gray);
                                    btn_forgetcode.setEnabled(false);
                                    timer_forget.start();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(LoginActivity.this, "您好像没有联网哦", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //忘记密码确认
        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = et_ForgetUsername.getText().toString();
                String password = et_ForgetPassword.getText().toString();
                String enpassword = et_ForgetEnsurePassword.getText().toString();
                String code = et_ForgetCode.getText().toString();
                if ( username.equals("") || code.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "您还有没填的信息哦", Toast.LENGTH_SHORT).show();
                }else if(!password.equals(enpassword))
                {
                    Toast.makeText(LoginActivity.this, "两次密码不一致诶", Toast.LENGTH_SHORT).show();
                }
                else {
                    final String secpassword = encryption.md5(password);//对密码加密
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("username", username);
                    params.add("identify", code);
                    params.add("password", secpassword);
                    params.add("isforget", "1");
                    client.post("http://123.207.46.157/near/index.php/Home/User/reg", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.e("debug", response);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                                String status = object.getString("status");
                                if (status.equals("error")) {
                                    Toast.makeText(LoginActivity.this, "验证码输入不正确", Toast.LENGTH_SHORT).show();
                                }else if (status.equals("success")) {
                                    Toast.makeText(LoginActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                    tv_ForgetLogin.performClick();
                                }
                                else if (status.equals("overdue")) {
                                    Toast.makeText(LoginActivity.this, "验证码已过期", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(LoginActivity.this, "您好像没有联网哦", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //发送验证码60秒倒计时
    CountDownTimer timer_reg = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            btn_regcode.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            btn_regcode.setEnabled(true);
            btn_regcode.setText("发送验证码");
            btn_regcode.setBackgroundResource(R.drawable.rs_select_btn_blue);
        }
    };

    //发送验证码60秒倒计时
    CountDownTimer timer_forget = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            btn_forgetcode.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            btn_forgetcode.setEnabled(true);
            btn_forgetcode.setText("发送验证码");
            btn_forgetcode.setBackgroundResource(R.drawable.rs_select_btn_blue);
        }
    };

    //在注册界面和忘记密码界面按下返回按钮将回到登录界面，登录界面按下返回将退出程序
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if(status ==1)
            {
                tv_RegLogin.performClick();
                status=0;
                return true;
            }
            else if(status ==2)
            {
                tv_ForgetLogin.performClick();
                status=0;
                return true;
            }else {
                LoginActivity.this.finish();
                System.exit(0);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer_reg.cancel();//销毁注册界面验证码计时器
        timer_forget.cancel();//销毁忘记密码界面验证码计时器
    }

    @Override
    protected void onResume(){
        super.onResume();
        et_RegCollege.setText(MyApplication.locschool);
        if(MyApplication.locschool.equals(""))
        {
            et_RegCollege.setText("还未选择学校");
        }else
        {
            et_RegCollege.setText(MyApplication.locschool);
        }

    }

}