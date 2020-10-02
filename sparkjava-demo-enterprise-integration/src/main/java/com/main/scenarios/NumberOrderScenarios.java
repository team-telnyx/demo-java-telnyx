package com.main.scenarios;


import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.apis.NumberOrdersApi;
import com.telnyx.sdk.apis.NumberSearchApi;
import com.telnyx.sdk.models.AvailablePhoneNumber;
import com.telnyx.sdk.models.CreateNumberOrderRequest;
import com.telnyx.sdk.models.ListAvailablePhoneNumbersResponse;
import com.telnyx.sdk.models.NumberOrderResponse;
import com.telnyx.sdk.models.PhoneNumber;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NumberOrderScenarios implements TestScenario {
    private final NumberOrdersApi apiInstance = new NumberOrdersApi();

    public void order_a_US_phone_number() {
        //given
        String countryCode = "US";
        NumberOrderResponse response = new NumberOrderResponse();
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersBasedOnLocation(countryCode, null, null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //when
        try {
            response = apiInstance.createNumberOrder(
                    new CreateNumberOrderRequest().addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumbers.get(0))));
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void order_5_US_phone_numbers() {
        //given
        String countryCode = "US";
        NumberOrderResponse response = new NumberOrderResponse();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        try {
            List<String> numbers = getPhoneNumbersBasedOnLocation(countryCode, null, null, 5);

            phoneNumbers = numbers.stream().map(number ->
                    new PhoneNumber().phoneNumber(number)).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //when
        try {
            response = apiInstance.createNumberOrder(new CreateNumberOrderRequest().phoneNumbers(phoneNumbers));
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void order_a_spanish_phone_number() {
        //given
        String countryCode = "ES";
        NumberOrderResponse response = new NumberOrderResponse();
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersBasedOnLocation(countryCode, null, null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //when
        try {
            response = apiInstance.createNumberOrder(
                    new CreateNumberOrderRequest().addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumbers.get(0))));
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void search_and_then_order_a_number_from_paris() {
        //given
        String city = "paris";
        NumberOrderResponse response = new NumberOrderResponse();
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersBasedOnLocation(null, null, city, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //when
        try {
            response = apiInstance.createNumberOrder(
                    new CreateNumberOrderRequest().addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumbers.get(0))));
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }


    private List<String> getPhoneNumbersBasedOnLocation(String countryCode, String state, String city, int count) throws ApiException {
        ListAvailablePhoneNumbersResponse availablePhoneNumbers = new NumberSearchApi()
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
                        count,
                        null,
                        null,
                        null);
        return availablePhoneNumbers.getData().stream()
                .map(AvailablePhoneNumber::getPhoneNumber)
                .collect(Collectors.toList());
    }

    @Override
    public void runAllScenarios() {
        order_a_US_phone_number();
        order_5_US_phone_numbers();
        order_a_spanish_phone_number();
        search_and_then_order_a_number_from_paris();

    }
}
