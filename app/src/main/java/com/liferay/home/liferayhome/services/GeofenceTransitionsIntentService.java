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

			List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

			Log.e("LiferayHome", "Event!" + triggeringGeofences.get(0).toString());

			createNotification(triggeringGeofences,
				geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ? "close to" : "leaving");
			Log.i(HomeActivity.TAG, triggeringGeofences.toString());
		} else {
			Log.e(HomeActivity.TAG, "Error!");
		}
	}

	private void createNotification(List<Geofence> triggeringGeofences, String message) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_home_2)
			.setContentTitle(triggeringGeofences.get(0).getRequestId())
			.setContentText("We're " + message + " home!");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, HomeActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(HomeActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}
}
