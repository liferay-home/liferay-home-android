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
		try {
			OkHttpClient client = new OkHttpClient();

			Request request = new Request.Builder().url(BASE_URL + "/sensor-data").get().build();
			Response response = client.newCall(request).execute();
			String result = response.body().string();

			JsonArray jsonArray = parseToArray(result);

			jsonArray = filterBy(jsonArray, "type", "TEMPERATURE");
			double value =
				jsonArray.size() > 0 ? jsonArray.get(jsonArray.size() - 1).getAsJsonObject().get("value").getAsDouble()
					: 0;
			EventBus.getDefault().post(value);
		} catch (Exception e) {
			EventBus.getDefault().post(e);
		}
	}

	private JsonArray filterBy(JsonArray jsonArray, String type, String temperature) {
		JsonArray array = new JsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			if (temperature.equals(jsonArray.get(i).getAsJsonObject().get(type).getAsString())) {
				array.add(jsonArray.get(i));
			}
		}
		return array;
	}

	private JsonArray parseToArray(String result) {
		JsonElement jsonElement = new JsonParser().parse(result);
		return jsonElement.getAsJsonObject().get("_embedded").getAsJsonObject().get("sensorDatas").getAsJsonArray();
	}
}
