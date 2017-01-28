package com.liferay.home.liferayhome;

import android.os.Bundle;
import android.util.Log;
import com.google.api.services.calendar.model.Events;
import com.liferay.home.liferayhome.interactors.CalendarRequest;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends LiferayHomeActivity {

	public static final String TAG = "LiferayHome";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getEventsFromCalendarAPI();
	}

	private void getEventsFromCalendarAPI() {
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

	@Override
	protected void doSomethingWithAnAccount() {
		requestsCalendarEvents();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(Events events) {
		Log.e("LiferayHome", events.getSummary());
	}
}
