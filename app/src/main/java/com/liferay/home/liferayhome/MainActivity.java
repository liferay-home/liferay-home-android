package com.liferay.home.liferayhome;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.util.Date;

public class MainActivity extends AppCompatActivity
	implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private static final String LOCATION_KEY = "LOCATION_KEY";
	private static final String LAST_UPDATED_TIME_STRING_KEY = "LAST_UPDATED_TIME_STRING_KEY";
	private GoogleApiClient googleApiClient;
	private Location lastLocation;
	private Date lastUpdateTime;

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
	}

	protected void onStart() {
		googleApiClient.connect();
		super.onStart();
	}

	protected void onStop() {
		googleApiClient.disconnect();
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
			Log.e("LiferayHome", lastLocation.toString());
		}

		requestLocationUpdates();
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
