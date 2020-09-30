package com.main.scenarios;

import io.swagger.client.ApiException;
import io.swagger.client.api.NumberOrdersApi;
import io.swagger.client.api.NumberSearchApi;
import io.swagger.client.model.AvailablePhoneNumber;
import io.swagger.client.model.CreateNumberOrderResponse;
import io.swagger.client.model.ListAvailablePhoneNumbersResponse;
import io.swagger.client.model.NumberOrder;
import io.swagger.client.model.PhoneNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NumberOrderScenarios implements TestScenario {
    private NumberOrdersApi apiInstance = new NumberOrdersApi();

    public void order_a_US_phone_number() {
        //given
        String countryCode = "US";
        CreateNumberOrderResponse response = new CreateNumberOrderResponse();
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersForCountry(countryCode, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //when
        try {
            response = apiInstance.createNumberOrder(
                    new NumberOrder().addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumbers.get(0))));
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: Assert false
        }

        //then
        //TODO: check the actual and expected and assert equality
    }

    public void order_5_US_phone_numbers() {
        //given
        String countryCode = "US";
        CreateNumberOrderResponse response = new CreateNumberOrderResponse();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        try {
            List<String> numbers = getPhoneNumbersForCountry(countryCode, 5);

            phoneNumbers = numbers.stream().map(number ->
                    new PhoneNumber().phoneNumber(number)).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //when
        try {
            response = apiInstance.createNumberOrder(new NumberOrder().phoneNumbers(phoneNumbers));
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: Assert false
        }

        //then
        //TODO: check the actual and expected and assert equality
    }

    public void order_a_spanish_phone_number() {
        //given
        String countryCode = "ES";
        CreateNumberOrderResponse response = new CreateNumberOrderResponse();
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersForCountry(countryCode, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //when
        try {
            response = apiInstance.createNumberOrder(
                    new NumberOrder().addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumbers.get(0))));
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: Assert false
        }

        //then
        //TODO: check the actual and expected and assert equality
    }

    public void search_and_then_order_a_number_from_paris() {
        //given
        String countryCode = "ES";
        CreateNumberOrderResponse response = new CreateNumberOrderResponse();
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersForCountry(countryCode, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //when
        try {
            response = apiInstance.createNumberOrder(
                    new NumberOrder().addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumbers.get(0))));
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: Assert false
        }

        //then
        //TODO: check the actual and expected and assert equality
    }

    private List<String> getPhoneNumbersForCountry(String countryCode, int count) throws ApiException {
        ListAvailablePhoneNumbersResponse availablePhoneNumbers = new NumberSearchApi()
                .listAvailablePhoneNumbers(
                        null,
                        null,
                        null,
                        null,
                        null,
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

    }
}
