package com.main.scenarios;


import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.apis.NumberConfigurationsApi;
import com.telnyx.sdk.apis.NumberOrdersApi;
import com.telnyx.sdk.apis.NumberSearchApi;
import com.telnyx.sdk.models.CallRecording;
import com.telnyx.sdk.models.CreateNumberOrderRequest;
import com.telnyx.sdk.models.ListMessagingSettingsResponse;
import com.telnyx.sdk.models.ListPhoneNumbersResponse;
import com.telnyx.sdk.models.ListPhoneNumbersWithVoiceSettingsResponse;
import com.telnyx.sdk.models.PhoneNumber;
import com.telnyx.sdk.models.PhoneNumberEnableEmergency;
import com.telnyx.sdk.models.PhoneNumberEnableEmergencyRequest;
import com.telnyx.sdk.models.RetrieveMessagingSettingsResponse;
import com.telnyx.sdk.models.RetrievePhoneNumberVoiceResponse;
import com.telnyx.sdk.models.UpdatePhoneNumberMessagingSettingsRequest;
import com.telnyx.sdk.models.UpdatePhoneNumberRequest;
import com.telnyx.sdk.models.UpdatePhoneNumberVoiceSettingsRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NumberConfigurationsScenarios implements TestScenario {
    private final String connectionId = "1476046853102371900";
    private NumberConfigurationsApi numberConfigurationsApi;
    private NumberSearchApi numberSearchApi;
    private NumberOrdersApi numberOrdersApi;

    public NumberConfigurationsScenarios(ApiClient client) {
        numberConfigurationsApi = new NumberConfigurationsApi(client);
        numberSearchApi = new NumberSearchApi(client);
        numberOrdersApi = new NumberOrdersApi(client);
    }

    public void get_all_of_your_phone_numbers() {
        //given
        ListPhoneNumbersResponse response = null;

        //when
        try {
            response = numberConfigurationsApi.listPhoneNumbers()
                    .pageNumber(0)
                    .pageSize(1000)
                    .execute();
        } catch (ApiException e) {
            assert false;
        }
        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    public void get_a_specific_phone_number() {
        //given
        ListPhoneNumbersResponse response = null;
        String phoneNumber = null;
        try {
            phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterLimit(1)
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();
        } catch (ApiException e) {
            assert false;
        }

        //when
        try {
            response = numberConfigurationsApi.listPhoneNumbers()
                    .pageNumber(0)
                    .pageSize(1)
                    .filterPhoneNumber(phoneNumber)
                    .execute();
        } catch (ApiException e) {
            assert false;
        }
        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void view_voice_settings_of_all_phone_numbers() {
        //given
        ListPhoneNumbersWithVoiceSettingsResponse response = null;
        //when
        try {
            response = numberConfigurationsApi.listPhoneNumbersWithVoiceSettings()
                    .pageNumber(0)
                    .pageSize(100)
                    .execute();
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    public void view_voice_settings_of_a_specific_phone_number() {
        //given
        String phoneNumberId = null;
        RetrievePhoneNumberVoiceResponse response = null;
        try {
            String phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterLimit(1)
                    .filterFeatures(List.of("sms", "voice"))
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();

            numberOrdersApi.createNumberOrder(new CreateNumberOrderRequest()
                    .phoneNumbers(Collections.singletonList(new PhoneNumber().phoneNumber(phoneNumber))))
                    .execute();

            phoneNumberId = Objects.requireNonNull(numberConfigurationsApi.retrievePhoneNumber(phoneNumber)
                    .execute()
                    .getData())
                    .getId();
        } catch (ApiException e) {
            assert false;
        }

        //when
        try {
            response = numberConfigurationsApi.retrievePhoneNumberWithVoiceSettings(phoneNumberId)
                    .execute();
        } catch (ApiException e) {
            assert false;
        }
        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void update_the_voice_settings_of_a_phone_number_to_enable_inbound_call_recording() {
        //given
        String phoneNumberId = null;
        RetrievePhoneNumberVoiceResponse response = null;
        try {
            String phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterLimit(1)
                    .filterFeatures(List.of("sms", "voice"))
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();

            numberOrdersApi.createNumberOrder(new CreateNumberOrderRequest()
                    .phoneNumbers(Collections.singletonList(new PhoneNumber().phoneNumber(phoneNumber))))
                    .execute();

            phoneNumberId = Objects.requireNonNull(numberConfigurationsApi.retrievePhoneNumber(phoneNumber)
                    .execute()
                    .getData())
                    .getId();
        } catch (ApiException e) {
            assert false;
        }

        //when
        try {
            response = numberConfigurationsApi.updatePhoneNumberWithVoiceSettings(phoneNumberId,
                    new UpdatePhoneNumberVoiceSettingsRequest()
                            .callRecording(new CallRecording().inboundCallRecordingEnabled(true)))
                    .execute();
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void update_the_voice_settings_of_a_phone_number_to_attach_the_number_to_a_new_connection() {
        //TODO: We don't have any SDK option to update connection id in this version of SDK
    }

    public void enable_emergency_on_a_phone_number() {
        //given
        String countryCode = "US";
        PhoneNumberEnableEmergency response = null;
        String phoneNumberId = null;
        try {
            String phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterCountryCode(countryCode)
                    .filterLimit(1)
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();

            numberOrdersApi.createNumberOrder(new CreateNumberOrderRequest()
                    .phoneNumbers(Collections.singletonList(new PhoneNumber().phoneNumber(phoneNumber))))
                    .execute();

            phoneNumberId = Objects.requireNonNull(numberConfigurationsApi.retrievePhoneNumber(phoneNumber)
                    .execute()
                    .getData())
                    .getId();
        } catch (ApiException e) {
            assert false;
        }

        //when
        try {
            response = numberConfigurationsApi.enableEmergencyPhoneNumber(
                    phoneNumberId, new PhoneNumberEnableEmergencyRequest()
                            .emergencyEnabled(true))
                    .execute();
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void view_messaging_settings_of_all_phone_numbers() {
        //given
        ListMessagingSettingsResponse response = null;

        //when
        try {
            response = numberConfigurationsApi.listPhoneNumbersWithMessagingSettings()
                    .execute();
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    public void view_messaging_settings_of_a_specific_phone_number() {
        //given
        RetrieveMessagingSettingsResponse response = null;
        String phoneNumberId = null;
        try {
            String phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterLimit(1)
                    .filterFeatures(List.of("sms", "voice"))
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();

            numberOrdersApi.createNumberOrder(new CreateNumberOrderRequest()
                    .phoneNumbers(Collections.singletonList(new PhoneNumber().phoneNumber(phoneNumber))))
                    .execute();

            phoneNumberId = Objects.requireNonNull(numberConfigurationsApi.retrievePhoneNumber(phoneNumber)
                    .execute()
                    .getData())
                    .getId();
        } catch (ApiException e) {
            assert false;
        }


        //when
        try {
            response = numberConfigurationsApi.retrievePhoneNumberWithMessagingSettings(phoneNumberId)
                    .execute();
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void update_the_messaging_settings_of_a_phone_number_to_attach_the_number_to_a_new_messaging_profile() {
        //given
        String targetMessagingProfileId = "400174f2-6eb8-429b-a946-a27646a94a1a";
        RetrieveMessagingSettingsResponse response = null;
        String phoneNumberId = null;
        try {
            String phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterLimit(1)
                    .filterFeatures(List.of("sms", "voice"))
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();

            numberOrdersApi.createNumberOrder(new CreateNumberOrderRequest()
                    .phoneNumbers(Collections.singletonList(new PhoneNumber().phoneNumber(phoneNumber))))
                    .execute();

            phoneNumberId = Objects.requireNonNull(numberConfigurationsApi.retrievePhoneNumber(phoneNumber)
                    .execute()
                    .getData())
                    .getId();
        } catch (ApiException e) {
            assert false;
        }

        //when
        try {
            response = numberConfigurationsApi.updatePhoneNumberWithMessagingSettings(
                    phoneNumberId,
                    new UpdatePhoneNumberMessagingSettingsRequest()
                            .messagingProfileId(targetMessagingProfileId))
                    .execute();
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void get_the_second_page_of_results_for_phone_numbers() {
        //given
        ListPhoneNumbersResponse response = null;

        //when
        try {
            response = numberConfigurationsApi.listPhoneNumbers()
                    .pageNumber(1)
                    .pageSize(10)
                    .execute();
        } catch (ApiException e) {
            assert false;
        }
        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    public void get_a_page_of_results_that_only_has_2_results_for_phone_numbers() {
        //given
        ListPhoneNumbersResponse response = null;

        //when
        try {
            response = numberConfigurationsApi.listPhoneNumbers()
                    .pageNumber(0)
                    .pageSize(2)
                    .execute();
        } catch (ApiException e) {
            assert false;
        }
        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    public void filter_phone_numbers_for_numbers_by_tag() {
        //given
        ListPhoneNumbersResponse response = null;
        String tag = "TEST_TAG";
        try {
            String phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterLimit(1)
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();

            numberOrdersApi.createNumberOrder(new CreateNumberOrderRequest()
                    .phoneNumbers(Collections.singletonList(new PhoneNumber().phoneNumber(phoneNumber))))
                    .execute();

            String phoneNumberId = Objects.requireNonNull(numberConfigurationsApi.retrievePhoneNumber(phoneNumber)
                    .execute()
                    .getData())
                    .getId();

            numberConfigurationsApi.updatePhoneNumber(
                    phoneNumberId,
                    new UpdatePhoneNumberRequest()
                            .tags(Collections.singletonList(tag)))
                    .execute();
        } catch (ApiException e) {
            assert false;
        }


        //when
        try {
            response = numberConfigurationsApi.listPhoneNumbers()
                    .pageNumber(0)
                    .pageSize(2)
                    .filterTag(tag)
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
        get_all_of_your_phone_numbers();
        get_a_specific_phone_number();
        view_voice_settings_of_all_phone_numbers();
        view_voice_settings_of_a_specific_phone_number();
        update_the_voice_settings_of_a_phone_number_to_enable_inbound_call_recording();

//        update_the_voice_settings_of_a_phone_number_to_attach_the_number_to_a_new_connection();

        enable_emergency_on_a_phone_number();
//TODO:: we have to fix on the specs        view_messaging_settings_of_all_phone_numbers();
//TODO:: we have to fix on the specs         view_messaging_settings_of_a_specific_phone_number();
        update_the_messaging_settings_of_a_phone_number_to_attach_the_number_to_a_new_messaging_profile();
        get_the_second_page_of_results_for_phone_numbers();
        get_a_page_of_results_that_only_has_2_results_for_phone_numbers();
        filter_phone_numbers_for_numbers_by_tag();
    }
}
