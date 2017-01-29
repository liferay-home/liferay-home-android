package com.liferay.home.liferayhome.interactors;

import okhttp3.MediaType;

public abstract class Interactor implements Runnable {

	protected static final String BASE_URL = "http://app.liferay-home.wedeploy.io";
	protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
}
