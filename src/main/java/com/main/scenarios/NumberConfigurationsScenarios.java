package com.main.scenarios;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.NumberConfigurationsApi;
import io.swagger.client.model.CallRecording;
import io.swagger.client.model.ListMessagingSettingsResponse;
import io.swagger.client.model.ListPhoneNumberVoicesResponse;
import io.swagger.client.model.ListPhoneNumbersResponse;
import io.swagger.client.model.MessagingPhoneNumberUpdate;
import io.swagger.client.model.PhoneNumberEnableEmergency;
import io.swagger.client.model.PhoneNumberUpdate;
import io.swagger.client.model.RetrieveMessagingSettingsResponse;
import io.swagger.client.model.RetrievePhoneNumberVoiceResponse;
import io.swagger.client.model.VoiceUpdate;

import java.util.Collections;
import java.util.UUID;

import static com.main.scenarios.NumberUtilities.getPhoneNumbersBasedOnLocation;
import static com.main.scenarios.NumberUtilities.orderNumber;

public class NumberConfigurationsScenarios implements TestScenario {
    private NumberConfigurationsApi apiInstance;
    private final String connectionId = "1476046853102371900";

    public NumberConfigurationsScenarios(ApiClient client) {
        apiInstance = new NumberConfigurationsApi(client);
    }

    public void get_all_of_your_phone_numbers() {
        //given
        ListPhoneNumbersResponse response = null;

        //when
        try {
            response = apiInstance.findPhoneNumbers(
                    0,
                    1000,
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
            phoneNumber = getPhoneNumbersBasedOnLocation(null, null, null, 1).get(0);
        } catch (ApiException e) {
            assert false;
        }

        //when
        try {
            response = apiInstance.findPhoneNumbers(
                    0,
                    1,
                    null,
                    phoneNumber,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        } catch (ApiException e) {
            assert false;
        }
        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void view_voice_settings_of_all_phone_numbers() {
        //given
        ListPhoneNumberVoicesResponse response = null;
        //when
        try {
            response = apiInstance.findPhoneNumberVoices(
                    0,
                    100,
                    null,
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

    public void view_voice_settings_of_a_specific_phone_number() {
        //given
        UUID phoneNumberId = null;
        RetrievePhoneNumberVoiceResponse response = null;
        try {
            String phoneNumber = getPhoneNumbersBasedOnLocation(null, null, null, 1).get(0);
            phoneNumberId = orderNumber(phoneNumber);
        } catch (ApiException e) {
            assert false;
        }

        //when
        try {
            response = apiInstance.retrievePhoneNumberVoice(phoneNumberId.toString());
        } catch (ApiException e) {
            assert false;
        }
        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void update_the_voice_settings_of_a_phone_number_to_enable_inbound_call_recording() {
        //given
        UUID phoneNumberId = null;
        RetrievePhoneNumberVoiceResponse response = null;
        try {
            String phoneNumber = getPhoneNumbersBasedOnLocation(null, null, null, 1).get(0);
            phoneNumberId = orderNumber(phoneNumber);
        } catch (ApiException e) {
            assert false;
        }

        //when
        try {
            response = apiInstance.updatePhoneNumberVoice(
                    new VoiceUpdate().callRecording(new CallRecording().inboundCallRecordingEnabled(true)),
                    phoneNumberId.toString()
            );
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
        PhoneNumberEnableEmergency response = null;
        UUID phoneNumberId = null;
        try {
            String phoneNumber = getPhoneNumbersBasedOnLocation("US", null, null, 1).get(0);
            phoneNumberId = orderNumber(phoneNumber);
        } catch (ApiException e) {
            assert false;
        }

        //when
        try {
            response = apiInstance.enableEmergencyPhoneNumber(new PhoneNumberEnableEmergency(), phoneNumberId.toString());
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
            response = apiInstance.listPhoneNumberMessagingSettings();
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
        UUID phoneNumberId = null;
        try {
            String phoneNumber = getPhoneNumbersBasedOnLocation(null, null, null, 1).get(0);
            phoneNumberId = orderNumber(phoneNumber);
        } catch (ApiException e) {
            assert false;
        }


        //when
        try {
            response = apiInstance.retrievePhoneNumberMessagingSettings(phoneNumberId.toString());
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert response.getData() != null;
    }

    public void update_the_messaging_settings_of_a_phone_number_to_attach_the_number_to_a_new_messaging_profile() {
        //given
        UUID targetMessagingProfileId = UUID.fromString("400174f2-6eb8-429b-a946-a27646a94a1a");
        RetrieveMessagingSettingsResponse response = null;
        UUID phoneNumberId = null;
        try {
            String phoneNumber = getPhoneNumbersBasedOnLocation(null, null, null, 1).get(0);
            phoneNumberId = orderNumber(phoneNumber);
        } catch (ApiException e) {
            assert false;
        }

        //when
        try {
            response = apiInstance.updatePhoneNumberMessagingSettings(new MessagingPhoneNumberUpdate().messagingProfileId(targetMessagingProfileId), phoneNumberId.toString());
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
            response = apiInstance.findPhoneNumbers(
                    1,
                    10,
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
            response = apiInstance.findPhoneNumbers(
                    0,
                    2,
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
            String phoneNumber = getPhoneNumbersBasedOnLocation(null, null, null, 1).get(0);
            UUID phoneNumberId = orderNumber(phoneNumber);
            apiInstance.updatePhoneNumber(new PhoneNumberUpdate().tags(Collections.singletonList(tag)), phoneNumberId.toString());
        } catch (ApiException e) {
            assert false;
        }


        //when
        try {
            response = apiInstance.findPhoneNumbers(
                    0,
                    2,
                    tag,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
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
        view_messaging_settings_of_all_phone_numbers();
        view_messaging_settings_of_a_specific_phone_number();
        update_the_messaging_settings_of_a_phone_number_to_attach_the_number_to_a_new_messaging_profile();
        get_the_second_page_of_results_for_phone_numbers();
        get_a_page_of_results_that_only_has_2_results_for_phone_numbers();
        filter_phone_numbers_for_numbers_by_tag();
    }
}
