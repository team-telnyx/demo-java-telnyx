package com.main.scenarios;


import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.apis.NumberOrdersApi;
import com.telnyx.sdk.models.CreateNumberOrderRequest;
import com.telnyx.sdk.models.ListNumberOrdersResponse;
import com.telnyx.sdk.models.NumberOrderResponse;
import com.telnyx.sdk.models.PhoneNumber;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.main.scenarios.NumberUtilities.getPhoneNumbersBasedOnLocation;
import static com.main.scenarios.NumberUtilities.orderNumber;

public class NumberOrderScenarios implements TestScenario {
    private NumberOrdersApi apiInstance;

    public NumberOrderScenarios(ApiClient client) {
        apiInstance = new NumberOrdersApi(client);
    }

    public void order_a_US_phone_number() {
        //given
        String countryCode = "US";
        NumberOrderResponse response = null;
        String phoneNumber = null;
        try {
            phoneNumber = getPhoneNumbersBasedOnLocation(countryCode, null, null, 1).get(0);
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = apiInstance.createNumberOrder(new CreateNumberOrderRequest()
                    .phoneNumbers(Collections.singletonList(new PhoneNumber().phoneNumber(phoneNumber))));
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
        NumberOrderResponse response = null;
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        try {
            List<String> numbers = getPhoneNumbersBasedOnLocation(countryCode, null, null, 5);

            phoneNumbers = numbers.stream().map(number ->
                    new PhoneNumber().phoneNumber(number)).collect(Collectors.toList());

        } catch (Exception e) {
            assert false;
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
        NumberOrderResponse response = null;
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersBasedOnLocation(countryCode, null, null, 1);
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = apiInstance.createNumberOrder(
                    new CreateNumberOrderRequest().addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumbers.get(0))));
        } catch (Exception e) {
            assert false;
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void search_and_then_order_a_number_from_paris() {
        //given
        String city = "paris";
        NumberOrderResponse response = null;
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersBasedOnLocation(null, null, city, 1);
        } catch (Exception e) {
            assert false;
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

    public void get_the_second_page_of_results_for_phone_number_orders() {
        //TODO: This version of SDK does not contain the pagination params
    }

    public void get_a_page_of_results_that_only_has_2_results_for_phone_number_orders() {
        //TODO: This version of SDK does not contain the pagination params
    }

    public void filter_phone_number_orders_by_created_at_date() {
        //given
        ListNumberOrdersResponse response = null;
        try {
            String phoneNumber = getPhoneNumbersBasedOnLocation(null, null, null, 1).get(0);
            UUID phoneNumberId = orderNumber(phoneNumber);
        } catch (ApiException e) {
            assert false;
        }
        LocalDate today = LocalDate.now();
        OffsetDateTime startOfToday = today.atTime(OffsetTime.MIN);
        OffsetDateTime endOfToday = today.atTime(OffsetTime.MAX);

        //when
        try {
            response = apiInstance.listNumberOrders(
                    null,
                    startOfToday.toString(),
                    endOfToday.toString(),
                    null,
                    null,
                    null
            );
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert !response.getData().isEmpty();

    }

    @Override
    public void runAllScenarios() {
        order_a_US_phone_number();
        order_5_US_phone_numbers();
        order_a_spanish_phone_number();
        search_and_then_order_a_number_from_paris();
//        get_the_second_page_of_results_for_phone_number_orders();
//        get_a_page_of_results_that_only_has_2_results_for_phone_number_orders();
        filter_phone_number_orders_by_created_at_date();
    }
}
