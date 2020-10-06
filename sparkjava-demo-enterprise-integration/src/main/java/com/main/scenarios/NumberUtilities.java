package com.main.scenarios;


import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.apis.NumberOrdersApi;
import com.telnyx.sdk.apis.NumberSearchApi;
import com.telnyx.sdk.models.AvailablePhoneNumber;
import com.telnyx.sdk.models.CreateNumberOrderRequest;
import com.telnyx.sdk.models.ListAvailablePhoneNumbersResponse;
import com.telnyx.sdk.models.PhoneNumber;

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
                        new CreateNumberOrderRequest().addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumber)))
                .getData().getId();
    }
}
