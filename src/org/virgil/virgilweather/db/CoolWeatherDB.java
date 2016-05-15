package org.virgil.virgilweather.db;

import java.util.ArrayList;
import java.util.List;

import org.virgil.virgilweather.model.City;
import org.virgil.virgilweather.model.Country;
import org.virgil.virgilweather.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	public static final String DB_NAME = "cool_weather";
	public static final int VERSION = 1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	public List<Province> loadProvince() {
		List<Province> list = new ArrayList<>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("provice_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}

	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("provice_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}

	public List<City> loadCitys(int provinceId) {
		List<City> list = new ArrayList<>();
		Cursor cursor = db.query("City", null, "province_id=?", new String[] { String.valueOf(provinceId) }, null, null,
				null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}

	public void saveCountry(Country country) {
		if (country != null) {
			ContentValues values = new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountrCode());
			values.put("city_id", country.getCityId());
			db.insert("Country", null, values);
		}
	}

	public List<Country> loadCountry(int cityId) {
		List<Country> list = new ArrayList<>();
		Cursor cursor = db.query("Country", null, "city_id=?", new String[] { String.valueOf(cityId) }, null, null,
				null);
		if (cursor.moveToFirst()) {
			do {
				Country country = new Country();
				country.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountrCode(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCityId(cityId);
				list.add(country);
			} while (cursor.moveToNext());
		}
		return list;
	}
}
