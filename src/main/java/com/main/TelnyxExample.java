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
import com.telnyx.sdk.models.CallInitiated;
import com.telnyx.sdk.models.CallInitiatedEvent;
import com.telnyx.sdk.models.OutboundMessage;
import com.telnyx.sdk.models.OutboundMessage.EventTypeEnum;
import com.telnyx.sdk.models.OutboundMessageEvent;

import io.github.cdimascio.dotenv.Dotenv;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.main.MessagingController.sendMessage;
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

    }


}
