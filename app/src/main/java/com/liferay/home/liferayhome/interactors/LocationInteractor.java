package com.liferay.home.liferayhome.interactors;

import android.location.Location;
import com.google.gson.Gson;
import com.liferay.home.liferayhome.models.PhoneLocation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;

public class LocationInteractor extends Interactor {

	private final Location lastLocation;

	public LocationInteractor(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public void run() {
		try {
			OkHttpClient client = new OkHttpClient();
			Gson gson = new Gson();

			String content = gson.toJson(new PhoneLocation(lastLocation.getLongitude(), lastLocation.getLatitude()));
			RequestBody phoneLocation = RequestBody.create(JSON, content);

			Request request = new Request.Builder().url(BASE_URL + "/locations").post(phoneLocation).build();
			Response response = client.newCall(request).execute();
			EventBus.getDefault().post(response.body());
		} catch (Exception e) {
			EventBus.getDefault().post(e);
		}
	}
}
