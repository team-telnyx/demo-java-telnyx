package com.main;

import static com.main.TelnyxExample.WEBHOOK_URL;
import static com.main.TelnyxExample.defaultClient;

import com.google.gson.Gson;
import com.main.model.MessageSendRequest;
import com.telnyx.sdk.apis.MessagesApi;
import com.telnyx.sdk.models.CreateMessageRequest;
import com.telnyx.sdk.models.MessageResponse;
import java.util.UUID;

public class MessagingController {

        public static String sendMessage (MessageSendRequest request) {

        MessagesApi apiInstance = new MessagesApi(defaultClient);

        CreateMessageRequest newMessage = new CreateMessageRequest()
                .from(request.from)
                .to(request.to)
                .text(request.text)
                .useProfileWebhooks(false)
                .webhookUrl(WEBHOOK_URL);

        //Create the payload

        // Send the message
        try {
            MessageResponse result = apiInstance.createMessage(newMessage);
            UUID id = result.getData().getId();
            System.out.printf("Sent message with ID: %s\n", id);
            return new Gson().toJson(result);
        } catch (Exception e) {
            System.err.println("Exception when calling MessagesApi#createLongCodeMessage");
            e.printStackTrace();
            return "{ \"error\": \"Problem sending message\"}";
        }
    }

}
