package com.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.main.model.PhoneNumberOrderRequest;
import com.main.model.SearchNumbersResponse;
import com.main.model.MessageSendRequest;
import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.Configuration;
import com.telnyx.sdk.models.*;
import com.telnyx.sdk.models.OutboundMessage.EventTypeEnum;

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
import static spark.Spark.*;


public class TelnyxExample {

    static Dotenv dotenv = Dotenv.load();

    private static final String TELNYX_API_KEY = dotenv.get("TELNYX_API_KEY");
//    private static final String TELNYX_PUBLIC_KEY = dotenv.get("TELNYX_PUBLIC_KEY");
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
//                OutboundMessage mm = new OutboundMessage()
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

        //Disable a messaging profile
        post("/messaging_profiles/:id/disable", (req, res) -> {
            String json = req.body();
            UpdateMessagingProfileRequest updateMessagingProfileRequest = new UpdateMessagingProfileRequest().enabled(false);
            String result = updateMessagingProfile(req.params("id"), updateMessagingProfileRequest);
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

        //Retrieve a messaging profile
        get("/messaging_profiles/:id", (req, res) -> {
            String json = req.body();
            String result = getMessagingProfile(req.params("id"));
            res.type("application/json");
            return result;
        });


    }


}
