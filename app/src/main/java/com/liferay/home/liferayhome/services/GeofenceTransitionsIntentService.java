package com.liferay.home.liferayhome.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.liferay.home.liferayhome.HomeActivity;
import com.liferay.home.liferayhome.R;
import java.util.List;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
import static com.liferay.home.liferayhome.LiferayHomeActivity.TAG;

public class GeofenceTransitionsIntentService extends IntentService {

	public GeofenceTransitionsIntentService() {
		super("GeofenceTransitionsIntentService");
	}

	protected void onHandleIntent(Intent intent) {

		Log.d(TAG, "Received a geofencing event!");
		GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
		if (geofencingEvent.hasError()) {
			Log.e("LiferayHome", String.valueOf(geofencingEvent.getErrorCode()));
			return;
		}

		int transition = geofencingEvent.getGeofenceTransition();
		if (transition == GEOFENCE_TRANSITION_ENTER || transition == GEOFENCE_TRANSITION_EXIT) {

			List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
			Geofence geofence = triggeringGeofences.get(0);
			Log.d(TAG, geofence.toString());

			String message = transition == GEOFENCE_TRANSITION_ENTER ? "We're close to home" : "We're leaving home";
			createNotification(triggeringGeofences, message);
		}
	}

	private void createNotification(List<Geofence> triggeringGeofences, String message) {

		NotificationCompat.Builder notificationBuilder =
			new NotificationCompat.Builder(this).setSmallIcon(R.drawable.home_icon)
				.setContentTitle(triggeringGeofences.get(0).getRequestId())
				.setContentText(message);

		Intent resultIntent = new Intent(this, HomeActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(HomeActivity.class);
		stackBuilder.addNextIntent(resultIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.setContentIntent(resultPendingIntent);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notificationBuilder.build());
	}
}
