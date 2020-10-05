package com.main.scenarios;

import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.apis.NumberOrderRegulatoryRequirementsApi;
import com.telnyx.sdk.apis.NumberOrdersApi;
import com.telnyx.sdk.models.CreateNumberOrderRequest;
import com.telnyx.sdk.models.ListNumberOrderRegulatoryRequirementsResponse;
import com.telnyx.sdk.models.ListPhoneNumberRegulatoryRequirementsResponse;
import com.telnyx.sdk.models.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

import static com.main.scenarios.NumberUtilities.getPhoneNumbersBasedOnLocation;

public class NumberOrderRegulatoryRequirementsScenarios implements TestScenario {
    private NumberOrderRegulatoryRequirementsApi apiInstance;

    public NumberOrderRegulatoryRequirementsScenarios(ApiClient client) {
        apiInstance = new NumberOrderRegulatoryRequirementsApi(client);
    }

    public void get_all_regulatory_requirements() {
        //given
        ListNumberOrderRegulatoryRequirementsResponse response = null;

        //when
        try {
            response = apiInstance.listNumberOrderRegulatoryRequirements(null, null, null);
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
        List<String> phoneNumbers = new ArrayList<>();
        try {
            phoneNumbers = getPhoneNumbersBasedOnLocation(countryCode, null, null, 1);
            NumberOrdersApi numberOrdersApiInstance = new NumberOrdersApi();
            numberOrdersApiInstance.createNumberOrder(
                    new CreateNumberOrderRequest().addPhoneNumbersItem(new PhoneNumber().phoneNumber(phoneNumbers.get(0))));
        } catch (Exception e) {
            assert false;
        }

        //when
        try {
            response = apiInstance.listPhoneNumberRegulatoryRequirements(phoneNumbers);
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
