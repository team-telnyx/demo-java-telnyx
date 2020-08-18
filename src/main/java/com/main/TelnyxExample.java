package com.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.api.MessagesApi;
import io.swagger.client.model.CreateMessageResponse;
import io.swagger.client.model.NewMessage;


import static spark.Spark.*;


public class TelnyxExample {

    static Dotenv dotenv = Dotenv.load();

    private static final String TELNYX_API_KEY = dotenv.get("TELNYX_API_KEY");
    private static final String TELNYX_PUBLIC_KEY = dotenv.get("TELNYX_PUBLIC_KEY");
    private static final String TELNYX_APP_PORT = dotenv.get("TELNYX_APP_PORT");
    private static final String WEBHOOK_URL = "http://d461e798f09e.ngrok.io/Callbacks/Messaging/Outbound";

    private static String sendMessage (sendRequest request) {

        // Instantiate the client
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setAccessToken(TELNYX_API_KEY);
        MessagesApi apiInstance = new MessagesApi(defaultClient);

        //Create the payload
        NewMessage newMessage = new NewMessage()
                .from(request.from)
                .to(request.to)
                .text(request.text)
                .useProfileWebhooks(true)
                .webhookUrl(WEBHOOK_URL);
        // Send the message
        try {
//            CreateMessageResponse result = apiInstance.createMessage(newMessage);
            apiInstance.createMessage(newMessage);
            return "result";
        } catch (Exception e) {
            System.err.println("Exception when calling MessagesApi#createLongCodeMessage");
            e.printStackTrace();
            return "new CreateMessageResponse();";
        }
    }


    public static void main(String[] args) {
        assert TELNYX_APP_PORT != null;
        port(Integer.parseInt(TELNYX_APP_PORT));
        get("/", (req, res) -> "Hello World");

        post("/SendMessage", (req, res) -> {
            String json = req.body();
            sendRequest sendRequest = new ObjectMapper().readValue(json, sendRequest.class);
//            CreateMessageResponse result = sendMessage(sendRequest);
            String result = sendMessage(sendRequest);
            return result;
        });

        post("/Callbacks/Messaging/Outbound", (req, res) -> {
            String json = req.body();
            CreateMessageResponse dlr = new ObjectMapper().readValue(json, CreateMessageResponse.class);

            res.status(200);

            return "";//Just needs an ACK
        });



    }
}
