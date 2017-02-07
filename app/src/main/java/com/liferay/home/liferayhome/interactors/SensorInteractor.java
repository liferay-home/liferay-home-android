package com.liferay.home.liferayhome.interactors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;

public class SensorInteractor extends Interactor {

	@Override
	public void run() {
		OkHttpClient client = new OkHttpClient();
		try {
			Request request = new Request.Builder().url(BASE_URL + "/sensor-data").get().build();
			Response response = client.newCall(request).execute();
			String result = response.body().string();

			JsonElement jsonElement = new JsonParser().parse(result);
			JsonArray jsonArray =
				jsonElement.getAsJsonObject().get("_embedded").getAsJsonObject().get("sensorDatas").getAsJsonArray();

			for (int i = jsonArray.size() - 1; i > 0; i--) {
				if (jsonArray.get(i).getAsJsonObject().get("type").getAsString().equals("TEMPERATURE")) {
					EventBus.getDefault().post(jsonArray.get(i).getAsJsonObject().get("value").getAsDouble());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
