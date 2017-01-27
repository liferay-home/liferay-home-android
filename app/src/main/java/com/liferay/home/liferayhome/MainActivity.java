package com.liferay.home.liferayhome;

import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;
import com.liferay.home.liferayhome.interactors.CalendarRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity
	implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
	ResultCallback<Status> {

	private static final String LOCATION_KEY = "LOCATION_KEY";
	private static final String LAST_UPDATED_TIME_STRING_KEY = "LAST_UPDATED_TIME_STRING_KEY";
	public static final String TAG = "LiferayHome";
	private GoogleApiClient googleApiClient;
	private Location lastLocation;
	private Date lastUpdateTime;

	GoogleAccountCredential credential;

	static final int REQUEST_ACCOUNT_PICKER = 1000;

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

		//getEventsFromCalendarAPI();
	}

	private void createGeofence() {
		List<Geofence> geofences = new ArrayList<>();
		geofences.add(new Geofence.Builder().setRequestId("0")
			.setCircularRegion(33.98436373, -117.39578247, 100000)
			.setExpirationDuration(Geofence.NEVER_EXPIRE)
			.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
			.build());

		GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
		builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
		builder.addGeofences(geofences);
		GeofencingRequest geofencingRequest = builder.build();

		Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
			FLAG_UPDATE_CURRENT);

		LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
			.setResultCallback(this);
	}

	private void getEventsFromCalendarAPI() {
		credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
			.setBackOff(new ExponentialBackOff());
		requestsCalendarEvents();
	}

	private void requestsCalendarEvents() {
		//FIXME check status of google play services
		if (credential.getSelectedAccountName() == null) {
			chooseAccount();
		} else {
			CalendarRequest calendarRequest = new CalendarRequest(credential);
			new Thread(calendarRequest).start();
		}
	}

	private void chooseAccount() {
		String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
		if (accountName != null) {
			credential.setSelectedAccountName(accountName);
			requestsCalendarEvents();
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
					savePreference(accountName);
					credential.setSelectedAccountName(accountName);
					requestsCalendarEvents();
				}
			}
		}
	}

	private void savePreference(String accountName) {
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_ACCOUNT_NAME, accountName);
		editor.apply();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(Events events) {
		Log.e("LiferayHome", events.getSummary());
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
		savedInstanceState.putLong(LAST_UPDATED_TIME_STRING_KEY, lastUpdateTime.getTime());
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		lastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
		lastUpdateTime = new Date(savedInstanceState.getLong(LAST_UPDATED_TIME_STRING_KEY));
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
		if (lastLocation != null) {
			showLocation();
		}

		createGeofence();

		requestLocationUpdates();
	}

	private void showLocation() {
		if (lastLocation != null) {
			Log.e("LiferayHome", lastLocation.toString());
			TextView lastUpdateTime = (TextView) findViewById(R.id.last_update_time);
			lastUpdateTime.setText(new Date().toString());

			TextView latitude = (TextView) findViewById(R.id.latitude);
			latitude.setText(String.valueOf(lastLocation.getLatitude()));

			TextView longitude = (TextView) findViewById(R.id.longitude);
			longitude.setText(String.valueOf(lastLocation.getLongitude()));
		}
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
		showLocation();
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

	@Override
	public void onResult(@NonNull Status status) {
		Log.e(TAG, status.toString());
	}

	//	LocationServices.GeofencingApi.removeGeofences(
	//	mGoogleApiClient,
	//	// This is the same pending intent that was used in addGeofences().
	//	getGeofencePendingIntent()
	//    ).setResultCallback(this); // Result processed in onResult().
	//}
}
