package org.virgil.virgilweather.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.virgil.virgilweather.db.CoolWeatherDB;
import org.virgil.virgilweather.model.City;
import org.virgil.virgilweather.model.Country;
import org.virgil.virgilweather.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utility {
	public synchronized static boolean processProvinceSResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			Log.d("virgil", Arrays.toString(allProvinces));
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");

					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean processCityResponse(
			CoolWeatherDB coolWeatherDB, String response, int provinceId) {
		System.out.println(response);
		if (!TextUtils.isEmpty(response)) {
			String[] allcity = response.split(",");
			if (allcity != null && allcity.length > 0) {
				for (String c : allcity) {
					String[] array = c.split("\\|");
					Log.d("virgil", "city:" + Arrays.toString(array));
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
			}
			return true;
		}
		return false;
	}

	public synchronized static boolean processCountryResponse(
			CoolWeatherDB coolWeatherDB, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCountry = response.split(",");
			for (String c : allCountry) {
				String[] array = c.split("\\|");
				Country country = new Country();
				country.setCountrCode(array[0]);
				country.setCountryName(array[1]);
				country.setCityId(cityId);
				coolWeatherDB.saveCountry(country);
			}
			return true;
		}
		return false;
	}

	public static void processWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			Log.d("virgil", response);
			JSONObject info = jsonObject.getJSONObject("weatherinfo");
			String cityName = info.getString("city");
			String weatherId = info.getString("cityid");
			String temp1 = info.getString("temp1");
			String temp2 = info.getString("temp2");
			String weatherDesp = info.getString("weather");
			String publishTime = info.getString("ptime");
			saveWeatherInfo(context, cityName, weatherId, temp1, temp2,
					weatherDesp, publishTime);
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_time", sdf.format(new Date()));
		editor.commit();
	}

}
