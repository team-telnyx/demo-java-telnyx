package com.main.scenarios;

import com.main.model.SearchNumbersResponse;
import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.apis.NumberSearchApi;
import com.telnyx.sdk.models.ListAvailablePhoneNumbersResponse;

public class NumberSearchScenarios implements TestScenario {
    private NumberSearchApi apiInstance;

    public NumberSearchScenarios(ApiClient client) {
        apiInstance = new NumberSearchApi(client);
    }

    public void search_for_phone_numbers_in_the_us() {
        //given
        String countryCode = "US";

        //when
        ListAvailablePhoneNumbersResponse availablePhoneNumbers = null;
        try {
            availablePhoneNumbers = apiInstance.listAvailablePhoneNumbers(null,
                    null,
                    null,
                    null,
                    null,
                    countryCode,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            assert false;
        }

        //then
        assert availablePhoneNumbers != null;
        assert !availablePhoneNumbers.getData().isEmpty();
    }

    public void search_for_phone_numbers_by_area_code() {
        //given
        //TODO: which area code? and what is the filter name
        String areaCode = "IL";
        SearchNumbersResponse response = null;

        //when
        ListAvailablePhoneNumbersResponse availablePhoneNumbers = null;
        try {
            availablePhoneNumbers = apiInstance.listAvailablePhoneNumbers(null,
                    null,
                    null,
                    null,
                    areaCode,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            assert false;
        }

        //then
        assert availablePhoneNumbers != null;
        assert !availablePhoneNumbers.getData().isEmpty();
    }

    public void search_for_phone_numbers_in_Canada() {
        //given
        String countryCode = "CA";
        SearchNumbersResponse response = null;

        //when
        ListAvailablePhoneNumbersResponse availablePhoneNumbers = null;
        try {
            availablePhoneNumbers = apiInstance.listAvailablePhoneNumbers(null,
                    null,
                    null,
                    null,
                    null,
                    countryCode,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            assert false;
        }

        //then
        assert availablePhoneNumbers != null;
        assert !availablePhoneNumbers.getData().isEmpty();
    }

    public void search_for_phone_numbers_in_spain() {
        //given
        String countryCode = "ES";
        ListAvailablePhoneNumbersResponse response = null;

        //when
        try {
            response = apiInstance.listAvailablePhoneNumbers(null,
                    null,
                    null,
                    null,
                    null,
                    countryCode,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    public void search_for_phone_numbers_that_begin_with_22() {
        //given
        String startsWith = "22";
        ListAvailablePhoneNumbersResponse response = null;

        //when
        try {
            response = apiInstance.listAvailablePhoneNumbers(
                    startsWith,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    public void search_for_100_phone_numbers_in_Chicago() {
        //given
        String state = "chi";
        int filterLimit = 100;
        ListAvailablePhoneNumbersResponse response = null;

        //when
        try {
            response = apiInstance.listAvailablePhoneNumbers(null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    filterLimit,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    @Override
    public void runAllScenarios() {
        search_for_phone_numbers_in_the_us();
        search_for_phone_numbers_by_area_code();
        search_for_phone_numbers_in_Canada();
        search_for_phone_numbers_in_spain();
        search_for_phone_numbers_that_begin_with_22();
        search_for_100_phone_numbers_in_Chicago();
    }
}
