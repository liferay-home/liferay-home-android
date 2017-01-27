package com.liferay.home.liferayhome;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;
import com.liferay.home.liferayhome.interactors.CalendarRequest;
import java.util.Arrays;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

	public static final String TAG = "LiferayHome";

	GoogleAccountCredential credential;

	static final int REQUEST_ACCOUNT_PICKER = 1000;

	private static final String PREF_ACCOUNT_NAME = "accountName";
	private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getEventsFromCalendarAPI();
	}

	private void getEventsFromCalendarAPI() {
		credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
			.setBackOff(new ExponentialBackOff());
		requestsCalendarEvents();
	}

	private void requestsCalendarEvents() {
		//FIXME check status of google play services
		if (credential.getSelectedAccountName() == null) {
			chooseAccount();
		} else {
			CalendarRequest calendarRequest = new CalendarRequest(credential);
			new Thread(calendarRequest).start();
		}
	}

	private void chooseAccount() {
		String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
		if (accountName != null) {
			credential.setSelectedAccountName(accountName);
			requestsCalendarEvents();
		} else {
			startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ACCOUNT_PICKER) {
			if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
				String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					savePreference(accountName);
					credential.setSelectedAccountName(accountName);
					requestsCalendarEvents();
				}
			}
		}
	}

	private void savePreference(String accountName) {
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_ACCOUNT_NAME, accountName);
		editor.apply();
	}

	protected void onStart() {
		EventBus.getDefault().register(this);
		super.onStart();
	}

	protected void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(Events events) {
		Log.e("LiferayHome", events.getSummary());
	}
}
