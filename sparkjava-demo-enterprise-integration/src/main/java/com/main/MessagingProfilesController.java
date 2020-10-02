package com.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.apis.MessagingProfilesApi;
import com.telnyx.sdk.models.*;

import java.util.UUID;

import static com.main.TelnyxExample.defaultClient;

public class MessagingProfilesController {
    public static String createMessagingProfile(CreateMessagingProfileRequest request) {
        MessagingProfilesApi apiInstance = new MessagingProfilesApi(defaultClient);

        try {
            MessagingProfileResponse createMessagingProfileResponse = apiInstance.createMessagingProfile(request);
            UUID id = createMessagingProfileResponse.getData().getId();
            System.out.printf("Created messaging profile with ID: %s\n", id);
            return new Gson().toJson(createMessagingProfileResponse);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagingProfilesApi#createMessagingProfile");
            e.printStackTrace();
            return e.getResponseBody();
        }
    }

    public static String getDetailedMessagingProfileMetrics(String id, String timeFrame) {
        MessagingProfilesApi apiInstance = new MessagingProfilesApi(defaultClient);

        if (id == null) {
            return "{ \"error\": \"ID path parameter is required and must be an existing uuid\"}";
        }

        try {
            RetrieveMessagingProfileMetricsResponse messagingProfileDetailedMetrics = apiInstance.getMessagingProfileDetailedMetrics(UUID.fromString(id), timeFrame);
            return new Gson().toJson(messagingProfileDetailedMetrics);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagingProfilesApi#getMessagingProfileMetrics");
            e.printStackTrace();
            return e.getResponseBody();
        } catch (Exception e) {
            System.err.println("Exception when calling MessagingProfilesApi#getMessagingProfileMetrics");
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String listMessagingProfileMetrics(Integer pageNumber, Integer pageSize, String id, String timeFrame) {
        MessagingProfilesApi apiInstance = new MessagingProfilesApi(defaultClient);

        UUID uuid = id != null ? UUID.fromString(id) : null;

        try {
            ListMessagingProfileMetricsResponse listMessagingProfileMetricsResponse = apiInstance.listMessagingProfileMetrics(pageNumber, pageSize, uuid, timeFrame);
            return new Gson().toJson(listMessagingProfileMetricsResponse);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagingProfilesApi#listMessagingProfileMetrics");
            e.printStackTrace();
            return e.getResponseBody();
        } catch (Exception e) {
            System.err.println("Exception when calling MessagingProfilesApi#listMessagingProfileMetrics");
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String deleteMessagingProfile(String id) {
        MessagingProfilesApi apiInstance = new MessagingProfilesApi(defaultClient);

        if (id == null) {
            return "{ \"error\": \"ID path parameter is required and must be an existing uuid\"}";
        }

        try {
            MessagingProfileResponse messagingProfileResponse = apiInstance.deleteMessagingProfile(UUID.fromString(id));
            System.out.printf("Deleted messaging profile with ID: %s\n", id);
            return new Gson().toJson(messagingProfileResponse);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagingProfilesApi#deleteMessagingProfile");
            e.printStackTrace();
            return e.getResponseBody();
        } catch (Exception e) {
            System.err.println("Exception when calling MessagingProfilesApi#deleteMessagingProfile");
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String updateMessagingProfile(String id, UpdateMessagingProfileRequest request) {
        MessagingProfilesApi apiInstance = new MessagingProfilesApi(defaultClient);

        if (id == null) {
            return "{ \"error\": \"ID path parameter is required and must be an existing uuid\"}";
        }

        try {
            MessagingProfileResponse messagingProfileResponse = apiInstance.updateMessagingProfile(request, UUID.fromString(id));
            System.out.printf("Updated messaging profile with ID: %s\n", messagingProfileResponse.getData().getId());
            return new Gson().toJson(messagingProfileResponse);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagingProfilesApi#updateMessagingProfile");
            e.printStackTrace();
            return e.getResponseBody();
        } catch (Exception e) {
            System.err.println("Exception when calling MessagingProfilesApi#updateMessagingProfile");
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String getMessagingProfilePhoneNumbers(String id, Integer pageNumber, Integer pageSize) {
        MessagingProfilesApi apiInstance = new MessagingProfilesApi(defaultClient);

        if (id == null) {
            return "{ \"error\": \"ID path parameter is required and must be an existing uuid\"}";
        }

        try {
            ListMessagingProfilePhoneNumbersResponse listMessagingProfilePhoneNumbersResponse = apiInstance.listMessagingProfilePhoneNumbers(UUID.fromString(id), pageNumber, pageSize);
            return new Gson().toJson(listMessagingProfilePhoneNumbersResponse);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagingProfilesApi#listMessagingProfilePhoneNumbers");
            e.printStackTrace();
            return e.getResponseBody();
        } catch (Exception e) {
            System.err.println("Exception when calling MessagingProfilesApi#listMessagingProfilePhoneNumbers");
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String disableNumberPool(String id) {
        //In ApiClient.java of the SDK, it uses GSON to serialize java request objects.
        //By default, GSON ignores any Java variables that are null, which is the value
        //required by the Telnyx api to remove the number pool. Hopefully there's a better
        //way to do this.
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        defaultClient.getJSON().setGson(gson);

        MessagingProfilesApi apiInstance = new MessagingProfilesApi(defaultClient);

        if (id == null) {
            return "{ \"error\": \"ID path parameter is required and must be an existing uuid\"}";
        }

        try {
            //TODO: This call isn't working - maybe the SDK is out of sync with the production API
            MessagingProfile existingMessagingProfile = apiInstance.retrieveMessagingProfile(UUID.fromString(id)).getData();

            UpdateMessagingProfileRequest updateMessagingProfileRequest = new UpdateMessagingProfileRequest()
                    .enabled(existingMessagingProfile.isEnabled())
                    .name(existingMessagingProfile.getName())
                    .numberPoolSettings(null)
                    .urlShortenerSettings(existingMessagingProfile.getUrlShortenerSettings())
                    .v1Secret(existingMessagingProfile.getV1Secret())
                    .webhookApiVersion(UpdateMessagingProfileRequest.WebhookApiVersionEnum.valueOf(existingMessagingProfile.getWebhookApiVersion().toString()))
                    .webhookFailoverUrl(existingMessagingProfile.getWebhookFailoverUrl())
                    .webhookUrl(existingMessagingProfile.getWebhookUrl())
                    .whitelistedDestinations(existingMessagingProfile.getWhitelistedDestinations());

            MessagingProfileResponse messagingProfileResponse = apiInstance.updateMessagingProfile(updateMessagingProfileRequest, UUID.fromString(id));
            System.out.printf("Updated messaging profile with ID: %s\n", messagingProfileResponse.getData().getId());
            return new Gson().toJson(messagingProfileResponse);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagingProfilesApi#disableNumberPool");
            e.printStackTrace();
            return e.getResponseBody();
        } catch (Exception e) {
            System.err.println("Exception when calling MessagingProfilesApi#disableNumberPool");
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String getMessagingProfile(String id) {
        MessagingProfilesApi apiInstance = new MessagingProfilesApi(defaultClient);

        if (id == null) {
            return "{ \"error\": \"ID path parameter is required and must be an existing uuid\"}";
        }

        try {
            MessagingProfileResponse messagingProfileResponse = apiInstance.retrieveMessagingProfile(UUID.fromString(id));
            return new Gson().toJson(messagingProfileResponse);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagingProfilesApi#getMessagingProfile");
            e.printStackTrace();
            return e.getResponseBody();
        } catch (Exception e) {
            System.err.println("Exception when calling MessagingProfilesApi#getMessagingProfile");
            e.printStackTrace();
            return e.getMessage();
        }
    }
}