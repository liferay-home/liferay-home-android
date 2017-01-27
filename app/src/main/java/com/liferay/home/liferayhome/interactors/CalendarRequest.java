package com.liferay.home.liferayhome.interactors;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;

public class CalendarRequest implements Runnable {

	private final GoogleAccountCredential credential;

	public CalendarRequest(GoogleAccountCredential credential) {
		this.credential = credential;
	}

	@Override
	public void run() {
		try {
			HttpTransport transport = AndroidHttp.newCompatibleTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			Calendar mService =
				new Calendar.Builder(transport, jsonFactory, credential).setApplicationName("Liferay Home").build();

			DateTime now = new DateTime(System.currentTimeMillis());

			Events events = mService.events()
				.list("primary")
				.setMaxResults(10)
				.setTimeMin(now)
				.setOrderBy("startTime")
				.setSingleEvents(true)
				.execute();

			EventBus.getDefault().post(events);
		} catch (IOException e) {
			//FIXME !
			e.printStackTrace();
		}
	}
}