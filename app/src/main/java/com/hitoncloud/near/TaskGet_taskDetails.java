package com.hitoncloud.near;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hitoncloud.near.application.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class TaskGet_taskDetails extends Activity {

    LinearLayout back;//顶部返回按钮
    ScrollView scrollView;
    TextView ttaskname, tdate, tmoeny, tloc, tputter, ttel, tdatails;
    Button btn_accept, btn_tel,btn_session;
    String ordernum;
    int cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_get_task_details);
        Intent intent = getIntent();
        String taskname = intent.getStringExtra("taskname");
        ordernum = intent.getStringExtra("ordernum");
        String moeny = intent.getStringExtra("moeny");
        String date = intent.getStringExtra("date");
        String putter = intent.getStringExtra("putter");
        cancel = intent.getIntExtra("cancel",0);


        ttaskname = (TextView) findViewById(R.id.taskget_taskdetails_taskname);
        tdate = (TextView) findViewById(R.id.taskget_taskdetails_tasktime);
        tmoeny = (TextView) findViewById(R.id.taskget_taskdetails_taskmoeny);
        tloc = (TextView) findViewById(R.id.taskget_taskdetails_taskloc);
        tputter = (TextView) findViewById(R.id.taskget_taskdetails_tasksender);
        ttel = (TextView) findViewById(R.id.taskget_taskdetails_tasktel);
        tdatails = (TextView) findViewById(R.id.taskget_taskdetails_details);
        btn_tel = (Button) findViewById(R.id.taskget_taskdetails_telbtn);
        btn_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ttel.getText()));
                if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(TaskGet_taskDetails.this,"没有电话权限，不能开启打电话功能",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });

        btn_session = (Button)findViewById(R.id.taskget_taskdetails_sessionbtn);
        btn_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });





        ttaskname.setText(taskname);
        tmoeny.setText(moeny);
        tputter.setText(putter);




        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("orderNum", ordernum);
        params.add("userid2", MyApplication.username);
        Log.e("测试",ordernum);
        client.post("http://123.207.46.157/near/index.php/Admin/Manager/showDetail", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Log.e("debug32", response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    String Jdetails = object.getString("details");
                    String Jtel = object.getString("tel");
                    String Jlimittime= object.getString("limittime");
                    String Jloc = object.getString("address");
                    tdatails.setText(Jdetails);
                    ttel.setText(Jtel);
                    tloc.setText(Jloc);
                    tdate.setText(Jlimittime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(TaskGet_taskDetails.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });

        back = (LinearLayout) findViewById(R.id.task_taskdetails_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scrollView = (ScrollView)findViewById(R.id.taskget_taskdetails_scrollview);
        btn_accept=(Button)findViewById(R.id.taskget_taskdetails_taskaccept);
        btn_accept.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              if(cancel ==0)
              {
                  AsyncHttpClient client = new AsyncHttpClient();
                  RequestParams params = new RequestParams();
                  params.add("orderNum", ordernum);
                  params.add("userid2", MyApplication.username);
                  Log.e("测试",ordernum);
                  client.post("http://123.207.46.157/near/index.php/Admin/Manager/acceptTask", params, new AsyncHttpResponseHandler() {
                      @Override
                      public void onSuccess(int i, Header[] headers, byte[] bytes) {
                          String response = new String(bytes);
                          Log.e("debug33", response);
                          JSONObject object = null;
                          try {
                              object = new JSONObject(response);
                              String status = object.getString("status");
                              if(status.equals("success"))
                              {
                                  Toast.makeText(TaskGet_taskDetails.this,"接受任务成功",Toast.LENGTH_SHORT).show();
                                  TaskGet_taskDetails.this.finish();
                              }else if(status.equals("error")){
                                  Toast.makeText(TaskGet_taskDetails.this, "亲，您不能接受自己的任务哦", Toast.LENGTH_SHORT).show();
                              }else{
                                  Toast.makeText(TaskGet_taskDetails.this, "出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                              }


                          } catch (JSONException e) {
                              e.printStackTrace();
                              Toast.makeText(TaskGet_taskDetails.this,"任务被别人抢走啦",Toast.LENGTH_SHORT).show();
                              TaskGet_taskDetails.this.finish();
                          }
                      }

                      @Override
                      public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                          Toast.makeText(TaskGet_taskDetails.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                      }
                  });
              }else if(cancel==1){
                  AsyncHttpClient client = new AsyncHttpClient();
                  RequestParams params = new RequestParams();
                  params.add("orderNum", ordernum);
                  Log.e("测试",ordernum);
                  client.post("http://123.207.46.157/near/index.php/Admin/Manager/redeleteTask", params, new AsyncHttpResponseHandler() {
                      @Override
                      public void onSuccess(int i, Header[] headers, byte[] bytes) {
                          String response = new String(bytes);
                          Log.e("debug33", response);
                          JSONObject object = null;
                          try {
                              object = new JSONObject(response);
                              String status = object.getString("status");
                              if(status.equals("success"))
                              {
                                  Toast.makeText(TaskGet_taskDetails.this,"取消任务成功",Toast.LENGTH_SHORT).show();
                                  TaskGet_taskDetails.this.finish();
                              }else{
                                  Toast.makeText(TaskGet_taskDetails.this, "出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                              }


                          } catch (JSONException e) {
                              e.printStackTrace();
                              Toast.makeText(TaskGet_taskDetails.this, "出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                              TaskGet_taskDetails.this.finish();
                          }
                      }

                      @Override
                      public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                          Toast.makeText(TaskGet_taskDetails.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                      }
                  });
              }else if(cancel==2)
              {
                  AsyncHttpClient client = new AsyncHttpClient();
                  RequestParams params = new RequestParams();
                  params.add("orderNum", ordernum);
                  Log.e("测试",ordernum);
                  client.post("http://123.207.46.157/near/index.php/Admin/Manager/finishTask", params, new AsyncHttpResponseHandler() {
                      @Override
                      public void onSuccess(int i, Header[] headers, byte[] bytes) {
                          String response = new String(bytes);
                          Log.e("debug33", response);
                          JSONObject object = null;
                          try {
                              object = new JSONObject(response);
                              String status = object.getString("status");
                              if(status.equals("success"))
                              {
                                  Toast.makeText(TaskGet_taskDetails.this,"任务已经完成",Toast.LENGTH_SHORT).show();
                                  TaskGet_taskDetails.this.finish();
                              }else{
                                  Toast.makeText(TaskGet_taskDetails.this, "出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                              }


                          } catch (JSONException e) {
                              e.printStackTrace();
                              Toast.makeText(TaskGet_taskDetails.this, "出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                              TaskGet_taskDetails.this.finish();
                          }
                      }

                      @Override
                      public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                          Toast.makeText(TaskGet_taskDetails.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                      }
                  });
              }


          }
      });
        if(cancel ==1)
        {
            btn_accept.setText("取消任务");
        }else if(cancel ==2)
            btn_accept.setText("确认完成");

    }
}
