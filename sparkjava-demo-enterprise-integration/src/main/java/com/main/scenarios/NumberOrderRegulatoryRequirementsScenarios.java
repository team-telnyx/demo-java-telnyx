package com.main.scenarios;

import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.apis.NumberOrderRegulatoryRequirementsApi;
import com.telnyx.sdk.apis.NumberOrdersApi;
import com.telnyx.sdk.apis.NumberSearchApi;
import com.telnyx.sdk.models.CreateNumberOrderRequest;
import com.telnyx.sdk.models.ListNumberOrderRegulatoryRequirementsResponse;
import com.telnyx.sdk.models.ListPhoneNumberRegulatoryRequirementsResponse;
import com.telnyx.sdk.models.PhoneNumber;

import java.util.Collections;
import java.util.Objects;

public class NumberOrderRegulatoryRequirementsScenarios implements TestScenario {
    private NumberOrderRegulatoryRequirementsApi numberOrderRegulatoryRequirementsApi;
    private NumberSearchApi numberSearchApi;
    private NumberOrdersApi numberOrdersApi;

    public NumberOrderRegulatoryRequirementsScenarios(ApiClient client) {
        numberOrderRegulatoryRequirementsApi = new NumberOrderRegulatoryRequirementsApi(client);
        numberSearchApi = new NumberSearchApi(client);
        numberOrdersApi = new NumberOrdersApi(client);
    }

    public void get_all_regulatory_requirements() {
        //given
        ListNumberOrderRegulatoryRequirementsResponse response = null;

        //when
        try {
            response = numberOrderRegulatoryRequirementsApi.listNumberOrderRegulatoryRequirements()
                    .execute();
        } catch (ApiException e) {
            assert false;
        }

        //then
        assert response != null;
        assert !response.getData().isEmpty();
    }

    public void get_the_regulatory_requirements_for_a_Spanish_phone_number_that_was_ordered() {
        //given
        ListPhoneNumberRegulatoryRequirementsResponse response = null;
        String countryCode = "ES";
        String phoneNumber = null;
        try {
            phoneNumber = Objects.requireNonNull(numberSearchApi.listAvailablePhoneNumbers()
                    .filterCountryCode(countryCode)
                    .filterLimit(1)
                    .execute()
                    .getData())
                    .get(0)
                    .getPhoneNumber();

            numberOrdersApi.createNumberOrder(
                    new CreateNumberOrderRequest()
                            .phoneNumbers(Collections.singletonList(new PhoneNumber().phoneNumber(phoneNumber))))
                    .execute();
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = numberOrderRegulatoryRequirementsApi.listPhoneNumberRegulatoryRequirements()
                    .filterPhoneNumber(Collections.singletonList(phoneNumber))
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
        get_all_regulatory_requirements();
        get_the_regulatory_requirements_for_a_Spanish_phone_number_that_was_ordered();
    }
}
