package com.liferay.home.liferayhome;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity
	implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private static final String LOCATION_KEY = "LOCATION_KEY";
	private static final String LAST_UPDATED_TIME_STRING_KEY = "LAST_UPDATED_TIME_STRING_KEY";
	private GoogleApiClient googleApiClient;
	private Location lastLocation;
	private Date lastUpdateTime;

	//889713454476-3jm4iblm7sneq0n5fm5krn71dflin8dn.apps.googleusercontent.com

	GoogleAccountCredential credential;

	static final int REQUEST_ACCOUNT_PICKER = 1000;
	static final int REQUEST_AUTHORIZATION = 1001;
	static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

	private static final String PREF_ACCOUNT_NAME = "accountName";
	private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (googleApiClient == null) {
			googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		}

		credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
			.setBackOff(new ExponentialBackOff());
		getResultsFromApi();

	}

	/**
	 * Attempt to call the API, after verifying that all the preconditions are
	 * satisfied. The preconditions are: Google Play Services installed, an
	 * account was selected and the device currently has online access. If any
	 * of the preconditions are not satisfied, the app will prompt the user as
	 * appropriate.
	 */
	private void getResultsFromApi() {
		if (!isGooglePlayServicesAvailable()) {
			acquireGooglePlayServices();
		} else if (credential.getSelectedAccountName() == null) {
			chooseAccount();
		} else {
			new Thread(new CalendarRequest()).start();
		}
	}

	private boolean isGooglePlayServicesAvailable() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
		return connectionStatusCode == ConnectionResult.SUCCESS;
	}

	/**
	 * Attempts to set the account used with the API credentials. If an account
	 * name was previously saved it will use that one; otherwise an account
	 * picker dialog will be shown to the user. Note that the setting the
	 * account to use with the credentials object requires the app to have the
	 * GET_ACCOUNTS permission, which is requested here if it is not already
	 * present. The AfterPermissionGranted annotation indicates that this
	 * function will be rerun automatically whenever the GET_ACCOUNTS permission
	 * is granted.
	 */
	private void chooseAccount() {
		String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
		if (accountName != null) {
			credential.setSelectedAccountName(accountName);
			getResultsFromApi();
		} else {
			// Start a dialog from which the user can choose an account
			startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		}
	}

	/**
	 * Called when an activity launched here (specifically, AccountPicker
	 * and authorization) exits, giving you the requestCode you started it with,
	 * the resultCode it returned, and any additional data from it.
	 *
	 * @param requestCode code indicating which activity result is incoming.
	 * @param resultCode code indicating the result of the incoming
	 * activity result.
	 * @param data Intent (containing result data) returned by incoming
	 * activity result.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_GOOGLE_PLAY_SERVICES:
				getResultsFromApi();
				break;
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
					String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null) {
						SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(PREF_ACCOUNT_NAME, accountName);
						editor.apply();
						credential.setSelectedAccountName(accountName);
						getResultsFromApi();
					}
				}
				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == RESULT_OK) {
					getResultsFromApi();
				}
				break;
		}
	}

	/**
	 * Attempt to resolve a missing, out-of-date, invalid or disabled Google
	 * Play Services installation via a user dialog, if possible.
	 */
	private void acquireGooglePlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
		}
	}

	/**
	 * Display an error dialog showing that Google Play Services is missing
	 * or out of date.
	 *
	 * @param connectionStatusCode code describing the presence (or lack of)
	 * Google Play Services on this device.
	 */
	void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		Dialog dialog =
			apiAvailability.getErrorDialog(MainActivity.this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
		dialog.show();
	}

	private class CalendarRequest implements Runnable {

		@Override
		public void run() {
			try {
				HttpTransport transport = AndroidHttp.newCompatibleTransport();
				JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
				com.google.api.services.calendar.Calendar mService =
					new com.google.api.services.calendar.Calendar.Builder(transport, jsonFactory,
						credential).setApplicationName("Google Calendar API Android Quickstart").build();

				DateTime now = new DateTime(System.currentTimeMillis());
				List<String> eventStrings = new ArrayList<>();

				Events events = mService.events()
					.list("primary")
					.setMaxResults(10)
					.setTimeMin(now)
					.setOrderBy("startTime")
					.setSingleEvents(true)
					.execute();

				List<Event> items = events.getItems();

				for (Event event : items) {
					DateTime start = event.getStart().getDateTime();
					if (start == null) {
						// All-day events don't have start times, so just use
						// the start date.
						start = event.getStart().getDate();
					}
					eventStrings.add(String.format("%s (%s)", event.getSummary(), start));
				}
				EventBus.getDefault().post(eventStrings);
			} catch (IOException e) {
				//FIXME !
				e.printStackTrace();
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(List<String> events) {
		for (String event : events) {
			Log.e("LiferayHome", event);
		}
	}

	protected void onStart() {
		googleApiClient.connect();
		EventBus.getDefault().register(this);
		super.onStart();
	}

	protected void onStop() {
		googleApiClient.disconnect();
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable(LOCATION_KEY, lastLocation);
		savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, lastUpdateTime.toString());
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
			lastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
		}

		// Update the value of mLastUpdateTime from the Bundle and update the UI.
		if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
			lastUpdateTime = new Date(savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY));
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
		if (lastLocation != null) {
			showLocation();
		}

		requestLocationUpdates();
	}

	private void showLocation() {
		//if (lastLocation != null) {
		//	Log.e("LiferayHome", lastLocation.toString());
		//	TextView lastUpdateTime = (TextView) findViewById(R.id.last_update_time);
		//	lastUpdateTime.setText(new Date().toString());
		//
		//	TextView latitude = (TextView) findViewById(R.id.latitude);
		//	latitude.setText(String.valueOf(lastLocation.getLatitude()));
		//
		//	TextView longitude = (TextView) findViewById(R.id.longitude);
		//	longitude.setText(String.valueOf(lastLocation.getLongitude()));
		//}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void onLocationChanged(Location location) {
		lastLocation = location;
		lastUpdateTime = new Date();
		//showLocation();
	}

	private void requestLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);
	}

	private LocationRequest createLocationRequest() {
		LocationRequest locationRequest = new LocationRequest();
		locationRequest.setInterval(10000);
		locationRequest.setFastestInterval(5000);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		return locationRequest;
	}
}
