package com.main.model;

import com.google.gson.annotations.SerializedName;

public class Meta{

	@SerializedName("delivered_to")
	private String deliveredTo;

	@SerializedName("attempt")
	private int attempt;

	public String getDeliveredTo(){
		return deliveredTo;
	}

	public int getAttempt(){
		return attempt;
	}
}