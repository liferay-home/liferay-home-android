package com.liferay.home.liferayhome;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService {

	public GeofenceTransitionsIntentService() {
		super("GeofenceTransitionsIntentService");
	}

	protected void onHandleIntent(Intent intent) {
		Log.e("LiferayHome", "Event!");
		GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
		if (geofencingEvent.hasError()) {

			//FIXME get the error
			Log.e("LiferayHome", String.valueOf(geofencingEvent.getErrorCode()));
			return;
		}

		int geofenceTransition = geofencingEvent.getGeofenceTransition();
		if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
			|| geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

			List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

			Log.i(MainActivity.TAG, triggeringGeofences.toString());
		} else {
			Log.e(MainActivity.TAG, "Error!");
		}
	}
}
