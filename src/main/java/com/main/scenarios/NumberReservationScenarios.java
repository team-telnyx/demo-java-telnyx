package com.main.scenarios;

import io.swagger.client.ApiClient;
import io.swagger.client.api.NumberReservationsApi;
import io.swagger.client.model.CreateNumberReservationsResponse;
import io.swagger.client.model.NumberReservation;
import io.swagger.client.model.ReservedPhoneNumber;
import io.swagger.client.model.RetrieveNumberReservationResponse;

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
        CreateNumberReservationsResponse response = null;
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersBasedOnLocation(countryCode, null, null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //when
        try {
            response = apiInstance.createNumberReservations(
                    new NumberReservation().addPhoneNumbersItem(new ReservedPhoneNumber().phoneNumber(phoneNumbers.get(0))));
        } catch (Exception e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void extend_the_reservation_of_a_phone_number() {
        //given
        RetrieveNumberReservationResponse response = null;
        String reservationId = null;
        try {
            List<String> phoneNumbers = getPhoneNumbersBasedOnLocation(null, null, null, 1);
            CreateNumberReservationsResponse numberReservationsResponse = apiInstance.createNumberReservations(
                    new NumberReservation().addPhoneNumbersItem(new ReservedPhoneNumber().phoneNumber(phoneNumbers.get(0))));
            reservationId = numberReservationsResponse.getData().getId().toString();
        } catch (Exception e) {
            e.printStackTrace();
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
        CreateNumberReservationsResponse response = null;
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersBasedOnLocation(null, null, null, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //when
        try {
            List<ReservedPhoneNumber> reservedPhoneNumbers = phoneNumbers.stream()
                    .map(phoneNumber -> new ReservedPhoneNumber().phoneNumber(phoneNumber))
                    .collect(Collectors.toList());
            response = apiInstance.createNumberReservations(
                    new NumberReservation().phoneNumbers(reservedPhoneNumbers));
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
