package com.main.scenarios;

import io.swagger.client.ApiException;
import io.swagger.client.api.NumberOrdersApi;
import io.swagger.client.api.NumberSearchApi;
import io.swagger.client.model.AvailablePhoneNumber;
import io.swagger.client.model.ListAvailablePhoneNumbersResponse;
import io.swagger.client.model.NumberOrder;
import io.swagger.client.model.PhoneNumber;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NumberUtilities {
    public static List<String> getPhoneNumbersBasedOnLocation(String countryCode, String state, String city, int count) throws ApiException {
        NumberSearchApi apiInstance = new NumberSearchApi();
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
                        count,
                        null,
                        null,
                        null);
        return availablePhoneNumbers.getData().stream()
                .map(AvailablePhoneNumber::getPhoneNumber)
                .collect(Collectors.toList());
    }

    public static UUID orderNumber(String phoneNumber) throws ApiException {
        NumberOrdersApi numberOrdersApi = new NumberOrdersApi();
        return numberOrdersApi
                .createNumberOrder(
                        new NumberOrder().addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumber)))
                .getData().getId();
    }
}
