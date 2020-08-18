package com.main.model;

import com.google.gson.annotations.SerializedName;

public class ToItem{

	@SerializedName("carrier")
	private String carrier;

	@SerializedName("line_type")
	private String lineType;

	@SerializedName("phone_number")
	private String phoneNumber;

	@SerializedName("status")
	private String status;

	public String getCarrier(){
		return carrier;
	}

	public String getLineType(){
		return lineType;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public String getStatus(){
		return status;
	}
}