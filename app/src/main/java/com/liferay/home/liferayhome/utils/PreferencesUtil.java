package com.liferay.home.liferayhome.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

public class PreferencesUtil {

	public static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";
	public static final String PREF_DEVICE_NAME = "PREF_DEVICE_NAME";
	public static final int REQUEST_ACCOUNT_PICKER = 1000;
	private static final String LIFERAY_HOME_DB = "LIFERAY_HOME_DB";

	@SuppressLint("HardwareIds")
	public static String getDeviceId(Context context) {
		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}

	public static void savePreference(Context context, String name, String value) {
		SharedPreferences.Editor editor = getPreferences(context).edit();
		editor.putString(name, value);
		editor.apply();
	}

	public static void savePreference(Context context, String name, boolean value) {
		SharedPreferences.Editor editor = getPreferences(context).edit();
		editor.putBoolean(name, value);
		editor.apply();
	}

	public static boolean getPreference(Context context, String name) {
		return getPreferences(context).getBoolean(name, false);
	}

	public static String getStrPreference(Context context, String name) {
		return getPreferences(context).getString(name, null);
	}

	private static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(LIFERAY_HOME_DB, Context.MODE_PRIVATE);
	}
}
