package com.liferay.home.liferayhome;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import java.util.Arrays;
import org.greenrobot.eventbus.EventBus;

import static com.liferay.home.liferayhome.utils.PreferencesUtil.PREF_ACCOUNT_NAME;
import static com.liferay.home.liferayhome.utils.PreferencesUtil.REQUEST_ACCOUNT_PICKER;
import static com.liferay.home.liferayhome.utils.PreferencesUtil.savePreference;

public abstract class LiferayHomeActivity extends AppCompatActivity {

	public static final String TAG = "LiferayHome";
	private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
	protected GoogleAccountCredential credential;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
			.setBackOff(new ExponentialBackOff());
	}

	protected void onStart() {
		EventBus.getDefault().register(this);
		super.onStart();
	}

	protected void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	protected void chooseAccount() {
		String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
		if (accountName != null) {
			credential.setSelectedAccountName(accountName);
			doSomethingWithAnAccount();
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
					savePreference(this, PREF_ACCOUNT_NAME, accountName);
					credential.setSelectedAccountName(accountName);

					doSomethingWithAnAccount();
				}
			}
		}
	}

	protected abstract void doSomethingWithAnAccount();
}
