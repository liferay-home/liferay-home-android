package com.liferay.home.liferayhome;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.triggertrap.seekarc.SeekArc;
import java.util.Date;

public class HomeActivity extends AppCompatActivity
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
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

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

		progress = 60D;
		refreshTemperature(progress);

		SeekArc seekArc = (SeekArc) findViewById(R.id.seekArc);
		seekArc.setArcColor(getResources().getColor(R.color.colorPrimary));
		seekArc.setArcWidth(30);
		seekArc.setProgressColor(getResources().getColor(R.color.colorPrimaryDark));
		seekArc.setOnSeekArcChangeListener(this);
		seekArc.setProgress(progress.intValue());
		seekArc.setProgressWidth(30);
	}

	private void refreshTemperature(Double value) {
		boolean celsius = PreferencesUtil.getPreference(this, SettingsActivity.CELSIUS);
		if (!celsius) {
			value = value * 1.8 + 32;
		}

		TextView temperature = (TextView) findViewById(R.id.temperature1);
		temperature.setText(String.valueOf(value.intValue()));
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		//if (id == R.id.action_settings) {
		//	return true;
		//}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
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
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
		Log.d(MainActivity.TAG, String.valueOf(i));
		progress = (double) i;
		refreshTemperature(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekArc seekArc) {

	}

	@Override
	public void onStopTrackingTouch(SeekArc seekArc) {

	}
}
