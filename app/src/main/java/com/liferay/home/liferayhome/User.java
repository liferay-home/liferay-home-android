package com.liferay.home.liferayhome;

public class User {

	private Integer id;
	private String name;
	private String googleId;

	public User(String name, String googleId) {
		this.name = name;
		this.googleId = googleId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}
}
