package com.liferay.home.liferayhome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ConfigureAccount extends LiferayHomeActivity implements View.OnClickListener {

	public static final String BASE_URL = "http://app.liferay-home.wedeploy.io";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configure_account);

		findViewById(R.id.button_configure).setOnClickListener(this);
	}

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Override
	protected void doSomethingWithAnAccount() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				OkHttpClient client = new OkHttpClient();

				Gson gson = new Gson();
				try {

					String androidId = PreferencesUtil.getDeviceId(ConfigureAccount.this);
					RequestBody user = RequestBody.create(JSON,
						gson.toJson(new User(credential.getSelectedAccountName(), credential.getToken())));

					Request request = new Request.Builder().url(BASE_URL + "/users").post(user).build();
					Response response = client.newCall(request).execute();
					String result = response.body().string();
					Log.d(TAG, result);

					RequestBody device = RequestBody.create(JSON, gson.toJson(new Device("", androidId)));
					request = new Request.Builder().url(BASE_URL + "/devices").post(device).build();
					response = client.newCall(request).execute();
					result = response.body().string();
					Log.d(TAG, result);

					EventBus.getDefault().post("Success!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		chooseAccount();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(String success) {
		View content = findViewById(android.R.id.content);
		Snackbar.make(content, "User registered successfully!", Snackbar.LENGTH_SHORT).show();

		startActivity(new Intent(this, HomeActivity.class));
	}
}
