package com.liferay.home.liferayhome;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {

	public static final String LIFERAY_HOME = "LIFERAY_HOME";
	public static final String PREF_ACCOUNT_NAME = "accountName";
	public static final int REQUEST_ACCOUNT_PICKER = 1000;

	public static void savePreference(Context context, String name, String value) {
		SharedPreferences.Editor editor = getEditor(context);
		editor.putString(name, value);
		editor.apply();
	}

	public static void savePreference(Context context, String name, boolean value) {
		SharedPreferences.Editor editor = getEditor(context);
		editor.putBoolean(name, value);
		editor.apply();
	}

	private static SharedPreferences.Editor getEditor(Context context) {
		SharedPreferences settings = context.getSharedPreferences(LIFERAY_HOME, Context.MODE_PRIVATE);
		return settings.edit();
	}

	public static boolean getPreference(Context context, String name) {
		SharedPreferences preferences = context.getSharedPreferences(LIFERAY_HOME, Context.MODE_PRIVATE);
		return preferences.getBoolean(name, false);
	}
}
