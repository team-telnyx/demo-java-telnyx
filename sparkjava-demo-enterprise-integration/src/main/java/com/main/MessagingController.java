package com.main;

import static com.main.TelnyxExample.WEBHOOK_URL;
import static com.main.TelnyxExample.defaultClient;

import com.google.gson.Gson;
import com.main.model.MessageSendRequest;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.apis.MessagesApi;
import com.telnyx.sdk.models.BaseMessageRequest;
import com.telnyx.sdk.models.CreateLongCodeMessageRequest;
import com.telnyx.sdk.models.CreateMessageRequest;
import com.telnyx.sdk.models.CreateNumberPoolMessageRequest;
import com.telnyx.sdk.models.MessageResponse;
import java.util.UUID;

public class MessagingController {

        public static String sendMessage (MessageSendRequest request) {

        MessagesApi apiInstance = new MessagesApi(defaultClient);

//        BaseMessageRequest newMessage = new CreateMessageRequest()
//                .from(request.from)
//                .to(request.to)
//                .text(request.text)
//                .useProfileWebhooks(false)
//                .webhookUrl(WEBHOOK_URL);
        CreateMessageRequest newMessage = new CreateMessageRequest();
        newMessage.from(request.from)
                .to(request.to)
                .text(request.text)
                .useProfileWebhooks(false)
                .webhookUrl(WEBHOOK_URL);

        //Create the payload

        // Send the message
        try {
            MessageResponse result = apiInstance.createMessage().createMessageRequest(newMessage).execute();
            UUID id = result.getData().getId();
            System.out.printf("Sent message with ID: %s\n", id);
            return new Gson().toJson(result);
        } catch (Exception e) {
            System.err.println("Exception when calling MessagesApi#createLongCodeMessage");
            e.printStackTrace();
            return "{ \"error\": \"Problem sending message\"}";
        }
    }

    //TODO: Fix this
    // Throws a deserialization error because the Telnyx api response sometimes? has a string response
    // but the sdk expects an OutboundMessagePayloadFrom object
    // example (actual json response in from field): "from": "+13125790236"
    // expected (per documentation): "from": {
    //      "carrier": "TELNYX LLC",
    //      "line_type": "VoIP",
    //      "phone_number": "+18445550001"
    //    }
    public static String sendMessageUsingNumberPool(CreateNumberPoolMessageRequest request) {
        MessagesApi apiInstance = new MessagesApi(defaultClient);

        try {
            MessageResponse result = apiInstance.createNumberPoolMessage().createNumberPoolMessageRequest(request).execute();
            UUID id = result.getData().getId();
            System.out.printf("Sent message using number pool with ID: %s\n", id);
            return new Gson().toJson(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagesApi#createNumberPoolMessage");
            e.printStackTrace();
            return e.getResponseBody();
        }
    }

    public static String retrieveMessage(String id) {
        MessagesApi apiInstance = new MessagesApi(defaultClient);

        if (id == null) {
            return "{ \"error\": \"ID path parameter is required and must be an existing uuid\"}";
        }

        try {
            MessageResponse result = apiInstance.retrieveMessage((UUID.fromString(id))).execute();
            return new Gson().toJson(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagesApi#retrieveMessage");
            e.printStackTrace();
            return e.getResponseBody();
        } catch (Exception e) {
            System.err.println("Exception when calling MessagesApi#retrieveMessage");
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String createLongCodeMessage(CreateLongCodeMessageRequest request) {
        MessagesApi apiInstance = new MessagesApi(defaultClient);

        try {
            MessageResponse result = apiInstance.createLongCodeMessage().createLongCodeMessageRequest(request).execute();
            UUID id = result.getData().getId();
            System.out.printf("Sent long code message with ID: %s\n", id);
            return new Gson().toJson(result);
        } catch (Exception e) {
            System.err.println("Exception when calling MessagesApi#createLongCodeMessage");
            e.printStackTrace();
            return "{ \"error\": \"Problem sending message\"}";
        }
    }

}
