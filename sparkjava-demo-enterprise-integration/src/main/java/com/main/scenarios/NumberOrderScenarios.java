package com.main.scenarios;


import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.api.NumberOrdersApi;
import com.telnyx.sdk.api.NumberSearchApi;
import com.telnyx.sdk.model.AvailablePhoneNumber;
import com.telnyx.sdk.model.CreateNumberOrderRequest;
import com.telnyx.sdk.model.ListNumberOrdersResponse;
import com.telnyx.sdk.model.NumberOrderResponse;
import com.telnyx.sdk.model.PhoneNumber;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NumberOrderScenarios implements TestScenario {
    private NumberOrdersApi numberOrderApi;
    private NumberSearchApi numberSearchApi;

    public NumberOrderScenarios(ApiClient client) {
        numberSearchApi = new NumberSearchApi(client);
        numberOrderApi = new NumberOrdersApi(client);
    }

    public void order_a_US_phone_number() {
        //given
        String countryCode = "US";
        NumberOrderResponse response = null;
        String phoneNumber = null;
        try {
            phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterCountryCode(countryCode)
                    .filterLimit(1)
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = numberOrderApi.createNumberOrder(
                    new CreateNumberOrderRequest()
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
            phoneNumbers = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterCountryCode(countryCode)
                    .filterLimit(5)
                    .execute()
                    .getData())
                    .stream()
                    .map(AvailablePhoneNumber::getPhoneNumber)
                    .map(number -> new PhoneNumber().phoneNumber(number))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = numberOrderApi.createNumberOrder(
                    new CreateNumberOrderRequest().phoneNumbers(phoneNumbers));
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
        String phoneNumber = null;
        try {
            phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterCountryCode(countryCode)
                    .filterLimit(1)
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = numberOrderApi.createNumberOrder(
                    new CreateNumberOrderRequest()
                            .addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumber)));
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
        String phoneNumber = null;
        try {
            phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterLocality(city)
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = numberOrderApi.createNumberOrder(
                    new CreateNumberOrderRequest()
                            .addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumber)));
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void get_the_second_page_of_results_for_phone_number_orders() {
        //given
        order_5_US_phone_numbers();

        //when
        ListNumberOrdersResponse response = null;
        try {
            response = numberOrderApi.listNumberOrders()
                    .pageNumber(1)
                    .pageSize(1)
                    .execute();
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void get_a_page_of_results_that_only_has_2_results_for_phone_number_orders() {
        //given
        order_5_US_phone_numbers();

        //when
        ListNumberOrdersResponse response = null;
        try {
            response = numberOrderApi.listNumberOrders()
                    .pageNumber(0)
                    .pageSize(2)
                    .execute();
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;

    }

    public void filter_phone_number_orders_by_created_at_date() {
        //given
        ListNumberOrdersResponse response = null;
        try {
            String phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterLimit(1)
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();

            numberOrderApi.createNumberOrder(
                    new CreateNumberOrderRequest()
                            .addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumber)));
        } catch (ApiException e) {
            assert false;
        }
        LocalDate today = LocalDate.now();
        OffsetDateTime startOfToday = today.atTime(OffsetTime.MIN);
        OffsetDateTime endOfToday = today.atTime(OffsetTime.MAX);

        //when
        try {
            response = numberOrderApi.listNumberOrders()
                    .filterCreatedAtGt(startOfToday.toString())
                    .filterCreatedAtLt(endOfToday.toString())
                    .execute();
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
        get_the_second_page_of_results_for_phone_number_orders();
        get_a_page_of_results_that_only_has_2_results_for_phone_number_orders();
        filter_phone_number_orders_by_created_at_date();
    }
}
