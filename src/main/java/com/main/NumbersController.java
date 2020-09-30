package com.main;

import com.google.gson.Gson;
import com.main.model.SearchNumbersResponse;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.apis.NumberOrdersApi;
import com.telnyx.sdk.apis.NumberReservationsApi;
import com.telnyx.sdk.apis.NumberSearchApi;
import com.telnyx.sdk.models.CreateNumberOrderRequest;
import com.telnyx.sdk.models.CreateNumberReservationRequest;
import com.telnyx.sdk.models.ListAvailablePhoneNumbersResponse;
import com.telnyx.sdk.models.NumberOrderResponse;
import com.telnyx.sdk.models.NumberReservationResponse;
import com.telnyx.sdk.models.PhoneNumber;
import com.telnyx.sdk.models.ReservedPhoneNumber;
import java.util.ArrayList;
import java.util.List;

public class NumbersController {
        public static SearchNumbersResponse searchNumbers(String countryCode, String state, String city){
        NumberSearchApi apiInstance = new NumberSearchApi();
        SearchNumbersResponse numbersResponse = new SearchNumbersResponse();

        try {
            ListAvailablePhoneNumbersResponse availablePhoneNumbers = apiInstance
                    .listAvailablePhoneNumbers(
                            null,
                            null,
                            null,
                            city,
                            state,
                            countryCode,
                            null,
                            null,
                            null,
                            null,
                            2,
                            null,
                            null,
                            null
                    );
            numbersResponse.setApiResponse(availablePhoneNumbers);
            numbersResponse.setJson(new Gson().toJson(availablePhoneNumbers));
            numbersResponse.setValid(true);
        } catch (Exception e) {
            System.err.println("Exception when calling NumberSearchApi#listAvailablePhoneNumbers");
            e.printStackTrace();
            numbersResponse.setJson("{ \"error\": \"Problem searching phone numbers\"}");
            numbersResponse.setValid(false);
        }
        return numbersResponse;
    }

    public static String orderNumber(String phoneNumber) {
        NumberOrdersApi apiInstance = new NumberOrdersApi();
        CreateNumberOrderRequest orderRequest = new CreateNumberOrderRequest()
                .addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumber));
        try {
            NumberOrderResponse orderResponse = apiInstance.createNumberOrder(orderRequest);
            return new Gson().toJson(orderResponse);
        } catch (ApiException e) {
            System.err.println("Exception when calling NumberOrdersApi#createNumberOrder");
            e.printStackTrace();
            return e.getResponseBody();
        }
        catch (Exception e) {
            System.err.println("Exception when calling NumberOrdersApi#createNumberOrder");
            e.printStackTrace();
            return String.format(
                    "{ \"error\": \"Problem searching phone numbers\", \"message\":\"%s\"}", e.getLocalizedMessage());
        }
    }

    public static String reserveNumbers(ListAvailablePhoneNumbersResponse apiResponse) {
        NumberReservationsApi apiInstance = new NumberReservationsApi();

        List<ReservedPhoneNumber> numberList = new ArrayList<ReservedPhoneNumber>();
        apiResponse.getData().forEach(availablePhoneNumber -> {
            ReservedPhoneNumber phoneNumber = new ReservedPhoneNumber()
                    .phoneNumber(availablePhoneNumber.getPhoneNumber());
            numberList.add(phoneNumber);
        });
        CreateNumberReservationRequest numberReservationRequest = new CreateNumberReservationRequest()
                .phoneNumbers(numberList);
        try {
            apiInstance.createNumberReservation(numberReservationRequest);
            NumberReservationResponse numberReservations = apiInstance
                    .createNumberReservation(numberReservationRequest);
            return new Gson().toJson(numberReservations);
        } catch (Exception e) {
            System.err.println("Exception when calling NumberReservationsApi#createNumberReservations");
            e.printStackTrace();
            return "{ \"error\": \"Problem reserving phone numbers\"}";
        }
    }
}
