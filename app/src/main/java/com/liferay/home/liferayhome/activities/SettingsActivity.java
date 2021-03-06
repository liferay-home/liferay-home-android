package com.liferay.home.liferayhome.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.liferay.home.liferayhome.R;
import com.liferay.home.liferayhome.interactors.CalendarInteractor;
import com.liferay.home.liferayhome.utils.PreferencesUtil;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.liferay.home.liferayhome.utils.PreferencesUtil.PREF_DEVICE_NAME;
import static com.liferay.home.liferayhome.utils.PreferencesUtil.getPreference;
import static com.liferay.home.liferayhome.utils.PreferencesUtil.savePreference;

public class SettingsActivity extends LiferayHomeActivity implements View.OnClickListener {

	public static final String CELSIUS = "CELSIUS";

	private TextView celsius;
	private TextView fahrenheit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		findViewById(R.id.connect_phone).setOnClickListener(this);
		findViewById(R.id.calendar).setOnClickListener(this);

		celsius = (TextView) findViewById(R.id.celsius);
		celsius.setOnClickListener(this);

		fahrenheit = (TextView) findViewById(R.id.fahrenheit);
		fahrenheit.setOnClickListener(this);

		saveDeviceName();

		if (getPreference(this, CELSIUS)) {
			celsius();
		} else {
			fahrenheit();
		}
	}

	private void saveDeviceName() {
		final EditText deviceNameEdit = (EditText) findViewById(R.id.device_name_edit);

		String deviceName = PreferencesUtil.getStrPreference(this, PREF_DEVICE_NAME);
		if (deviceName != null) {
			deviceNameEdit.setText(deviceName);
		}

		deviceNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					PreferencesUtil.savePreference(SettingsActivity.this, PREF_DEVICE_NAME,
						deviceNameEdit.getText().toString());
				}
			}
		});
	}

	@Override
	protected void doSomethingWithAnAccount() {
		new Thread(new CalendarInteractor(credential)).start();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(Events events) {
		Log.d(TAG, events.getSummary());
		for (Event event : events.getItems()) {
			Log.d(TAG, "Event: " + event.getSummary());
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.celsius) {
			celsius();
		} else if (v.getId() == R.id.fahrenheit) {
			fahrenheit();
		} else if (v.getId() == R.id.calendar) {
			if (credential.getSelectedAccountName() == null) {
				chooseAccount();
			} else {
				doSomethingWithAnAccount();
			}
		}
	}

	private void fahrenheit() {
		celsius.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
		fahrenheit.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
		savePreference(this, CELSIUS, false);
	}

	private void celsius() {
		celsius.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
		fahrenheit.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
		savePreference(this, CELSIUS, true);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(String result) {
		Log.d(TAG, result);
	}
}
