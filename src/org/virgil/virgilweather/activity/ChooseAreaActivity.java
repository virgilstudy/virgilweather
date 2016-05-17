package org.virgil.virgilweather.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.virgil.virgilweather.R;
import org.virgil.virgilweather.db.CoolWeatherDB;
import org.virgil.virgilweather.db.CoolWeatherOpenHelper;
import org.virgil.virgilweather.model.City;
import org.virgil.virgilweather.model.Country;
import org.virgil.virgilweather.model.Province;
import org.virgil.virgilweather.util.HttpCallBackListenser;
import org.virgil.virgilweather.util.HttpUtil;
import org.virgil.virgilweather.util.Utility;

import android.app.Activity;
import android.app.DownloadManager.Query;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;
	private ProgressDialog progressDialog;
	private TextView titleView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<>();
	private CoolWeatherDB coolWeatherDB;
	private List<Province> provinceList;
	private List<City> cityList;
	private List<Country> countryList;
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra(
				"from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		// queryprovince();
		Log.d("virgil", "" + dataList.size());
		listView = (ListView) findViewById(R.id.list_view);
		titleView = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				// TODO 自动生成的方法存根
				if (currentLevel == LEVEL_PROVINCE) {
					Log.d("virgil", "query_city");
					selectedProvince = provinceList.get(index);
					queryCity();
				} else if (currentLevel == LEVEL_CITY) {
					Log.d("virgil", "query_country");
					selectedCity = cityList.get(index);
					queryCountry();
				} else if (currentLevel == LEVEL_COUNTRY) {
					String countryCode = countryList.get(index).getCountrCode();
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("coutry_code", countryCode);
					startActivity(intent);
					finish();
				}
			}
		});

		queryprovince();
	}

	public void queryprovince() {
		provinceList = coolWeatherDB.loadProvince();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province p : provinceList) {
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("中国");
			currentLevel = this.LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	public void queryCity() {
		cityList = coolWeatherDB.loadCitys(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				Log.d("virgil", "city:" + city.getCityName());
				System.out.println(city.getCityName());
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedProvince.getProvinceName());
			currentLevel = this.LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	public void queryCountry() {
		countryList = coolWeatherDB.loadCountry(selectedCity.getId());
		if (countryList.size() > 0) {
			dataList.clear();

			for (Country c : countryList) {
				Log.d("virgil", "countryName:" + c.getCountryName());
				dataList.add(c.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedCity.getCityName());
			currentLevel = this.LEVEL_COUNTRY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "country");
		}
	}

	private void queryFromServer(final String code, final String type) {
		String address;
		Log.d("virgil", "" + code);
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		Log.d("vigil", address);
		showProcessDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallBackListenser() {

			@Override
			public void onFinish(String response) {
				// TODO 自动生成的方法存根
				Log.d("virgil", response);
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.processProvinceSResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.processCityResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("country".equals(type)) {
					result = Utility.processCountryResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				Log.d("virgil", "request_result:" + result);
				if (result) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO 自动生成的方法存根
							closeProcessDialog();
							if ("province".equals(type)) {
								queryprovince();
							} else if ("city".equals(type)) {
								queryCity();
							} else if ("country".equals(type)) {
								queryCountry();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception exception) {
				Log.d("virgil", "request_error");
				// TODO 自动生成的方法存根
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO 自动生成的方法存根
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

	}

	public void showProcessDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	public void closeProcessDialog() {
		progressDialog.dismiss();
	}

	@Override
	public void onBackPressed() {
		// TODO 自动生成的方法存根
		if (currentLevel == this.LEVEL_COUNTRY) {
			queryCity();
		} else if (currentLevel == this.LEVEL_CITY) {
			queryprovince();
		} else {
			if(isFromWeatherActivity){
				Intent intent=new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}

}
