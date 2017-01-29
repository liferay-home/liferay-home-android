package com.liferay.home.liferayhome;

public class Device {

	private Integer id;
	private String description;
	private String serialNumber;
	private String type;
	private String name;

	public Device(String serialNumber) {
		this.serialNumber = serialNumber;
		this.type = "Home";
		this.name = "";
		this.description = "";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
