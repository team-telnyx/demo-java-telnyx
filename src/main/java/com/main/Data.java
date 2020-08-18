package com.main;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("occurred_at")
	private String occurredAt;

	@SerializedName("event_type")
	private String eventType;

	@SerializedName("payload")
	private Payload payload;

	@SerializedName("id")
	private String id;

	@SerializedName("record_type")
	private String recordType;

	public String getOccurredAt(){
		return occurredAt;
	}

	public String getEventType(){
		return eventType;
	}

	public Payload getPayload(){
		return payload;
	}

	public String getId(){
		return id;
	}

	public String getRecordType(){
		return recordType;
	}
}