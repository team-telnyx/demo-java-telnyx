package com.main.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Payload{

	@SerializedName("cost")
	private Object cost;

	@SerializedName("messaging_profile_id")
	private String messagingProfileId;

	@SerializedName("webhook_url")
	private String webhookUrl;

	@SerializedName("media")
	private List<Object> media;

	@SerializedName("encoding")
	private String encoding;

	@SerializedName("type")
	private String type;

	@SerializedName("record_type")
	private String recordType;

	@SerializedName("tags")
	private List<Object> tags;

	@SerializedName("sent_at")
	private String sentAt;

	@SerializedName("completed_at")
	private String completedAt;

	@SerializedName("valid_until")
	private String validUntil;

	@SerializedName("webhook_failover_url")
	private String webhookFailoverUrl;

	@SerializedName("received_at")
	private String receivedAt;

	@SerializedName("organization_id")
	private String organizationId;

	@SerializedName("parts")
	private int parts;

	@SerializedName("from")
	private String from;

	@SerializedName("id")
	private String id;

	@SerializedName("text")
	private String text;

	@SerializedName("to")
	private List<ToItem> to;

	@SerializedName("errors")
	private List<Object> errors;

	@SerializedName("direction")
	private String direction;

	public Object getCost(){
		return cost;
	}

	public String getMessagingProfileId(){
		return messagingProfileId;
	}

	public String getWebhookUrl(){
		return webhookUrl;
	}

	public List<Object> getMedia(){
		return media;
	}

	public String getEncoding(){
		return encoding;
	}

	public String getType(){
		return type;
	}

	public String getRecordType(){
		return recordType;
	}

	public List<Object> getTags(){
		return tags;
	}

	public String getSentAt(){
		return sentAt;
	}

	public String getCompletedAt(){
		return completedAt;
	}

	public String getValidUntil(){
		return validUntil;
	}

	public String getWebhookFailoverUrl(){
		return webhookFailoverUrl;
	}

	public String getReceivedAt(){
		return receivedAt;
	}

	public String getOrganizationId(){
		return organizationId;
	}

	public int getParts(){
		return parts;
	}

	public String getFrom(){
		return from;
	}

	public String getId(){
		return id;
	}

	public String getText(){
		return text;
	}

	public List<ToItem> getTo(){
		return to;
	}

	public List<Object> getErrors(){
		return errors;
	}

	public String getDirection(){
		return direction;
	}
}