package com.liferay.home.liferayhome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		String accountName = PreferencesUtil.getStrPreference(this, PreferencesUtil.PREF_ACCOUNT_NAME);
		final Class clasz = accountName == null ? ConfigureAccount.class : HomeActivity.class;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this, clasz));
			}
		}, 1500L);
	}
}
