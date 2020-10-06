package com.main.scenarios;


import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.apis.NumberReservationsApi;
import com.telnyx.sdk.models.CreateNumberReservationRequest;
import com.telnyx.sdk.models.NumberReservationResponse;
import com.telnyx.sdk.models.ReservedPhoneNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.main.scenarios.NumberUtilities.getPhoneNumbersBasedOnLocation;

public class NumberReservationScenarios implements TestScenario {
    private NumberReservationsApi apiInstance;

    public NumberReservationScenarios(ApiClient client) {
        apiInstance = new NumberReservationsApi(client);
    }

    public void reserve_a_US_phone_number() {
        //given
        String countryCode = "US";
        NumberReservationResponse response = null;
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersBasedOnLocation(countryCode, null, null, 1);
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = apiInstance.createNumberReservation(
                    new CreateNumberReservationRequest().addPhoneNumbersItem(new ReservedPhoneNumber().phoneNumber(phoneNumbers.get(0))));
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void extend_the_reservation_of_a_phone_number() {
        //given
        NumberReservationResponse response = null;
        String reservationId = null;
        try {
            List<String> phoneNumbers = getPhoneNumbersBasedOnLocation(null, null, null, 1);
            NumberReservationResponse numberReservationsResponse = apiInstance.createNumberReservation(
                    new CreateNumberReservationRequest().addPhoneNumbersItem(new ReservedPhoneNumber().phoneNumber(phoneNumbers.get(0))));
            reservationId = numberReservationsResponse.getData().getId().toString();
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = apiInstance.extendNumberReservationExpiryTime(reservationId);
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void reserve_5_phone_numbers() {
        //given
        NumberReservationResponse response = null;
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersBasedOnLocation(null, null, null, 5);
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            List<ReservedPhoneNumber> reservedPhoneNumbers = phoneNumbers.stream()
                    .map(phoneNumber -> new ReservedPhoneNumber().phoneNumber(phoneNumber))
                    .collect(Collectors.toList());
            response = apiInstance.createNumberReservation(
                    new CreateNumberReservationRequest().phoneNumbers(reservedPhoneNumbers));
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    @Override
    public void runAllScenarios() {
        reserve_a_US_phone_number();
        extend_the_reservation_of_a_phone_number();
        reserve_5_phone_numbers();
    }
}
