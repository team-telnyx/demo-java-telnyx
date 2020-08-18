package com.main;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.client.ApiClient;
import io.swagger.client.Configuration;
import io.swagger.client.api.MessagesApi;
import io.swagger.client.model.CreateMessageResponse;
import io.swagger.client.model.NewMessage;


import java.util.UUID;

import static spark.Spark.*;


public class TelnyxExample {

    static Dotenv dotenv = Dotenv.load();

    private static final String TELNYX_API_KEY = dotenv.get("TELNYX_API_KEY");
//    private static final String TELNYX_PUBLIC_KEY = dotenv.get("TELNYX_PUBLIC_KEY");
    private static final String TELNYX_APP_PORT = dotenv.get("TELNYX_APP_PORT");
    private static final String WEBHOOK_URL = "http://d461e798f09e.ngrok.io/Callbacks/Messaging/Outbound";

    private static CreateMessageResponse sendMessage (sendRequest request) {
        // Instantiate the client
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setAccessToken(TELNYX_API_KEY);
        MessagesApi apiInstance = new MessagesApi(defaultClient);

        //Create the payload
        NewMessage newMessage = new NewMessage()
                .from(request.from)
                .to(request.to)
                .text(request.text)
                .useProfileWebhooks(false)
                .webhookUrl(WEBHOOK_URL);
        // Send the message
        try {
            CreateMessageResponse result = apiInstance.createMessage(newMessage);
            return result;
        } catch (Exception e) {
            System.err.println("Exception when calling MessagesApi#createLongCodeMessage");
            e.printStackTrace();
            return new CreateMessageResponse();
        }
    }


    public static void main(String[] args) {
        assert TELNYX_APP_PORT != null;
        port(Integer.parseInt(TELNYX_APP_PORT));
        get("/", (req, res) -> "Hello World");

        post("/SendMessage", (req, res) -> {
            String json = req.body();
            sendRequest sendRequest = new Gson().fromJson(json, sendRequest.class);
            CreateMessageResponse result = sendMessage(sendRequest);
            UUID id = result.getData().getId();
            System.out.printf("Sent message with ID: %s\n", id);
            String resultJson = new Gson().toJson(result);
            res.type("application/json");
            return resultJson;
        });

        post("/Callbacks/Messaging/Outbound", (req, res) -> {
            String json = req.body();
            try {

                Dlr dlr = new Gson().fromJson(json, Dlr.class);
                String id = dlr.getData().getPayload().getId();
                String eventType = dlr.getData().getEventType();
                System.out.printf("Message id: %s Status: %s\n", id, eventType);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            res.status(200);

            return "";//Just needs an ACK
        });



    }
}
