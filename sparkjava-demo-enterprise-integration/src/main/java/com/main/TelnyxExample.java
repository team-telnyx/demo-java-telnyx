package com.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.main.model.MessageSendRequest;
import com.main.model.PhoneNumberOrderRequest;
import com.main.model.SearchNumbersResponse;
import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.Configuration;
import com.telnyx.sdk.models.CreateLongCodeMessageRequest;
import com.telnyx.sdk.models.CreateNumberPoolMessageRequest;
import com.telnyx.sdk.models.OutboundMessage;
import com.telnyx.sdk.models.OutboundMessage.EventTypeEnum;
import com.telnyx.sdk.models.OutboundMessageEvent;
import io.github.cdimascio.dotenv.Dotenv;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import static com.main.MessagingController.sendMessage;
import static com.main.MessagingProfilesController.*;
import static com.main.MessagingProfilesController.getDetailedMessagingProfileMetrics;
import static com.main.MessagingProfilesController.listMessagingProfileMetrics;
import static com.main.NumbersController.orderNumber;
import static com.main.NumbersController.reserveNumbers;
import static com.main.NumbersController.searchNumbers;
import static com.main.CallController.step1CreateOutboundLeg;
import static com.main.MessagingController.*;

import static spark.Spark.*;


public class TelnyxExample {

    static Dotenv dotenv = Dotenv.load();

    private static final String TELNYX_API_KEY = dotenv.get("TELNYX_API_KEY");
    private static final String TELNYX_APP_PORT = dotenv.get("TELNYX_APP_PORT");
    public static final String WEBHOOK_URL = "http://e8d1164da322.ngrok.io/Callbacks/Messaging/Outbound";
    public static final String TELNYX_PHONE_NUMBER = "+19196468161";

    // Instantiate the client
    static ApiClient defaultClient = Configuration.getDefaultApiClient();

    public static void main(String[] args) {

        JsonDeserializer jsonDeserializer = (json, type, jsonDeserializationContext) -> OffsetDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        Gson gson = new GsonBuilder().registerTypeAdapter(OffsetDateTime.class, jsonDeserializer).create();

        assert TELNYX_APP_PORT != null;
        // Instantiate the client
        defaultClient.setAccessToken(TELNYX_API_KEY);
        port(Integer.parseInt(TELNYX_APP_PORT));
        get("/", (req, res) -> "Hello World");

        post("/SendMessage", (req, res) -> {
            String json = req.body();
            MessageSendRequest messageSendRequest = new Gson().fromJson(json, MessageSendRequest.class);
            String result = sendMessage(messageSendRequest);
            res.type("application/json");
            return result;
        });

        post("/Callbacks/Messaging/Outbound", (req, res) -> {
            String json = req.body();
            try {
                OutboundMessageEvent dlr = new Gson().fromJson(json, OutboundMessageEvent.class);
                OutboundMessage data = dlr.getData();
                UUID id = data.getId();
                EventTypeEnum eventType = data.getEventType();
                System.out.printf("Message id: %s Status: %s\n", id, eventType);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            res.status(200);

            return "";//Just needs an ACK
        });

        post("/Callbacks/Voice/Inbound/Init", (req, res) -> {
            String json = req.body();

            CallInitiatedEvent inboundCall = null;
            try {
                inboundCall = gson.fromJson(json, CallInitiatedEvent.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            UUID callControlId = inboundCall.getData().getPayload().getCallControlId();
            UUID outboundLegId = step1CreateOutboundLeg(callControlId);


            res.status(200);

            return "";//Just needs an ACK
        });

        post("/Callbacks/Voice/Outbound", (req, res) -> {
            String json = req.body();
            CallInitiatedEvent inboundCall = new Gson().fromJson(json, CallInitiatedEvent.class);


            UUID callControlId = inboundCall.getData().getPayload().getCallControlId();
            UUID outboundLegId = step1CreateOutboundLeg(callControlId);


            res.status(200);

            return "";//Just needs an ACK
        });

        get("/availableNumbers", (req, res)-> {
            res.type("application/json");
            SearchNumbersResponse availableNumbers = searchNumbers(
                    req.queryParams("countryCode"),
                    req.queryParams("state"),
                    req.queryParams("city")
            );
            if (Boolean.parseBoolean(req.queryParams("reserve")) && availableNumbers.valid) {
                String reservation = reserveNumbers(availableNumbers.getApiResponse());
                return reservation;
            }
            else {
                return availableNumbers.getJson();
            }
        });

        post("/availableNumbers", (req, res) -> {
            String json = req.body();
            PhoneNumberOrderRequest orderRequest = new Gson().fromJson(json, PhoneNumberOrderRequest.class);
            String result = orderNumber(orderRequest.phoneNumber);
            res.type("application/json");
            return result;
        });

        //Create a messaging profile
        post("/messaging_profiles", (req, res) -> {
            String json = req.body();
            CreateMessagingProfileRequest createMessagingProfileRequest = new Gson().fromJson(json, CreateMessagingProfileRequest.class);
            String result = createMessagingProfile(createMessagingProfileRequest);
            res.type("application/json");
            return result;
        });

        //Send a long code message
        post("/messages", (req, res) -> {
            String json = req.body();
            MessageSendRequest messageSendRequest = new Gson().fromJson(json, MessageSendRequest.class);
            String result = sendMessage(messageSendRequest);
            res.type("application/json");
            return result;
        });

        //Get the metrics of a messaging profile
        get("/messaging_profiles/:id/metrics", (req, res) -> {
            String result = getDetailedMessagingProfileMetrics(req.params("id"), req.queryParams("time_frame"));
            res.type("application/json");
            return result;
        });

        //Get all messaging profile metrics
        get("/messaging_profile_metrics", (req, res) -> {
            Integer pageNumber = req.queryParams("page[number]") != null ? Integer.valueOf(req.queryParams("page[number]")) : null;
            Integer pageSize = req.queryParams("page[size]") != null ? Integer.valueOf(req.queryParams("page[size]")) : null;
            String result = listMessagingProfileMetrics(pageNumber, pageSize, req.queryParams("id"), req.queryParams("time_frame"));
            res.type("application/json");
            return result;
        });

        //Delete a messaging profile
        delete("/messaging_profiles/:id", (req, res) -> {
            String result = deleteMessagingProfile(req.params("id"));
            res.type("application/json");
            return result;
        });

        //Send 5 long code messages in a row
        post("/messages/sendFive", (req, res) -> {
            String json = req.body();
            MessageSendRequest messageSendRequest = new Gson().fromJson(json, MessageSendRequest.class);
            String originalText = messageSendRequest.text;

            //send request 5 times
            StringBuilder result = new StringBuilder();
            messageSendRequest.text = originalText + " (1/5)";
            result.append(sendMessage(messageSendRequest));
            messageSendRequest.text = originalText + " (2/5)";
            result.append(sendMessage(messageSendRequest));
            messageSendRequest.text = originalText + " (3/5)";
            result.append(sendMessage(messageSendRequest));
            messageSendRequest.text = originalText + " (4/5)";
            result.append(sendMessage(messageSendRequest));
            messageSendRequest.text = originalText + " (5/5)";
            result.append(sendMessage(messageSendRequest));
            res.type("application/json");
            return result;
        });

        //Update messaging profile
        patch("/messaging_profiles/:id", (req, res) -> {
            String json = req.body();
            UpdateMessagingProfileRequest updateMessagingProfileRequest = new Gson().fromJson(json, UpdateMessagingProfileRequest.class);
            String result = updateMessagingProfile(req.params("id"), updateMessagingProfileRequest);
            res.type("application/json");
            return result;
        });

        //Enable number pool on a messaging profile
        post("/messaging_profiles/:id/enableNumberPool", (req, res) -> {
            String json = req.body();
            NumberPoolSettings numberPoolSettings = new NumberPoolSettings()
                    .geomatch(false)
                    .longCodeWeight(BigDecimal.valueOf(2))
                    .skipUnhealthy(false)
                    .stickySender(true)
                    .tollFreeWeight(BigDecimal.valueOf(10));
            UpdateMessagingProfileRequest updateMessagingProfileRequest = new UpdateMessagingProfileRequest().numberPoolSettings(numberPoolSettings);
            String result = updateMessagingProfile(req.params("id"), updateMessagingProfileRequest);
            res.type("application/json");
            return result;
        });

        //Send a message using number pool
        post("/messages/number_pool", (req, res) -> {
            String json = req.body();

            CreateNumberPoolMessageRequest createNumberPoolMessageRequest = new Gson().fromJson(json, CreateNumberPoolMessageRequest.class);
            String result = sendMessageUsingNumberPool(createNumberPoolMessageRequest);
            res.type("application/json");
            return result;
        });

        //Disable a messaging profile
        post("/messaging_profiles/:id/disable", (req, res) -> {
            String json = req.body();
            UpdateMessagingProfileRequest updateMessagingProfileRequest = new UpdateMessagingProfileRequest().enabled(false);
            String result = updateMessagingProfile(req.params("id"), updateMessagingProfileRequest);
            res.type("application/json");
            return result;
        });
        //Retrieve details for a message
        get("/messages/:id", (req, res) -> {
            String result = retrieveMessage(req.params("id"));
            res.type("application/json");
            return result;
        });

        //List all phone numbers associated with a messaging profile
        get("/messaging_profiles/:id/phone_numbers", (req, res) -> {
            Integer pageNumber = req.queryParams("page[number]") != null ? Integer.valueOf(req.queryParams("page[number]")) : null;
            Integer pageSize = req.queryParams("page[size]") != null ? Integer.valueOf(req.queryParams("page[size]")) : null;
            String result = getMessagingProfilePhoneNumbers(req.params("id"), pageNumber, pageSize);
            res.type("application/json");
            return result;
        });

        //TODO: Figure out how to make updates that set properties to null
        //Disable number pool on a messaging profile
        post("/messaging_profiles/:id/disableNumberPool", (req, res) -> {
            String json = req.body();
            String result = disableNumberPool(req.params("id"));
            res.type("application/json");
            return result;
        });

        //Receive a message via webhook
        post("/messages/inboundMessageWebhook", (req, res) -> {
            System.out.println(req.body());
            res.type("application/json");
            res.status(200);
            return "{}";
        });

        //Send a long code message
        post("/messages/long_code", (req, res) -> {
            String json = req.body();
            CreateLongCodeMessageRequest createLongCodeMessageRequest = new Gson().fromJson(json, CreateLongCodeMessageRequest.class);
            String result = createLongCodeMessage(createLongCodeMessageRequest);
            res.type("application/json");
            return result;
        });

        //Retrieve a messaging profile
        get("/messaging_profiles/:id", (req, res) -> {
            String json = req.body();
            String result = getMessagingProfile(req.params("id"));
            res.type("application/json");
            return result;
        });

        //Send 5 long code messages in a row
        post("/messages/long_code/sendFive", (req, res) -> {
            String json = req.body();
            CreateLongCodeMessageRequest createLongCodeMessageRequest = new Gson().fromJson(json, CreateLongCodeMessageRequest.class);
            String originalText = createLongCodeMessageRequest.getText();

            //send request 5 times
            StringBuilder result = new StringBuilder();
            createLongCodeMessageRequest.setText(originalText + " (1/5)");
            result.append(createLongCodeMessage(createLongCodeMessageRequest));
            createLongCodeMessageRequest.setText(originalText + " (2/5)");
            result.append(createLongCodeMessage(createLongCodeMessageRequest));
            createLongCodeMessageRequest.setText(originalText + " (3/5)");
            result.append(createLongCodeMessage(createLongCodeMessageRequest));
            createLongCodeMessageRequest.setText(originalText + " (4/5)");
            result.append(createLongCodeMessage(createLongCodeMessageRequest));
            createLongCodeMessageRequest.setText(originalText + " (5/5)");
            result.append(createLongCodeMessage(createLongCodeMessageRequest));
            res.type("application/json");
            return result;
        });
    }


}
