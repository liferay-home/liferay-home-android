package com.liferay.home.liferayhome.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.liferay.home.liferayhome.R;
import com.liferay.home.liferayhome.interactors.LocationInteractor;
import com.liferay.home.liferayhome.interactors.SensorInteractor;
import com.liferay.home.liferayhome.utils.PreferencesUtil;
import com.triggertrap.seekarc.SeekArc;
import java.util.Date;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.liferay.home.liferayhome.utils.PreferencesUtil.PREF_DEVICE_NAME;

public class HomeActivity extends LiferayHomeActivity
	implements NavigationView.OnNavigationItemSelectedListener, LocationListener, GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener, SeekArc.OnSeekArcChangeListener {

	private static final String LOCATION_KEY = "LOCATION_KEY";
	private static final String LAST_UPDATED_TIME_STRING_KEY = "LAST_UPDATED_TIME_STRING_KEY";
	private GoogleApiClient googleApiClient;
	private Location lastLocation;
	private Date lastUpdateTime;
	private Double progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
			R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		TextView deviceNameText = (TextView) findViewById(R.id.device_name_text);
		TextView deviceNameHeader = (TextView) navigationView.getHeaderView(0).findViewById(R.id.device_name_header);
		String deviceName = PreferencesUtil.getStrPreference(this, PREF_DEVICE_NAME);
		if (deviceName != null) {
			deviceNameHeader.setText(deviceName);
			deviceNameText.setText(deviceName);
		}

		if (googleApiClient == null) {
			googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		new Thread(new SensorInteractor()).start();
	}

	@Override
	protected void onStart() {
		googleApiClient.connect();
		super.onStart();
	}

	@Override
	protected void onStop() {
		googleApiClient.disconnect();
		super.onStop();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(Double sensorDatas) {

		progress = sensorDatas;
		refreshTemperature(progress);

		SeekArc seekArc = (SeekArc) findViewById(R.id.seekArc);

		seekArc.setArcColor(ContextCompat.getColor(this, R.color.colorAccent));
		seekArc.setArcWidth(30);
		seekArc.setProgressColor(ContextCompat.getColor(this, R.color.colorPrimary));
		seekArc.setOnSeekArcChangeListener(this);
		seekArc.setProgress(progress.intValue());
		seekArc.setProgressWidth(30);
	}

	@Override
	protected void doSomethingWithAnAccount() {
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		if (lastLocation != null) {
			savedInstanceState.putParcelable(LOCATION_KEY, lastLocation);
		}
		if (lastUpdateTime != null) {
			savedInstanceState.putLong(LAST_UPDATED_TIME_STRING_KEY, lastUpdateTime.getTime());
		}
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
		requestLocationUpdates();
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
	}

	@Override
	public void onLocationChanged(final Location location) {

		if (location != null && lastLocation != null && (location.getLatitude() != lastLocation.getLatitude()
			|| location.getLongitude() != lastLocation.getLongitude())) {

			lastLocation = location;
			lastUpdateTime = new Date();

			new Thread(new LocationInteractor(lastLocation)).start();
		}
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
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.set_your_home) {
			Intent intent = new Intent(this, MapsActivity.class);
			intent.putExtra("position", lastLocation);
			startActivity(intent);
		} else if (id == R.id.home) {
			Intent intent = new Intent(this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			overridePendingTransition(0, 0);
		} else {
			startActivity(new Intent(this, SettingsActivity.class));
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public void onProgressChanged(SeekArc seekArc, int i, boolean userChanged) {
		if (userChanged) {
			Log.d(TAG, String.valueOf(i));
			progress = (double) i;
			refreshTemperature(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekArc seekArc) {
	}

	@Override
	public void onStopTrackingTouch(SeekArc seekArc) {
	}

	private void refreshTemperature(Double value) {
		boolean celsius = PreferencesUtil.getPreference(this, SettingsActivity.CELSIUS);
		if (!celsius) {
			value = value * 1.8 + 32;
		}

		TextView temperature = (TextView) findViewById(R.id.temperature1);
		temperature.setText(String.valueOf(((int) (value * 100.0) / 100.0)) + "º");
	}
}
