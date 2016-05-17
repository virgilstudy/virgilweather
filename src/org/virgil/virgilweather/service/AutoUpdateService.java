package org.virgil.virgilweather.service;

import org.virgil.virgilweather.util.HttpCallBackListenser;
import org.virgil.virgilweather.util.HttpUtil;
import org.virgil.virgilweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO 自动生成的方法存根
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO 自动生成的方法存根
				updateWeather();
			}
		}).start();
		AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);	
		int anHour=2*60*60*1000;
		long trigger=SystemClock.elapsedRealtime()+anHour;
		Intent i=new Intent(this,AutoUpdateService.class);
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	public void updateWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallBackListenser() {

			@Override
			public void onFinish(String response) {
				// TODO 自动生成的方法存根
				Utility.processWeatherResponse(AutoUpdateService.this, response);
			}

			@Override
			public void onError(Exception exception) {
				// TODO 自动生成的方法存根
				exception.printStackTrace();
			}
		});
	}

}
