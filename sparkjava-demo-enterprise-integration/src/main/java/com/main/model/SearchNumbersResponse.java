package com.main.model;

import com.telnyx.sdk.models.ListAvailablePhoneNumbersResponse;

public class SearchNumbersResponse {
    public ListAvailablePhoneNumbersResponse apiResponse;
    public String json;
    public Boolean valid;

    public ListAvailablePhoneNumbersResponse getApiResponse() {
        return apiResponse;
    }

    public SearchNumbersResponse setApiResponse(ListAvailablePhoneNumbersResponse apiResponse) {
        this.apiResponse = apiResponse;
        return this;
    }

    public String getJson() {
        return json;
    }

    public SearchNumbersResponse setJson(String json) {
        this.json = json;
        return this;
    }

    public Boolean getValid() {
        return valid;
    }

    public SearchNumbersResponse setValid(Boolean valid) {
        this.valid = valid;
        return this;
    }
}
