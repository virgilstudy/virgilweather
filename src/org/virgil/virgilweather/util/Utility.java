package org.virgil.virgilweather.util;

import org.virgil.virgilweather.db.CoolWeatherDB;
import org.virgil.virgilweather.model.City;
import org.virgil.virgilweather.model.Country;
import org.virgil.virgilweather.model.Province;
import org.w3c.dom.Text;

import android.R.bool;
import android.text.TextUtils;

public class Utility {
	public synchronized static boolean processProvinceSResponse(CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
					return true;
				}
			}
		}
		return false;
	}

	public synchronized static boolean processCityResponse(CoolWeatherDB coolWeatherDB, String response,
			int provinceId) {
		if (TextUtils.isEmpty(response)) {
			String[] allcity = response.split(",");
			if (allcity != null && allcity.length > 0) {
				for (String c : allcity) {
					String[] array = c.split("//|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
					return true;
				}
			}
			return true;
		}
		return false;
	}

	public synchronized static boolean processCountryResponse(CoolWeatherDB coolWeatherDB, String response,
			int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCountry = response.split(",");
			for (String c : allCountry) {
				String[] array = c.split("\\|");
				Country country = new Country();
				country.setCountrCode(array[0]);
				country.setCountryName(array[1]);
				country.setCityId(cityId);
				return true;
			}
		}
		return false;
	}
}
