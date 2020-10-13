package com.main.scenarios;


import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.apis.NumberReservationsApi;
import com.telnyx.sdk.apis.NumberSearchApi;
import com.telnyx.sdk.models.AvailablePhoneNumber;
import com.telnyx.sdk.models.CreateNumberReservationRequest;
import com.telnyx.sdk.models.NumberReservationResponse;
import com.telnyx.sdk.models.ReservedPhoneNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class NumberReservationScenarios implements TestScenario {
    private NumberReservationsApi numberReservationsApi;
    private NumberSearchApi numberSearchApi;

    public NumberReservationScenarios(ApiClient client) {
        numberReservationsApi = new NumberReservationsApi(client);
        numberSearchApi = new NumberSearchApi(client);
    }

    public void reserve_a_US_phone_number() {
        //given
        String countryCode = "US";
        NumberReservationResponse response = null;
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
            response = numberReservationsApi.createNumberReservation(
                    new CreateNumberReservationRequest()
                            .addPhoneNumbersItem(new ReservedPhoneNumber().phoneNumber(phoneNumber)))
                    .execute();
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
        UUID reservationId = null;
        try {
            String phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterLimit(1)
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();

            reservationId = Objects.requireNonNull(numberReservationsApi.createNumberReservation(
                    new CreateNumberReservationRequest()
                            .addPhoneNumbersItem(new ReservedPhoneNumber().phoneNumber(phoneNumber)))
                    .execute()
                    .getData())
                    .getId();
            assert reservationId != null;
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = numberReservationsApi.extendNumberReservationExpiryTime(reservationId.toString())
                    .execute();
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
            phoneNumbers = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterLimit(5)
                    .execute()
                    .getData())
                    .stream()
                    .map(AvailablePhoneNumber::getPhoneNumber)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            List<ReservedPhoneNumber> reservedPhoneNumbers = phoneNumbers.stream()
                    .map(phoneNumber -> new ReservedPhoneNumber().phoneNumber(phoneNumber))
                    .collect(Collectors.toList());
            response = numberReservationsApi.createNumberReservation(
                    new CreateNumberReservationRequest().phoneNumbers(reservedPhoneNumbers))
                    .execute();
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
