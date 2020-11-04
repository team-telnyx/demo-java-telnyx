package com.telnyx.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.Configuration;
import com.telnyx.sdk.api.NumberOrdersApi;
import com.telnyx.sdk.api.NumberReservationsApi;
import com.telnyx.sdk.api.NumberSearchApi;
import com.telnyx.sdk.auth.HttpBearerAuth;
import com.telnyx.sdk.model.AvailablePhoneNumber;
import com.telnyx.sdk.model.CreateNumberOrderRequest;
import com.telnyx.sdk.model.CreateNumberReservationRequest;
import com.telnyx.sdk.model.ListAvailablePhoneNumbersResponse;
import com.telnyx.sdk.model.NumberOrder;
import com.telnyx.sdk.model.NumberOrderEvent;
import com.telnyx.sdk.model.NumberOrderResponse;
import com.telnyx.sdk.model.NumberReservation;
import com.telnyx.sdk.model.NumberReservationResponse;
import com.telnyx.sdk.model.PhoneNumber;
import com.telnyx.sdk.model.ReservedPhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ExampleApplication {

    private static final Dotenv dotenv = Dotenv.load();
    private final String YOUR_TELNYX_MESSAGING_PROFILE_ID = dotenv.get("TELNYX_MESSAGING_PROFILE_ID");
    private static final String NUMBER_ORDER_CALLBACK_PATH = "/numbers/orders";
    private static final String NUMBERS_PATH = "/numbers";
    private static final String RESERVATIONS_PATH = "/reservations";

    private static ApiClient defaultClient;


    // Configure HTTP bearer authorization: bearerAuth


    public static void main(String[] args) {
        final String YOUR_TELNYX_API_KEY = dotenv.get("TELNYX_API_KEY");

        defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.telnyx.com/v2");
        HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
        bearerAuth.setBearerToken(YOUR_TELNYX_API_KEY);
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

    @PostMapping(RESERVATIONS_PATH)
    public NumberReservation reservationOrder(@RequestBody Map<String,String> allParams){
        String phoneNumber = allParams.get("phoneNumber");

        NumberReservationsApi apiInstance = new NumberReservationsApi(defaultClient);
        CreateNumberReservationRequest createNumberReservationRequest = new CreateNumberReservationRequest()
            .addPhoneNumbersItem(new ReservedPhoneNumber().phoneNumber(phoneNumber));
        try {
            NumberReservationResponse result = apiInstance.createNumberReservation(createNumberReservationRequest);
            NumberReservation reservation = result.getData();
            System.out.println(result);
            return reservation;
        } catch (ApiException e) {
            System.err.println("Exception when calling NumberReservationsApi#createNumberReservation");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            return new NumberReservation();
        }
    }

    @PostMapping(NUMBER_ORDER_CALLBACK_PATH)
    public String numberOrder(@RequestBody NumberOrderEvent numberOrderEvent) {
        NumberOrder numberOrder = numberOrderEvent.getData().getPayload();
        numberOrder.getPhoneNumbers().forEach(System.out::println);
        return numberOrder.getPhoneNumbers().get(0).getId().toString();
    }

    @GetMapping(NUMBERS_PATH)
    public List<String> numberSearch(@RequestParam Map<String,String> allParams){
        String countryCode = allParams.get("countryCode");
        String state = allParams.get("state");
        String city = allParams.get("city");
        Integer limit = Integer.parseInt(allParams.get("limit"));

        NumberSearchApi apiInstance = new NumberSearchApi(defaultClient);

        try {
            ListAvailablePhoneNumbersResponse availablePhoneNumbers = apiInstance
                    .listAvailablePhoneNumbers()
                    .filterCountryCode(countryCode)
                    .filterAdministrativeArea(state)
                    .filterLocality(city)
                    .filterLimit(limit)
                    .execute();
            List<String> phoneNumbers = availablePhoneNumbers
                .getData()
                .stream()
                .map(AvailablePhoneNumber::getPhoneNumber)
                .collect(Collectors.toList());
            return phoneNumbers;
        } catch (Exception e) {
            System.err.println("Exception when calling NumberSearchApi#listAvailablePhoneNumbers");
            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    @PostMapping(NUMBERS_PATH)
    public NumberOrder numberOrder(@RequestBody Map<String,String> allParams){
        String phoneNumber = allParams.get("phoneNumber");

        NumberOrdersApi apiInstance = new NumberOrdersApi(defaultClient);
        CreateNumberOrderRequest createNumberOrderRequest = new CreateNumberOrderRequest()
            .addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumber))
            .messagingProfileId(YOUR_TELNYX_MESSAGING_PROFILE_ID);
        try {
            NumberOrderResponse result = apiInstance.createNumberOrder(createNumberOrderRequest);
            return result.getData();
        } catch (ApiException e) {
            System.err.println("Exception when calling NumberOrdersApi#createNumberOrder");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            return new NumberOrder();
        }
    }


}
