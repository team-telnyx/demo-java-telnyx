package com.main.scenarios;

import com.google.gson.Gson;
import com.main.model.SearchNumbersResponse;
import com.telnyx.sdk.apis.NumberSearchApi;
import com.telnyx.sdk.models.ListAvailablePhoneNumbersResponse;


public class NumberSearchScenarios implements TestScenario{
    private final NumberSearchApi apiInstance = new NumberSearchApi();

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
        SearchNumbersResponse response = new SearchNumbersResponse();

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
        SearchNumbersResponse response = new SearchNumbersResponse();

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
        SearchNumbersResponse response = new SearchNumbersResponse();

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

    public void search_for_phone_numbers_that_begin_with_22() {
        //given
        String startsWith = "22";
        SearchNumbersResponse response = new SearchNumbersResponse();

        //when
        ListAvailablePhoneNumbersResponse availablePhoneNumbers = null;
        try {
            availablePhoneNumbers = apiInstance.listAvailablePhoneNumbers(
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
        assert availablePhoneNumbers != null;
        assert !availablePhoneNumbers.getData().isEmpty();
    }

    public void search_for_100_phone_numbers_in_Chicago() {
        //given
        String state = "chi";
        int filterLimit = 100;
        SearchNumbersResponse response = new SearchNumbersResponse();

        //when
        ListAvailablePhoneNumbersResponse availablePhoneNumbers = null;
        try {
            availablePhoneNumbers = apiInstance.listAvailablePhoneNumbers(null,
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
        assert availablePhoneNumbers != null;
        assert !availablePhoneNumbers.getData().isEmpty();
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
