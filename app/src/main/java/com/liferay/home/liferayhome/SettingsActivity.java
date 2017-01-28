package com.liferay.home.liferayhome;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.liferay.home.liferayhome.interactors.CalendarRequest;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

		Button connectCalendar = (Button) findViewById(R.id.calendar);
		connectCalendar.setOnClickListener(this);

		celsius = (TextView) findViewById(R.id.celsius);
		celsius.setOnClickListener(this);

		fahrenheit = (TextView) findViewById(R.id.fahrenheit);
		fahrenheit.setOnClickListener(this);

		final EditText piName = (EditText) findViewById(R.id.pi_name);

		String piNameValue = PreferencesUtil.getStrPreference(this, "piname");
		if (piNameValue != null) {
			piName.setText(piNameValue);
		}


		piName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					PreferencesUtil.savePreference(SettingsActivity.this, "piname", piName.getText().toString());
				}
			}
		});

		if (getPreference(this, CELSIUS)) {
			celsius();
		} else {
			fahrenheit();
		}
	}

	@Override
	protected void doSomethingWithAnAccount() {
		CalendarRequest calendarRequest = new CalendarRequest(credential);
		new Thread(calendarRequest).start();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(Events events) {
		Log.e(MainActivity.TAG, events.getSummary());
		for (Event event : events.getItems()) {
			Log.d(MainActivity.TAG, "Event: " + event.getSummary());
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
