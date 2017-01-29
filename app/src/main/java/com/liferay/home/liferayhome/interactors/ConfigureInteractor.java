package com.liferay.home.liferayhome.interactors;

import android.util.Log;
import com.google.gson.Gson;
import com.liferay.home.liferayhome.ConfigureAccountActivity;
import com.liferay.home.liferayhome.interactors.Interactor;
import com.liferay.home.liferayhome.models.Device;
import com.liferay.home.liferayhome.models.User;
import com.liferay.home.liferayhome.utils.PreferencesUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;

public class ConfigureInteractor extends Interactor {

	@Override
	public void run() {
		OkHttpClient client = new OkHttpClient();

		Gson gson = new Gson();
		try {

			String androidId = PreferencesUtil.getDeviceId(ConfigureAccountActivity.this);
			RequestBody user = RequestBody.create(JSON,
				gson.toJson(new User(credential.getSelectedAccountName(), credential.getToken())));

			Request request = new Request.Builder().url(BASE_URL + "/users").post(user).build();
			Response response = client.newCall(request).execute();
			String result = response.body().string();
			Log.d(TAG, result);

			RequestBody device = RequestBody.create(JSON, gson.toJson(new Device(androidId)));
			request = new Request.Builder().url(BASE_URL + "/devices").post(device).build();
			response = client.newCall(request).execute();
			result = response.body().string();
			Log.d(TAG, result);

			EventBus.getDefault().post("Success!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
