package com.main.scenarios;

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
        ListAvailablePhoneNumbersResponse response = null;

        //when
        try {
            response = apiInstance.listAvailablePhoneNumbers()
                    .filterCountryCode(countryCode)
                    .execute();
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    public void search_for_phone_numbers_by_area_code() {
        //given
        String areaCode = "IL";
        ListAvailablePhoneNumbersResponse availablePhoneNumbers = null;

        //when
        try {
            availablePhoneNumbers = apiInstance.listAvailablePhoneNumbers()
                    .filterAdministrativeArea(areaCode)
                    .execute();
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
        ListAvailablePhoneNumbersResponse availablePhoneNumbers = null;

        //when
        try {
            availablePhoneNumbers = apiInstance.listAvailablePhoneNumbers()
                    .filterCountryCode(countryCode)
                    .execute();
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
            response = apiInstance.listAvailablePhoneNumbers()
                    .filterCountryCode(countryCode)
                    .execute();
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
            response = apiInstance.listAvailablePhoneNumbers()
                    .filterPhoneNumberStartsWith(startsWith)
                    .execute();
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    public void search_for_100_phone_numbers_in_Chicago() {
        //given
        String city = "chi";
        int limit = 100;
        ListAvailablePhoneNumbersResponse response = null;

        //when
        try {
            response = apiInstance.listAvailablePhoneNumbers()
                    .filterLocality(city)
                    .filterLimit(limit)
                    .execute();
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
