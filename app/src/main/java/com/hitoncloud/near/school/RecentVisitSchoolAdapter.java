package com.hitoncloud.near.school;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecentVisitSchoolAdapter extends BaseAdapter {
	
	private List<String> mRecentVisitCityList;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public RecentVisitSchoolAdapter(Context context, List<String> recentVisitCityList) {
		this.mRecentVisitCityList=recentVisitCityList;
		this.mContext=context;
		mInflater= LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mRecentVisitCityList.size();
	}

	@Override
	public Object getItem(int position) {
		return mRecentVisitCityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=mInflater.inflate(R.layout.item_school,null);
			viewHolder.tvCityName=(TextView) convertView.findViewById(R.id.tv_city_name);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tvCityName.setText(mRecentVisitCityList.get(position));
		viewHolder.tvCityName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(mContext,mRecentVisitCityList.get(position)+"", Toast.LENGTH_SHORT).show();
				if(mRecentVisitCityList.get(position).toString().equals("成都东软学院"))
				{
					SharedPreferences.Editor editor = mContext.getSharedPreferences("nowschool",0x0000).edit();
					editor.putString("nowschool", mRecentVisitCityList.get(position).toString());
					editor.apply();

					AsyncHttpClient client = new AsyncHttpClient();
					RequestParams params = new RequestParams();
					params.add("username", MyApplication.username);
					params.add("college", MyApplication.locschool);
					client.post("http://123.207.46.157/near/index.php/Home/User/changeCollege", params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int i, Header[] headers, byte[] bytes) {
							String response = new String(bytes);
							Log.e("debug4", response);
							JSONObject object = null;
							try {
								object = new JSONObject(response);
								String nowschool = object.getString("status");
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
							//Toast.makeText(SchoolActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
						}
					});


					Toast.makeText(mContext,"学校已切换至："+mRecentVisitCityList.get(position) , Toast.LENGTH_SHORT).show();
					MyApplication.locschool = mRecentVisitCityList.get(position);
					((Activity)mContext).finish();
				}
				else{
					Toast.makeText(mContext,"暂时仅支持成都东软学院", Toast.LENGTH_SHORT).show();
				}
			}
		});
		return convertView;
	}
	
	class ViewHolder{
		TextView tvCityName;
	}

}
