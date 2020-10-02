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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.main.MessagingController.*;
import static com.main.NumbersController.*;
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


        //Send a long code message
        post("/messages", (req, res) -> {
            String json = req.body();
            MessageSendRequest messageSendRequest = new Gson().fromJson(json, MessageSendRequest.class);
            String result = sendMessage(messageSendRequest);
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

        //Send a message using number pool
        post("/messages/number_pool", (req, res) -> {
            String json = req.body();

            CreateNumberPoolMessageRequest createNumberPoolMessageRequest = new Gson().fromJson(json, CreateNumberPoolMessageRequest.class);
            String result = sendMessageUsingNumberPool(createNumberPoolMessageRequest);
            res.type("application/json");
            return result;
        });

        //Retrieve details for a message
        get("/messages/:id", (req, res) -> {
            String result = retrieveMessage(req.params("id"));
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
