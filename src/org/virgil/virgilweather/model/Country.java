package org.virgil.virgilweather.model;

import java.io.Serializable;

public class Country implements Serializable {
	private int id;
	private String countryName;
	private String countrCode;
	private int cityId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountrCode() {
		return countrCode;
	}

	public void setCountrCode(String countrCode) {
		this.countrCode = countrCode;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

}
