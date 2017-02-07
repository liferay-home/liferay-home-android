package com.liferay.home.liferayhome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import com.liferay.home.liferayhome.interactors.ConfigureInteractor;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

		View content = findViewById(android.R.id.content);
		Snackbar.make(content, "User registered successfully!", Snackbar.LENGTH_SHORT).show();

		startActivity(new Intent(this, HomeActivity.class));
	}
}
