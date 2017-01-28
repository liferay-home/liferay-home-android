package com.liferay.home.liferayhome;

public class PhoneLocation {
	Integer id;
	Double longitude;
	Double latitude;
	String device;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getLongitude() {
		return longitude;
	}

	public PhoneLocation(Integer id, Double longitude, Double latitude, String device) {
		this.id = id;
		this.longitude = longitude;
		this.latitude = latitude;
		this.device = device;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}
}