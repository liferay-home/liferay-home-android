package com.liferay.home.liferayhome;

public class SensorData {

	public SensorData(Integer id, String type, Double value) {
		this.id = id;
		this.type = type;
		this.value = value;
	}

	public Integer id;
	public String type;

	SensorData() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double value;
}
