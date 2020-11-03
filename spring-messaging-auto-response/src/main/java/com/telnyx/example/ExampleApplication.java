package com.telnyx.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.Configuration;
import com.telnyx.sdk.api.MessagesApi;
import com.telnyx.sdk.auth.HttpBearerAuth;
import com.telnyx.sdk.model.CreateMessageRequest;
import com.telnyx.sdk.model.InboundMessageEvent;
import com.telnyx.sdk.model.InboundMessagePayload;
import com.telnyx.sdk.model.MessageResponse;
import com.telnyx.sdk.model.OutboundMessageEvent;
import com.telnyx.sdk.model.OutboundMessagePayload;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@SpringBootApplication
@RestController
public class ExampleApplication {

    Dotenv dotenv = Dotenv.load();
    private final String YOUR_TELNYX_API_KEY = dotenv.get("TELNYX_API_KEY");
    private static final String OUTBOUND_PATH = "/messaging/outbound";

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @Autowired
    void configureObjectMapper(final ObjectMapper mapper) {
        mapper.registerModule(new JsonNullableModule());
    }
    @GetMapping("/")
    public String hello(){
        return "Hello World";
    }

    @PostMapping(OUTBOUND_PATH)
    public String outboundMessage(@RequestBody OutboundMessageEvent messageEvent){
        OutboundMessagePayload messagePayload = messageEvent.getData().getPayload();
        UUID messageId = messagePayload.getId();
        System.out.printf("Received message: %s\n", messageId.toString());
        return messageId.toString();
    }

    @PostMapping("/messaging/inbound")
    public String inboundMessage(@RequestBody InboundMessageEvent messageEvent){
        InboundMessagePayload messagePayload = messageEvent.getData().getPayload();
        UUID messageId = messagePayload.getId();
        String inboundText = messagePayload.getText().toLowerCase().trim();
        String from = messagePayload.getFrom().getPhoneNumber();
        String to = messagePayload.getTo().get(0).getPhoneNumber();
        String webhookUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(OUTBOUND_PATH).build().toUriString();

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.telnyx.com/v2");
        // Configure HTTP bearer authorization: bearerAuth
        HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
        bearerAuth.setBearerToken(YOUR_TELNYX_API_KEY);
        MessagesApi apiInstance = new MessagesApi(defaultClient);

        String text;
        switch (inboundText) {
            case "hello":
                text = "Hello world";
                break;
            case "bye":
                text = "Goodnight Moon";
                break;
            default:
                text = "I can respond to 'hello' or 'bye', try sending one of those words";
                break;
        }


        CreateMessageRequest createMessageRequest = new CreateMessageRequest()
                .to(from)
                .from(to)
                .text(text)
                .webhookUrl(webhookUrl)
                .useProfileWebhooks(false);

        try {
            MessageResponse result = apiInstance.createMessage(createMessageRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagesApi#createMessage");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }


        return messageId.toString();
    }
}
