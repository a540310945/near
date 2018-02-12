package com.hitoncloud.near.school;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class SchoolListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Schoollist> mAllSchoolList;
	private List<Schoollist> mNearSchoolList;
	private List<String> mRecentSchoolList;
	public HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private String currentSchool;//当前学校
//	private MyLocationListener myLocationListener;
	private boolean isNeedRefresh;//当前定位的学校是否需要刷新
	private TextView tvCurrentSchool;
	private ProgressBar pbLocate;
	private TextView tvLocate;
	private final int VIEW_TYPE = 5;//view的类型个数

	public SchoolListAdapter(Context context, List<Schoollist> allCityList,
                             List<Schoollist> hotCityList, List<String> recentCityList) {
		this.mContext = context;
		this.mAllSchoolList = allCityList;
		this.mNearSchoolList = hotCityList;
		this.mRecentSchoolList=recentCityList;
		
		alphaIndexer = new HashMap<String, Integer>();
		sections = new String[allCityList.size()];

		//这里的主要目的是将listview中要显示字母的条目保存下来，方便在滑动时获得位置，alphaIndexer在Acitivity有调用
		for (int i = 0; i < mAllSchoolList.size(); i++) {
			// 当前汉语拼音首字母
			String currentStr = getAlpha(mAllSchoolList.get(i).getPinyin());
			// 上一个汉语拼音首字母，如果不存在为" "
			String previewStr = (i - 1) >= 0 ? getAlpha(mAllSchoolList.get(i - 1).getPinyin()) : " ";
			if (!previewStr.equals(currentStr)) {
				String name = getAlpha(mAllSchoolList.get(i).getPinyin());
				alphaIndexer.put(name, i);
				sections[i] = name;
			}
		}
		isNeedRefresh=true;
		//initLocation();
	}

	@Override
	public int getViewTypeCount() {

		return VIEW_TYPE;
	}

	@Override
	public int getItemViewType(int position) {
		return position < 4 ? position : 4;
	}

	@Override
	public int getCount() {
		return mAllSchoolList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAllSchoolList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		int viewType = getItemViewType(position);
		if (viewType == 0) {//view类型为0，也就是：当前定位学校的布局
			convertView = View.inflate(mContext, R.layout.item_location_school,
					null);
			tvLocate=(TextView) convertView.findViewById(R.id.tv_locate);
			tvCurrentSchool=(TextView) convertView.findViewById(R.id.tv_current_locate_school);
			pbLocate = (ProgressBar) convertView.findViewById(R.id.pb_loacte);

//			if(!isNeedRefresh){
			if(MyApplication.locschool.equals("")){
				currentSchool = "还未选择学校";
			}else
			{
				currentSchool = MyApplication.locschool;
			}

			Log.e("debug1",currentSchool);
			Log.e("debug1",MyApplication.locschool);

				tvLocate.setText("当前选择学校");
				tvCurrentSchool.setVisibility(View.VISIBLE);
				tvCurrentSchool.setText(currentSchool);
				pbLocate.setVisibility(View.GONE);
//			}else{
//				//myLocationClient.start();
//			}
//
//			tvCurrentSchool.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					pbLocate.setVisibility(View.VISIBLE);
//					tvLocate.setText("正在定位");
//					tvCurrentSchool.setVisibility(View.GONE);
//					//myLocationClient.start();
//				}
//			});

		} else if (viewType == 1) {//最近访问学校
			convertView = View.inflate(mContext,R.layout.item_near_school, null);
			TextView tvRecentVisitCity=(TextView) convertView.findViewById(R.id.tv_recent_visit_city);
			tvRecentVisitCity.setText("最近访问学校");
			SchoollistGridView gvRecentVisitCity = (SchoollistGridView) convertView.findViewById(R.id.gv_recent_visit_city);
			gvRecentVisitCity.setAdapter(new RecentVisitSchoolAdapter(mContext,mRecentSchoolList));

		} else if (viewType == 2) {//附近学校
			convertView = View.inflate(mContext,R.layout.item_near_school, null);
			TextView tvRecentVisitCity=(TextView) convertView.findViewById(R.id.tv_recent_visit_city);
			tvRecentVisitCity.setText("附近的学校");
			SchoollistGridView gvRecentVisitCity = (SchoollistGridView) convertView.findViewById(R.id.gv_recent_visit_city);
			gvRecentVisitCity.setAdapter(new NearSchoolAdapter(mContext,mNearSchoolList));
		} else if (viewType == 3) {//全部学校，仅展示“全部学校这四个字”
			convertView = View.inflate(mContext,R.layout.item_all_school_textview, null);
		} else {//数据库中所有的学校的名字展示
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(mContext, R.layout.item_school_list,null);
				viewHolder.tvAlpha = (TextView) convertView.findViewById(R.id.tv_alpha);
				viewHolder.tvSchoolName = (TextView) convertView.findViewById(R.id.tv_city_name);
				viewHolder.llMain=(LinearLayout) convertView.findViewById(R.id.ll_main);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (position >= 1) {
				viewHolder.tvSchoolName.setText(mAllSchoolList.get(position).getName());
				viewHolder.llMain.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(mAllSchoolList.get(position).getName().toString().equals("成都东软学院"))
						{
							SharedPreferences.Editor editor = mContext.getSharedPreferences("nowschool",0x0000).edit();
							editor.putString("nowschool", mAllSchoolList.get(position).getName().toString());
							editor.apply();

							AsyncHttpClient client = new AsyncHttpClient();
							RequestParams params = new RequestParams();
							params.add("username", MyApplication.username);
							params.add("college", MyApplication.locschool);
							client.post("http://123.207.46.157/near/index.php/Home/User/changeCollege", params, new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(int i, Header[] headers, byte[] bytes) {
									String response = new String(bytes);
									Log.e("debug", response);
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

							Toast.makeText(mContext,"学校已切换至："+mAllSchoolList.get(position).getName() , Toast.LENGTH_SHORT).show();
							MyApplication.locschool = mAllSchoolList.get(position).getName();
							((Activity)mContext).finish();
						}
						else{
							Toast.makeText(mContext,"暂时仅支持成都东软学院", Toast.LENGTH_SHORT).show();
						}
					}
				});
				String currentStr = getAlpha(mAllSchoolList.get(position).getPinyin());
				String previewStr = (position - 1) >= 0 ? getAlpha(mAllSchoolList
						.get(position - 1).getPinyin()) : " ";
				//如果当前的条目的学校名字的拼音的首字母和其前一条条目的学校的名字的拼音的首字母不相同，则将布局中的展示字母的TextView展示出来
				if (!previewStr.equals(currentStr)) {
					viewHolder.tvAlpha.setVisibility(View.VISIBLE);
					viewHolder.tvAlpha.setText(currentStr);
				} else {
					viewHolder.tvAlpha.setVisibility(View.GONE);
				}
			}

		}

		return convertView;
	}

	// 获得汉语拼音首字母
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
//		} else if (str.equals("0")) {
//			return "定位";
		} else if (str.equals("1")) {
			return "最近";
		} else if (str.equals("2")) {
			return "热门";
		} else if (str.equals("3")) {
			return "全部";
		} else {
			return "#";
		}
	}

	class ViewHolder {
		TextView tvAlpha;
		TextView tvSchoolName;
		LinearLayout llMain;
	}

//	public void initLocation() {
//		myLocationClient = new LocationClient(mContext);
//		myLocationListener=new MyLocationListener();
//		myLocationClient.registerLocationListener(myLocationListener);
//		// 设置定位参数
//		LocationClientOption option = new LocationClientOption();
//		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(10000); // 10分钟扫描1次
//		// 需要地址信息，设置为其他任何值（string类型，且不能为null）时，都表示无地址信息。
//		option.setAddrType("all");
//		// 设置是否返回POI的电话和地址等详细信息。默认值为false，即不返回POI的电话和地址信息。
//		option.setPoiExtraInfo(true);
//		// 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
//		option.setProdName("通过GPS定位我当前的位置");
//		// 禁用启用缓存定位数据
//		option.disableCache(true);
//		// 设置最多可返回的POI个数，默认值为3。由于POI查询比较耗费流量，设置最多返回的POI个数，以便节省流量。
//		option.setPoiNumber(3);
//		// 设置定位方式的优先级。
//		// 当gps可用，而且获取了定位结果时，不再发起网络请求，直接返回给用户坐标。这个选项适合希望得到准确坐标位置的用户。如果gps不可用，再发起网络请求，进行定位。
//		option.setPriority(LocationClientOption.GpsFirst);
//		myLocationClient.setLocOption(option);
//		myLocationClient.start();
//	}

//	public class MyLocationListener implements BDLocationListener{
//
//		@Override
//		public void onReceiveLocation(BDLocation arg0) {
//
//			isNeedRefresh=false;
//			if(arg0.getCity()==null){
//				//定位失败
//				tvLocate.setText("未定位到学校,请选择");
//				tvCurrentSchool.setVisibility(View.VISIBLE);
//				tvCurrentSchool.setText("重新选择");
//				pbLocate.setVisibility(View.GONE);
//				return;
//			}else{
//				//定位成功
//				currentSchool=arg0.getCity().substring(0,arg0.getCity().length()-1);
//				tvLocate.setText("当前定位学校");
//				tvCurrentSchool.setVisibility(View.VISIBLE);
//				tvCurrentSchool.setText(currentSchool);
//				myLocationClient.stop();
//				pbLocate.setVisibility(View.GONE);
//			}
//		}
//
//		@Override
//		public void onReceivePoi(BDLocation arg0) {
//
//		}
//
//	}

}
