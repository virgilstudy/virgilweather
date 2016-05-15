package org.virgil.virgilweather.activity;

import java.util.List;

import org.virgil.virgilweather.db.CoolWeatherDB;
import org.virgil.virgilweather.model.City;
import org.virgil.virgilweather.model.Country;
import org.virgil.virgilweather.model.Province;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;
	private ProgressDialog progressDialog;
	private TextView titleView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private List<String> dataList;
	private CoolWeatherDB coolWeatherDB;
	private List<Province> provinceList;
	private List<City> cityList;
	private List<Country> countryList;

}
