package com.liferay.home.liferayhome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import com.liferay.home.liferayhome.R;
import com.liferay.home.liferayhome.interactors.ConfigureInteractor;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class ConfigureAccountActivity extends LiferayHomeActivity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configure_account);

		findViewById(R.id.button_configure).setOnClickListener(this);
	}

	@Override
	protected void doSomethingWithAnAccount() {
		new Thread(new ConfigureInteractor(this, credential)).start();
	}

	@Override
	public void onClick(View v) {
		chooseAccount();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(String success) {
		Log.d(TAG, success);
		Snackbar.make(content, "User registered successfully!", LENGTH_SHORT).show();
		startActivity(new Intent(this, HomeActivity.class));
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(Exception e) {
		Log.e(TAG, e.toString());
		Snackbar.make(content, "Error configuring calendar", LENGTH_SHORT).show();
	}
}
