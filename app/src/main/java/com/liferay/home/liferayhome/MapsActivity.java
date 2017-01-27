package com.liferay.home.liferayhome;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class MapsActivity extends FragmentActivity
	implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, ResultCallback<Status>,
	GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private Location position;
	private GoogleApiClient googleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		position = getIntent().getParcelableExtra("position");
		Log.d(MainActivity.TAG, String.valueOf(position));

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		if (googleApiClient == null) {
			googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		}
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

	protected void onStart() {
		googleApiClient.connect();
		super.onStart();
	}

	protected void onStop() {
		googleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		createGeofence();
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onResult(@NonNull Status status) {
		Log.e(MainActivity.TAG, status.toString());
	}

	//	LocationServices.GeofencingApi.removeGeofences(
	//	mGoogleApiClient,
	//	// This is the same pending intent that was used in addGeofences().
	//	getGeofencePendingIntent()
	//    ).setResultCallback(this); // Result processed in onResult().
	//}

	@Override
	public void onMapReady(GoogleMap googleMap) {

		LatLng latLng =
			position == null ? new LatLng(33.88, -117) : new LatLng(position.getLatitude(), position.getLongitude());
		MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Home").draggable(true);
		googleMap.addMarker(markerOptions);
		googleMap.setOnMarkerDragListener(this);
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	}

	@Override
	public void onMarkerDragStart(Marker marker) {

	}

	@Override
	public void onMarkerDrag(Marker marker) {

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}
}
