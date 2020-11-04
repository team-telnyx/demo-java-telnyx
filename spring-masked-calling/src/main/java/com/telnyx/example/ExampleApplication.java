package com.telnyx.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.Configuration;
import com.telnyx.sdk.api.CallCommandsApi;
import com.telnyx.sdk.api.MessagesApi;
import com.telnyx.sdk.auth.HttpBearerAuth;
import com.telnyx.sdk.model.AnswerRequest;
import com.telnyx.sdk.model.CallAnsweredEvent;
import com.telnyx.sdk.model.CallControlCommandResponse;
import com.telnyx.sdk.model.CallInitiatedEvent;
import com.telnyx.sdk.model.CreateMessageRequest;
import com.telnyx.sdk.model.InboundMessageEvent;
import com.telnyx.sdk.model.InboundMessagePayload;
import com.telnyx.sdk.model.MessageResponse;
import com.telnyx.sdk.model.OutboundMessageEvent;
import com.telnyx.sdk.model.OutboundMessagePayload;
import com.telnyx.sdk.model.TransferCallRequest;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Arrays;
import java.util.Map;
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

    static Dotenv dotenv = Dotenv.load();
    private final static String USER_PHONE_NUMBER_A = dotenv.get("USER_PHONE_NUMBER_A");
    private final static String USER_PHONE_NUMBER_B = dotenv.get("USER_PHONE_NUMBER_B");
    private final static String TELNYX_PHONE_NUMBER = dotenv.get("TELNYX_PHONE_NUMBER");
    private final static String YOUR_TELNYX_API_KEY = dotenv.get("TELNYX_API_KEY");
    private static final String OUTBOUND_MESSAGING_PATH = "/messaging/outbound";
    private static final String INBOUND_MESSAGING_PATH = "/messaging/inbound";
    private static final String OUTBOUND_TRANSFER_CALL_CONTROL_PATH = "/call-control/outbound/transfer";
    private static final String INBOUND_CALL_CONTROL_PATH = "/call-control/inbound";
    private static final String INBOUND_ANSWER_CALL_CONTROL_PATH = "/call-control/inbound/answer";

    private static ApiClient defaultClient = Configuration.getDefaultApiClient();
    private static NumberMappings numberMappings;

    public static void main(String[] args) {
        defaultClient.setBasePath("https://api.telnyx.com/v2");
        HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
        bearerAuth.setBearerToken(YOUR_TELNYX_API_KEY);
        numberMappings = new NumberMappings();
        numberMappings.addNumberMapping(TELNYX_PHONE_NUMBER,
                Arrays.asList(USER_PHONE_NUMBER_A, USER_PHONE_NUMBER_B));
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

    @PostMapping(OUTBOUND_TRANSFER_CALL_CONTROL_PATH)
    public String outboundTransfer(@RequestBody Map<String,Object> event ){
        String eventType = (String) ((Map<String, Object>) event.get("data")).get("event_type");
        return eventType;
    }

    @PostMapping(INBOUND_CALL_CONTROL_PATH)
    public String inboundCall(@RequestBody CallInitiatedEvent callInitiatedEvent){
        CallCommandsApi apiInstance = new CallCommandsApi(defaultClient);
        String callControlId = callInitiatedEvent.getData().getPayload().getCallControlId();
        String webhookUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(INBOUND_ANSWER_CALL_CONTROL_PATH)
                .build()
                .toUriString();

        AnswerRequest answerRequest= new AnswerRequest()
                .webhookUrl(webhookUrl);
        try {
            CallControlCommandResponse result = apiInstance.callAnswer(callControlId, answerRequest);
        } catch (ApiException e) {
            System.err.println("Exception when calling CallCommandsApi#callAnswer");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        return callControlId;
    }

    @PostMapping(INBOUND_ANSWER_CALL_CONTROL_PATH)
    public String inboundCallAnswer(@RequestBody CallAnsweredEvent callAnsweredEvent){
        CallCommandsApi apiInstance = new CallCommandsApi(defaultClient);
        String callControlId = callAnsweredEvent.getData().getPayload().getCallControlId();
        String telnyxPhoneNumber = callAnsweredEvent.getData().getPayload().getTo();
        String userPhoneNumber = callAnsweredEvent.getData().getPayload().getFrom();
        String forwardToPhoneNumber = numberMappings.getDestinationNumber(telnyxPhoneNumber, userPhoneNumber);

        String webhookUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(OUTBOUND_TRANSFER_CALL_CONTROL_PATH)
                .build()
                .toUriString();

        TransferCallRequest transferCallRequest = new TransferCallRequest()
                .webhookUrl(webhookUrl)
                .from(telnyxPhoneNumber)
                .to(forwardToPhoneNumber);
        try {
            CallControlCommandResponse result = apiInstance.callTransfer(callControlId, transferCallRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CallCommandsApi#callAnswer");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        return callControlId;
    }

    @PostMapping(OUTBOUND_MESSAGING_PATH)
    public String outboundMessage(@RequestBody OutboundMessageEvent messageEvent){
        OutboundMessagePayload messagePayload = messageEvent.getData().getPayload();
        UUID messageId = messagePayload.getId();
        System.out.printf("Received message: %s\n", messageId.toString());
        return messageId.toString();
    }

    @PostMapping(INBOUND_MESSAGING_PATH)
    public String inboundMessage(@RequestBody InboundMessageEvent messageEvent){
        InboundMessagePayload messagePayload = messageEvent.getData().getPayload();
        UUID messageId = messagePayload.getId();
        String inboundText = messagePayload.getText().toLowerCase().trim();
        String userPhoneNumber = messagePayload.getFrom().getPhoneNumber();
        String telnyxPhoneNumber = messagePayload.getTo().get(0).getPhoneNumber();
        String forwardToPhoneNumber = numberMappings.getDestinationNumber(telnyxPhoneNumber, userPhoneNumber);

        String webhookUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(OUTBOUND_MESSAGING_PATH).build().toUriString();

        MessagesApi apiInstance = new MessagesApi(defaultClient);

        CreateMessageRequest createMessageRequest = new CreateMessageRequest()
                .to(forwardToPhoneNumber)
                .from(telnyxPhoneNumber)
                .text(inboundText)
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
