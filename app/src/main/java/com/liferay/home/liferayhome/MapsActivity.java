package com.liferay.home.liferayhome;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

	private Location position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		position = getIntent().getParcelableExtra("position");

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

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
}
