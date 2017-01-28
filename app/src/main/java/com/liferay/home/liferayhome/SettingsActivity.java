package com.liferay.home.liferayhome;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.liferay.home.liferayhome.PreferencesUtil.PREF_ACCOUNT_NAME;
import static com.liferay.home.liferayhome.PreferencesUtil.REQUEST_ACCOUNT_PICKER;
import static com.liferay.home.liferayhome.PreferencesUtil.getPreference;
import static com.liferay.home.liferayhome.PreferencesUtil.savePreference;

public class SettingsActivity extends LiferayHomeActivity implements View.OnClickListener {

	public static final String CELSIUS = "CELSIUS";

	private TextView celsius;
	private TextView fahrenheit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		Button connectPhone = (Button) findViewById(R.id.connect_phone);
		connectPhone.setOnClickListener(this);

		celsius = (TextView) findViewById(R.id.celsius);
		celsius.setOnClickListener(this);

		fahrenheit = (TextView) findViewById(R.id.fahrenheit);
		fahrenheit.setOnClickListener(this);

		if (getPreference(this, CELSIUS)) {
			celsius();
		} else {
			fahrenheit();
		}
	}

	@Override
	protected void doSomethingWithAnAccount() {

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.celsius) {
			celsius();
		} else if (v.getId() == R.id.fahrenheit) {
			fahrenheit();
		} else {
			if (credential.getSelectedAccountName() == null) {
				chooseAccount();
			}
		}
	}

	private void fahrenheit() {
		celsius.setTextColor(getResources().getColor(R.color.colorAccent));
		fahrenheit.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
		savePreference(this, CELSIUS, false);
	}

	private void celsius() {
		celsius.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
		fahrenheit.setTextColor(getResources().getColor(R.color.colorAccent));
		savePreference(this, CELSIUS, true);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(String success) {
	}
}
