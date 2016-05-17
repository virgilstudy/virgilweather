package org.virgil.virgilweather.activity;

import java.util.Arrays;

import org.virgil.virgilweather.R;
import org.virgil.virgilweather.service.AutoUpdateService;
import org.virgil.virgilweather.util.HttpCallBackListenser;
import org.virgil.virgilweather.util.HttpUtil;
import org.virgil.virgilweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1;
	private TextView temp2;
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_id);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshButton = (Button) findViewById(R.id.refresh_weather);
		String countryCode = getIntent().getStringExtra("coutry_code");
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		if (!TextUtils.isEmpty(countryCode)) {
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		} else {
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = preferences.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;

		default:
			break;
		}
	}

	public void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countryCode + ".xml";
		queryFromServer(address, "countryCode");
	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallBackListenser() {

			@Override
			public void onFinish(String response) {
				// TODO 自动生成的方法存根
				if ("countryCode".equals(type)) {
					String[] array = response.split("\\|");
					Log.d("virgil",Arrays.toString(array));
					if (array != null && array.length == 2) {
						queryWeatherInfo(array[1]);
					}
				} else if ("weatherCode".equals(type)) {
					Utility.processWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO 自动生成的方法存根
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception exception) {
				// TODO 自动生成的方法存根
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO 自动生成的方法存根
						publishText.setText("Sorry,同步失败！");
					}
				});
			}
		});
	}

	public void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1.setText(prefs.getString("temp1", ""));
		temp2.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		System.out.println(prefs.getString("current_date", ""));
		currentDateText.setText(prefs.getString("current_time", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent=new Intent(this,AutoUpdateService.class);
		startService(intent);
	}

}
